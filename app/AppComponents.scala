import com.gu.contentapi.sanity.Application
import com.gu.contentapi.sanity.support.CloudWatchReporter
import play.api.ApplicationLoader.Context
import play.api.{ BuiltInComponentsFromContext, NoHttpFiltersComponents }
import play.api.inject.DefaultApplicationLifecycle
import play.api.mvc.legacy.Controller
import play.api.routing.Router
import controllers.HealthcheckController
import router.Routes

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with NoHttpFiltersComponents {

  Controller.init(controllerComponents)

  val cloudWatchReporter = CloudWatchReporter(configuration)

  val app: Application = new Application(new DefaultApplicationLifecycle(), cloudWatchReporter)
  val ht: HealthcheckController = new HealthcheckController()

  val router: Router = new Routes(httpErrorHandler, ht)
}