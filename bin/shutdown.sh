#!/bin/bash
ps -ef|grep sealtalk-server|grep java | grep -v grep
if [ $? -eq 0 ];then
  PID_sealtalk_server_java=$(ps -ef|grep sealtalk-server|grep java |awk '{print $2}')
  echo  PID=$PID_sealtalk_server_java
  kill -15 $PID_sealtalk_server_java
  echo 'shutdown success!'
else
  echo ‘Not found PID!‘
fi