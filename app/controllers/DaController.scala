package controllers

import play.api._
import mvc._
import da.DaEnv

trait DaController extends Controller {
  protected val env = new DaEnv(Play.unsafeApplication.configuration)
}
