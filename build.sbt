name := "test-http-client"

version := "1.0"

scalaVersion := "2.11.8"
//libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "2.4.9-RC2"
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT"
//libraryDependencies +=  "org.scalatest" %% "scalatest" % "2.2.1" % "test"


libraryDependencies ++= {
  val akkaV       = "2.4.3"
  val scalaTestV  = "2.2.6"
  val scalamockVersion = "3.2.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "org.scalamock"     %% "scalamock-scalatest-support" % scalamockVersion % "test"
  )
}


assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("com.google.**" -> "shadeio.@1").inAll
)

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}
