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
  guide: Option[Guide] = None,
  songs: List[Song] = Nil,
  links: List[Link] = Nil
  ) {

  override def toString = name

  }

object Da {

  case class DaRow(
      id: Long,
      directory: String,
      name: String)

  case class TextRow(
      textId: Option[Long],
      text: Option[String],
      textType: Option[String],
      textAuthor: Option[String],
      textMail: Option[String],
      textUpdatedAt: Option[DateTime])

  case class LinkRow(
      id: Option[Long],
      url: Option[String],
      name: Option[String],
      language: Option[String])

  case class SongRow(
      songId: Option[Long],
      songFrom: Option[String],
      songName: Option[String],
      songText: Option[String])

  case class ImageRow(
      imageId: Option[Long],
      imageThumbName: Option[String],
      imageName: Option[String])

  case class GuideRow(
      guideId: Option[Long],
      guideFrom: Option[String],
      guideList: Option[String])

  case class SingleRow(
      daRow: DaRow,
      textRow: TextRow,
      imageRow: ImageRow,
      guideRow: GuideRow,
      songRow: SongRow,
      linkRow: LinkRow)


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
      optStr("guide_list") ~
      optLong("song_id") ~
      optStr("song_from") ~
      optStr("song_name") ~
      optStr("song_text") ~
      optLong("link_id") ~
      optStr("link_url") ~
      optStr("link_name") ~
      optStr("link_language") map {

        case id ~ directory ~ name ~ text_id ~ text ~ text_type ~ text_author ~ text_mail ~ text_updated_at ~ image_id ~ image_thumb_name ~ image_name ~ guide_id ~ guide_from ~ guide_list ~ song_id ~ song_from ~ song_name ~ song_text ~ link_id ~ link_url ~ link_name ~ link_language =>
          SingleRow(
            DaRow(id,
              directory,
              name),
            TextRow(text_id,
              text,
              text_type,
              text_author,
              text_mail,
              text_updated_at),
            ImageRow(image_id,
              image_thumb_name,
              image_name),
            GuideRow(guide_id,
              guide_from,
              guide_list),
            SongRow(song_id,
              song_from,
              song_name,
              song_text),
            LinkRow(
              link_id,
              link_url,
              link_name,
              link_language)
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
        guide.liste as guide_list,
        parole.id_parole as song_id,
        parole.provenance as song_from,
        parole.nom as song_name,
        parole.parole as song_text,
        liens.id_liens as link_id,
        liens.lien as link_url,
        liens.nom_site as link_name,
        liens.langage as link_language
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
      LEFT JOIN
        parole ON d.id = parole.id_da
      LEFT JOIN
        liens ON d.id = liens.id_da
      WHERE
        d.id = {id}
      ORDER BY
        noms_da.defaut DESC
      """)

      .on("id" -> id)
      .as(parsing *)

    results.headOption.map { r =>
      Da(
        r.daRow.id,
        r.daRow.directory,
        r.daRow.name,
        results.filterNot(_.daRow.name == r.daRow.name).map(_.daRow.name),
        results.find(_.textRow.textType == Some("resume")).flatMap( f =>
            for {
              id <- f.textRow.textId
              text <- f.textRow.text
            } yield Text(id, text, f.textRow.textAuthor, f.textRow.textMail, f.textRow.textUpdatedAt)
          ),
        results.find(_.textRow.textType == Some("commentaire")).flatMap( f =>
            for {
              id <- f.textRow.textId
              text <- f.textRow.text
            } yield Text(id, text, f.textRow.textAuthor, f.textRow.textMail, f.textRow.textUpdatedAt)
          ),

        results.filterNot(_.imageRow.imageId.isEmpty).flatMap( r =>
            for {
              id <- r.imageRow.imageId
              thumb <- r.imageRow.imageThumbName
              big <- r.imageRow.imageName
            } yield Image(id, big, thumb)
          ).distinct,

        results.find(!_.guideRow.guideId.isEmpty).flatMap( r =>
            for {
              id <- r.guideRow.guideId
              list <- r.guideRow.guideList
            } yield Guide(id, r.guideRow.guideFrom, list)
          ),

        results.filterNot(_.songRow.songId.isEmpty).flatMap( r =>
            for {
              id <- r.songRow.songId
              name <- r.songRow.songName
              text <- r.songRow.songText
            } yield Song(id, r.songRow.songFrom, name, text)
          ).distinct,

        results.filterNot(_.linkRow.id.isEmpty).flatMap( r =>
            for {
              id <- r.linkRow.id
              url <- r.linkRow.url
              name <- r.linkRow.name
              language <- r.linkRow.language
            } yield Link(id, url, name, language)
          ).distinct
      )
    }
  }
}
