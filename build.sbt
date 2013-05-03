organization := "novoda.tool"

name := "Morphine"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-netty-websockets" % "0.6.8",
  "org.scala-lang" % "scala-actors" % "2.10.1"
)
