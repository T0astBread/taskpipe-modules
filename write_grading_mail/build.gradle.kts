import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.11"
    application
}

group = "cc.t0ast.taskpipe.modules.write_grading_mail"
version = "1.0-SNAPSHOT"

application.mainClassName = "cc.t0ast.taskpipe.modules.write_grading_mail.Main"

repositories {
    mavenCentral()
}

dependencies {
    compile(project(":mark"))
    compile("com.google.code.gson:gson:2.8.5")
    compile("org.simpleframework:simple-xml:2.7.1")
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
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) }) // make it a fat JAR
}