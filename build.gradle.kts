import java.io.ByteArrayOutputStream

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.github.CatsT0day"
version = "1.02.95-SNAPSHOT-ENTERp"

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
    .replace("-SNAPSHOT", "-indev+$gitDepth-$gitCommit").replace("-NORMAL", "-stable+$gitDepth-$gitCommit")
    .replace("-ENTERp", "-enterprise").replace("-FREE", "-regular")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to fullVersion))
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://lucko.me")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")
    implementation("org.reflections:reflections:0.10.2") {
        exclude(group = "org.javassist", module = "javassist")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    implementation("org.javassist:javassist:3.30.2-GA")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}


tasks.shadowJar {
    archiveBaseName.set("EclipseAPI")
    archiveClassifier.set("")
    archiveVersion.set(fullVersion)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(tasks.processResources)
    relocate("net.kyori", "me.catst0day.Eclipse.libs.kyori")
    relocate("org.reflections", "me.catst0day.Eclipse.libs.reflections")
    relocate("org.javassist", "me.catst0day.Eclipse.libs.javassist")
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