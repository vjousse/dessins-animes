package da
package models

case class Song (
  id: Long,
  from: Option[String],
  name: String,
  text: String)
