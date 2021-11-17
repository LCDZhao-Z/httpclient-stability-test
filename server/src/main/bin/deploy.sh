#! /bin/bash

currentDir=`pwd`
appName=server
zipName=server-dev
appZip="${zipName}.zip"
currentDate=`date +"%Y%m%d%H%M%S"`
tempDir="temp${currentDate}"

# 如果存在新的包，则备份原来的包
if [ -f ${appZip} ]; then
    if [ -d "${appName}" ]; then
        echo "开始将原发布包备份到${appName}_${currentDate}.zip"
        mkdir -p "${tempDir}"
        mv ${appName}/logs/ ${tempDir}
        mv ${appName}/server.pid ${tempDir}
        zip -r "${appName}_${currentDate}.zip" "${appName}"
        rm -rf ${appName}
    fi
    unzip -o ${appZip}
    mv ${zipName} ${appName}
    mv ${tempDir}/logs/ ${appName}
    mv ${tempDir}/server.pid ${appName}
    rm -rf ${tempDir}
fi

if [ -d "${appName}" ]; then
    echo "启动程序"
    sh ${appName}/bin/start.sh $1 $2
    rm -rf ${appZip}
else
    echo "发布包不存在，程序退出"
fi