#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/setenv.sh"
JAR_FILE="$SCRIPT_DIR/gastbot-widget-api-1.0-SNAPSHOT.jar"
LOG_FILE="$SCRIPT_DIR/gastbot-widget-api.log"

echo "Using env file: $ENV_FILE"

source "$ENV_FILE"
echo "GASTBOT_PROFILE=$GASTBOT_PROFILE"
java -jar -Djasypt.encryptor.password=$GASTBOT_SECRET -Dspring.profiles.active=$GASTBOT_PROFILE $JAR_FILE > $LOG_FILE &
processPid=$!
echo "Started process with PID $processPid"
echo "See gastbot-widget-api.log for logs"
pidFile="$SCRIPT_DIR/${processPid}.pid"
echo "$processPid" > "$pidFile"
