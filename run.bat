@echo off
REM Compiles and runs the E-Commerce Shopping System.
REM Double-click this file, or type run.bat in a terminal opened in this folder.

cd /d "%~dp0"
chcp 65001 >nul

if exist out rmdir /s /q out
echo Compiling...
javac -encoding UTF-8 -d out -sourcepath src src\com\ecommerce\Main.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Make sure the Java JDK is installed and "javac" works in your terminal.
    pause
    exit /b 1
)

java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp out com.ecommerce.Main
pause
