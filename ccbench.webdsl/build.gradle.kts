plugins {
    java
    application
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

val metaborgTigerVersion = "latest.integration"
//val metaborgTigerVersion = "10.0.0-cc2109-SNAPSHOT"

dependencies {
    implementation(project(":ccbench.core"))
    implementation("org.metaborg:webdsl:$metaborgTigerVersion")
}

application {
    mainClass.set("mb.ccbench.webdsl.MainKt")
}
