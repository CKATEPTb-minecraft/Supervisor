package dev.ckateptb.minecraft.supervisor.handler;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.supervisor.Supervisor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Set;

@Component
public class PluginEnableListener implements Listener {
    private final Supervisor plugin;

    public PluginEnableListener() {
        this.plugin = IoC.getBean(Supervisor.class);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PluginEnableEvent event) {
        Plugin eventPlugin = event.getPlugin();
        Map<Plugin, Set<Runnable>> executeOnEnable = plugin.getExecuteOnEnable();
        if (executeOnEnable.containsKey(eventPlugin)) {
            Set<Runnable> runnables = executeOnEnable.get(eventPlugin);
            runnables.removeIf(runnable -> {
                try {
                    runnable.run();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return true;
            });
            if (runnables.size() == 0) {
                executeOnEnable.remove(eventPlugin);
            }
        }
    }
}
