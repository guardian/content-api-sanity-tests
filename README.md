Simple set of sanity test for things like the AMI version.

## Config
In the `conf` folder edit the `application.conf.sample` file with your own values and rename to `application.conf`.

## Note: Separation of CODE and PROD tests

The tests are split into two types: __CODE tests__ and __PROD tests__.

### CODE tests

* rely on endpoints that are only accessible from within the Guardian network, thus cannot be run on an EC2 machine
* and/or insert dummy data into the system, making execution against PROD undesirable. 

CODE tests are run using the sbt `test` command.

#### Run all tests locally
`sbt clean test` will run all CODE tests, as well as all PROD tests that make sense to run against CODE. Any tests that only make sense on PROD will be excluded.

#### Running remotely
The tests are run in TeamCity by the [content-api-sanity-tests (CODE)](https://teamcity.gutools.co.uk/viewType.html?buildTypeId=bt1321) build.

#### Adding a test
Add it to the `test` source root.

-----------------

### PROD tests
* only rely on internet accessible endpoints, thus can be run on an EC2 machine
* and do not insert anything (apart from one special canary collection) into the system, thus can be safely executed against PROD. 

These tests are run as a scheduled service in the Play app running on an EC2 machine.

#### Running remotely
Deploy the `sanity-tests` project from Riff-Raff.
Once deployed the tests run on a regular schedule in EC2. The application will send PagerDuty alerts if tests fail.

#### Adding a test
Add it to the `app` source root and to the appropriate meta-suite in `MetaSuites.scala`

-----------

> __canary in a coal mine__ (plural __canaries in a coal mine__)

>> *(idiomatic)* Something whose sensitivity to adverse conditions makes it a useful early indicator of such conditions; something which warns of the coming of greater danger or trouble by a deterioration in its health or welfare.

