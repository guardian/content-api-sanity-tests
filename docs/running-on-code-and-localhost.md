Problem Statement
=================
Currently it is difficult (though not impossible) to run the Sanity Tests against localhost or from localhost against CODE. Making this simpler would improve the chances of team members running these tests prior to putting code into production.

## Current steps to run against CODE
* Get a copy of the complete application.conf file
* Set the properties to CODE environments
* Run SBT using `sbt -d config.file
* Run `sbt test` to run internal facing (GC2) tests
* Run `sbt start` to run external facing (internet accessible) tests. You have to force quit the process once the tests have run one loop.


## Specific Problems

* To run against CODE you have to duplicate same property values between `content-api-sanity-tests` and `content-api-sanity-code`
* Tests are hardcoded to read properties from `content-api-sanity-tests` or `content-api-sanity-code` from the config file.
* Tests are split between the `test` and `app` source roots and do not have access to eachother
* You need to run two commands `sbt test` and `sbt start` to run the full suite of tests
* The external facing tests only run as a scheduled service and therefore need to be force killed rather than reporting their result after a single run.
* Some tests may not be runnable against localhost as they require large amount of dependencies such as R2-Admin or Flex integration.

## Ideal solution

* Seperate properties files for PROD, CODE and DEV.
* Configurable at runtime by a flag to run against PROD, CODE or DEV
* Ability to define and execute subsets of tests defined in a test suite (e.g. high priority tests) 
* Configurable at runtime by a flag to run tests as a scheduler service or a single run
* Tests are taggable to indicate if they are only runnable against PROD, CODE or DEV (e.g. we may not want to publish some test data on PROD so would rather have certain tests run against CODE or localhost only)
* All tests and helper functions are within a single source root