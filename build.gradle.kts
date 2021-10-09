plugins {
    java
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.21"
}

val metaborgCoreVersion = "cc2109-SNAPSHOT"
val metaborgSpoofaxTermsVersion = "spoofax3-SNAPSHOT"
val metaborgLogVersion = "develop-SNAPSHOT"
val metaborgPieVersion = "develop-SNAPSHOT"
val metaborgNabl2TermsVersion = "spoofax3-SNAPSHOT"
val metaborgStatixSolverVersion = "spoofax3-SNAPSHOT"
val metaborgStatixGeneratorVersion = "spoofax3-SNAPSHOT"

val slf4jVersion = "1.7.30"
val logbackVersion = "1.2.6"
val microutilsLoggingVersion = "2.0.11"

val daggerVersion = "2.36"

val progressbarVersion = "0.9.2"
val cliktVersion = "3.2.0"

val commonsCsvVersion = "1.9.0"

val jacksonVersion = "2.13.0"

val jfreechartVersion = "1.5.3"
val itextVersion = "7.1.16"

val commonsIoVersion = "2.8.0"
val commonsMathVersion = "3.6.1"

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "mb.ccbench"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        maven("https://artifacts.metaborg.org/content/groups/public/")
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

        // Spoofax
        implementation("org.metaborg:spoofax.core:$metaborgCoreVersion")
        implementation("org.metaborg:constraint.pie:$metaborgCoreVersion")
        implementation("org.metaborg:constraint.common:$metaborgCoreVersion")
        implementation("org.metaborg:statix.codecompletion.pie:$metaborgCoreVersion")
        implementation("org.metaborg:statix.codecompletion:$metaborgCoreVersion")
        implementation("org.metaborg:jsglr.pie:$metaborgCoreVersion")
        implementation("org.metaborg:jsglr.pie:$metaborgCoreVersion")
        implementation("org.metaborg.devenv:statix.generator:$metaborgStatixGeneratorVersion")
        implementation("org.metaborg.devenv:org.spoofax.terms:$metaborgSpoofaxTermsVersion")
        implementation("org.metaborg.devenv:nabl2.terms:$metaborgNabl2TermsVersion")
        implementation("org.metaborg.devenv:statix.solver:$metaborgStatixSolverVersion")
        implementation("org.metaborg:pie.api:$metaborgPieVersion")
        implementation("org.metaborg:pie.runtime:$metaborgPieVersion")
        implementation("org.metaborg:pie.dagger:$metaborgPieVersion")
        implementation("org.metaborg:log.backend.slf4j:$metaborgLogVersion")


        /*
        com.google.dagger:dagger (n)
        com.google.dagger:dagger:2.36 (c)
        javax.inject:javax.inject:1 (c)
        org.checkerframework:checker-qual-android:3.16.0 (c)
        org.metaborg.devenv:nabl2.solver:0.1.14 -> project :spoofax2.releng.java.root:nabl2.solver (c)
        org.metaborg.devenv:nabl2.terms:0.1.14 -> project :spoofax2.releng.java.root:nabl2.terms (c)
        org.metaborg.devenv:org.metaborg.parsetable:0.1.14 -> project :spoofax2.releng.java.root:org.metaborg.parsetable (c)
        org.metaborg.devenv:org.metaborg.util:0.1.14 -> project :spoofax2.releng.java.root:org.metaborg.util (c)
        org.metaborg.devenv:org.spoofax.interpreter.core:0.1.14 -> project :spoofax2.releng.java.root:org.spoofax.interpreter.core (c)
        org.metaborg.devenv:org.spoofax.jsglr2:0.1.14 -> project :spoofax2.releng.java.root:org.spoofax.jsglr2 (c)
        org.metaborg.devenv:org.spoofax.jsglr:0.1.14 -> project :spoofax2.releng.java.root:org.spoofax.jsglr (c)
        org.metaborg.devenv:org.spoofax.terms:0.1.14 -> project :spoofax2.releng.java.root:org.spoofax.terms (c)
        org.metaborg.devenv:org.strategoxt.strj (n)
        org.metaborg.devenv:org.strategoxt.strj:0.1.14 -> project :spoofax2.releng.java.root:org.strategoxt.strj (c)
        org.metaborg.devenv:statix.solver:0.1.14 -> project :spoofax2.releng.java.root:statix.solver (c)
        org.metaborg:aterm.common:cc2109-SNAPSHOT (n)
        org.metaborg:common (n)
        org.metaborg:common:0.9.7 -> project :common.root:common (c)
        org.metaborg:completions.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:completions.common (c)
        org.metaborg:constraint.common:cc2109-SNAPSHOT (n)
        org.metaborg:constraint.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:constraint.common (c)
        org.metaborg:constraint.pie:cc2109-SNAPSHOT (n)
        org.metaborg:esv.common:cc2109-SNAPSHOT (n)
        org.metaborg:esv.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:esv.common (c)
        org.metaborg:jsglr.common:cc2109-SNAPSHOT (n)
        org.metaborg:jsglr.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:jsglr.common (c)
        org.metaborg:jsglr.pie:cc2109-SNAPSHOT (n)
        org.metaborg:jsglr.pie:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:jsglr.pie (c)
        org.metaborg:jsglr1.common:cc2109-SNAPSHOT (n)
        org.metaborg:jsglr1.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:jsglr1.common (c)
        org.metaborg:log.api (n)
        org.metaborg:log.api:0.5.5 -> project :log.root:log.api (c)
        org.metaborg:log.dagger:0.5.5 -> project :log.root:log.dagger (c)
        org.metaborg:nabl2.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:nabl2.common (c)
        org.metaborg:pie.api (n)
        org.metaborg:pie.api:0.17.0 -> project :pie.core.root:pie.api (c)
        org.metaborg:pie.dagger:0.17.0 -> project :pie.core.root:pie.dagger (c)
        org.metaborg:resource (n)
        org.metaborg:resource.dagger:0.11.5 -> project :resource.root:resource.dagger (c)
        org.metaborg:resource:0.11.5 -> project :resource.root:resource (c)
        org.metaborg:spoofax.compiler.interfaces:cc2109-SNAPSHOT (n)
        org.metaborg:spoofax.compiler.interfaces:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:spoofax.compiler.interfaces (c)
        org.metaborg:spoofax.core:cc2109-SNAPSHOT (n)
        org.metaborg:spoofax.core:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:spoofax.core (c)
        org.metaborg:spoofax.depconstraints:cc2109-SNAPSHOT (n)
        org.metaborg:spoofax.resource:cc2109-SNAPSHOT (n)
        org.metaborg:spoofax.resource:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:spoofax.resource (c)
        org.metaborg:spoofax2.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:spoofax2.common (c)
        org.metaborg:spt.api:cc2109-SNAPSHOT (n)
        org.metaborg:statix.codecompletion.pie:cc2109-SNAPSHOT (n)
        org.metaborg:statix.codecompletion.pie:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:statix.codecompletion.pie (c)
        org.metaborg:statix.codecompletion:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:statix.codecompletion (c)
        org.metaborg:statix.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:statix.common (c)
        org.metaborg:statix.pie:cc2109-SNAPSHOT (n)
        org.metaborg:statix.pie:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:statix.pie (c)
        org.metaborg:stratego.common:cc2109-SNAPSHOT (n)
        org.metaborg:stratego.common:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:stratego.common (c)
        org.metaborg:stratego.pie:cc2109-SNAPSHOT (n)
        org.metaborg:strategoxt-min-jar (n)
        org.metaborg:tego.pie:cc2109-SNAPSHOT (n)
        org.metaborg:tego.pie:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:tego.pie (c)
        org.metaborg:tego:cc2109-SNAPSHOT (n)
        org.metaborg:tego:{require cc2109-SNAPSHOT} -> project :spoofax3.core.root:tego (c)
        org.yaml:snakeyaml:1.26 (c)
       */

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
        implementation("com.itextpdf:itext7-core:$itextVersion")

        // Utils
        implementation("commons-io:commons-io:$commonsIoVersion")
        implementation("org.apache.commons:commons-math3:$commonsMathVersion")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    kapt {
        correctErrorTypes = true
    }
}