package it.futurecraft.foxes;

import it.futurecraft.foxes.goals.FoxFollowOwnerGoal;
import it.futurecraft.foxes.goals.FoxSitWhenOrderedToGoal;
import it.futurecraft.foxes.utils.UUIDDataType;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.EntityAI;
import me.gamercoder215.mobchip.ai.controller.EntityController;
import me.gamercoder215.mobchip.ai.goal.WrappedPathfinder;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class TamableFox {
    private final Foxes plugin;

    private final Random rand;

    private final Fox fox;
    private final PersistentDataContainer container;

    private final EntityController navigation;
    private final EntityAI goalSelector;
    private final EntityAI targetSelector;

    public TamableFox(@NotNull Foxes plugin, @NotNull Fox fox) {
        this.plugin = plugin;

        this.rand = new Random();

        this.fox = fox;
        this.container = fox.getPersistentDataContainer();

        EntityBrain brain = BukkitBrain.getBrain(fox);
        this.navigation = brain.getController();
        this.goalSelector = brain.getGoalAI();
        this.targetSelector = brain.getTargetAI();

        setTame(isTame());
        plugin.getLogger().info("is tamed" + isTame());
    }

    public static boolean isFox(@NotNull Entity m) {
        return m instanceof Fox;
    }

    public void registerGoals() {
        this.goalSelector.put(new FoxSitWhenOrderedToGoal(this), 2);
        // this.goalSelector.put(new FoxSleepWithOwnerGoal(this), 2);

        FoxFollowOwnerGoal follow = new FoxFollowOwnerGoal(this, 10D, 2F, 1.3D);
        this.goalSelector.put(follow, 6);
    }

    public Foxes getPlugin() {
        return plugin;
    }

    public Location getLocation() {
        return fox.getLocation();
    }

    public World getWorld() {
        return fox.getWorld();
    }

    public Random getRand() {
        return rand;
    }

    public EntityController getNavigation() {
        return navigation;
    }

    public Fox getFox() {
        return fox;
    }

    public void setOwner(@Nullable Player p) {
        if (p == null) container.remove(plugin.FOX_OWNER_KEY);
        else container.set(plugin.FOX_OWNER_KEY, UUIDDataType.INSTANCE, p.getUniqueId());
    }

    @Nullable
    public Player getOwner() {
        UUID id = container.get(plugin.FOX_OWNER_KEY, UUIDDataType.INSTANCE);
        return id == null ? null : plugin.getServer().getPlayer(id);
    }

    public boolean isTame() {
        boolean tamed = Boolean.TRUE.equals(container.get(plugin.FOX_TAMED_KEY, PersistentDataType.BOOLEAN));
        return tamed && getOwner() != null;
    }

    public void setTame(boolean t) {
        container.set(plugin.FOX_TAMED_KEY, PersistentDataType.BOOLEAN, t);
        this.removeNonTameGoals(t);
        this.applyTamingSideEffect(t);
    }

    public boolean isOrderedToSit() {
        Optional<FoxSitWhenOrderedToGoal> sit = plugin.getPathfinder(fox, FoxSitWhenOrderedToGoal.class);
        return sit.isPresent() && sit.get().isOrderedToSit();
    }

    public void setOrderedToSit(boolean s) {
        Optional<FoxSitWhenOrderedToGoal> sit = plugin.getPathfinder(fox, FoxSitWhenOrderedToGoal.class);
        sit.ifPresent(f -> f.setOrderedToSit(s));
    }

    private void applyTamingSideEffect(boolean s) {
        AttributeInstance health = fox.getAttribute(Attribute.MAX_HEALTH);

        assert health != null;
        health.setBaseValue(health.getDefaultValue() * 4);
        fox.setHealth(health.getValue());
    }

    private void removeNonTameGoals(boolean s) {
        if (!s) return;

        plugin.getLogger().info("Removing non-taming goals from fox...");

        for (WrappedPathfinder wp : targetSelector) {
            plugin.getLogger().info("Removing " + wp.getPathfinder().getInternalName());
            targetSelector.remove(wp.getPathfinder());
        }
    }

    public void tame(@NotNull Player p) {
        setTame(true);
        setOwner(p);

        Advancement advancement = plugin.getServer().getAdvancement(plugin.TAME_ADVANCEMENT);
        assert advancement != null;

        AdvancementProgress progress = p.getAdvancementProgress(advancement);
        Collection<String> criteria = progress.getRemainingCriteria();
        for (String name : criteria) {
            progress.awardCriteria(name);
        }

        if (fox.getFirstTrustedPlayer() == null) {
            fox.setFirstTrustedPlayer(p);
        } else {
            fox.setSecondTrustedPlayer(p);
        }
    }

    public boolean canTeleportTo(Location loc) {
        return true;
    }

    public boolean shouldTryTeleportToOwner() {
        Location loc = this.getLocation();
        return this.getOwner() != null && loc.distanceSquared(this.getOwner().getLocation()) >= (double) 144.0F;
    }

    public void tryToTeleportOwner() {
        if (this.getOwner() == null) return;

        Location loc = this.getOwner().getLocation();
        this.teleportToAroundBlockPos(loc);
    }

    private void teleportToAroundBlockPos(@NotNull Location loc) {
        for (int i = 0; i < 10; ++i) {
            int j = this.nextIntBetweenInclusive(-3, 3);
            int k = this.nextIntBetweenInclusive(-3, 3);

            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = this.nextIntBetweenInclusive(-1, 1);

                if (this.maybeTeleportTo(loc.getBlockX() + j, loc.getBlockY() + l, loc.getBlockZ() + k)) {
                    return;
                }
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        Player p = this.getOwner();
        assert p != null;

        Location to = new Location(p.getWorld(), x, y, z);
        if (!this.canTeleportTo(to)) return false;

        EntityTeleportEvent event = new EntityTeleportEvent(fox, this.getLocation(), to);
        if (!event.callEvent()) return false;

        fox.teleport(to);
        this.navigation.stop();

        return true;
    }

    private int nextIntBetweenInclusive(int i, int j) {
        return this.rand.nextInt(j - i + 1) + i;
    }
}
