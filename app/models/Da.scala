package da
package models

import anorm._
import anorm.SqlParser._
import java.sql.Connection
import scala.language.postfixOps

import da.util.SqlParser.optStr

case class Da(
  id: Long,
  name: String,
  text: Option[String] = None) {

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


  def findOneById(id: Long)(implicit conn: Connection): Option[Da] = {

    val parsing = int("id") ~
      str("nom") ~
      optStr("texte")

    val mapping = parsing map {
      case id ~ name ~ text ⇒
        new Da(
          id,
          name,
          text)
    }

    SQL("""
      SELECT
        d.id,
        noms_da.nom,
        textes_da.texte
      FROM
        da AS d
      LEFT JOIN
        textes_da ON d.id = textes_da.id_da
      LEFT JOIN
        noms_da ON d.id = noms_da.id_da
      WHERE
        d.id = {id}
      """)

      .on("id" -> id)
      .as(mapping singleOpt)

  }
}
