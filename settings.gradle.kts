rootProject.name = "ccbench"

include("ccbench.core")
include("ccbench.tiger")
include("ccbench.chocopy")
//include("ccbench.webdsl")
include("ccbench.platform")

includeBuild("../devenv")
includeBuild("../chocopy-grading-2021")
