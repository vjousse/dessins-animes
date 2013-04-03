package da
package util

import anorm.{ SqlParser ⇒ AnormSqlParser, _ }
import org.joda.time.DateTime
import java.util.Date

object SqlParser {

  import AnormSqlParser._

  // DateTime anorm mapper
  implicit def rowToDateTime: Column[DateTime] = Column.nonNull { (value, meta) ⇒
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case date: Date ⇒ Right(new DateTime(date))
      case _          ⇒ Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to DateTime for column " + qualified))
    }
  }

  def dateTime(columnName: String): RowParser[DateTime] = get[DateTime](columnName)

  def optDateTime(columnName: String): RowParser[Option[DateTime]] = opt[DateTime](columnName)

  def optStr(columnName: String): RowParser[Option[String]] = opt[String](columnName)

  def optInt(columnName: String): RowParser[Option[Int]] = opt[Int](columnName)

  def optLong(columnName: String): RowParser[Option[Long]] = opt[Long](columnName)

  def opt[T](columnName: String)(implicit extractor: Column[T]): RowParser[Option[T]] =
    get[Option[T]](columnName)
}
