#!/usr/bin/env bash

set -euxo pipefail

clojure -A:native-image
