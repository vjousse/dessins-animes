package da
package models

import anorm._
import anorm.SqlParser._
import java.sql.Connection
import scala.language.postfixOps

import da.util.SqlParser.{optStr, optLong}

case class Da(
  id: Long,
  dirName: String,
  name: String,
  otherNames: List[String] = Nil,
  summary: Option[Text] = None,
  comment: Option[Text] = None,
  images: List[Image] = Nil) {

  override def toString = name

  }

object Da {

  def findAll()(implicit conn: Connection): List[Da] = {

    val parsing = int("id") ~
      str("repertoire") ~
      str("nom")

    val mapping = parsing map {
      case id ~ dirName ~ name ⇒
        new Da(
          id,
          dirName,
          name)
    }

    SQL("SELECT d.id, d.repertoire, n.nom FROM da AS d, noms_da AS n WHERE d.id = n.id_da AND n.defaut = 1 ORDER BY nom ASC").as(mapping *)

  }


  def findOneById(id: Long)(implicit conn: Connection): Option[Da] = {

    val parsing = int("id") ~ // 1
      str("repertoire") ~     // 2
      str("nom") ~            // 3
      optLong("texte_id") ~   // 4
      optStr("texte") ~       // 5
      optStr("type") ~        // 6
      optStr("auteur") ~      // 7
      optStr("mail") ~        // 8
      optLong("image_id") ~   // 9
      optStr("nomthumb") ~    // 10
      optStr("nombig")        // 11

    val results = SQL("""
      SELECT
        d.id,
        d.repertoire,
        noms_da.nom,
        textes_da.id as texte_id,
        textes_da.texte,
        textes_da.type,
        textes_da.auteur,
        textes_da.mail,
        images_da.id as image_id,
        images_da.nomthumb,
        images_da.nombig
      FROM
        da AS d
      LEFT JOIN
        textes_da ON d.id = textes_da.id_da
      LEFT JOIN
        noms_da ON d.id = noms_da.id_da
      LEFT JOIN
        images_da ON d.id = images_da.id_da
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
        f._3,
        results.filterNot(_._3 == f._3).map(_._3),
        results.find(_._6 == Some("resume")).flatMap( r =>
            for {
              id <- r._4
              text <- r._5
            } yield Text(id, text, r._7, r._8)
          ),

        results.find(_._6 == Some("commentaire")).flatMap( r =>
            for {
              id <- r._4
              text <- r._5
            } yield Text(id, text, r._7, r._8)
          ),
        results.filterNot(_._9.isEmpty).flatMap( r =>
            for {
              id <- r._9
              thumb <- r._10
              big <- r._11
            } yield Image(id, big, thumb)
          )
      )
    }
  }
}
