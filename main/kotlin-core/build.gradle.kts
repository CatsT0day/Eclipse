plugins {
    kotlin("jvm") version "2.0.0"
}

group = "me.catst0day.Eclipse"
version = "1.02.96-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://lucko.me")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(project(":main:plugin-core"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
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
    compileOnly("me.clip:placeholderapi:2.11.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Javadoc> {
    enabled = false
}