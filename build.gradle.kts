import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("com.dorongold.task-tree") version "1.5"
}

group = "ru.emkn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://kotlin.bintray.com/kotlinx")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    //JUnit Tests
    val junitVersion = "5.6.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation(kotlin("test"))
    //Command Line Interface implementation
    implementation("com.github.ajalt.clikt:clikt:3.0.1")
    //gradle kotlin DSL
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3")
    implementation("org.apache.commons:commons-csv:1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
    //JSON Parser
    implementation ("com.jayway.jsonpath", "json-path", "2.0.0")
    implementation ("com.google.code.gson:gson:2.8.5")
}

tasks.withType(KotlinCompile::class.java) {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    maxHeapSize = "1024m"
}

tasks.register("hello") {
    doLast {
        println("Hello, World!")
    }
}

tasks.shadowJar {
    archiveBaseName.set("runnable")
    archiveClassifier.set("")
    mergeServiceFiles()

    manifest {
        attributes["Main-Class"] = "ru.emkn.kotlin.MainKt"
    }
}

val runJar by tasks.creating(Exec::class) {
    dependsOn(tasks.shadowJar)
    val jarFile = tasks.shadowJar.get().outputs.files.singleFile
    val evalArgs = listOf("java", "-jar", jarFile.absolutePath)
    commandLine(*evalArgs.toTypedArray())
}

