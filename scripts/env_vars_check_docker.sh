#!/usr/bin/env bash

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
    }
}" | docker run -i concourse-opsman-installations-resource /opt/resource/check
