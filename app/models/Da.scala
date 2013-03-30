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
  otherNames: List[String] = Nil,
  summary: Option[String] = None,
  comment: Option[String] = None) {

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

    SQL("SELECT d.id,n.nom FROM da AS d, noms_da AS n WHERE d.id = n.id_da AND n.defaut = 1 ORDER BY nom ASC").as(mapping *)

  }


  def findOneById(id: Long)(implicit conn: Connection): Option[Da] = {

    val parsing = int("id") ~
      str("nom") ~
      optStr("texte") ~
      optStr("type")

    val results = SQL("""
      SELECT
        d.id,
        noms_da.nom,
        textes_da.texte,
        textes_da.type
      FROM
        da AS d
      LEFT JOIN
        textes_da ON d.id = textes_da.id_da
      LEFT JOIN
        noms_da ON d.id = noms_da.id_da
      WHERE
        d.id = {id}
      ORDER BY
        noms_da.defaut DESC
      """)

      .on("id" -> id)
      .as(parsing map(flatten) *)

    results.headOption.map { f =>
      Da(
        f._1,
        f._2,
        results.filterNot(_._2 == f._2).map(_._2),
        results.find(_._4 == Some("resume")).flatMap(_._3),
        results.find(_._4 == Some("commentaire")).flatMap(_._3))
    }
  }
}
