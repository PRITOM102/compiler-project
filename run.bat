@echo off
chcp 65001 >nul
cd /d "%~dp0new bangla compiler"
echo Compiling Sylheti Compiler...
javac -encoding UTF-8 banglacompiler\*.java
if errorlevel 1 (
    echo.
    echo Compilation failed.
    pause
    exit /b 1
)
echo.
echo Running Sylheti Compiler...
echo ==================================================
java -Dfile.encoding=UTF-8 banglacompiler.Main
echo.
pause
