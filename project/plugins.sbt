addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.19")


addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")



//To display dependencies in project  https://stackoverflow.com/questions/25519926/how-to-see-dependency-tree-in-sbt
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.6.8")


// https://github.com/djspiewak/sbt-github-actions
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.13.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.9")