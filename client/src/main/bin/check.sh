#!/bin/sh

pid=`ps -ef | grep check | grep -v grep | awk '{print $2}'`
if [ ! "$pid" == "" ];
then
	kill -9 $pid
fi

current_dir=`dirname $0`

appName=http-protocol

while [ 1 = 1 ]
do
	tmp=`ps -ef | grep java | grep ${appName}`
	if [[ "$tmp" = *${appName}* ]];
	then
		echo "${appName} is exists"
	else
		$current_dir/start.sh
		echo "${appName} is start"
	fi
	sleep 300
done

