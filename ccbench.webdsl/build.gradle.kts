plugins {
    java
    application
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":ccbench.core"))
}

//application {
//    mainClass.set("mb.ccbench.MainKt")
//}
