resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")
resolvers += "witi83 at bintray" at "https://dl.bintray.com/witi83/maven"

name := "se-shop-backend"
version := "0.1"
scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http"   % "10.1.1",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.1" % Test,
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.2",
  "com.h2database" % "h2" % "1.4.185",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.mockito" % "mockito-core" % "2.18.3" % Test,
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.evolutiongaming" %% "scala-tools" % "2.1",
  "com.evolutiongaming" %% "play-json-tools" % "0.1.1",
  "com.evolutiongaming" %% "akka-http-play-json" % "0.1.10",
  "com.softwaremill.akka-http-session" %% "core" % "0.5.5",
  "com.softwaremill.akka-http-session" %% "jwt"  % "0.5.5",
  "ch.megard" %% "akka-http-cors" % "0.3.0"
)
