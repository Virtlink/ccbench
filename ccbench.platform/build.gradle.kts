plugins {
    `java-platform`
}

val spoofax3CoreVersion       = "latest.integration"
val commonVersion             = "latest.integration"
val logVersion                = "latest.integration"
val resourceVersion           = "latest.integration"
val pieVersion                = "latest.integration"
val spoofax2DevenvVersion     = "latest.integration"

val slf4jVersion              = "1.7.30"
val logbackVersion            = "1.2.6"
val microutilsLoggingVersion  = "2.0.11"
val daggerVersion             = "2.36"
val progressbarVersion        = "0.9.2"
val cliktVersion              = "3.2.0"
val commonsCsvVersion         = "1.9.0"
val jacksonVersion            = "2.13.0"
val jfreechartVersion         = "1.5.3"
val jfreePdfVersion           = "2.0"
val commonsIoVersion          = "2.8.0"
val commonsMathVersion        = "3.6.1"
val junitVersion              = "5.8.1"

//val spoofax3CoreVersion = "10.0.0-cc2109-SNAPSHOT"
//val commonVersion = "10.0.0-develop-SNAPSHOT"
//val logVersion = "10.0.0-develop-SNAPSHOT"
//val resourceVersion = "10.0.0-develop-SNAPSHOT"
//val pieVersion = "10.0.0-develop-SNAPSHOT"
//val spoofax2DevenvVersion = "10.0.0-spoofax3-SNAPSHOT"

dependencies {
    constraints {
        // Spoofax 3 Core
        api("org.metaborg:completions.common:$spoofax3CoreVersion")
        api("org.metaborg:jsglr.common:$spoofax3CoreVersion")
        api("org.metaborg:jsglr1.common:$spoofax3CoreVersion")
        api("org.metaborg:jsglr.pie:$spoofax3CoreVersion")
        api("org.metaborg:jsglr2.common:$spoofax3CoreVersion")
        api("org.metaborg:esv.common:$spoofax3CoreVersion")
        api("org.metaborg:stratego.common:$spoofax3CoreVersion")
        api("org.metaborg:constraint.common:$spoofax3CoreVersion")
        api("org.metaborg:nabl2.common:$spoofax3CoreVersion")
        api("org.metaborg:statix.codecompletion:$spoofax3CoreVersion")
        api("org.metaborg:statix.codecompletion.pie:$spoofax3CoreVersion")
        api("org.metaborg:statix.common:$spoofax3CoreVersion")
        api("org.metaborg:statix.pie:$spoofax3CoreVersion")
        api("org.metaborg:statix.multilang:$spoofax3CoreVersion")
        api("org.metaborg:spoofax2.common:$spoofax3CoreVersion")
        api("org.metaborg:tego:$spoofax3CoreVersion")
        api("org.metaborg:tego.pie:$spoofax3CoreVersion")
        api("org.metaborg:tooling.eclipsebundle:$spoofax3CoreVersion")

        // Spoofax 3
        api("org.metaborg:spoofax.compiler.interfaces:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.resource:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.core:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.cli:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.intellij:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.eclipse:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.compiler:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.compiler.gradle:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.compiler.eclipsebundle:$spoofax3CoreVersion")

        // Spoofax 3 Common
        api("org.metaborg:common:$commonVersion")

        // Spoofax 3 Log
        api("org.metaborg:log.api:$logVersion")
        api("org.metaborg:log.backend.logback:$logVersion")
        api("org.metaborg:log.backend.slf4j:$logVersion")
        api("org.metaborg:log.dagger:$logVersion")

        // Spoofax 3 Resource
        api("org.metaborg:resource:$resourceVersion")
        api("org.metaborg:resource.dagger:$resourceVersion")

        // Spoofax 3 PIE
        api("org.metaborg:pie.api:$pieVersion")
        api("org.metaborg:pie.runtime:$pieVersion")
        api("org.metaborg:pie.dagger:$pieVersion")
        api("org.metaborg:pie.task.java:$pieVersion")
        api("org.metaborg:pie.task.archive:$pieVersion")
        api("org.metaborg:pie.serde.fst:$pieVersion")

        // Spoofax 2
        api("org.metaborg.devenv:org.strategoxt.strj:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:org.spoofax.terms:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:org.metaborg.util:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:org.spoofax.interpreter.core:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:org.spoofax.jsglr:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:org.spoofax.jsglr2:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:sdf2table:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:sdf2parenthesize:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:org.metaborg.parsetable:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:stratego.build:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:nabl2.terms:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:nabl2.solver:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:statix.solver:$spoofax2DevenvVersion")
        api("org.metaborg.devenv:statix.generator:$spoofax2DevenvVersion")
        // Apparently strategolib is not a jar with classes.
//        api("org.metaborg.devenv:strategolib:$spoofax2DevenvVersion@spoofax-language")
        // I found a strategolib.jar in the Spoofax 3 build and copied it to the libs/ directory instead.

        // Logging
        api("ch.qos.logback:logback-classic:$logbackVersion")
        api("ch.qos.logback:logback-core:$logbackVersion")
        api("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")

        // Dependency Injection
        api("com.google.dagger:dagger:$daggerVersion")
        api("com.google.dagger:dagger-compiler:$daggerVersion")

        // CLI
        api("me.tongfei:progressbar:$progressbarVersion")
        api("com.github.ajalt.clikt:clikt:$cliktVersion")

        // CSV
        api("org.apache.commons:commons-csv:$commonsCsvVersion")

        // YAML
        api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

        // Charts
        api("org.jfree:jfreechart:$jfreechartVersion")
        api("org.jfree:org.jfree.pdf:$jfreePdfVersion")

        // Utils
        api("commons-io:commons-io:$commonsIoVersion")
        api("org.apache.commons:commons-math3:$commonsMathVersion")

        // Testing
        api("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        api("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }
}