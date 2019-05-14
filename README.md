# concourse-opsman-installations-resource

Models the completed "Apply Changes" in OpsMan

## Source Configuration
- opsmgr:
    - url
    - username
    - password
- history_hack_mode: true|false _(Optional)_

## Behavior

### check

Timestamp of the finished_at of the installations

If `history_hack_mode` is on, then on first check the resource will
report the oldest version only. Subsequent checks will report the
remaining versions. This allows jobs that get this resource to specify
`version: every` and trigger on all versions since the beginning of
time.

### in

Fetch the installation metadata for an installation

- params:
  - fetch_logs: true|**false**
  - include_history: true|**false**

### out

This resource cannot put.
