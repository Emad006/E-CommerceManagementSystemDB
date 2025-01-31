@echo off
setlocal

:: Absolute path of MySQL Connector JAR
set CLASSPATH=lib\mysql-connector-j-9.2.0.jar

:: Create the bin directory if it doesn't exist
if not exist bin mkdir bin

:: Compile all Java files, including the gui.auth package
for /r %%f in (*.java) do (
    javac -d bin -cp "%CLASSPATH%" "%%f"
)

:: Change to bin directory
cd bin

:: Run Main class with MySQL Connector in classpath
java -cp ".;%CLASSPATH%" Main

endlocal
