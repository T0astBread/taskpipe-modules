import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.11"
    application
}

group = "com.t0ast.taskpipe.modules.moodle_downloader"
version = "1.0-SNAPSHOT"

application.mainClassName = "cc.t0ast.taskpipe.modules.moodle_downloader.Main"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.github.kittinunf.fuel:fuel:1.16.0")
    compile("org.jsoup:jsoup:1.11.3")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest.attributes.apply {
        put("Main-Class", application.mainClassName)
        from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
    }
}
