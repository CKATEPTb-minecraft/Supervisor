package dev.ckateptb.minecraft.supervisor;

import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface Command<P extends Plugin> {

    P getPlugin();

    default void parserRegistry(ParserRegistry<CommandSender> registry) {
    }

    default BukkitCommandManager<CommandSender> createCommandManager() throws Exception {
        return BukkitCommandManager.createNative(this.getPlugin(),
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build());
    }

    public interface PaperCommand<P extends Plugin> extends Command<P> {
        @Override
        default BukkitCommandManager<CommandSender> createCommandManager() throws Exception {
            return PaperCommandManager.createNative(this.getPlugin(),
                    AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build());
        }
    }
}
