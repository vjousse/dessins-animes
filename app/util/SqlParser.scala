package da
package util

import anorm.{ SqlParser ⇒ AnormSqlParser, _ }

object SqlParser {

  import AnormSqlParser._

  def optStr(columnName: String): RowParser[Option[String]] = opt[String](columnName)

  def optInt(columnName: String): RowParser[Option[Int]] = opt[Int](columnName)

  def optLong(columnName: String): RowParser[Option[Long]] = opt[Long](columnName)

  def opt[T](columnName: String)(implicit extractor: Column[T]): RowParser[Option[T]] =
    get[Option[T]](columnName)
}
