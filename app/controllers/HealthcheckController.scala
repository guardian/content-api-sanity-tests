package controllers

import play.api.mvc.{Action, Controller}

object HealthcheckController extends Controller {
  def report() = Action { request =>
    Ok("heartbeat")
  }
}
