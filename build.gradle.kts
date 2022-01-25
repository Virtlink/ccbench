import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    java
    kotlin("jvm") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.adarshr.test-logger") version "3.1.0"
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


val spoofax3Version             = "0.16.17"   // "latest.integration"
//val spoofax3Version             = "0.16.15"   // "latest.integration"
val slf4jVersion                = "1.7.33"
val logbackVersion              = "1.2.6"
val microutilsLoggingVersion    = "2.0.11"
val daggerVersion               = "2.40.5"
val progressbarVersion          = "0.9.2"
val cliktVersion                = "3.2.0"
val mordantVersion              = "2.0.0-beta3"
val commonsCsvVersion           = "1.9.0"
val jacksonVersion              = "2.13.0"
val jfreechartVersion           = "1.5.3"
val jfreePdfVersion             = "2.0"
val commonsIoVersion            = "2.8.0"
val commonsMathVersion          = "3.6.1"
val junitVersion                = "5.8.1"


allprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

        // Logging
        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")

        // Testing
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testRuntimeOnly   ("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
//        testlogger {
//            theme = ThemeType.MOCHA
//        }
    }
}

configure(subprojects.filter { "spree" !in it.name }) {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
//    apply(plugin = "com.adarshr.test-logger")

    dependencies {
        // Platform
        implementation(platform("org.metaborg:spoofax.depconstraints:$spoofax3Version"))
        api(platform("org.metaborg:spoofax.depconstraints:$spoofax3Version"))
        kapt(platform("org.metaborg:spoofax.depconstraints:$spoofax3Version"))
        testImplementation(platform("org.metaborg:spoofax.depconstraints:$spoofax3Version"))
        testRuntimeOnly(platform("org.metaborg:spoofax.depconstraints:$spoofax3Version"))
        implementation(kotlin("stdlib"))

//        implementation(platform(project(":ccbench.platform")))
//        api(platform(project(":ccbench.platform")))
//        kapt(platform(project(":ccbench.platform")))
//        testImplementation(platform(project(":ccbench.platform")))
//        testRuntimeOnly(platform(project(":ccbench.platform")))
//        implementation(kotlin("stdlib"))
        implementation(project(":spree"))

        // Spoofax
        implementation("org.metaborg:statix.codecompletion.pie")
        implementation("org.metaborg:statix.codecompletion")
        implementation("org.metaborg:jsglr.pie")
//        api("org.metaborg:gpp")
        implementation("org.metaborg.devenv:org.spoofax.terms")
        implementation("org.metaborg.devenv:nabl2.terms")
        implementation("org.metaborg.devenv:statix.solver")
        //implementation(files("../libs/strategolib.jar"))
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
        implementation("com.github.ajalt.mordant:mordant:$mordantVersion")

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
//        testlogger {
//            theme = ThemeType.MOCHA
//        }
    }

    kapt {
        correctErrorTypes = true
    }
}