scalaVersion := "3.1.3"
name := "pirene"

Compile / scalaSource := baseDirectory.value / "src"
Test / scalaSource := baseDirectory.value / "test"

scalacOptions ++= Seq("-Xmax-inlines", "60")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.8.0",
  "org.typelevel" %% "cats-mtl" % "1.3.0",
  "io.higherkindness" %% "droste-core" % "0.9.0",
  "io.circe" %% "circe-core" % "0.14.2",
  "org.typelevel" %% "kittens" % "3.0.0-M4",
  "eu.timepit" %% "refined-cats" % "0.10.1",
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "com.lihaoyi" %% "pprint" % "0.7.0" % Test
)
