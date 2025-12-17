plugins {
    application
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://stianloader.org/maven")
    }
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.asmplus)
    implementation(libs.jlib)
    implementation(libs.commons.io)
    implementation(libs.gson)
    implementation(libs.reflections)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "xyz.breversed.BReversed"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
