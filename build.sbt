ThisBuild / scalaVersion := "3.1.3"
ThisBuild / scalacOptions ++= Seq("-Xmax-inlines", "60")
ThisBuild / tlBaseVersion := "2.9"

lazy val root = tlCrossRootProject.aggregate(core, client)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    moduleName := "pirene-core",
    name := "Pirene Core",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % "2.8.0",
      "io.higherkindness" %%% "droste-core" % "0.9.0",
      "io.circe" %%% "circe-core" % "0.14.2",
      "org.typelevel" %%% "kittens" % "3.0.0-M4",
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
      "com.lihaoyi" %%% "pprint" % "0.7.0" % Test
    )
  )

// Example React setup: https://github.com/shashkovdanil/scalajs-react-boilerplate
lazy val client = project
  .settings(
    moduleName := "pirene-client",
    name := "Pirene Client",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % "2.1.1",
      "com.github.japgolly.scalacss" %%% "core" % "1.0.0",
      "com.github.japgolly.scalacss" %%% "ext-react" % "1.0.0"
    ),
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(core.js)
