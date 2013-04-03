package da
package models

import org.joda.time.DateTime

case class Text(id: Long,
  content: String,
  author: Option[String],
  email: Option[String],
  updatedAt: Option[DateTime])
