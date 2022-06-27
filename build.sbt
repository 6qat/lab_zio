ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.2" //"2.13.8" //

// bloop run root -w

scalacOptions := Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8"
  // "-Xsource:3"
  // "-language:postfixOps",
  // "-language:strictEquality"
)

libraryDependencies ++= {
  val akkaV = "2.6.19"
  val zioV = "2.0.0"
  val zioConfigV = "3.0.0"
  Seq(
    "org.slf4j" % "slf4j-simple" % "1.7.36",
    "dev.zio" %% "zio" % zioV,
    "dev.zio" %% "zio-config" % zioConfigV,
    "dev.zio" %% "zio-config-typesafe" % zioConfigV,
    "dev.zio" %% "zio-config-magnolia" % zioConfigV
  ) ++ (Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaV
  ).map(_.cross(CrossVersion.for3Use2_13)))

}.map(_.withSources().withJavadoc())

lazy val root = (project in file("."))
  .settings(
    name := "lab_zio"
  )
