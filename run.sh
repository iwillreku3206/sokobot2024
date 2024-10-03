#!/usr/bin/env bash

javac src/*/*.java -d out/ -cp out

java -classpath out main.Driver $1 $2
