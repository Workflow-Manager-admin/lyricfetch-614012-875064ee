#!/bin/bash
cd /tmp/kavia/workspace/code-generation/lyricfetch-614012-875064ee/lyricfetch
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

