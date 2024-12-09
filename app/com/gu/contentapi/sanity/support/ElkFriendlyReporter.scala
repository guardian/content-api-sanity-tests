package com.gu.contentapi.sanity.support
import org.scalatest.Reporter
import org.scalatest.events.{AlertProvided, Event, LineInFile, Location, TestFailed, TestIgnored, TestStarting, TestSucceeded, TopOfClass, TopOfMethod}
import org.slf4j.{LoggerFactory, MDC}

import scala.util.Try

/**
 * This is a custom Reporter class for Scalatest which aims to give nicely parseable logs in central ELK
 */
class ElkFriendlyReporter extends Reporter {
  private val logger = LoggerFactory.getLogger(getClass)

  //Saves a copy of the logger context, calls the block safely and resets the context afterward
  private def withIsolatedContext(f: =>Unit) = {
    val backup = MDC.getCopyOfContextMap
    try {
      f
    } finally  {
      MDC.setContextMap(backup)
    }
  }

  private def renderLocationMaybe(loc: Option[Location]) = loc match {
    case Some(LineInFile(lineNumber, fileName, filePathname))=>
      s"$fileName line $lineNumber"
    case Some(TopOfClass(className))=>
      s"Top of $className"
    case Some(TopOfMethod(className, methodId))=>
      s"Top of ${className}.$methodId"
    case None=>
      "(not provided)"
  }

  override def apply(event: Event): Unit = withIsolatedContext {
    event match {
      case AlertProvided(ordinal, message, nameInfo, throwable, formatter, location, payload, threadName, timeStamp) =>
        MDC.put("threadName", threadName)
        nameInfo.foreach(names=>{
          MDC.put("suite", names.suiteName)
          MDC.put("testName", names.testName.getOrElse("(not provided)"))
        })
        MDC.put("location", renderLocationMaybe(location))
        logger.error(message, throwable)
      case TestSucceeded(ordinal, suiteName, suiteId, suiteClassName, testName, testText, recordedEvents, duration, formatter, location, rerunner, payload, threadName, timeStamp)=>
        MDC.put("threadName", threadName)
        MDC.put("suiteName", suiteName)
        MDC.put("status", "success")
        MDC.put("logger_name", suiteClassName.getOrElse("(not provided)"))
        MDC.put("testName", testName)
        MDC.put("testText", testText)
        MDC.put("location", renderLocationMaybe(location))
        logger.info(s"'$testName' succeeded in ${duration.map(_.toString).getOrElse("(not provided)")}ms")
      case TestFailed(ordinal, message, suiteName, suiteId, suiteClassName, testName, testText, recordedEvents, analysis, throwable, duration, formatter, location, rerunner, payload, threadName, timeStamp)=>
        MDC.put("threadName", threadName)
        MDC.put("suiteName", suiteName)
        MDC.put("status", "Failed")
        MDC.put("logger_name", suiteClassName.getOrElse("(not provided)"))
        MDC.put("testName", testName)
        MDC.put("testText", testText)
        MDC.put("failureMessage", message)
        MDC.put("exceptionMsg", throwable.flatMap(t=>Option(t.getMessage)).getOrElse("(not present)"))
        MDC.put("location", renderLocationMaybe(location))
        analysis.foreach[Unit](a=>{
          logger.info(a)
        })
        logger.warn(s"'$testName' failed in ${duration.map(_.toString).getOrElse("(not provided)")}ms: $message")
      case TestIgnored(ordinal, suiteName, suiteId, suiteClassName, testName, testText, formatter, location, payload, threadName, timeStamp)=>
        MDC.put("threadName", threadName)
        MDC.put("suiteName", suiteName)
        MDC.put("status", "ignored")
        MDC.put("logger_name", suiteClassName.getOrElse("(not provided)"))
        MDC.put("testName", testName)
        MDC.put("testText", testText)
        MDC.put("location", renderLocationMaybe(location))
        logger.info(s"'$testName' was ignored")
      case TestStarting(ordinal, suiteName, suiteId, suiteClassName, testName, testText, formatter, location, rerunner, payload, threadName, timeStamp)=>
        MDC.put("threadName", threadName)
        MDC.put("suiteName", suiteName)
        MDC.put("logger_name", suiteClassName.getOrElse("(not provided)"))
        MDC.put("testName", testName)
        MDC.put("testText", testText)
        MDC.put("location", renderLocationMaybe(location))
        logger.debug(s"'$testName' starting")
      case _=>
        //there are loads of other events we don't need to concern ourselves with right now
    }
  }
}