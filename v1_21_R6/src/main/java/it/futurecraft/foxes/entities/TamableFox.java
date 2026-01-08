package it.futurecraft.foxes.entities;

import it.futurecraft.foxes.goals.SitWhenOrderedToGoal;
import it.futurecraft.foxes.utils.CustomPersistentDataType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class TamableFox extends Fox implements Tamable, ComfortSeeker {
    protected static final NamespacedKey KEY_OWNER = new NamespacedKey("foxes", "owner");
    protected static final NamespacedKey KEY_TAME = new NamespacedKey("foxes", "tame");

    private static final Predicate<Entity> AVOID_PLAYER = (e) -> !e.isCrouching();

    private final PersistentDataContainer pdc;

    private boolean isOrderedToSit = false;

    public TamableFox(EntityType<? extends Fox> entitytype, Level world) {
        super(entitytype, world);

        CraftEntity e = getBukkitEntity();
        pdc = e.getPersistentDataContainer();
    }

    @Override
    protected void registerGoals() {

    }

    @Override
    public Optional<Player> owner() {
        UUID id = pdc.get(KEY_OWNER, CustomPersistentDataType.UUID);
        if (id == null) return Optional.empty();

        Player p = Bukkit.getPlayer(id);
        return Optional.ofNullable(p);
    }

    @Override
    public void owner(@Nullable Player p) {
        if (p == null) pdc.remove(KEY_OWNER);
        else pdc.set(KEY_OWNER, CustomPersistentDataType.UUID, p.getUniqueId());
    }

    @Override
    public boolean tame() {
        boolean t = Boolean.TRUE.equals(pdc.get(KEY_TAME, PersistentDataType.BOOLEAN));
        return t && owner().isPresent();
    }

    @Override
    public void tame(boolean t) {
        pdc.set(KEY_TAME, PersistentDataType.BOOLEAN, t);
        applyTamingSideEffects(t);
    }

    @Override
    public void applyTamingSideEffects(boolean t) {
        AttributeInstance a = getAttribute(Attributes.MAX_HEALTH);

        assert a != null;
        a.setBaseValue(t ? 40.0D : 10.0D);

        setHealth(getMaxHealth());
    }

    @Override
    public boolean orderedToSit() {
        return isOrderedToSit;
    }

    @Override
    public void orderedToSit(boolean o) {
        isOrderedToSit = o;
    }

    @Override
    public boolean sit() {
        return isSitting();
    }

    @Override
    public void sit(boolean s) {
        setSitting(s, true);
    }

    @Override
    public boolean lie() {
        return isSleeping();
    }

    @Override
    public void lie(boolean l) {
        setSleeping(l);
    }

    @Override
    public void tame(@NotNull Player p) {
        owner(p);
        tame(true);

        CraftPlayer cp = (CraftPlayer) p;
        ServerPlayer sp = cp.getHandle();

        CriteriaTriggers.TAME_ANIMAL.trigger(sp, this);
    }

    @Override
    public boolean shouldTryTeleportToOwner() {
        CraftPlayer p = (CraftPlayer) owner().get();
        return owner().isPresent() && distanceToSqr(p.getHandle()) >= (double) 144.0F;
    }

    @Override
    public void tryToTeleportToOwner() {
        if (owner().isPresent()) return;

        Location loc = owner().get().getLocation();
        teleportAroundBlockPos(loc);
    }

    private void teleportAroundBlockPos(@NotNull Location loc) {
        for (int i = 0; i < 10; ++i) {
            int j = random.nextIntBetweenInclusive(-3, 3);
            int k = random.nextIntBetweenInclusive(-3, 3);

            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = random.nextIntBetweenInclusive(-1, 1);

                if (maybeTeleportTo(loc.getBlockX() + j,  loc.getBlockY() + l, loc.getBlockZ() + k)) {
                    return;
                }
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if(!canTeleportTo(new BlockPos(x, y, z))) return false;

        EntityTeleportEvent e = CraftEventFactory.callEntityTeleportEvent(this, x + .5, y, z + .5);
        if (e.isCancelled() || e.getTo() == null) return false;

        Location to = e.getTo();
        snapTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
        navigation.stop();

        return true;
    }

    private boolean canTeleportTo(BlockPos bp0) {
        PathType type = WalkNodeEvaluator.getPathTypeStatic(this, bp0);

        if (type != PathType.WALKABLE) return false;

        BlockState bs = level().getBlockState(bp0);
        if (bs.getBlock() instanceof LeavesBlock) return false;

        BlockPos bp1 = bp0.subtract(blockPosition());
        return level().noCollision(this, getBoundingBox().move(bp1));
    }

    @Override
    public boolean canMoveToOwner() {
        return false;
    }

    @Override
    public boolean comfortable(BlockData bd) {
        CraftBlockData cbd = (CraftBlockData) bd;
        BlockState bs = cbd.getState();

        return bs.is(Blocks.FURNACE) ? bs.getValue(FurnaceBlock.LIT) :
                bs.is(BlockTags.BEDS, s -> s.getOptionalValue(BedBlock.PART).map(p -> p != BedPart.HEAD).orElse(true));
    }

    @Override
    public boolean wantsToAttack(@NotNull LivingEntity target, @NotNull LivingEntity owner) {
        return true;
    }
}
