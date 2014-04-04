#!/bin/bash

#Node package management for Grunt build steps




if [ -f "~/.sbtconfig" ]; then
  . ~/.sbtconfig
fi


# SBT configuration
export SBT_BOOT_DIR=${HOME}/.sbt/boot/

if [ ! -d "${SBT_BOOT_DIR}" ]; then
  mkdir -p ${SBT_BOOT_DIR}
fi


# Build configuration
BUILD_PARAMS=""
if [ -n "$BUILD_NUMBER" ]; then
  BUILD_PARAMS="${BUILD_PARAMS} -Dbuild.number=\"$BUILD_NUMBER\""
fi 
if [ -n "$BUILD_VCS_NUMBER" ]; then
  BUILD_PARAMS="${BUILD_PARAMS} -Dbuild.vcs.number=\"$BUILD_VCS_NUMBER\""
fi 


# Ivy configuration
echo "setting ivy cache dir"
IVY_PARAMS="-Dsbt.ivy.home=$HOME/.ivy2-frontend -Divy.home=$HOME/.ivy2-frontend"
echo $IVY_PARAMS


DOMAIN=`hostname -d`

if [ "$DOMAIN" != "gc2.dc1.gnm" ]; then
    # proxy that Play uses
    export proxy_host=devproxy.gul3.gnl
    export proxy_port=3128
fi

if [ "$DOMAIN" = "gc2.dc1.gnm" ]; then
    PROXY_CONF=""
else
    PROXY_CONF="-Dhttp.proxyHost=devproxy.gul3.gnl -Dhttp.proxyPort=3128"
fi

#MaxPermSize specifies the the maximum size for the permanent generation heap,
# a heap that holds objects such as classes and methods. Xmx is the heap size.
java -Xmx4096M -XX:MaxPermSize=2048m \
  -XX:ReservedCodeCacheSize=128m \
  -Dsbt.boot.directory=$SBT_BOOT_DIR \
  -Dsbt.log.noformat=true \
  $PROXY_CONF \
  $BUILD_PARAMS \
  $IVY_PARAMS \
  $SBT_EXTRA_PARAMS \
  -jar `dirname $0`/dev/sbt-launch.jar "$@"