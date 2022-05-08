plugins {
  kotlin("jvm") version "1.6.10"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")
}
