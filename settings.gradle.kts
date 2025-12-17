plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "BReversed"
include("app")
includeBuild("../ASMPlus") {
    dependencySubstitution {
        substitute(module("me.exeos:asmplus")).using(project(":asmplus-lib"))
    }
}
includeBuild("../jlib") {
    dependencySubstitution {
        substitute(module("me.exeos:jlib")).using(project(":jlib-lib"))
    }
}