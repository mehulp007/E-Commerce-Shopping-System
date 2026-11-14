#!/usr/bin/env sh
# Compiles and runs the E-Commerce Shopping System on macOS / Linux / Git Bash.
# Usage: sh run.sh

cd "$(dirname "$0")" || exit 1
rm -rf out
echo "Compiling..."
javac -encoding UTF-8 -d out -sourcepath src src/com/ecommerce/Main.java || exit 1
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp out com.ecommerce.Main
