@echo off
echo Compiling ChatClient.java...
javac ChatClient.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b
)

echo Building ChatClient.jar...
jar cfe ChatClient.jar ChatClient *.class

if %errorlevel% neq 0 (
    echo JAR build failed!
    pause
    exit /b
)

echo Done! You can now run: java -jar ChatClient.jar
pause
