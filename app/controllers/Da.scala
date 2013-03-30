package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db._

import da.apis.DaApi

object Da extends Controller {

  def show(id: Long, dir: String) = Action {
    DB.withConnection { implicit conn =>
      DaApi.getDaById(id) match {
        case Some(da) => Ok(views.html.showDa(da))
        case None => NotFound
      }
    }
  }

}
