#!/usr/bin/env bash

# !! Exit immediately if a command exits with a non-zero status !!
set -e
set -x

AVD_NAME="TestAVD"

API_LEVEL=36
# shellcheck disable=SC1091
# export "$(grep -v '^#' .env | xargs)" # commented out because it was causing the workflow to fail

echo "$API_LEVEL"

# the package path based on imported env variables
PACKAGE="system-images;android-${API_LEVEL};google_apis;x86_64"

echo " Installing android image for API level: ${API_LEVEL}"

#install system image
yes | sdkmanager "$PACKAGE"

echo "Creating AVD: ${AVD_NAME} using package ${PACKAGE}"

echo "yes" | avdmanager -v create avd \
  --force \
  --name "${AVD_NAME}" \
  --package "${PACKAGE}" \
  --device "pixel_4"

echo "AVD ${AVD_NAME} created successfully!"