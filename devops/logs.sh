#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$SCRIPT_DIR/gastbot-widget-api.log"

if [ ! -f "$LOG_FILE" ]; then
  echo "Log file not found: $LOG_FILE"
  exit 1
fi

echo "📜 Showing live logs from: $LOG_FILE"
echo "--------------------------------------"

tail -n 100 -f "$LOG_FILE"