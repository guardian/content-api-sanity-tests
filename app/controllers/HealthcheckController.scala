package controllers

import play.api.mvc.{Action, BaseController, ControllerComponents}

class HealthcheckController(override val controllerComponents: ControllerComponents) extends BaseController {
  def report = Action {
    Ok("heartbeat")
  }
}
