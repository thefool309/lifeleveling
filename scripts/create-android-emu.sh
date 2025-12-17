#!/usr/bin/env bash

# !! Exit immediately if a command exits with a non-zero status !!
set -e
set -x

# shellcheck disable=SC1091
# export "$(grep -v '^#' .env | xargs)" # commented out because it was causing the workflow to fail

# the package path based on imported env variables
PACKAGE="system-images;android-${API_LEVEL};google_apis;x86_64"

echo " Installing android image for API level: ${API_LEVEL}"

#install system image
yes | sdkmanager "$PACKAGE"

echo "Creating AVD: ${AVD_NAME} using package ${PACKAGE}"

echo "yes" | avdmanager create avd \
  -v \
  --force \
  --name "${AVD_NAME}" \
  --package "${PACKAGE}" \
  --device "pixel_4"

echo "AVD ${AVD_NAME} created successfully!"