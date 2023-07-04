package dev.ckateptb.minecraft.supervisor.holder;

import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;

public interface CommandManagerHolder {
    void setCommandManager(PaperCommandManager<CommandSender> manager);

    PaperCommandManager<CommandSender> getCommandManager();
}
