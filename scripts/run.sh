#!/usr/bin/env bash

set -euxo pipefail

clojure -m concourse-opsman-installations-resource.cli "$@"
