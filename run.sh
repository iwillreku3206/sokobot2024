#!/usr/bin/env bash
echo "Running on $OSTYPE."
javac `find . | grep \.java$` -d out/ -cp out
java -classpath out main.Driver "$1" "$2"