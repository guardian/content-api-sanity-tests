import com.gu.contentapi.sanity.support.CloudWatchReporter
import play.api.ApplicationLoader.Context
import play.api.{ BuiltInComponentsFromContext, NoHttpFiltersComponents }
import play.api.inject.DefaultApplicationLifecycle
import play.api.libs.ws.ahc.AhcWSClient
import play.api.mvc.legacy.Controller
import play.api.routing.Router
import controllers.HealthcheckController
import router.Routes

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with NoHttpFiltersComponents {

  Controller.init(controllerComponents)

  implicit val _materializer = materializer
  val wsClient = AhcWSClient()

  val app = new App(new DefaultApplicationLifecycle(), CloudWatchReporter(configuration), wsClient)
  val router: Router = new Routes(httpErrorHandler, new HealthcheckController())
  
  app.start
}