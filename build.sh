#!/usr/bin/env bash

SBT_OPTIONS="-Xmx1G \
    -XX:MaxPermSize=250m \
    -XX:+UseCompressedOops \
    -Dsbt.log.noformat=true \
    -Dbuild.number=$BUILD_NUMBER \
    -Dbuild.vcs.number=$BUILD_VCS_NUMBER"

[ -d target ] && rm -rf target
mkdir target
cd target

mkdir downloads
mkdir -p sanity-tests

# sanity-tests
if [ -z "$JDK_HOME" ]; then
    JAVA=java
else
    JAVA=$JDK_HOME/bin/java
fi

if cd .. && $JAVA $SBT_OPTIONS -jar sbt-launch.jar dist && cd target
then
    cp universal/sanity-tests-1.0.zip downloads/sanity-tests-1.0.zip
    cp ../sanity-tests.service downloads
else
    echo 'Failed to build Sanity Tests'
    exit 1
fi
cd downloads/ && tar -zcvf ../sanity-tests/sanity-tests.tar.gz * && cd ..
cp ../app/deploy/riff-raff.yaml .

[ -d downloads ] && rm -rf downloads

echo "##teamcity[publishArtifacts '$(pwd) => .']"
