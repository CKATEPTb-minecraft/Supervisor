package dev.ckateptb.minecraft.supervisor;

import org.bukkit.plugin.Plugin;

public interface Command<P extends Plugin> {

    P getPlugin();
}
