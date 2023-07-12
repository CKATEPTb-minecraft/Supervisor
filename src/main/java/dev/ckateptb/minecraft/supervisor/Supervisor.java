package dev.ckateptb.minecraft.supervisor;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.common.tableclothcontainer.event.ComponentRegisterEvent;
import dev.ckateptb.common.tableclothevent.EventBus;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Supervisor extends JavaPlugin {
    @Getter
    private final Map<Plugin, Set<Runnable>> executeOnEnable = new ConcurrentHashMap<>();

    public Supervisor() {
        IoC.registerBean(this, Supervisor.class);
        IoC.scan(Supervisor.class);
        EventBus.GLOBAL.registerEventHandler(ComponentRegisterEvent.class, event -> {
            Object instance = event.getInstance();
            if (instance instanceof Command<? extends Plugin> command) {
                Plugin plugin = command.getPlugin();
                executeOnEnable.computeIfAbsent(plugin, key -> new HashSet<>()).add(() -> {
                    try {
                        BukkitCommandManager<CommandSender> manager = command.createCommandManager();
                        ParserRegistry<CommandSender> registry = manager.parserRegistry();
                        command.parserRegistry(registry);
                        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
                            manager.registerBrigadier();
                        }
                        if (manager instanceof PaperCommandManager<CommandSender> paperManager && manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                            paperManager.registerAsynchronousCompletions();
                        }
                        Function<ParserParameters, CommandMeta> noDescription = sender -> SimpleCommandMeta.builder()
                                .with(CommandMeta.DESCRIPTION, "No description")
                                .build();
                        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class, noDescription);
                        new MinecraftExceptionHandler<CommandSender>()
                                .withInvalidSyntaxHandler()
                                .withInvalidSenderHandler()
                                .withNoPermissionHandler()
                                .withArgumentParsingHandler()
                                .apply(manager, (sender) -> sender);
                        annotationParser.parse(command);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            }
        });
    }
}