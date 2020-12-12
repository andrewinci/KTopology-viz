import java.net.URI

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    `maven-publish`
    `java-library`
}

repositories {
    jcenter()
}

val kotestVersion = "4.3.0"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Graphviz java
    implementation("guru.nidi:graphviz-java:0.18.0")

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xallow-result-return-type")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "KTopologyViz" //  optional target repository name
            url = URI("https://maven.pkg.github.com/andrea-vinci/KTopology-viz")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
