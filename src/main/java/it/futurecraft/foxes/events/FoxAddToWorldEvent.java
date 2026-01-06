package it.futurecraft.foxes.events;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import it.futurecraft.foxes.Foxes;
import it.futurecraft.foxes.TamableFox;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.goal.WrappedPathfinder;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.event.EventHandler;

public class FoxAddToWorldEvent extends EventListener {
    public FoxAddToWorldEvent(Foxes plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(EntityAddToWorldEvent e) {
        Entity entity = e.getEntity();
        if (!TamableFox.isFox(entity)) return;

        TamableFox f = new TamableFox(plugin, (Fox) entity);
        f.registerGoals();

        EntityBrain brain = BukkitBrain.getBrain(f.getFox());
        for(WrappedPathfinder wp : brain.getTargetAI()) {
            plugin.getLogger().info("Found " + wp.getPathfinder().getInternalName());
        }
    }
}
