package da
package apis

import models.Da
import play.api.db._
import java.sql.Connection

object DaApi {

  def getAllDas()(implicit conn: Connection): List[Da] = Da.findAll()

}
