rootProject.name = "EclipseAPI"

val isCI = System.getenv("CI") != null

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("eclipseLibs") {
            from(files("gradle/eclipseLibs.versions.toml"))
        }
    }
}

include(":main:plugin-core")
include(":main:nms-core")
include("main:core-nms")
include("main:kotlin-core")