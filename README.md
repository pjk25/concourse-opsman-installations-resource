# concourse-opsman-installations-resource

Models the completed "Apply Changes" in OpsMan

## Source Configuration
- opsmgr:
    - url
    - username
    - password

## Behavior

### check

Timestamp of the finished_at of the installations

### in

Fetch the installation metadata for an installation

params:
  fetch_logs: true|false
  include_history: true|false

### out

This resource cannot put.
