#!/bin/bash
git pull
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED"
mvn -DskipTests=true clean install
cp gastbot-widget/target/gastbot-widget-api-1.0-SNAPSHOT.jar /home/gastbot/back/widget

cp gastbot-widget/devops/start.sh /home/gastbot/back/widget
cp gastbot-widget/devops/stop.sh /home/gastbot/back/widget
cp gastbot-widget/devops/logs.sh /home/gastbot/back/widget

echo "Stopping gastbot widget api application (backend)..."
/home/gastbot/back/widget/stop.sh

echo "Starting gastbot widget api application (backend)..."
/home/gastbot/back/widget/start.sh
