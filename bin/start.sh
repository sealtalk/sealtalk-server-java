#!/bin/bash

ps -ef|grep sealtalk-xxxserver|grep java | grep -v grep
if [ $? -eq 0 ];then
  echo ‘can not start,sealtalk-server is running now,please shutdown first!‘
else
    echo "startup server......"
    nohup java -server -Xms1G -Xmx4G  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./ -jar sealtalk-server.jar --server.port=8080 --spring.profiles.active=prod  &
fi