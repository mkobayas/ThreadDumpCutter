@echo off

set CLASSPATH="lib\args4j-2.33.jar;lib\ThreadDumpCutter-1.0.0.jar"
set JAVA_OPTS=-Xmx512m

java %JAVA_OPTS% -classpath %CLASSPATH% -Dapp.name="tdcutter" org.mk300.tdcutter.Main %*
