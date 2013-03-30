package da
package models

case class Text(id: Long,
  content: String,
  author: Option[String],
  email: Option[String])
