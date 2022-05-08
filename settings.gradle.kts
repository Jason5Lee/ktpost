rootProject.name = "ktpost"

rootDir.resolve("gradle.properties").copyTo(
  target = rootDir.resolve("buildSrc").resolve("gradle.properties"),
  overwrite = true,
)
