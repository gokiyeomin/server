#!/usr/bin/env bash
set -euo pipefail

name="$(bash .github/scripts/source-fingerprint.sh)"
found="$(gh api "repos/$GITHUB_REPOSITORY/actions/artifacts?name=$name&per_page=20" \
  --jq '[.artifacts[] | select(.expired == false)] | first | if . == null then "" else "\(.id) \(.workflow_run.id)" end')"

artifact_id=""
run_id=""
read -r artifact_id run_id <<<"$found"

{
  echo "artifact-id=$artifact_id"
  echo "run-id=$run_id"
} >> "$GITHUB_OUTPUT"

if [ -n "$artifact_id" ]; then
  echo "Reusing the tested JAR. fingerprint=$name artifact=$artifact_id run=$run_id"
else
  echo "No tested JAR to reuse. fingerprint=$name"
fi
