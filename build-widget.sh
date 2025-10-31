#!/bin/bash
set -e

GASTBOT_WIDGET_FOLDER="/home/gastbot/back/gastbot-widget-api"

git pull
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED"
mvn -DskipTests=true clean install
cp gastbot-widget/target/gastbot-widget-api-1.0-SNAPSHOT.jar $GASTBOT_WIDGET_FOLDER

cp devops/start.sh $GASTBOT_WIDGET_FOLDER
cp devops/stop.sh $GASTBOT_WIDGET_FOLDER
cp devops/logs.sh $GASTBOT_WIDGET_FOLDER

echo "Stopping gastbot widget api application (backend)..."
$GASTBOT_WIDGET_FOLDER/stop.sh

echo "Starting gastbot widget api application (backend)..."
$GASTBOT_WIDGET_FOLDER/start.sh
