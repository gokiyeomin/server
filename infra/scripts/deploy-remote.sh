#!/usr/bin/env bash
set -euo pipefail

: "${REGISTRY:?REGISTRY is required}"
: "${IMAGE_TAG:?IMAGE_TAG is required}"
: "${AWS_REGION:?AWS_REGION is required}"

APP_DIR=/opt/weiter
RELEASE_DIR="${RELEASE_DIR:-/tmp/weiter-release}"
ENV_PARAMETER="${ENV_PARAMETER:-/weiter/prod/env}"
NGINX_ENV_PARAMETER="${NGINX_ENV_PARAMETER:-/weiter/prod/nginx-env}"
REPOSITORY="${REPOSITORY:-weiter/server}"
BACKEND_HEALTH_URL="${BACKEND_HEALTH_URL:-http://127.0.0.1:8080/api/rooms?size=1}"
NGINX_HEALTH_URL="${NGINX_HEALTH_URL:-http://127.0.0.1/healthz}"

export AWS_DEFAULT_REGION="$AWS_REGION"
export IMAGE="$REGISTRY/$REPOSITORY:$IMAGE_TAG"

log() {
  printf '[deploy] %s\n' "$*"
}

fetch_parameter() {
  (
    umask 077
    aws ssm get-parameter --name "$1" --with-decryption \
      --query Parameter.Value --output text > "$2"
  )
  chmod 600 "$2"
}

read_nginx_value() {
  awk -v key="$1" 'index($0, key "=") == 1 { sub(/^[^=]*=/, ""); sub(/\r$/, ""); print; exit }' "$APP_DIR/.env.nginx"
}

wait_until_healthy() {
  local attempt
  for attempt in $(seq 1 40); do
    if curl -fsS -o /dev/null "$BACKEND_HEALTH_URL" && curl -fsS -o /dev/null "$NGINX_HEALTH_URL"; then
      return 0
    fi
    sleep 3
  done
  return 1
}

install_renewal_timer() {
  cat > /etc/systemd/system/weiter-certbot-renew.service <<UNIT
[Unit]
Description=Renew the Weiter TLS certificate
After=docker.service

[Service]
Type=oneshot
ExecStart=/bin/sh -c 'certbot renew --quiet --webroot -w $APP_DIR/certbot --deploy-hook "docker exec weiter-nginx nginx -s reload"'
UNIT
  cat > /etc/systemd/system/weiter-certbot-renew.timer <<UNIT
[Unit]
Description=Run the Weiter TLS renewal twice a day

[Timer]
OnCalendar=*-*-* 03,15:00:00
RandomizedDelaySec=1h
Persistent=true

[Install]
WantedBy=timers.target
UNIT
  systemctl daemon-reload
  systemctl enable --now weiter-certbot-renew.timer
}

install -d -m 0755 "$APP_DIR" "$APP_DIR/certbot"
install -m 0644 "$RELEASE_DIR/compose.prod.yml" "$APP_DIR/compose.yaml"
install -m 0644 "$RELEASE_DIR/infra/nginx/nginx.conf.template" "$APP_DIR/nginx.conf.template"
install -m 0755 "$RELEASE_DIR/infra/scripts/tls.sh" "$APP_DIR/tls.sh"
fetch_parameter "$ENV_PARAMETER" "$APP_DIR/.env"
fetch_parameter "$NGINX_ENV_PARAMETER" "$APP_DIR/.env.nginx"

log "rendering nginx.conf"
export SERVER_NAME="$(read_nginx_value SERVER_NAME)"
export CERT_DOMAIN="$(read_nginx_value CERT_DOMAIN)"
export NGINX_ALLOWED_ORIGIN="$(read_nginx_value NGINX_ALLOWED_ORIGIN)"
export NGINX_ALLOWED_ORIGIN_ALT="$(read_nginx_value NGINX_ALLOWED_ORIGIN_ALT)"
: "${SERVER_NAME:?SERVER_NAME must be set in $NGINX_ENV_PARAMETER}"
: "${CERT_DOMAIN:?CERT_DOMAIN must be set in $NGINX_ENV_PARAMETER}"
: "${NGINX_ALLOWED_ORIGIN:?NGINX_ALLOWED_ORIGIN must be set in $NGINX_ENV_PARAMETER}"
: "${NGINX_ALLOWED_ORIGIN_ALT:=http://127.0.0.1:5173}"
export NGINX_ALLOWED_ORIGIN_ALT
envsubst '${SERVER_NAME} ${CERT_DOMAIN} ${NGINX_ALLOWED_ORIGIN} ${NGINX_ALLOWED_ORIGIN_ALT}' \
  < "$APP_DIR/nginx.conf.template" > "$APP_DIR/nginx.conf"
test -s "$APP_DIR/nginx.conf"
if grep -q '\${' "$APP_DIR/nginx.conf"; then
  log "nginx.conf still has unresolved variables"
  exit 1
fi

bash "$APP_DIR/tls.sh" bootstrap

cd "$APP_DIR"
previous_image="$(docker inspect -f '{{.Config.Image}}' weiter-server 2>/dev/null || true)"

aws ecr get-login-password | docker login --username AWS --password-stdin "$REGISTRY"
log "pulling $IMAGE"
docker compose pull
docker compose up -d --remove-orphans
docker exec weiter-nginx nginx -t
docker exec weiter-nginx nginx -s reload

if ! wait_until_healthy; then
  log "health check failed for $IMAGE"
  docker compose logs --tail 80 backend || true
  if [ -n "$previous_image" ] && [ "$previous_image" != "$IMAGE" ]; then
    log "rolling back to $previous_image"
    IMAGE="$previous_image" docker compose up -d
  fi
  exit 1
fi
log "healthy: $IMAGE"

log "issuing the TLS certificate"
if ! bash "$APP_DIR/tls.sh" issue; then
  log "TLS certificate issuance failed; the app keeps serving with the current certificate"
fi
install_renewal_timer

docker image prune --all --force --filter "until=72h"
