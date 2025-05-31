import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "data-collector",
    libraryDependencies ++= akkaLibs ++ mongoLib ++ commonLibs ++ testLibs ++ testContainerLibs
  )

assembly / assemblyMergeStrategy := {
  case "module-info.class"                                    => MergeStrategy.discard
  case "META-INF/native-image/reflect-config.json"            => MergeStrategy.first
  case "META-INF/native-image/native-image.properties"        => MergeStrategy.first
  // Keep SLF4J service providers
  case "META-INF/services/org.slf4j.spi.SLF4JServiceProvider" => MergeStrategy.concat
  case x if x.endsWith(".properties")                         => MergeStrategy.concat
  case x if x.contains("slf4j") || x.contains("logback")      => MergeStrategy.first
  case PathList("META-INF", xs @ _*)
      if xs.nonEmpty &&
        (xs.last.endsWith(".DSA") || xs.last.endsWith(".SF") || xs.last.endsWith(".RSA")) =>
    MergeStrategy.discard
  case PathList("META-INF", xs @ _*)                          => MergeStrategy.discard
  case x                                                      =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

//Docker / packageName := "bike-data-collector"
//Docker / version := "latest"

//dockerBaseImage := "openjdk:17-jdk-slim"
//dockerUpdateLatest := true

assembly / mainClass := Some("app.Main")
