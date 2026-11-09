#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/opt/weiter}"
NGINX_ENV="${NGINX_ENV:-$APP_DIR/.env.nginx}"
WEBROOT="$APP_DIR/certbot"

read_value() {
  awk -v key="$1" 'index($0, key "=") == 1 { sub(/^[^=]*=/, ""); sub(/\r$/, ""); print; exit }' "$NGINX_ENV"
}

CERT_DOMAIN="$(read_value CERT_DOMAIN)"
CERT_EMAIL="$(read_value CERT_EMAIL)"
: "${CERT_DOMAIN:?CERT_DOMAIN must be set in $NGINX_ENV}"

LIVE_DIR="/etc/letsencrypt/live/$CERT_DOMAIN"
RENEWAL_CONF="/etc/letsencrypt/renewal/$CERT_DOMAIN.conf"

has_live_cert() {
  [ -s "$LIVE_DIR/fullchain.pem" ] && [ -s "$LIVE_DIR/privkey.pem" ]
}

has_renewal_conf() {
  [ -s "$RENEWAL_CONF" ]
}

is_bootstrap_cert() {
  local issuer
  issuer="$(openssl x509 -in "$LIVE_DIR/fullchain.pem" -noout -issuer 2>/dev/null | tr -d '[:space:]')"
  [[ "$issuer" == *"CN=$CERT_DOMAIN"* ]]
}

bootstrap() {
  mkdir -p "$WEBROOT/.well-known/acme-challenge"
  if has_live_cert || has_renewal_conf; then
    return 0
  fi
  mkdir -p "$LIVE_DIR"
  openssl req -x509 -nodes -newkey rsa:2048 -days 30 \
    -subj "/CN=$CERT_DOMAIN" \
    -addext "subjectAltName=DNS:$CERT_DOMAIN" \
    -keyout "$LIVE_DIR/privkey.pem" \
    -out "$LIVE_DIR/fullchain.pem"
  chmod 600 "$LIVE_DIR/privkey.pem"
  chmod 644 "$LIVE_DIR/fullchain.pem"
}

issue() {
  if has_renewal_conf; then
    certbot renew --quiet --webroot -w "$WEBROOT"
  else
    if has_live_cert && is_bootstrap_cert; then
      rm -rf -- "$LIVE_DIR" "/etc/letsencrypt/archive/$CERT_DOMAIN"
    fi
    local registration=(--register-unsafely-without-email)
    if [ -n "$CERT_EMAIL" ]; then
      registration=(--email "$CERT_EMAIL" --no-eff-email)
    fi
    certbot certonly --webroot -w "$WEBROOT" \
      --non-interactive --agree-tos "${registration[@]}" \
      --keep-until-expiring --cert-name "$CERT_DOMAIN" -d "$CERT_DOMAIN"
  fi
  docker exec weiter-nginx nginx -s reload
}

case "${1:-}" in
  bootstrap) bootstrap ;;
  issue) issue ;;
  *) echo "Usage: tls.sh {bootstrap|issue}" >&2; exit 2 ;;
esac
