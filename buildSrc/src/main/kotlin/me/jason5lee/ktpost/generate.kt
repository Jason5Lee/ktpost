package me.jason5lee.ktpost

import java.io.File
import java.nio.charset.Charset

private val logger = mu.KotlinLogging.logger {}

fun generate() {
  File("src/main/kotlin/me/jason5lee/ktpost/generated.kt")
    .writer(Charset.forName("utf-8"))
    .use { writer ->
      writer.write("// DO NOT EDIT\n\n")
      writer.write("package me.jason5lee.ktpost\n\n")
      writer.write("fun routeAll(scope: kotlinx.coroutines.CoroutineScope, router: io.vertx.ext.web.Router, deps: me.jason5lee.ktpost.common.Dependencies) {\n")
      writer.write("  val logger: mu.KLogger = deps.getDependency()\n\n")
      val files = File("src/main/kotlin/me/jason5lee/ktpost").listFiles()
      if (files != null)
        for (file in files) {
          val name = file.name
          if (file.isDirectory && name != "common") {
            try {
              val apiArgsNumber = getArgumentsNumber(File(file, "api.kt"), "fun ${name}Api")
              val implementationArgsNumber = getArgumentsNumber(File(file, "Implementation.kt"), "class Implementation")

              writer.write("  me.jason5lee.ktpost.")
              writer.write(name)
              writer.write(".")
              writer.write(name)
              writer.write("Api(me.jason5lee.ktpost.")
              writer.write(name)
              writer.write(".Implementation(")
              for (i in 0 until implementationArgsNumber) {
                writer.write("deps.getDependency(), ")
              }
              writer.write(")")
              for (i in 1 until apiArgsNumber) {
                writer.write(", deps.getDependency()")
              }
              writer.write(").addRoute(router, scope, logger)\n")
            } catch (e: Exception) {
              logger.error { "error while generating route for workflow \"$name\": ${e.message}" }
            }
          }
        }

      writer.write("}\n")
    }
}

fun getArgumentsNumber(file: File, prefix: String): Int {
  val apiContent = file.readText()
  val start = apiContent.findAfter(prefix)
    .let { it ?: throw Exception("failed to find prefix \"$prefix\" in file \"$file\"") }
    .let { apiContent.findAfter("(", it) } ?: throw Exception("failed to find \"(\" after prefix")
  val end = apiContent.indexOf(")", start)
  if (end == -1) {
    throw Exception("failed to find \")\" after \"(\"")
  }
  val arguments = apiContent.substring(start, end)
  return arguments.count { it == ':' }
}

fun String.findAfter(string: String, startIndex: Int = 0): Int? =
  indexOf(string, startIndex).let {
    if (it < 0) {
      null
    } else {
      it + string.length
    }
  }
