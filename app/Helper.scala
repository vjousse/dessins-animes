package da

import java.util.Date
import java.util.Locale

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime

object Helper {

  def pluralize(s: String, n: Int) =
    "%d %s%s".format(n, s, if (n > 1) "s" else "")

  def show(date: Option[Date]): String = date.map { d ⇒ show(new DateTime(d)) }.getOrElse("-")

  def show(date: Date): String = show(new DateTime(date))

  def show(date: DateTime): String = DateTimeFormat.forPattern("dd MMMM yyy").withLocale(Locale.FRANCE).print(date)

  def showDateTime(date: DateTime): String = DateTimeFormat.forPattern("dd MMMM yyy h:m").withLocale(Locale.FRANCE).print(date)

  def showIfNotEmpty(prefix: String, value: Option[String]): String = {
   if(!value.isEmpty && value.get != "") {
    prefix + value.get
   } else {
    ""
   }
  }
}
