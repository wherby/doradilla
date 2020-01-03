import Dependencies.commonSettings

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "document for doradilla",
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    paradoxIllegalLinkPath := raw".*\\.md".r,
    paradoxProperties in Compile ++=Map("project.description" -> "Description for doradilla library.",
      "github.base_url" -> s"https://github.com/wherby/doradilla/tree/v${version.value}")
  )


// Define task to  copy html files
val copyDocs = taskKey[Unit]("Copy html files from src/main/html to cross-version target directory")

// Implement task
copyDocs := {
  import Path._

  val src = baseDirectory.value  /"docs" /"target" / "paradox"/"site"/ "main"

  val dest = baseDirectory.value /"public" /"docs"
  IO.delete(dest)
  dest.mkdir()
  // Copy files to source files to target
  IO.copyDirectory(src,dest)
}

