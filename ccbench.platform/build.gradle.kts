plugins {
    `java-platform`
}

val spoofax3CoreVersion = "latest.integration"
val commonVersion = "latest.integration"
val logVersion = "latest.integration"
val resourceVersion = "latest.integration"
val pieVersion = "latest.integration"
val spoofax2DevenvVersion = "latest.integration"

//val spoofax3CoreVersion = "10.0.0-cc2109-SNAPSHOT"
//val commonVersion = "10.0.0-develop-SNAPSHOT"
//val logVersion = "10.0.0-develop-SNAPSHOT"
//val resourceVersion = "10.0.0-develop-SNAPSHOT"
//val pieVersion = "10.0.0-develop-SNAPSHOT"
//val spoofax2DevenvVersion = "10.0.0-spoofax3-SNAPSHOT"

dependencies {
    constraints {
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

        api("org.metaborg:spoofax.compiler.interfaces:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.resource:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.core:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.cli:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.intellij:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.eclipse:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.compiler:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.compiler.gradle:$spoofax3CoreVersion")
        api("org.metaborg:spoofax.compiler.eclipsebundle:$spoofax3CoreVersion")

        api("org.metaborg:common:$commonVersion")

        api("org.metaborg:log.api:$logVersion")
        api("org.metaborg:log.backend.logback:$logVersion")
        api("org.metaborg:log.backend.slf4j:$logVersion")
        api("org.metaborg:log.dagger:$logVersion")

        api("org.metaborg:resource:$resourceVersion")
        api("org.metaborg:resource.dagger:$resourceVersion")

        api("org.metaborg:pie.api:$pieVersion")
        api("org.metaborg:pie.runtime:$pieVersion")
        api("org.metaborg:pie.dagger:$pieVersion")
        api("org.metaborg:pie.task.java:$pieVersion")
        api("org.metaborg:pie.task.archive:$pieVersion")
        api("org.metaborg:pie.serde.fst:$pieVersion")

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
        // I found a strategolib.jar in the Spoofax 3 build and copied it to the libs/ directory instead.
//        api("org.metaborg.devenv:strategolib:$spoofax2DevenvVersion@spoofax-language")
    }
}