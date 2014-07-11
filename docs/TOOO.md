Done
====
* Integrated Mariot's scheduler
<https://github.com/guardian/content-api-sanity-tests/blob/quartz-scheduler/src/main/scala/Main.scala>
* Moved a test into Main so it is accessible to the scheduler
* Written a custom Test Failed handler that will send a trigger to
PagerDuty

Todo
====
## Known work
* Implement healthcheck
* Make exception handling re-useable
* Group tests into appropriate schedule
* Implement retry logic to ensure alerts are genuine
* Deploy on server

## Outstanding questions
* Do we keep tests in both `main` and `test`
* Currently the test name is not exposed to the Test Failed Exception I believe this is possible but requires a bit of work

