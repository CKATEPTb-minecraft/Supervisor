package dev.ckateptb.minecraft.supervisor;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
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
        IoC.scan(Supervisor.class);
        IoC.registerBean(this);
        final var commandCoordinator = AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();
        EventBus.GLOBAL.registerEventHandler(ComponentRegisterEvent.class, event -> {
            Object instance = event.getInstance();
            if (instance instanceof Command<? extends Plugin> command) {
                Plugin plugin = command.getPlugin();
                executeOnEnable.computeIfAbsent(plugin, key -> new HashSet<>()).add(() -> {
                    try {
                        PaperCommandManager<CommandSender> manager = PaperCommandManager.createNative(plugin, commandCoordinator);
                        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
                            manager.registerBrigadier();
                        }
                        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                            manager.registerAsynchronousCompletions();
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