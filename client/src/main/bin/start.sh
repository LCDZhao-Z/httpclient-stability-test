#!/bin/sh

if [ -f $(dirname $0)/../../env.sh ]; then
    source $(dirname $0)/../../env.sh
fi

case "`uname`" in
    Linux)
		bin_abs_path=$(readlink -f $(dirname $0))
		;;
	*)
		bin_abs_path=`cd $(dirname $0); pwd`
		;;
esac
base=${bin_abs_path}/..

appName=$1

get_pid() {
        STR=$1
        PID=$2
        if [ ! -z "$PID" ]; then
                JAVA_PID=`ps -C java -f --width 1000|grep "$STR"|grep "$PID"|grep -v grep|awk '{print $2}'`
            else 
                JAVA_PID=`ps -C java -f --width 1000|grep "$STR"|grep -v grep|awk '{print $2}'`
        fi
    echo $JAVA_PID;
}

pid=`get_pid "appName=${appName}"`
if [ ! "$pid" = "" ]; then
	echo "${appName} is running."
	exit -1;
fi


JAVA_OPTS="-Djava.io.tmpdir=$base/tmp -DappName=${appName} -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS_MEM_AND_GC="-Xms1024m -Xmx1024m -Dio.netty.leakDetection.level=paraniod -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -Xloggc:logs/gc-${appName}.log -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+PrintTenuringDistribution -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=200M -XX:+HeapDumpAfterFullGC -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=logs/dumpfile-${appName}"
TYPE=$2
COMMAND=$3
URL=$4

cd $base
if [ ! -d "logs" ]; then
  mkdir logs
fi
java $JAVA_OPTS $JAVA_OPTS_MEM_AND_GC -classpath 'lib/*:conf' io.esastack.ClientApplication $TYPE $COMMAND $URL 1>>logs/server.log 2>&1 &

echo $! > $base/server.pid

echo OK!`cat $base/server.pid`
 
