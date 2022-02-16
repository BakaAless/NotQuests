import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id ("com.github.johnrengelman.shadow")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


repositories {
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/"){
        content {
            includeGroup("io.papermc.paper")
            includeGroup("net.kyori")
        }
    }

    maven("https://hub.spigotmc.org/nexus/content/groups/public/"){
        content {
            includeGroup("org.spigotmc")
        }
    }

    maven("https://repo.citizensnpcs.co/"){
        content {
            includeGroup("net.citizensnpcs")
        }
    }

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/"){
        content {
            includeGroup("me.clip")
        }
    }

    maven("https://jitpack.io"){
        content {
            includeGroup("com.github.MilkBowl")
            includeGroup("com.github.TheBusyBiscuit")
            includeGroup("com.github.retrooper")
            includeGroup("com.github.retrooper.packetevents")
            includeGroup("io.github.retrooper")
            includeGroup("com.github.AlessioGr")
            includeGroup("com.github.AlessioGr.packetevents")
            includeGroup("com.github.TownyAdvanced")
            includeGroup("com.github.Zrips")
        }
        metadataSources {
            artifact()
        }
    }

    maven("https://repo.minebench.de/"){
        content {
            includeGroup("de.themoep")
        }
    }

    maven("https://mvn.lumine.io/repository/maven-public/"){
        content {
            includeGroup("io.lumine.xikage")
        }
    }

    maven("https://betonquest.org/nexus/repository/betonquest/"){
        content {
            includeGroup("org.betonquest")
        }
        metadataSources {
            artifact()
        }
    }

    maven("https://maven.enginehub.org/repo/"){
        content {
            includeGroup("com.sk89q.worldedit")
        }
        metadataSources {
            artifact()
        }
    }

    maven("https://repo.incendo.org/content/repositories/snapshots"){
        content {
            includeGroup("org.incendo.interfaces")
        }
    }

    maven("https://libraries.minecraft.net/"){
        content {
            includeGroup("com.mojang")
        }
    }

    maven("https://repo.incendo.org/content/repositories/snapshots"){
        content {
            includeGroup("cloud.commandframework")
        }
    }

    //mavenLocal()

}




dependencies {
    //implementation project(':common')

    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("de.themoep:inventorygui:1.5-SNAPSHOT")

    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    compileOnly("net.citizensnpcs:citizens-main:2.0.29-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")


    compileOnly("io.lumine.xikage:MythicMobs:4.12.0")
    compileOnly(files("libs/EliteMobs.jar"))
    compileOnly(files("libs/UClans-API.jar"))
    compileOnly(files("libs/ProjectKorra-1.9.2.jar"))


    compileOnly("org.betonquest:betonquest:2.0.0-SNAPSHOT")

    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")

    compileOnly("com.github.TheBusyBiscuit:Slimefun4:RC-30")

    compileOnly("net.luckperms:api:5.4")

    //compileOnly "com.github.NEZNAMY:TAB:2.9.2"
    compileOnly("com.github.TownyAdvanced:Towny:0.97.5.0")

    compileOnly("com.github.Zrips:Jobs:v4.17.2")


    //Shaded
    implementation("net.kyori:adventure-text-minimessage:4.10.0-20220207.012501-47") {
        exclude(group = "net.kyori", module = "adventure-api")
        exclude(group = "net.kyori", module = "adventure-bom")
    }
    implementation("net.kyori:adventure-platform-bukkit:4.0.1")

    //CloudCommands
    implementation("cloud.commandframework:cloud-paper:1.7.0-SNAPSHOT")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.7.0-SNAPSHOT")
    //Else it errors:
    implementation("io.leangen.geantyref:geantyref:1.3.13")

    //implementation 'com.github.retrooper.packetevents:bukkit:2.0-SNAPSHOT'
    implementation("com.github.AlessioGr.packetevents:bukkit:2.0-SNAPSHOT")

    implementation("commons-io:commons-io:2.11.0")
    //implementation 'org.apache.commons:commons-text:1.9'
    //implementation 'org.apache.commons:commons-lang3:3.12.0'
    //implementation 'org.apache.commons:commons-lang:3.1'

    implementation("io.netty:netty-all:4.1.72.Final")

    compileOnly("com.mojang:brigadier:1.0.18")
}

/**
 * Configure NotQuests for shading
 */
val shadowPath = "rocks.gravili.notquests.spigot.shadow"
tasks.withType<ShadowJar> {
    minimize()

    //exclude('com.mojang:brigadier')
    relocate("net.kyori", "$shadowPath.kyori")


    //relocate('io.papermc.lib', path.concat('.paper'))
    relocate("org.bstats", "$shadowPath.bstats")
    relocate("cloud.commandframework", "$shadowPath.cloud")
    relocate("io.leangen.geantyref", "$shadowPath.geantyref")
    relocate("de.themoep", "$shadowPath.de.themoep")

    relocate("org.apache.commons.io", "$shadowPath.commons.io")
    //relocate("org.apache.commons.text", path.concat('.commons.text'))
    //relocate("org.apache.commons.lang3", path.concat('.commons.lang'))

    relocate("io.github.retrooper.packetevents", "$shadowPath.packetevents.bukkit")
    relocate("com.github.retrooper.packetevents", "$shadowPath.packetevents.api")


    dependencies {
        //include(dependency('org.apache.commons:')
        include(dependency("commons-io:commons-io:"))

        //include(dependency('io.papermc:paperlib')
        include(dependency("de.themoep:inventorygui:1.5-SNAPSHOT"))
        include(dependency("org.bstats:"))
        include(dependency("cloud.commandframework:"))
        include(dependency("io.leangen.geantyref:"))
        include(dependency("me.lucko:"))

        include(dependency("com.github.retrooper.packetevents:"))
        //include(dependency('io.github.retrooper.packetevents:')

        include(dependency("com.github.AlessioGr.packetevents:"))

        //include(dependency('net.kyori:adventure-platform-bukkit:')
        include(dependency("net.kyori:"))


    }
    //archiveBaseName.set("notquests")
    archiveClassifier.set("")



}


tasks {
    // Run reobfJar on build
    //build {
    //    dependsOn(shadowJar)
    //}
    build {
        dependsOn(shadowJar)
    }

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