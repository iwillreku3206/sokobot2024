#!/usr/bin/env bash

javac `find src | grep \.java$` -d out/ -cp out

java -classpath out main.Driver $1 $2
