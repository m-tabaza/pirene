ThisBuild / scalaVersion := "3.3.0-RC4"
ThisBuild / scalacOptions ++= Seq("-Xmax-inlines", "60", "-Wunused:imports")
ThisBuild / tlBaseVersion := "2.9"

lazy val root = tlCrossRootProject.aggregate(core, stdlib)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    moduleName := "pirene-core",
    name := "Pirene Core",
    libraryDependencies ++= Seq(
      "io.higherkindness" %%% "droste-core" % "0.9.0",
      "io.circe" %%% "circe-core" % "0.14.5",
      "org.typelevel" %%% "kittens" % "3.0.0",
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
      "com.lihaoyi" %%% "pprint" % "0.7.0" % Test
    )
  )

lazy val stdlib = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("stdlib"))
  .dependsOn(core)
  .settings(
    moduleName := "pirene-stdlib",
    name := "Pirene standard library",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % "0.23.14"
    )
  )

lazy val client = project
  .settings(
    moduleName := "pirene-client",
    name := "Pirene UI",
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % "2.1.1"
    ),
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(core.js, stdlib.js)
