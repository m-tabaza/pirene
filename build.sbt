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

lazy val prelude = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("prelude"))
  .dependsOn(core)
  .settings(
    moduleName := "pirene-prelude",
    name := "Pirene Prelude",
    libraryDependencies ++= Seq()
  )

lazy val client = project
  .settings(
    moduleName := "pirene-client",
    name := "Pirene Client",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "me.shadaj" %%% "slinky-web" % "0.7.2",
      "me.shadaj" %%% "slinky-hot" % "0.7.2"
    ),
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2",
      "react-proxy" -> "1.1.8",
      "file-loader" -> "6.2.0",
      "style-loader" -> "2.0.0",
      "css-loader" -> "5.2.7",
      "html-webpack-plugin" -> "4.5.2",
      "copy-webpack-plugin" -> "6.4.1",
      "webpack-merge" -> "5.8.0"
    ),
    webpack / version := "4.44.2",
    startWebpackDevServer / version := "3.11.2",
    webpackResources := baseDirectory.value / "webpack" * "*",
    fastOptJS / webpackConfigFile := Some(
      baseDirectory.value / "webpack" / "webpack-fastopt.config.js"
    ),
    fullOptJS / webpackConfigFile := Some(
      baseDirectory.value / "webpack" / "webpack-opt.config.js"
    ),
    Test / webpackConfigFile := Some(
      baseDirectory.value / "webpack" / "webpack-core.config.js"
    ),
    fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot"),
    fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
    Test / requireJsDomEnv := true,
    addCommandAlias("dev", ";fastOptJS/startWebpackDevServer;~fastOptJS"),
    addCommandAlias("build", "fullOptJS/webpack")
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(core.js, prelude.js)
