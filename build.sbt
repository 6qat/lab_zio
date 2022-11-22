ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"//"2.13.8" //"3.1.2" //"2.13.8" //

// bloop run root -w

scalacOptions := Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8",
  "-Xsource:3"
  // "-language:postfixOps",
  // "-language:strictEquality"
)

libraryDependencies ++= {
  val akkaV = "2.7.0"
  val akkaHttpV = "10.4.0"

  val zioV = "2.0.4"
  val zioConfigV = "3.0.2"
  val zioHttpV = "2.0.0-RC9"

  Seq(
    "org.slf4j" % "slf4j-simple" % "2.0.4",
    "dev.zio" %% "zio" % zioV,
    "dev.zio" %% "zio-config" % zioConfigV,
    "dev.zio" %% "zio-config-typesafe" % zioConfigV,
    "dev.zio" %% "zio-config-magnolia" % zioConfigV,
    "io.d11" %% "zhttp" % zioHttpV,
    "io.d11" %% "zhttp-test" % zioHttpV % Test,

  ) ++ Seq(

    "com.typesafe.akka" %% "akka-actor-typed" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV
  ).map(_.cross(CrossVersion.for3Use2_13))

}.map(_.withSources().withJavadoc())

lazy val root = (project in file("."))
  .settings(
    name := "lab_zio"
  )
