#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p bin
cc license_validator.c -O2 -o bin/license_validator -lcrypto
