<p align="center">
<h3 align="center">Supervisor</h3>

------

<p align="center">
Implements a cloud command inside a DI container that simplifies the description of commands and is similar to Mojang Brigadier. It is useful for people with basic understanding of java, gradle, workflow and is designed for lazy people
</p>

<p align="center">
<img alt="License" src="https://img.shields.io/github/license/CKATEPTb-minecraft/Supervisor">
<a href="#Download"><img alt="Sonatype Nexus (Snapshots)" src="https://img.shields.io/nexus/s/dev.ckateptb.minecraft/Supervisor?label=repo&server=https://repo.animecraft.fun/"></a>
<img alt="Publish" src="https://img.shields.io/github/workflow/status/CKATEPTb-minecraft/Supervisor/Publish/production">
<a href="https://docs.gradle.org/7.5/release-notes.html"><img src="https://img.shields.io/badge/Gradle-7.5-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://discord.gg/P7FaqjcATp" target="_blank"><img alt="Discord" src="https://img.shields.io/discord/925686623222505482?label=discord"></a>
</p>

------

# Versioning

We use [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) to manage our releases.

# Features

- [X] Easy to use
- [X] Automatic registration
- [X] Annotation based
- [ ] Fully customizable
- [ ] Documented
- 
# Download

Download from our repository or depend via Gradle:

```kotlin
repositories {
    maven("https://repo.animecraft.fun/repository/maven-snapshots/")
}
dependencies {
    implementation("dev.ckateptb.minecraft:Supervisor:<version>")
}
```

# How To

* Import the dependency [as shown above](#Download)
* Add Supervisor as a dependency to your `plugin.yml`
```yaml
name: ...
version: ...
main: ...
depend: [ Supervisor ]
authors: ...
description: ...
```
* Study the basic example to understand how to use Supervisor
```java
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.supervisor.Command;
import dev.ckateptb.minecraft.supervisor.Supervisor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

@Getter // Generate getters for fields
@Component // Auto-inject
public class SupervisorCommand implements Command<Supervisor> /*implements Command<your plugin>*/ {
    protected final Supervisor plugin; // implement Command#getPlugin

    public SupervisorCommand() {
        this.plugin = IoC.getBean(Supervisor.class); // Initialize plugin in constructor
    }

    @CommandMethod("supervisor help") // declare command path
    @CommandDescription("Display Supervisor help") // declare command description
    @CommandPermission("supervisor.command.help") // permission to use command
    public void help(CommandSender sender) { // process command for Player and Server sender
        Stream.of(
                        "&7/supervisor help - display help",
                        "&7/supervisor version - display version"
                )
                .forEach(text -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text)));
    }


    @CommandMethod("supervisor version")
    @CommandDescription("Display Supervisor version")
    @CommandPermission("supervisor.command.version")
    public void version(CommandSender sender) {
        String text = String.format("&7%s - &6%s", plugin.getName(), plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }

    @CommandMethod("supervisor debug player <target> [message]") // declare command path and indicate that target is requirement, message is optional
    @CommandDescription("Debug player argument")
    @CommandPermission("supervisor.admin.debug")
    public void debugPlayer(CommandSender sender, @Argument("target") Player target /*apply target as player*/, @Argument("message") String message /*apply message as string*/) {
        target.sendMessage(message == null ? "Work fine!" : message);
    }

    @CommandMethod("supervisor debug location <location>")
    @CommandDescription("Debug player argument")
    @CommandPermission("supervisor.admin.debug")
    public void debugLocation(Player sender, @Argument("location") Location location) {
        sender.teleportAsync(location);
    }
}
```
* For more info visit [Incendo/cloud](https://github.com/Incendo/cloud)
* Start work