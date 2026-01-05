package it.futurecraft.foxes.goals;

import it.futurecraft.foxes.TamableFox;
import me.gamercoder215.mobchip.ai.goal.CustomPathfinder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FoxSitWhenOrderedToGoal extends CustomPathfinder {
    private final TamableFox fox;

    private boolean orderedToSit;

    public FoxSitWhenOrderedToGoal(@NotNull TamableFox fox) {
        super(fox.getFox());

        this.fox = fox;
        this.orderedToSit = false;
    }

    public boolean isOrderedToSit() {
        return orderedToSit;
    }

    public void setOrderedToSit(boolean orderedToSit) {
        this.orderedToSit = orderedToSit;
    }

    @Override
    public @NotNull PathfinderFlag[] getFlags() {
        return new PathfinderFlag[]{ PathfinderFlag.MOVEMENT, PathfinderFlag.JUMPING };
    }

    @Override
    public boolean canStart() {
        if (!orderedToSit && !fox.isTame()) return false;

        if (fox.getFox().isInWater()) return false;
        if (!fox.getFox().isOnGround()) return false;

        Player p = fox.getOwner();
        if (p == null || !p.getWorld().equals(fox.getFox().getWorld())) return true;

        Location l = fox.getFox().getLocation();
        return l.distanceSquared(p.getLocation()) <= (double) 144.0F && p.getLastDamageCause() != null && orderedToSit;
    }

    @Override
    public boolean canContinueToUse() {
        return orderedToSit;
    }

    @Override
    public void start() {
        fox.getPlugin().getLogger().info("Starting FoxSitWhenOrderedToGoal");
        Bukkit.getScheduler().runTaskLater(fox.getPlugin(), () -> {
            fox.getNavigation().stop();
            fox.getFox().setSitting(true);
        }, 1L);
    }

    @Override
    public void stop() {
        fox.getPlugin().getLogger().info("Stopping FoxSitWhenOrderedToGoal");
        fox.getFox().setSitting(false);
    }

    @Override
    public void tick() {}
}
