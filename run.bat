javac src/gui/*.java src/main/*.java src/reader/*.java src/solver/*.java -d out/ -cp out
java -classpath out main.Driver %*
