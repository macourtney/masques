REM Masques Launch script

@echo off
setlocal

set CLASSPATH=resources
set CLASSPATH=%CLASSPATH%;lib/*

@echo on

java -cp "%CLASSPATH%" masques.main %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal
