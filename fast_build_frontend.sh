#!/bin/bash 

set -e

# remove old build
rm -rf src/main/resources/public

# build frontend
cd christmas-lottery-frontend
npm i
npx ng build --prod
cd ..

# copy build to spring statics
mkdir -p src/main/resources/public/
mv christmas-lottery-frontend/dist/santa2-frontend/* src/main/resources/public/
