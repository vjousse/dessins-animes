package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db._

import da.apis.DaApi

object Application extends Controller {

  def index = Action {
    DB.withConnection { implicit conn =>
      val das = DaApi.getAllDas()
      Ok(views.html.index(das))
    }
  }

}
