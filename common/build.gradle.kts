/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2022 Alessio Gravili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

group = "rocks.gravili.notquests"
version = rootProject.version

repositories {
    mavenCentral()
    //mavenLocal()

}

dependencies {
    implementation("net.kyori:adventure-api:4.11.0")
    //implementation(project(":spigot"))
    //implementation(project(":paper"))

    //compileOnly(files("libs/EliteMobs.jar"))
    //compileOnly(files("libs/UClans-API.jar"))
    //compileOnly(files("libs/ProjectKorra-1.9.2.jar"))
}

/**
 * Configure NotQuests for shading
 */
val shadowPath = "rocks.gravili.notquests"
tasks.withType<ShadowJar> {
    minimize()

    relocate("net.kyori", "$shadowPath.kyori")

    dependencies {
        include(dependency("net.kyori:"))


    }
    //archiveBaseName.set("notquests")
    archiveClassifier.set("")
}
/*processResources {
    def props = [version: version]
    inputs.properties props
    filteringCharset 'UTF-8'
    filesMatching('plugin.yml') {
        expand props
    }
}*/


tasks {
    // Run reobfJar on build
    //build {
    //    dependsOn(shadowJar)
    //}
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

/*publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "rocks.gravili.notquests"
            artifactId = "NotQuests"
            version = "4.0.0-dev"

            from(components["java"])
        }
    }
}*/

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/alessiogr/NotQuests")
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
