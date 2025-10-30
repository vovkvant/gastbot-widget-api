#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Проверяем, есть ли PID-файлы
if [ 0 == $(ls *.pid 2>/dev/null | wc -l) ]; then
    echo "Process is not started (PID file not found)!"
    exit 1
fi

pidFileName="$(ls *.pid | head -n 1)"

processPid=$(cat "$pidFileName")

if ps -p "$processPid" > /dev/null 2>&1; then
    echo "Stopping process with PID $processPid..."
    kill -9 "$processPid"
    rm "$pidFileName"
    echo "Process stopped and PID file removed."
else
    echo "No process found with PID $processPid (removing stale PID file)."
    rm "$pidFileName"
fi