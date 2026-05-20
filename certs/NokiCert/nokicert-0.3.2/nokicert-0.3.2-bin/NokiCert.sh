#!/bin/bash

# OS
OS=`uname -s`
# Hardware Platform
HW=`uname -m`

CLASSPATH=${CLASSPATH}:bluecove.jar
CLASSPATH=${CLASSPATH}:nokicert.jar

if [ $OS == "Linux" ]
then
	CLASSPATH=${CLASSPATH}:linux-$HW/bluecove-gpl.jar
	CLASSPATH=${CLASSPATH}:linux-$HW/bluecove-bluez.jar
	CLASSPATH=${CLASSPATH}:linux-$HW/swt.jar
	java -cp ${CLASSPATH} net.tuxed.nokicert.NokiCertGUI
elif [ $OS == "Darwin" ] 
then
	CLASSPATH=${CLASSPATH}:mac/swt.jar
	java -XstartOnFirstThread -d32 -cp ${CLASSPATH} net.tuxed.nokicert.NokiCertGUI
fi

