lazy val commonSettings = Seq(
  name := "scala-client",
  scalaVersion := "2.10.6",
  organization := "com.couchbase.client",
  version := "1.0.0-SNAPSHOT",
  crossScalaVersions := Seq("2.11.7", "2.10.6"),
  javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),
  scalacOptions += "-target:jvm-1.7"
)

lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.couchbase.client" % "core-io" % "1.2.1",
      "io.reactivex" %% "rxscala" % "0.25.0",
      "io.reactivex" % "rxjava-reactive-streams" % "1.0.1",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.3",

      "org.scalatest" %% "scalatest" % "2.2.5" % "test,it",
      "com.typesafe.akka" %% "akka-stream-experimental" % "2.0-M2" % "test,it",
      "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "2.0-M2" % "test,it"
    )
  )