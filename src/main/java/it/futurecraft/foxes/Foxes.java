package it.futurecraft.foxes;

import it.futurecraft.foxes.events.FoxAddToWorldEvent;
import it.futurecraft.foxes.events.PlayerInteractFoxEvent;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.EntityAI;
import me.gamercoder215.mobchip.ai.controller.EntityController;
import me.gamercoder215.mobchip.ai.goal.Pathfinder;
import me.gamercoder215.mobchip.ai.goal.WrappedPathfinder;
import me.gamercoder215.mobchip.ai.navigation.EntityNavigation;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Foxes extends JavaPlugin {
    public final NamespacedKey FOX_OWNER_KEY = new NamespacedKey(this, "owner_uuid");
    public final NamespacedKey FOX_TAMED_KEY = new NamespacedKey(this, "tamed");
    public final NamespacedKey FOX_ORDERED_TO_SIT_KEY = new NamespacedKey(this, "ordered_to_sit");

    public final NamespacedKey TAME_ADVANCEMENT = NamespacedKey.minecraft("husbandry/tame_an_animal");

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        new PlayerInteractFoxEvent(this).register();
        new FoxAddToWorldEvent(this).register();
    }

    @Override
    public void onDisable() {
    }

    public @NotNull EntityAI getMobGoals(@NotNull Mob m) {
        EntityBrain brain = BukkitBrain.getBrain(m);
        return brain.getGoalAI();
    }

    public @NotNull EntityController getMobController(@NotNull Mob m) {
        EntityBrain brain = BukkitBrain.getBrain(m);
        return brain.getController();
    }

    public <T extends Pathfinder> Optional<T> getPathfinder(@NotNull Mob m, @NotNull Class<T> clazz) {
        EntityAI goals = getMobGoals(m);

        return goals.stream().map(WrappedPathfinder::getPathfinder)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst();
    }

    public EntityNavigation getMobNavigation(@NotNull Mob m) {
        EntityBrain brain = BukkitBrain.getBrain(m);
        return brain.createNavigation();
    }
}
