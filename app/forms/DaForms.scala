package da
package forms

import play.api.data._
import play.api.data.Forms._

object DaForms {

  val searchForm = Form(play.api.data.Forms.tuple(
      "search" -> text
    ))

}
