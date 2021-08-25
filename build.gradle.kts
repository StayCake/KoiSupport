plugins {
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/") // PaperMC
}

dependencies {
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.1")
    implementation("com.github.hazae41:mc-kutils:+")
    compileOnly(files("/libs/CustomEnchant-dist.jar"))
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.5.21") // Kotlin
    compileOnly("io.github.monun:kommand-api:2.6.4")
    compileOnly("com.github.milkbowl:VaultAPI:+")
    compileOnly("io.papermc.paper:paper-api:+") // Paper Latest
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "16" // https://papermc.io/java16 | 모장 개놈들아
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filteringCharset = "UTF-8"
    }
    shadowJar {
        archiveClassifier.set("dist")
        archiveVersion.set("")
    }
    create<Copy>("dist") {
        from (shadowJar)
        into(".\\")
    }
}