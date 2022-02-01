rootProject.name = "ccbench"

pluginManagement {
    repositories {
        maven("https://artifacts.metaborg.org/content/groups/public/")
    }
    includeBuild("../devenv")
//    includeBuild("../devenv-220119")
}

include("spree")
include("asterms")
include("ccbench.core")
include("ccbench.tiger")
include("ccbench.chocopy")
//include("ccbench.webdsl")

includeBuild("../devenv")
//includeBuild("../devenv-220119")
//includeBuild("../chocopy-grading-2021")


