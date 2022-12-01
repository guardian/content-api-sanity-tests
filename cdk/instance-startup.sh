#!/bin/bash -ev

adduser --disabled-password content-api

cd /home/content-api

# shellcheck disable=SC2016
aws --region eu-west-1 s3 cp 's3://content-api-dist/content-api-sanity-tests/${Stage}/sanity-tests/sanity-tests_1.0_all.deb' /home/content-api/sanity-tests_1.0_all.deb
dpkg -i /home/content-api/sanity-tests_1.0_all.deb
systemctl stop sanity-tests #just in case it gets auto-started!

# shellcheck disable=SC2016
aws --region eu-west-1 s3 cp 's3://content-api-config/content-api-sanity-tests/${Stage}/sanity-tests/content-api-sanity-tests.conf' /usr/share/sanity-tests/conf/content-api-sanity-tests.conf
ln -s /usr/share/sanity-tests /home/content-api/sanity-tests
echo JAVA_OPTS=\"-Dpidfile.path=/var/run/sanity-tests/sanity-tests.pid -Dconfig.file=/usr/share/sanity-tests/conf/content-api-sanity-tests.conf -Dplay.http.secret.key=kkMQbRpa1ttyM0oXwlyPwd5ODNeFMv63h452itOGzWulEUipJjMfm73\" >> /etc/default/sanity-tests
# shellcheck disable=SC2016
sed 's/{logging-stream}/${LoggingStreamName}/' </usr/share/sanity-tests/conf/logback.xml > /usr/share/sanity-tests/conf/logback-updated.xml
mv /usr/share/sanity-tests/conf/logback-updated.xml /usr/share/sanity-tests/conf/logback.xml

systemctl start sanity-tests