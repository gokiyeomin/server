#!/usr/bin/env bash
set -euo pipefail

: "${REGISTRY:?REGISTRY is required}"
: "${IMAGE_TAG:?IMAGE_TAG is required}"
: "${AWS_REGION:?AWS_REGION is required}"

instance_id="$(aws ec2 describe-instances \
  --filters Name=tag:Name,Values=weiter-server Name=instance-state-name,Values=running \
  --query 'Reservations[].Instances[].InstanceId' --output text)"
if [ -z "$instance_id" ] || [ "$(wc -w <<<"$instance_id")" -ne 1 ]; then
  echo "Expected exactly one running instance tagged Name=weiter-server, got: '$instance_id'" >&2
  exit 1
fi

bundle_b64="$(tar czf - compose.prod.yml infra/nginx/nginx.conf.template infra/scripts/deploy-remote.sh infra/scripts/tls.sh | base64 -w0)"
remote_command="rm -rf /tmp/weiter-release && mkdir -p /tmp/weiter-release && echo $bundle_b64 | base64 -d | tar xz -C /tmp/weiter-release && REGISTRY=$REGISTRY IMAGE_TAG=$IMAGE_TAG AWS_REGION=$AWS_REGION bash /tmp/weiter-release/infra/scripts/deploy-remote.sh"

command_id="$(aws ssm send-command \
  --instance-ids "$instance_id" \
  --document-name AWS-RunShellScript \
  --comment "weiter deploy $IMAGE_TAG" \
  --timeout-seconds 600 \
  --parameters "$(jq -cn --arg command "$remote_command" '{commands: [$command]}')" \
  --query Command.CommandId --output text)"
echo "SSM command $command_id on $instance_id"

status="Pending"
for _ in $(seq 1 120); do
  sleep 5
  status="$(aws ssm get-command-invocation --command-id "$command_id" --instance-id "$instance_id" \
    --query Status --output text 2>/dev/null || echo Pending)"
  case "$status" in
    Pending|InProgress|Delayed) ;;
    *) break ;;
  esac
done

aws ssm get-command-invocation --command-id "$command_id" --instance-id "$instance_id" \
  --query '[StandardOutputContent, StandardErrorContent]' --output text
echo "SSM status: $status"
[ "$status" = "Success" ]
