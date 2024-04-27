ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

val Http4sVersion = "0.23.26"
val CirceVersion = "0.14.6"
val CirceGenericExtrasVersion = "0.14.3"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.5.6"
val MunitCatsEffectVersion = "1.0.7"
val ScalaLoggingVersion = "3.9.5"
val PureConfigVersion = "0.17.6"

lazy val root = (project in file("."))
  .settings(
    name := "WeatherService",
    organization := "com.jackhenry",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.13",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-generic-extras" % CirceGenericExtrasVersion,
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "com.beachape"        %% "enumeratum"          % "1.7.3",
      "com.beachape"        %% "enumeratum-circe"    % "1.7.3",
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion % Runtime,
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.3" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )