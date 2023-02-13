#!/bin/sh

exec java \
  -showversion -server -Dfile.encoding=UTF-8 \
  -cp "/app:/app/lib/*" \
  -Xms$JAVA_XMS -Xmx$JAVA_XMX \
  -XX:ActiveProcessorCount=$CPU_CORES \
  $ADDITIONAL_JAVA_OPTOINS \
  -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled -XX:+ExplicitGCInvokesConcurrent \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:+ExitOnOutOfMemoryError \
  com.naumov.dotnetscriptsscheduler.DotnetScriptsSchedulerApplication