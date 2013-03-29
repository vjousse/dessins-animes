package da
package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import java.sql.Connection
import scala.language.postfixOps

case class Da(
  id: Integer,
  name: String)

object Da {

  def findAll()(implicit conn: Connection): List[Da] = {

    val parsing = int("id") ~
      str("nom")

    val mapping = parsing map {
      case id ~ name ⇒
        new Da(
          id,
          name)
    }

    SQL("SELECT * FROM da").as(mapping *)

  }
}
