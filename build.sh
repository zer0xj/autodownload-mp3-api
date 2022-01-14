#!/bin/bash

PROJECT_NAME="autodownload-mp3-api"

if [ -z "$YOUTUBE_APIKEY" ]; then

	LOCALCONFIGFILE="/etc/julien/$PROJECT_NAME/application.yml"

	if [ -e "$LOCALCONFIGFILE" ]; then
		export YOUTUBE_APIKEY="$(grep -A5 '^youtube:' "$LOCALCONFIGFILE" | grep 'api-key:' | cut -f2- -d':' | sed 's/ //g')"
	fi
fi

SERVICE_RUNNING="$(systemctl status "$PROJECT_NAME" &>/dev/null; echo $?)"

if [ $SERVICE_RUNNING -eq "0" ]; then
	sudo systemctl stop "$PROJECT_NAME"
fi

BUILD_SUCCESS=0

./gradlew clean build -Pyoutube.api-key="$YOUTUBE_APIKEY" && BUILD_SUCCESS=1

[ $SERVICE_RUNNING -eq "0" ] && sudo systemctl start "$PROJECT_NAME"

[ $BUILD_SUCCESS -ne "0" ] && ./build-deb.sh

exit 0

