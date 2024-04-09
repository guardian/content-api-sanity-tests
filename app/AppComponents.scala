import org.apache.pekko.stream.ActorMaterializer
import com.gu.contentapi.sanity.support.CloudWatchReporter
import play.api.ApplicationLoader.Context
import play.api.{ BuiltInComponentsFromContext, NoHttpFiltersComponents }
import play.api.inject.DefaultApplicationLifecycle
import play.api.libs.ws.ahc.{AhcWSComponents, AhcWSClient}
import play.api.mvc.legacy.Controller
import play.api.routing.Router
import controllers.HealthcheckController
import router.Routes

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with NoHttpFiltersComponents { 
    
  Controller.init(controllerComponents)

  val app = new App(new DefaultApplicationLifecycle(), CloudWatchReporter(configuration), wsClient)
  val router: Router = new Routes(httpErrorHandler, new HealthcheckController())
  
  app.start
}