#!/bin/bash

set -e

sh fast_build_frontend.sh

# build spring application
./gradlew build
