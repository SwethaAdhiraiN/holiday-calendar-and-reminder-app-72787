#!/bin/bash
cd /home/kavia/workspace/code-generation/holiday-calendar-and-reminder-app-72787/calendar_app_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

