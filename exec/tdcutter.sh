#!/bin/sh

DIRNAME=`dirname "$0"`

JAVA_OPTS="-Xmx512m"
CLASS_PATH="$DIRNAME/lib/args4j-2.33.jar:$DIRNAME/lib/ThreadDumpCutter-1.0.0.jar"

java $JAVA_OPTS -classpath $CLASS_PATH org.mk300.tdcutter.Main "$@"
