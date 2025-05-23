#!/bin/bash
export JAVA_HOME=/home/codespace/java/21.0.6-ms
export PATH=$JAVA_HOME/bin:$PATH
./gradlew "$@"