plugins {
    java
    application
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

val metaborgTigerVersion = "cc2109-SNAPSHOT"

dependencies {
    implementation(project(":ccbench.core"))
    implementation("org.metaborg:tiger.spoofax3:$metaborgTigerVersion")
}

application {
    mainClass.set("mb.codecompletion.bench.tiger.MainKt")
}
