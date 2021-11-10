import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.21"
}

allprojects {
    group = "mb.ccbench"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        maven(url = "https://artifacts.metaborg.org/content/groups/public/")
        mavenCentral()
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
        resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
    }

//    java {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

configure(subprojects.filter { "ccbench.platform" !in it.name }) {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        // Platform
        implementation(platform(project(":ccbench.platform")))
        api(platform(project(":ccbench.platform")))
        kapt(platform(project(":ccbench.platform")))
        testImplementation(platform(project(":ccbench.platform")))
        testRuntimeOnly(platform(project(":ccbench.platform")))
        implementation(kotlin("stdlib"))

        // Spoofax
        implementation("org.metaborg:statix.codecompletion.pie")
        implementation("org.metaborg:statix.codecompletion")
        implementation("org.metaborg:jsglr.pie")
        implementation("org.metaborg.devenv:org.spoofax.terms")
        implementation("org.metaborg.devenv:nabl2.terms")
        implementation("org.metaborg.devenv:statix.solver")
        implementation(files("../libs/strategolib.jar"))
        implementation("org.metaborg:pie.api")
        implementation("org.metaborg:pie.runtime")
        implementation("org.metaborg:pie.dagger")
        implementation("org.metaborg:log.backend.slf4j")
        implementation("org.metaborg:resource")

        // Logging
        implementation("ch.qos.logback:logback-classic")
        implementation("ch.qos.logback:logback-core")
        implementation("io.github.microutils:kotlin-logging-jvm")

        // Dependency Injection
        implementation("com.google.dagger:dagger")
        implementation("com.google.dagger:dagger-compiler")
        kapt("com.google.dagger:dagger-compiler")

        // CLI
        implementation("me.tongfei:progressbar")
        implementation("com.github.ajalt.clikt:clikt")

        // CSV
        implementation("org.apache.commons:commons-csv")

        // YAML
        implementation("com.fasterxml.jackson.core:jackson-databind")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

        // Charts
        implementation("org.jfree:jfreechart")
        implementation("org.jfree:org.jfree.pdf")

        // Utils
        implementation("commons-io:commons-io")
        implementation("org.apache.commons:commons-math3")

        // Testing
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly   ("org.junit.jupiter:junit-jupiter-engine")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    kapt {
        correctErrorTypes = true
    }
}