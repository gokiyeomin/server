#!/usr/bin/env bash
set -euo pipefail

inputs=(src build.gradle settings.gradle gradle.properties gradle lombok.config)
existing=()
for path in "${inputs[@]}"; do
  if [ -e "$path" ]; then
    existing+=("$path")
  fi
done

hash="$(git ls-tree -r HEAD -- "${existing[@]}" | sha256sum | cut -c1-16)"
echo "jar-$hash"
