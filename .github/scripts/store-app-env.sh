#!/usr/bin/env bash
set -euo pipefail

: "${DB_HOST:?DB_HOST secret is required}"
: "${DB_NAME:?DB_NAME secret is required}"
: "${DB_USER:?DB_USER secret is required}"
: "${DB_PASSWORD:?DB_PASSWORD secret is required}"

env_file="$(mktemp)"
trap 'rm -f "$env_file"' EXIT
chmod 600 "$env_file"

{
  printf 'DB_HOST=%s\n' "$DB_HOST"
  printf 'DB_PORT=%s\n' "${DB_PORT:-3306}"
  printf 'DB_NAME=%s\n' "$DB_NAME"
  printf 'DB_USER=%s\n' "$DB_USER"
  printf 'DB_PASSWORD=%s\n' "$DB_PASSWORD"
} > "$env_file"

aws ssm put-parameter \
  --name "${ENV_PARAMETER:-/weiter/prod/env}" \
  --type SecureString \
  --overwrite \
  --value "file://$env_file" > /dev/null
echo "Stored the app environment in SSM."
