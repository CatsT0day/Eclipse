import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm") version "2.0.0"
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.github.CatsT0day"
version = "1.03.12-SNAPSHOT-ENTERp-MCVER"

val paperVersion by lazy {
    val buildToolsMaven = file("maven")
    if (buildToolsMaven.exists()) {
        val paperApiDir = buildToolsMaven.resolve("io/papermc/paper/paper-api")
        if (paperApiDir.exists()) {
            val versions = paperApiDir.listFiles()?.map { it.name }?.sortedDescending()
            versions?.firstOrNull { it.contains("-SNAPSHOT") } ?: versions?.firstOrNull() ?: "1.21.1-R0.1-SNAPSHOT"
        } else {
            "1.21.1-R0.1-SNAPSHOT"
        }
    } else {
        "1.21.1-R0.1-SNAPSHOT"
    }
}

val gitCommit by lazy {
    try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "--short=7", "HEAD")
            standardOutput = stdout
        }
        stdout.toString().trim()
    } catch (e: Exception) {
        "unknown"
    }
}

val gitDepth by lazy {
    try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-list", "--count", "HEAD")
            standardOutput = stdout
        }
        stdout.toString().trim()
    } catch (e: Exception) {
        "0"
    }
}

val gitBranch by lazy {
    try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
            standardOutput = stdout
        }
        stdout.toString().trim()
    } catch (e: Exception) {
        "unknown"
    }
}

val fullVersion = version.toString()
    .replace("-SNAPSHOT", "-indev-prerelease+$gitDepth-$gitCommit").replace("-NORMAL", "-stable+$gitDepth-$gitCommit")
    .replace("-ENTERp", "-enterprise").replace("-FREE", "-regular")
    .replace("-MCVER", paperVersion.replace("-SNAPSHOT", ""))
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to fullVersion))
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

repositories {
    mavenCentral()
    maven("file://${projectDir}/maven")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://lucko.me")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation(project(":main:plugin-core"))
}


tasks.shadowJar {
    archiveBaseName.set("EclipseAPI")
    archiveClassifier.set("")
    archiveVersion.set(fullVersion)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(tasks.processResources)
    // relocate("net.kyori", "me.catst0day.Eclipse.libs.kyori")
    relocate("org.reflections", "me.catst0day.Eclipse.libs.reflections")
    relocate("org.javassist", "me.catst0day.Eclipse.libs.javassist")
}

tasks.register("cat") {
    dependsOn(tasks.named("shadowJar"))
    group = "build"
    description = "run server with cats"
    val noRun = project.hasProperty("noRun")

    doFirst {
        logger.lifecycle("=========================================")
        if (noRun) {
            logger.lifecycle(" cats refuse to run (--noRun active)...")
        } else {
            logger.lifecycle(" cats are doing their job...")
        }
        logger.lifecycle("=========================================")
    }

    doLast {
        logger.lifecycle("=========================================")
        if (noRun) {
            logger.lifecycle("  cats just slept through the build, but build is done.")
        } else {
            logger.lifecycle("  cats did the job cooler than expected, lol")
        }
        logger.lifecycle("=========================================")
    }
}

tasks.register("kitten") {
    dependsOn(tasks.named("shadowJar"))
    group = "build"
    description = "run server with cats"
    val noRun = project.hasProperty("noRun")

    doFirst {
        logger.lifecycle("=========================================")
        if (noRun) {
            logger.lifecycle(" cats refuse to run (--noRun active)...")
        } else {
            logger.lifecycle(" cats are doing their job...")
        }
        logger.lifecycle("=========================================")
    }

    doLast {
        logger.lifecycle("=========================================")
        if (noRun) {
            logger.lifecycle("  cats just slept through the build, but build is done.")
        } else {
            logger.lifecycle("  cats did the job cooler than expected, lol")
        }
        logger.lifecycle("=========================================")
    }
}

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("maven") {
            groupId = "com.github.CatsT0day"
            artifactId = "EclipseAPI"
            version = fullVersion

            artifact(tasks.shadowJar)
        }
    }
}

tasks.jar {
    enabled = false
}