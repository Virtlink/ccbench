plugins {
    java
    application
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

val metaborgTigerVersion        = "0.16.15"   // "latest.integration"
//val metaborgTigerVersion = "10.0.0-cc2109-SNAPSHOT"

dependencies {
    implementation(project(":ccbench.core"))
    implementation("org.metaborg:tiger.spoofax3:$metaborgTigerVersion")
}

application {
    mainClass.set("mb.ccbench.tiger.MainKt")
}
