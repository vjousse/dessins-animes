package da
package models

import anorm._
import anorm.SqlParser._
import java.sql.Connection
import scala.language.postfixOps

case class Da(
  id: Integer,
  name: String) {

  override def toString = name

  }

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

    SQL("SELECT d.id,n.nom FROM da AS d, noms_da AS n WHERE d.id = n.id_da ORDER BY nom ASC").as(mapping *)

  }
}
