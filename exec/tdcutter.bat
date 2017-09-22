@echo off
set "DIRNAME=%~dp0%"
set CLASSPATH="%DIRNAME%lib\args4j-2.33.jar;%DIRNAME%lib\ThreadDumpCutter-1.0.0.jar"
set JAVA_OPTS=-Xmx512m

java %JAVA_OPTS% -classpath %CLASSPATH% -DTDC_BASE=%DIRNAME% org.mk300.tdcutter.Main %*
