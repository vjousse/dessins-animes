import sbt._
import Keys._
import play.Play.autoImport._

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
    "com.typesafe.play" %% "anorm" % "2.4.0",
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
  )


  private val buildSettings = Project.defaultSettings ++ Seq(
    organization := "com.dessins-animes",
    version := appVersion,
    resolvers := Seq(),
    libraryDependencies := Seq(
      jdbc,
      "com.typesafe.play" %% "anorm" % "2.4.0",
      postgres),
    libraryDependencies in test := Seq(specs2),
    shellPrompt := {
      (state: State) ⇒ "%s> ".format(Project.extract(state).currentProject.id)
    },
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature"),
    testOptions in Test += Tests.Argument("junitxml", "console")
  )
  val da = Project(appName, file("."))
    .enablePlugins(play.PlayScala)
    .settings(
    libraryDependencies ++= appDependencies,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
  )

  val cli = Project("cli", file("cli"), settings = buildSettings).settings(
    libraryDependencies ++= Seq(),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

  ) dependsOn (da)
}
