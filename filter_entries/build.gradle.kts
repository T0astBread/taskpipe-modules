import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.30"
    application
}

group = "cc.t0ast.taskpipe.modules.filter_entries"
version = "1.0-SNAPSHOT"

application.mainClassName = "cc.t0ast.taskpipe.modules.filter_entries.Main"

repositories {
    mavenCentral()
}

dependencies {
    compile(project(":mark"))
    compile(kotlin("stdlib-jdk8"))
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
    }
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
}
