#!/usr/bin/env bash

ORIGINAL_DIR="$(pwd)"

cd "$(dirname "$0")/.."

set -euxo pipefail

jq -n "{
    \"source\": {
        \"opsmgr\": {
            \"url\": \"${OM_TARGET}\",
            \"username\": \"${OM_USERNAME}\",
            \"password\": \"${OM_PASSWORD}\"
        }
    },
    \"version\": {
        \"finished_at\": \"2019-05-09T03:27:24.576Z\"
    },
    \"params\": {
        \"fetch_logs\": true,
        \"include_history\": true
    }
}" | ./scripts/run.sh in ${ORIGINAL_DIR}
