#!/bin/bash

# Absolute path of MySQL Connector JAR
CLASSPATH="../lib/mysql-connector-j-9.2.0.jar"

# Compile all Java files, including the gui.auth package
javac -d bin -cp "$CLASSPATH" $(find . -name "*.java")

# Change to bin directory
cd bin

# Run Main class with MySQL Connector in classpath
java -cp ".:$CLASSPATH" Main
