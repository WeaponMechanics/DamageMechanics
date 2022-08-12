import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "me.cjcrafter"
version = "1.0.2"

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

// See https://github.com/Minecrell/plugin-yml
bukkit {
    main = "me.cjcrafter.damagemechanics.DamageMechanics"
    apiVersion = "1.13"

    authors = listOf("CJCrafter")

    commands {
        register("damagemechanics") {
            description = "Reload DamageMechanics"
            permission = "damagemechanics.reload"
        }
    }
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks.named<ShadowJar>("shadowJar") {
    classifier = null
    archiveFileName.set("DamageMechanics-${project.version}.jar")
    configurations = listOf(project.configurations["shadeOnly"], project.configurations["runtimeClasspath"])

    dependencies {
        relocate ("org.bstats", "me.cjcrafter.damagemechanics.lib.bstats") {
            include(dependency("org.bstats:"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(8)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}