Simple set of sanity test for things like the AMI version.

## Config
In the `conf` folder edit the `application.conf.sample` file with your own values and rename to `application.conf`.

## Note: Separation of Internal and External facing tests

The tests are split into two types. __Internal facing tests__ rely on endpoints that are only accessible from within the Guardian network . __External facing tests__  are tests using only internet accessible endpoints.

### Internal facing tests
Internal facing tests are run using the sbt `test` command.

#### Run all tests locally
``sbt clean test``

#### Running remotely
The tests can be run in TeamCity by creating a build which runs `sbt test`

#### Adding a test
Add it to the `test` source root with a filename ending in `Test`

-----------------

### External facing tests
External facing tests are run as a scheduled service started by the sbt `start` command.              

#### Run all tests locally
`sbt start` 

#### Running remotely
Deploy the `sanity-tests` project from Riff-Raff.
Once deployed the tests run on a regular schedule in EC2.

#### Adding a test
Add it to the `app` source root and to `Global.scala`

-----------

> __canary in a coal mine__ (plural __canaries in a coal mine__)

>> *(idiomatic)* Something whose sensitivity to adverse conditions makes it a useful early indicator of such conditions; something which warns of the coming of greater danger or trouble by a deterioration in its health or welfare.

