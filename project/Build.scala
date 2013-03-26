import sbt._
import Keys._
import play.Project._

trait Resolvers {
  val codahale = "repo.codahale.com" at "http://repo.codahale.com/"
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
}

trait Dependencies {
  val specs2 = "org.specs2" %% "specs2" % "1.12"
  val postgres = "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
}

object ApplicationBuild extends Build with Resolvers with Dependencies {

  val appName         = "dessins-animes"
  val appVersion      = "1.0-SNAPSHOT"



  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
  )


  private val buildSettings = Project.defaultSettings ++ Seq(
    organization := "com.dessins-animes",
    version := appVersion,
    resolvers := Seq(),
    libraryDependencies := Seq(
      jdbc,
      anorm,
      postgres),
    libraryDependencies in test := Seq(specs2),
    shellPrompt := {
      (state: State) ⇒ "%s> ".format(Project.extract(state).currentProject.id)
    },
    scalacOptions := Seq("-deprecation", "-unchecked"),
    testOptions in Test += Tests.Argument("junitxml", "console")
  )
  val da = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

  val cli = Project("cli", file("cli"), settings = buildSettings).settings(
    libraryDependencies ++= Seq()
  ) dependsOn (da)
}
