#!/usr/bin/env bash
# Compile a module and start its main class with JDWP on port 5006 (suspended),
# then attach from IntelliJ with the "Attach 5006" run configuration.
# Usage: ./debug5006.sh <module-dir> <MainClass> [args...]
#   e.g. ./debug5006.sh hw03 IntListMystery
set -euo pipefail

PORT=5006
JDK="$HOME/.jdks/ms-25.0.4.1"
ROOT="$(cd "$(dirname "$0")" && pwd)"
MODULE="$ROOT/${1:?module dir, e.g. hw03}"
MAIN="${2:?main class, e.g. IntListMystery}"
shift 2

OUT="$ROOT/out/debug5006/$(basename "$MODULE")"
CP="$OUT:$ROOT/../library-sp26/*"

rm -rf "$OUT" && mkdir -p "$OUT"
SRCS=$(find "$MODULE/src" "$MODULE/tests" -name '*.java' 2>/dev/null)
"$JDK/bin/javac" -g -d "$OUT" -cp "$CP" $SRCS

echo "Waiting for debugger on port $PORT ..."
exec "$JDK/bin/java" \
  -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:$PORT \
  -cp "$CP" "$MAIN" "$@"
