import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    println("Starting application")
  }

  // called when a route is found, but it was not possible to bind the request parameters
  override def onBadRequest(request: RequestHeader, error: String) = {
    Future.successful(
      BadRequest("Bad Request: " + error)
    )
  }

  // 404 - page not found error
  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful(
      NotFound(views.html.errors.onHandlerNotFound(request))
    )
  }

}
