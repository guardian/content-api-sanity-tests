#!/bin/bash -ev

adduser --disabled-password content-api

cd /home/content-api

aws --region eu-west-1 s3 cp s3://content-api-dist/content-api-sanity-tests/${Stage}/sanity-tests/sanity-tests_1.0_all.deb /home/content-api/sanity-tests_1.0_all.deb
dpkg -i /home/content-api/sanity-tests_1.0_all.deb
aws --region eu-west-1 s3 cp s3://content-api-config/content-api-sanity-tests/${Stage}/sanity-tests/content-api-sanity-tests.conf /usr/share/sanity-tests/conf/content-api-sanity-tests.conf
ln -s /usr/share/sanity-tests /home/content-api/sanity-tests
echo JAVA_OPTS=\"-Dpidfile.path=/var/run/sanity-tests/sanity-tests.pid -Dconfig.file=/usr/share/sanity-tests/conf/content-api-sanity-tests.conf -Dplay.http.secret.key=kkMQbRpa1ttyM0oXwlyPwd5ODNeFMv63h452itOGzWulEUipJjMfm73\" >> /etc/default/sanity-tests
mkdir logs

systemctl start sanity-tests