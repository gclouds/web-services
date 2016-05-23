name := """SSP 2.0"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "be.objectify"  %% "deadbolt-java"     % "2.3.0-RC1",
  "org.mongodb" % "mongo-java-driver" % "3.1.1",
  "org.mongodb.morphia" % "morphia" % "0.108",
  "org.mongodb.morphia" % "morphia-logging-slf4j" % "0.108",
  "org.mongodb.morphia" % "morphia-validation" % "0.108",
  "org.apache.axis2" % "axis2-kernel" % "1.6.3",
  "org.apache.axis2" % "axis2" % "1.6.3",
  "org.apache.axis2" % "axis2-transport-local" % "1.6.3",
  "org.apache.axis2" % "axis2-transport-http" % "1.6.3",
  "net.sf.json-lib" % "json-lib" % "2.4"  classifier "jdk15",
  "com.google.code.gson" % "gson" % "2.5"
)


fork in run := true
