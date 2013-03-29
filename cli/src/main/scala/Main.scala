package da.cli

import java.io.File

import play.api.{ Play, Mode, Application }
import play.api.Play.current
import play.api.db.DB

object Main {

  Play.start(new Application(
    path = new File("."),
    classloader = getClass.getClassLoader,
    sources = None,
    mode = Mode.Dev))

  def main(args: Array[String]): Unit = sys exit {

    try {
      args.toList match {
        case "migrations" :: Nil ⇒ println("Running migrations")
        case _ ⇒ println("Usage: run command args")
      }
    } finally {
      Play.stop()
    }
    0
  }
}
