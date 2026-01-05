package it.futurecraft.foxes.events;

import it.futurecraft.foxes.Foxes;
import org.bukkit.event.Listener;

public abstract class EventListener implements Listener {
    protected final Foxes plugin;

    public EventListener(Foxes plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
