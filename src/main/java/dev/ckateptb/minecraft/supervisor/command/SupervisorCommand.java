package dev.ckateptb.minecraft.supervisor.command;

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

@Getter
@Component
public class SupervisorCommand implements Command<Supervisor> {
    protected final Supervisor plugin;

    public SupervisorCommand() {
        this.plugin = IoC.getBean(Supervisor.class);
    }

    @CommandMethod("supervisor help")
    @CommandDescription("Display Supervisor help")
    @CommandPermission("supervisor.command.help")
    public void help(CommandSender sender) {
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

    @CommandMethod("supervisor debug player <target> [message]")
    @CommandDescription("Debug player argument")
    @CommandPermission("supervisor.admin.debug")
    public void debugPlayer(CommandSender sender, @Argument("target") Player target, @Argument("message") String message) {
        target.sendMessage(message == null ? "Work fine!" : message);
    }

    @CommandMethod("supervisor debug location <location>")
    @CommandDescription("Debug player argument")
    @CommandPermission("supervisor.admin.debug")
    public void debugLocation(Player sender, @Argument("location") Location location) {
        sender.teleportAsync(location);
    }
}
