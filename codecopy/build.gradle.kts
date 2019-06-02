import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30"
    application
}

group = "cc.t0ast.taskpipe.modules.codecopy"
version = "1.0-SNAPSHOT"

application.mainClassName = "cc.t0ast.taskpipe.modules.codecopy.Main"

dependencies {
    compile(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

repositories {
    mavenCentral()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest.attributes.apply {
        put("Main-Class", application.mainClassName)
    }
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) }) // make it a fat JAR
}

