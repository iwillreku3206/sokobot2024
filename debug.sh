#!/usr/bin/env bash
echo "Running on $OSTYPE."
javac `find . | grep \.java$` -d out/ -cp out
java -classpath out visualizer.Visualizer "$1"
