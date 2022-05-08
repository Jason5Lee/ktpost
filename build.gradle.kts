import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import me.jason5lee.ktpost.Versions

plugins {
  kotlin ("jvm") version me.jason5lee.ktpost.Versions.kotlin
  application
  id("com.github.johnrengelman.shadow") version me.jason5lee.ktpost.Versions.shadow
}

group = "me.jason5lee"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = Versions.vertx
val junitJupiterVersion = Versions.junitJupiter

val mainVerticleName = "me.jason5lee.ktpost.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-auth-jwt")
  implementation("io.vertx:vertx-config")

  implementation(kotlin("stdlib-jdk8"))

  implementation("me.jason5lee:resukt-jvm:1.0.0")
  implementation("com.relops:snowflake:1.1")
  implementation("at.favre.lib:bcrypt:0.9.0")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.+")

  implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")
  implementation("org.slf4j:slf4j-simple:1.7.29")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

tasks.register<Task>("generate") {
  group = "source-generation"
  description = "Generate function that routes the endpoint of all workflows"

  doLast {
    me.jason5lee.ktpost.generate()
  }
}

tasks.withType<KotlinCompile> {
  // This cause infinite redeploy
  //  dependsOn("generate")
  kotlinOptions.jvmTarget = "11"
}
tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
