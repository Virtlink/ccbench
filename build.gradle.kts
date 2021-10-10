plugins {
    java
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.21"
}

//val metaborgCoreVersion = "cc2109-SNAPSHOT"
//val metaborgSpoofaxTermsVersion = "spoofax3-SNAPSHOT"
//val metaborgLogVersion = "develop-SNAPSHOT"
//val metaborgPieVersion = "develop-SNAPSHOT"
//val metaborgResourceVersion = "develop-SNAPSHOT"
//val metaborgNabl2TermsVersion = "spoofax3-SNAPSHOT"
//val metaborgStatixSolverVersion = "spoofax3-SNAPSHOT"
//val metaborgStatixGeneratorVersion = "spoofax3-SNAPSHOT"
val slf4jVersion = "1.7.30"
val logbackVersion = "1.2.6"
val microutilsLoggingVersion = "2.0.11"
val daggerVersion = "2.36"
val progressbarVersion = "0.9.2"
val cliktVersion = "3.2.0"
val commonsCsvVersion = "1.9.0"
val jacksonVersion = "2.13.0"
val jfreechartVersion = "1.5.3"
val jfreePdfVersion = "2.0"
val commonsIoVersion = "2.8.0"
val commonsMathVersion = "3.6.1"
val junitVersion = "5.8.1"

// Metaborg
//val spoofax3CoreVersion = "cc2109-SNAPSHOT"
//val commonVersion = "cc2109-SNAPSHOT"
//val logVersion = "develop-SNAPSHOT"
//val resourceVersion = "develop-SNAPSHOT"
//val pieVersion = "develop-SNAPSHOT"
//val spoofax2DevenvVersion = "spoofax3-SNAPSHOT"
//
//val spoofax3CoreVersion = "latest.integration"
//val commonVersion = "latest.integration"
//val logVersion = "latest.integration"
//val resourceVersion = "latest.integration"
//val pieVersion = "latest.integration"
//val spoofax2DevenvVersion = "latest.integration"

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
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("ch.qos.logback:logback-core:$logbackVersion")
        implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")

        // Dependency Injection
        implementation("com.google.dagger:dagger:$daggerVersion")
        implementation("com.google.dagger:dagger-compiler:$daggerVersion")
        kapt("com.google.dagger:dagger-compiler:$daggerVersion")

        // CLI
        implementation("me.tongfei:progressbar:$progressbarVersion")
        implementation("com.github.ajalt.clikt:clikt:$cliktVersion")

        // CSV
        implementation("org.apache.commons:commons-csv:$commonsCsvVersion")

        // YAML
        implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

        // Charts
        implementation("org.jfree:jfreechart:$jfreechartVersion")
        implementation("org.jfree:org.jfree.pdf:$jfreePdfVersion")

        // Utils
        implementation("commons-io:commons-io:$commonsIoVersion")
        implementation("org.apache.commons:commons-math3:$commonsMathVersion")

        // Testing
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testRuntimeOnly   ("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    kapt {
        correctErrorTypes = true
    }
}