#! /bin/sh

./gradlew clean 
./gradlew aR 
cp app/build/outputs/aar/app-release.aar app/build/outputs/aar/bioticgamessdk.aar 
open app/build/outputs/aar/
