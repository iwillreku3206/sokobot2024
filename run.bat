forfiles /s /m "*.java" /c "cmd /c echo @relpath" > sources.txt
powershell -Command "(gc sources.txt) -replace '\\', '\\\\' | Out-File -encoding ASCII sources.txt"
javac @sources.txt -d out/ -cp out
del /s sources.txt
java -classpath out main.Driver %* > out/debug.txt