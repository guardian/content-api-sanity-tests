import com.gu.contentapi.sanity.Application
import com.gu.contentapi.sanity.support.CloudWatchReporter
import play.api.ApplicationLoader.Context
import play.api.{ BuiltInComponentsFromContext, NoHttpFiltersComponents }
import play.api.inject.DefaultApplicationLifecycle
import play.api.routing.Router
import controllers.AssetsComponents
import router.Routes

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with NoHttpFiltersComponents {

  val cloudWatchReporter = CloudWatchReporter(configuration)

  val app: Application = new Application(controllerComponents, new DefaultApplicationLifecycle(), cloudWatchReporter)

  val router: Router = new Routes()
}