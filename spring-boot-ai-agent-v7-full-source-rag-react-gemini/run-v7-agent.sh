#!/usr/bin/env bash
set -e
if [ -z "$GEMINI_API_KEY" ]; then echo 'Set GEMINI_API_KEY first'; exit 1; fi
mvn spring-boot:run
