plugins {
    kotlin("jvm") version "+"
    id("com.github.johnrengelman.shadow") version "+"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/") // PaperMC
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:+") // Paper Latest
    compileOnly(files("/libs/CustomEnchant-dist.jar"))
    compileOnly(kotlin("stdlib")) // Kotlin
    compileOnly("io.github.monun:kommand-api:+")
    compileOnly("com.github.milkbowl:VaultAPI:+")
    implementation("com.github.stefvanschie.inventoryframework:IF:+")
    implementation("com.github.hazae41:mc-kutils:+")
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