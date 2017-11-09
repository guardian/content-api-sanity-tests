package controllers

import play.api.mvc.Action
import play.api.mvc.legacy.Controller

class HealthcheckController extends Controller {

  def report = Action {
    Ok("heartbeat")
  }
}
