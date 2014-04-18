#!/bin/bash

# trap "kill 0" SIGINT SIGTERM EXIT

mvn exec:java -Dexec.args="$@"

