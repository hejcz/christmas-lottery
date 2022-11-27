#!/bin/bash 

# pull frontend submodule
git pull --recurse-submodules

# install frontend dependencies
cd christmas-lottery-frontend
npm i
cd ..

# build
source fast_build.sh

# push to mydevil
scp -r build/libs/meet-your-santa-0.1.jar hejcz@s55.mydevil.net:/home/hejcz/domains/santa.hejcz.usermd.net