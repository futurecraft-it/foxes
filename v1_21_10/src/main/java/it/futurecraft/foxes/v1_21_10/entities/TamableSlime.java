package it.futurecraft.foxes.v1_21_10.entities;

import it.futurecraft.foxes.Foxes;
import it.futurecraft.foxes.entities.Tamable;
import it.futurecraft.foxes.utils.CustomPersistentDataType;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper;
import it.futurecraft.foxes.v1_21_10.goals.FollowOwnerGoal;
import it.futurecraft.foxes.v1_21_10.goals.SitWhenOrderedToGoal;
import it.futurecraft.foxes.v1_21_10.goals.target.NonTameRandomTargetGoal;
import it.futurecraft.foxes.v1_21_10.goals.target.OwnerHurtByTargetGoal;
import it.futurecraft.foxes.v1_21_10.goals.target.OwnerHurtTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class TamableSlime extends Slime implements Tamable {
    private static final Supplier<ItemStack> EMPTY_BOTTLE = () -> new ItemStack(Items.GLASS_BOTTLE);

    private final PersistentDataContainer pdc;

    public TamableSlime(@NotNull EntityType<? extends Slime> type, Level level) {
        super(type, level);

        pdc = getBukkitEntity().getPersistentDataContainer();
    }

    @Override
    protected void registerGoals() {
        Goal floatGoal = (Goal) ReflectionHelper.newInstance(
                Slime.class,
                "SlimeFloatGoal",
                this,
                new ReflectionHelper.Argument<>(Slime.class, this)
        );
        this.goalSelector.addGoal(1, floatGoal);

        Goal attackGoal = (Goal) ReflectionHelper.newInstance(
                Slime.class,
                "SlimeAttackGoal",
                this,
                new ReflectionHelper.Argument<>(Slime.class, this)
        );
        this.goalSelector.addGoal(2, attackGoal);

        Goal randomGoal = (Goal) ReflectionHelper.newInstance(
                Slime.class,
                "SlimeRandomDirectionGoal",
                this,
                new ReflectionHelper.Argument<>(Slime.class, this)
        );
        this.goalSelector.addGoal(3, randomGoal);

        Goal jumpingGoal = (Goal) ReflectionHelper.newInstance(
                Slime.class,
                "SlimeKeepOnJumpingGoal",
                this,
                new ReflectionHelper.Argument<>(Slime.class, this)
        );
        this.goalSelector.addGoal(5, jumpingGoal);

        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1d, 10f, 2f));

        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(
                this,
                net.minecraft.world.entity.player.Player.class,
                true,
                (entity, level) -> Math.abs(entity.getY() - this.getY()) <= (double) 4.0F
        ));
        this.targetSelector.addGoal(3, new NonTameRandomTargetGoal<>(
                this,
                IronGolem.class,
                true,
                null
        ));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    public Optional<Player> owner() {
        UUID id = pdc.get(Tamable.KEY_OWNER, CustomPersistentDataType.UUID);
        if (id == null) return Optional.empty();

        return Optional.ofNullable(Bukkit.getPlayer(id));
    }

    @Override
    public void owner(@Nullable Player p) {
        if (p == null) pdc.remove(Tamable.KEY_OWNER);
        else pdc.set(Tamable.KEY_OWNER, CustomPersistentDataType.UUID, p.getUniqueId());
    }

    @Override
    public boolean tame() {
        return Boolean.TRUE.equals(pdc.get(Tamable.KEY_TAME, PersistentDataType.BOOLEAN)) && owner().isPresent();
    }

    @Override
    public void tame(boolean t) {
        pdc.set(Tamable.KEY_TAME, PersistentDataType.BOOLEAN, t);
        applyTamingSideEffects(t);
    }

    @Override
    public boolean orderedToSit() {
        return Boolean.TRUE.equals(pdc.get(Tamable.KEY_SIT, PersistentDataType.BOOLEAN));
    }

    @Override
    public void orderedToSit(boolean o) {
        pdc.set(Tamable.KEY_SIT, PersistentDataType.BOOLEAN, o);
    }

    @Override
    public boolean sit() {
        return true;
    }

    @Override
    public void sit(boolean s) {

    }

    @Override
    public boolean lie() {
        return false;
    }

    @Override
    public void lie(boolean l) {

    }

    @Override
    public boolean ownedBy(@NotNull Player p) {
        Optional<Player> o = owner();
        return o.isPresent() && o.get().equals(p);
    }

    @Override
    public void tame(@NotNull Player p) {
        owner(p);
        tame(true);

//        CraftPlayer cp = (CraftPlayer) p;
//        CriteriaTriggers.TAME_ANIMAL.trigger(cp.getHandle(), this);
    }

    @Override
    public void applyTamingSideEffects(boolean t) {

    }

    @Override
    public boolean shouldTryTeleportToOwner() {
        CraftPlayer p = (CraftPlayer) owner().get();
        return owner().isPresent() && distanceToSqr(p.getHandle()) >= (double) 144.0F;
    }

    @Override
    public void tryToTeleportToOwner() {
        if (owner().isEmpty()) return;

        Location loc = owner().get().getLocation();
        teleportAroundBlockPos(loc);
    }

    private void teleportAroundBlockPos(@NotNull Location loc) {
        for (int i = 0; i < 10; ++i) {
            int j = random.nextIntBetweenInclusive(-3, 3);
            int k = random.nextIntBetweenInclusive(-3, 3);

            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = random.nextIntBetweenInclusive(-1, 1);

                if (maybeTeleportTo(loc.getBlockX() + j, loc.getBlockY() + l, loc.getBlockZ() + k)) {
                    return;
                }
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!canTeleportTo(new BlockPos(x, y, z))) return false;

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
        return !orderedToSit() && !isPassenger() && !mayBeLeashed() && owner().isPresent() && owner().get().getGameMode() != GameMode.SPECTATOR;
    }

    @Override
    protected InteractionResult mobInteract(net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (tame()) {
            if (itemstack.is(Items.HONEY_BOTTLE)) {
                if (getHealth() < getMaxHealth()) {
                    itemstack.consume(1, player);
                    if (!player.isCreative()) player.addItem(EMPTY_BOTTLE.get());

                    FoodProperties props = item.components().get(DataComponents.FOOD);
                    int nutrition = props == null ? 1 : props.nutrition();

                    heal(2.f * nutrition, EntityRegainHealthEvent.RegainReason.EATING);

                    return InteractionResult.SUCCESS;
                }

                if (getHealth() == getMaxHealth()) {
                    // TODO: If tamed and on max health, feeding will make them bigger.
                    return InteractionResult.SUCCESS;
                }
            }

            InteractionResult res = super.mobInteract(player, hand);
            Player p = (Player) player.getBukkitEntity();

            if (!res.consumesAction() && ownedBy(p) && hand == InteractionHand.MAIN_HAND) {
                orderedToSit(!orderedToSit());

                System.out.println("Set sitting to " + orderedToSit());

                jumping = false;
                navigation.stop();

                setTarget(null, EntityTargetEvent.TargetReason.FORGOT_TARGET);

                return InteractionResult.SUCCESS.withoutItem();
            }
            return res;
        }

        if (!level().isClientSide() && itemstack.is(Items.HONEY_BOTTLE)) {
            itemstack.consume(1, player);
            if (!player.isCreative()) player.addItem(EMPTY_BOTTLE.get());

            tryToTame(player);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void tryToTame(net.minecraft.world.entity.player.Player player) {
        Vec3 pos = position();
        Location loc = new Location(level().getWorld(), pos.x, pos.y, pos.z);

        if (random.nextInt(4) == 0 && !CraftEventFactory.callEntityTameEvent(this, player).isCancelled()) {
            tame((Player) player.getBukkitEntity());

            navigation.stop();

            setTarget(null);
            orderedToSit(true);

            level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);

            Bukkit.getScheduler().runTask(Foxes.plugin(), () -> Particle.HEART
                    .builder()
                    .location(loc)
                    .offset(.5d, .5d, .5d)
                    .count(14)
                    .extra(0)
                    .spawn()
            );
        } else {
            level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);

            Bukkit.getScheduler().runTask(Foxes.plugin(), () -> Particle.SMOKE
                    .builder()
                    .location(loc)
                    .offset(.5d, .5d, .5d)
                    .count(14)
                    .extra(0)
                    .spawn()
            );
        }
    }

    @Override
    public void die(DamageSource source) {
        Level level = level();

        if (level instanceof ServerLevel server) {
            if (server.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                Optional<Player> p = owner();
                p.ifPresent(player -> {
                    ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                    serverPlayer.sendSystemMessage(getCombatTracker().getDeathMessage());
                });
            }
        }

        super.die(source);
    }

    @Override
    protected void dealDamage(net.minecraft.world.entity.LivingEntity livingEntity) {
        Level var3 = this.level();
        if (var3 instanceof ServerLevel serverLevel) {
            if (this.isAlive() && this.isWithinMeleeAttackRange(livingEntity) && this.hasLineOfSight(livingEntity)) {
                if (tame() && getTarget() == null) return;

                DamageSource damageSource = this.damageSources().mobAttack(this);
                if (livingEntity.hurtServer(serverLevel, damageSource, this.getAttackDamage())) {
                    this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (super.random.nextFloat() - super.random.nextFloat()) * 0.2F + 1.0F);
                    EnchantmentHelper.doPostAttackEffects(serverLevel, livingEntity, damageSource);
                }
            }
        }
    }

    @Override
    public boolean wantsToAttack(@NotNull LivingEntity target, @NotNull LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) {
            return false;
        } else {
            net.minecraft.world.entity.LivingEntity t = ((CraftLivingEntity) owner).getHandle();
            net.minecraft.world.entity.LivingEntity o = ((CraftLivingEntity) owner).getHandle();
            return t instanceof Wolf wolf
                    ? !wolf.isTame() || wolf.getOwner() != o
                    : t instanceof TamableFox fox ?
                    !fox.tame() || fox.ownedBy((Player) o)
                    : t instanceof TamableSlime slime ?
                    !slime.tame() || slime.ownedBy((Player) o)
                    :
                    !(t instanceof net.minecraft.world.entity.player.Player player && o instanceof net.minecraft.world.entity.player.Player player1 && !player1.canHarmPlayer(player))
                            && !(t instanceof AbstractHorse abstractHorse && abstractHorse.isTamed())
                            && !(t instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame());
        }
    }
}
