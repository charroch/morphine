name := "android"

organization := "novoda.morphine"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.0.3",
  "org.jdom" % "jdom" % "2.0.2",
  "javassist" % "javassist" % "3.12.1.GA",
  "com.google.android" % "android" % "4.1.1.4",
  "com.typesafe" % "config" % "1.0.0",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "com.google.android" % "support-v4" % "r7",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)
