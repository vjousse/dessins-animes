package da
package models

import anorm._
import anorm.SqlParser._
import java.sql.Connection
import scala.language.postfixOps
import org.joda.time.DateTime

import da.util.SqlParser.{optStr, optLong, optDateTime}

case class Da(
  id: Long,
  dirName: String,
  name: String,
  otherNames: List[String] = Nil,
  summary: Option[Text] = None,
  comment: Option[Text] = None,
  images: List[Image] = Nil,
  guide: Option[Guide] = None
  ) {

  override def toString = name

  }

object Da {

  case class SingleRow(
      id: Long,
      directory: String,
      name: String,
      textId: Option[Long],
      text: Option[String],
      textType: Option[String],
      textAuthor: Option[String],
      textMail: Option[String],
      textUpdatedAt: Option[DateTime],
      imageId: Option[Long],
      imageThumbName: Option[String],
      imageName: Option[String],
      guideId: Option[Long],
      guideFrom: Option[String],
      guideList: Option[String])

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

    val parsing = int("id") ~
      str("repertoire") ~
      str("nom") ~
      optLong("texte_id") ~
      optStr("texte") ~
      optStr("type") ~
      optStr("auteur") ~
      optStr("mail") ~
      optDateTime("textes_da.updated_at") ~
      optLong("image_id") ~
      optStr("nomthumb") ~
      optStr("nombig") ~
      optLong("guide_id") ~
      optStr("guide_from") ~
      optStr("guide_list") map {
        case id ~ directory ~ name ~ text_id ~ text ~ text_type ~ text_author ~ text_mail ~ text_updated_at ~ image_id ~ image_thumb_name ~ image_name ~ guide_id ~ guide_from ~ guide_list =>
          SingleRow(
            id,
            directory,
            name,
            text_id,
            text,
            text_type,
            text_author,
            text_mail,
            text_updated_at,
            image_id,
            image_thumb_name,
            image_name,
            guide_id,
            guide_from,
            guide_list
          )
      }

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
        textes_da.updated_at,
        images_da.id as image_id,
        images_da.nomthumb,
        images_da.nombig,
        guide.id_guide as guide_id,
        guide.provenance as guide_from,
        guide.liste as guide_list
      FROM
        da AS d
      LEFT JOIN
        textes_da ON d.id = textes_da.id_da
      LEFT JOIN
        noms_da ON d.id = noms_da.id_da
      LEFT JOIN
        images_da ON d.id = images_da.id_da
      LEFT JOIN
        guide ON d.id = guide.id_da
      WHERE
        d.id = {id}
      ORDER BY
        noms_da.defaut DESC
      """)

      .on("id" -> id)
      .as(parsing *)

    results.headOption.map { r =>
      Da(
        r.id,
        r.directory,
        r.name,
        results.filterNot(_.name == r.name).map(_.name),
        results.find(_.textType == Some("resume")).flatMap( f =>
            for {
              id <- f.textId
              text <- f.text
            } yield Text(id, text, f.textAuthor, f.textMail, f.textUpdatedAt)
          ),
        results.find(_.textType == Some("commentaire")).flatMap( f =>
            for {
              id <- f.textId
              text <- f.text
            } yield Text(id, text, f.textAuthor, f.textMail, f.textUpdatedAt)
          ),

        results.filterNot(_.imageId.isEmpty).flatMap( r =>
            for {
              id <- r.imageId
              thumb <- r.imageThumbName
              big <- r.imageName
            } yield Image(id, big, thumb)
          ).distinct,

        results.find(!_.guideId.isEmpty).flatMap( r =>
            for {
              id <- r.guideId
              list <- r.guideList
            } yield Guide(id, r.guideFrom, list)
          )
      )
    }
  }
}
