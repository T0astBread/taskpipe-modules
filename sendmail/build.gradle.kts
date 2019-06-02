import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30"
    application
}

group = "cc.t0ast.taskpipe.modules.sendmail"
version = "1.0-SNAPSHOT"
application.mainClassName = "cc.t0ast.taskpipe.modules.sendmail.Main"

dependencies {
    compile("org.apache.commons:commons-email:1.5")
    compile("com.google.code.gson:gson:2.8.5")
    compile(kotlin("stdlib-jdk8"))
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
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
}
