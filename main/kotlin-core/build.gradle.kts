plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.github.CatsT0day"
version = "1.03.11-SNAPSHOT-ENTERp-MCVER"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}