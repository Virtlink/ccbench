rootProject.name = "ccbench"

pluginManagement {
    repositories {
        maven("https://artifacts.metaborg.org/content/groups/public/")
    }
}

include("ccbench.core")
include("ccbench.tiger")
include("ccbench.chocopy")
//include("ccbench.webdsl")

includeBuild("../devenv")
includeBuild("../chocopy-grading-2021")


