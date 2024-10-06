#!/usr/bin/env bash
echo "Running on $OSTYPE."
if [[ "$OSTYPE" =~ ^linux ]]; then
    javac `find . | grep \.java$` -d out/ -cp out
elif [[ "$OSTYPE" =~ ^msys ]] || [[ "$OSTYPE" =~ ^win32 ]]; then
    javac `find src | grep \.java$` -d out/ -cp out
else # Assuming default is windows
    echo "Unsupported OS: $OSTYPE. This project is not supported on this system."
    javac `find src | grep \.java$` -d out/ -cp out
fi
java -classpath out main.Driver "$1" "$2"