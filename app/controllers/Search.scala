package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db._

import da.apis.DaApi

object Search extends DaController {

  def search = Action {
    DB.withConnection { implicit conn =>
      //val (searchValue) = env.searchForm.bindFromRequest.get
      val das = DaApi.getAllDas()
      Ok(views.html.index(das))
    }
  }

}
