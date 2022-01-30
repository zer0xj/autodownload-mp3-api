#!/bin/bash

set -e

PROJECT_NAME="autodownload-mp3-api"

PROJECT_FOLDER="$HOME/Personal/$PROJECT_NAME"

if [ ! -e $PROJECT_FOLDER ]; then
	echo "ERROR: Cannot find project's folder ($PROJECT_FOLDER)"
	exit 1
fi

PROJECT_VERSION="$(grep -E '^version' build.gradle | cut -f2 -d"'")"

if [[ "$PROJECT_VERSION" == "" ]]; then
	echo "ERROR: Cannot find project's folder ($PROJECT_FOLDER)"
	exit 1
fi

ARCH="amd64"

USER="www-data"
GROUP="www-data"

BUILD_FOLDER="$PROJECT_FOLDER/build"

JARFILE="$BUILD_FOLDER/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"

if [ ! -e "$JARFILE" ]; then
	echo "ERROR: Cannot find built JAR file ($JARFILE)"
	exit 1
fi

JVMFILE="$BUILD_FOLDER/resources/main/jvm.conf"

if [ ! -e "$JVMFILE" ]; then
	echo "ERROR: Cannot find built JVM configuration file ($JVMFILE)"
	exit 1
fi

CONFIGFILE="$BUILD_FOLDER/resources/main/application.yml"

if [ ! -e "$CONFIGFILE" ]; then
	echo "ERROR: Cannot find built configuration file ($CONFIGFILE)"
	exit 1
fi

SERVICEFILE="$BUILD_FOLDER/resources/main/${PROJECT_NAME}.service"

if [ ! -e "$SERVICEFILE" ]; then
	echo "ERROR: Cannot find built systemd service file ($SERVICEFILE)"
	exit 1
fi

DEB_TARGET="$BUILD_FOLDER/${PROJECT_NAME}_${PROJECT_VERSION}_$ARCH"

if [ -e "$DEB_TARGET" ]; then
	rm -rfv "$DEB_TARGET"
fi

JAR_TARGET="/opt/julien/$PROJECT_NAME"

mkdir -pv "${DEB_TARGET}${JAR_TARGET}"
cp -v "$JARFILE" "${DEB_TARGET}${JAR_TARGET}/${PROJECT_NAME}.jar"
cp -v "$JVMFILE" "${DEB_TARGET}${JAR_TARGET}"

mkdir -pv "$DEB_TARGET/etc/julien/$PROJECT_NAME/"
cp -v "$CONFIGFILE" "$DEB_TARGET/etc/julien/$PROJECT_NAME/"

mkdir -pv "$DEB_TARGET/etc/systemd/system/"
cp -v "$SERVICEFILE" "$DEB_TARGET/etc/systemd/system/"
find "$DEB_TARGET/etc/systemd/system/" -maxdepth 1 -type f ! -perm 0700 -exec chmod 0644 {} \;

mkdir -pv "$DEB_TARGET/DEBIAN/"
printf "Package: $PROJECT_NAME\nVersion: $PROJECT_VERSION\nArchitecture: $ARCH\nMaintainer: Julien Neidballa\nDescription: Youtube to MP3 API\n" > "$DEB_TARGET/DEBIAN/control"

printf "#!/bin/bash\n\nsystemctl enable ${PROJECT_NAME}.service || exit 1\n\nsystemctl daemon-reload\n\nchown -R ${USER}:${GROUP} $JAR_TARGET\n\nsystemctl start ${PROJECT_NAME}\n\nexit 0\n" >> "$DEB_TARGET/DEBIAN/postinst"
chmod 755 "$DEB_TARGET/DEBIAN/postinst"

printf "#!/bin/bash\n\nset -e\n\nsystemctl stop ${PROJECT_NAME}\n\nsystemctl disable ${PROJECT_NAME}.service\n\nsystemctl daemon-reload\n\nexit 0\n" >> "$DEB_TARGET/DEBIAN/prerm"
chmod 755 "$DEB_TARGET/DEBIAN/prerm"

dpkg-deb --build --root-owner-group "$DEB_TARGET"

exit 0

