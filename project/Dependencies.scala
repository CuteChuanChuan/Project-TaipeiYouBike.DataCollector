import sbt.*

object Dependencies {

  lazy val akkaVersion          = "2.6.18"
  lazy val akkaHttpVersion      = "10.2.9"
  lazy val mongoVersion         = "5.3.1"
  lazy val scalaTestVersion     = "3.2.19"
  lazy val testContainerVersion = "1.20.5"

  lazy val akkaLibs: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  )

  lazy val mongoLib: Seq[ModuleID] = Seq("org.mongodb.scala" %% "mongo-scala-driver" % mongoVersion)

  lazy val commonLibs: Seq[ModuleID] = Seq(
    "io.spray" %% "spray-json" % "1.3.6",
    "com.typesafe" % "config" % "1.4.3",
    "ch.qos.logback" % "logback-classic" % "1.5.17"
  )

  lazy val testLibs: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.mockito" % "mockito-core" % "5.15.2" % Test
  )

  lazy val testContainerLibs: Seq[ModuleID] = Seq(
    "org.testcontainers" % "testcontainers" % testContainerVersion % Test,
    "org.testcontainers" % "mongodb" % testContainerVersion % Test
  )
}
