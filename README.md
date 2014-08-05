Simple set of sanity test for things like the AMI version.

## Config
In the `conf` folder edit the `application.conf.sample` file with your own values and rename to `application.conf`.

## Running

## Run as a scheduled service
``sbt start``

### Run all tests
``sbt clean test``

### Run tests with a specific tag
``sbt "test-only -- -n InfrequentTest"``



> __canary in a coal mine__ (plural __canaries in a coal mine__)

>> *(idiomatic)* Something whose sensitivity to adverse conditions makes it a useful early indicator of such conditions; something which warns of the coming of greater danger or trouble by a deterioration in its health or welfare.

