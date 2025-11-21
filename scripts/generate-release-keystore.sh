#!/usr/bin/env bash
set -euo pipefail

KEYSTORE="release.keystore"
# optional: pass password as argument

SECRET_KEYSTORE="ANDROID_KEYSTORE_BASE64"
SECRET_STOREPASS="ANDROID_KEYSTORE_PASSWORD"
SECRET_KEYPASS="ANDROID_KEY_PASSWORD"
SECRET_ALIAS="ANDROID_KEY_ALIAS"


if [[ -z "$PASSWORD" ]]; then
  echo "Usage: $0 <keystore-password>"
  exit 1
fi

if [[ -f "$KEYSTORE" ]]; then
  echo "Error: $KEYSTORE already exists. Delete it or choose a different name."
  exit 1
fi

abort() {
  echo "Error: $1"
  exit 1
}

check_gh_auth() {
  echo "Checking Github CLI authentication..."
  if ! gh auth status > /dev/null 2>&1; then
    echo "if you're trying to run this script without our permission you need to try harder, SCR1PT K1DD13"
    abort "if you should be here you must run: gh auth login."
  fi
}

check_permissions() {
  echo "Checking permission to modify secrets..."

  USERNAME=$(gh api user --jq '.login')  # get current user reliably

  # Try fetching permission for this user
  if ! PERM=$(gh api "repos/$REPO/collaborators/$USERNAME/permission" --jq '.permission' 2>/dev/null); then
    abort "You don't have write/admin permissions to $REPO. Ask a maintainer for access."
  fi

  if [[ "$PERM" != "admin" && "$PERM" != "write" ]]; then
    abort "Your permission level ($PERM) is not sufficient to upload secrets."
  fi

  echo "Repo access confirmed for $USERNAME: $PERM"
}


confirm() {
    read -rp "Are you sure you want to overwrite secrets? (y/N) " answer
    [[ "$answer" == "y" || "$answer" == "Y" ]] || abort "User cancelled."
}

upload_secret() {
  local name="$1"
  local value="$2"
  echo "Uploading secret: $name"
  gh secret set "$name" --repo "REPO" --body "$value" > /dev/null
}

[[ -z "$REPO" ]] && abort "Set REPO=owner/repo in your environment or .env file."

check_gh_auth
check_permissions

read -rsp "Enter keystore password: " PASSWORD
echo
read -rsp "Confirm keystore password: " PASSWORD2
echo
[[ "$PASSWORD" == "$PASSWORD2" ]] || abort "Passwords do not match!"

read -rp "Enter key alias: " ALIAS
echo
read -rp "Confirm key alias: " ALIAS2
echo
[[ "$ALIAS" == "$ALIAS2" ]] || abort "Aliases do not match!"

keytool -genkeypair \
  -v \
  -keystore "$KEYSTORE" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000 \
  -storepass "$PASSWORD" \
  -keypass "$PASSWORD" \
  -dname "CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, S=Unknown, C=US"

KEYSTORE_BASE64="${KEYSTORE}.base64"

# consistent encoding across platforms
base64 "$KEYSTORE" | tr -d '\n' > "$KEYSTORE_BASE64"

[[ -f $KEYSTORE_BASE64 ]] || abort "For some reason the $KEYSTORE_BASE64 file was not generated"

echo "Created:"
echo "$KEYSTORE"
echo "$KEYSTORE_BASE64"

confirm

KEYSTORE_BASE64_CONTENT=$(< "$KEYSTORE_BASE64")

upload_secret "$SECRET_KEYSTORE" "$KEYSTORE_BASE64_CONTENT"
upload_secret "$SECRET_STOREPASS" "$PASSWORD"
upload_secret "$SECRET_KEYPASS" "$PASSWORD"
upload_secret "$SECRET_ALIAS" "$ALIAS"

echo "All secrets uploaded successfully!!!"
