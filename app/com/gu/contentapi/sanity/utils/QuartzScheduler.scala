package com.gu.contentapi.sanity.utils

import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.CronScheduleBuilder
import org.quartz.CronExpression
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler
import org.quartz.Job
import org.quartz.JobExecutionContext

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.Duration

object QuartzScheduler {

  private val scheduler = StdSchedulerFactory.getDefaultScheduler

  def start() {scheduler.start()}

  def stop() {
    scheduler.clear()
    scheduler.standby()
  }

  def schedule(name: String, g: => Unit): ScheduleHolder = {
    val wrap: JobExecutionContext => Unit = x => g
    scheduleWithContext(name, wrap)
  }

  def scheduleWithContext(name: String, f: JobExecutionContext => Unit): ScheduleHolder = {
    new ScheduleHolder(name, f, scheduler)
  }
}

class ScheduleHolder (name: String, f: JobExecutionContext => Unit, scheduler: Scheduler) {

  def at(cronPattern: String) {
    ScheduleHolder.add(name, f)
    val job = newJob(classOf[GenJob]).withIdentity(name).build()
    val trigger = newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(cronPattern))).build()
    scheduler.scheduleJob(job, trigger)
  }

  def every(interval: Duration) {
    ScheduleHolder.add(name, f)
    val job = newJob(classOf[GenJob]).withIdentity(name).build()
    val trigger = newTrigger().withSchedule(simpleSchedule().withIntervalInMilliseconds(interval.toMillis).repeatForever()).build()
    scheduler.scheduleJob(job, trigger)
  }

}

object ScheduleHolder {

  type JobFunc = JobExecutionContext => Unit

  private val jobs = new TrieMap[String, JobFunc]

  def add(name: String, job: JobFunc) {jobs.put(name, job)}
  def get(name: String):Option[JobFunc] = {jobs.get(name)}

}

class GenJob extends Job {
  def execute(ctx: JobExecutionContext): Unit = {
    val name = ctx.getJobDetail.getKey.getName
    ScheduleHolder.get(name).foreach(f => f(ctx))
  }
}
