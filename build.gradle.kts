plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

group = "net.fuzzyblocks"
version = "2.2"
description = "FuzzyMessenger"

repositories {
    mavenLocal()
    
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation(libs.be.darnell.fuzzylog)
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf(
        "name" to project.description.toString(),
        "version" to project.version.toString()
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

