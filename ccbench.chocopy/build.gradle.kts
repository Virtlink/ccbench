plugins {
    java
    application
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

val metaborgChocopyVersion = "latest.integration"

dependencies {
    implementation(project(":ccbench.core"))
    implementation("org.example:chocopy.reference:$metaborgChocopyVersion")
}

application {
    mainClass.set("mb.ccbench.chocopy.MainKt")
}
