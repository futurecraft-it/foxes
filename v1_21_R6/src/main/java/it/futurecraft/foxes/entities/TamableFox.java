package it.futurecraft.foxes.entities;

import it.futurecraft.foxes.Foxes;
import it.futurecraft.foxes.goals.*;
import it.futurecraft.foxes.goals.FollowOwnerGoal;
import it.futurecraft.foxes.goals.SitWhenOrderedToGoal;
import it.futurecraft.foxes.goals.target.NonTameRandomTargetGoal;
import it.futurecraft.foxes.goals.target.OwnerHurtByTargetGoal;
import it.futurecraft.foxes.goals.target.OwnerHurtTargetGoal;
import it.futurecraft.foxes.utils.CustomPersistentDataType;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper.Argument;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class TamableFox extends Fox implements Tamable, ComfortSeeker {
    protected static final NamespacedKey KEY_OWNER = new NamespacedKey("foxes", "owner");
    protected static final NamespacedKey KEY_TAME = new NamespacedKey("foxes", "tame");
    protected static final NamespacedKey KEY_SIT = new NamespacedKey("foxes", "sit");

    private static final Predicate<Entity> AVOID_PLAYER = (e) -> !e.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e);
    private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = (entity) -> {
        boolean var10000;
        if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
            if (livingEntity.getLastHurtMob() != null && livingEntity.getLastHurtMobTimestamp() < livingEntity.tickCount + 600) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    };

    private final PersistentDataContainer pdc;

    public TamableFox(EntityType<? extends Fox> entitytype, Level world) {
        super(entitytype, world);

        CraftEntity e = getBukkitEntity();
        pdc = e.getPersistentDataContainer();
    }

    @Override
    protected void registerGoals() {
        try {
            Field land = getClass().getSuperclass().getDeclaredField("cP");
            ReflectionHelper.set(land, this, new NonTameRandomTargetGoal<>(this, Animal.class, false, (e, w) -> e instanceof Chicken || e instanceof Rabbit));

            Field turtle = getClass().getSuperclass().getDeclaredField("cS");
            ReflectionHelper.set(turtle, this, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));

            Field fish = getClass().getSuperclass().getDeclaredField("cT");
            ReflectionHelper.set(fish, this, new NonTameRandomTargetGoal<>(this, AbstractFish.class, false, (e, w) -> e instanceof AbstractFish));

            Goal floatGoal = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxFloatGoal", this);
            goalSelector.addGoal(0, floatGoal);
            goalSelector.addGoal(0, new ClimbOnTopOfPowderSnowGoal(this, level()));

            Goal faceplant = (Goal) ReflectionHelper.newInstance(Fox.class, "FaceplantGoal", this);
            goalSelector.addGoal(1, faceplant);
            goalSelector.addGoal(2, new TamablePanicGoal(this, 2.2d, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
            goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));

            Goal breed = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxBreedGoal", this, new Argument<>(double.class, 1.0d));
            goalSelector.addGoal(3, breed);
            goalSelector.addGoal(4,
                    new AvoidEntityGoal<>(
                            this,
                            net.minecraft.world.entity.player.Player.class,
                            16.0f,
                            1.6d,
                            1.4d,
                            e -> AVOID_PLAYER.test(e) && !isDefending() && !tame()
                    )
            );
            goalSelector.addGoal(4,
                    new AvoidEntityGoal<>(
                            this,
                            Wolf.class,
                            8.0f,
                            1.6d,
                            1.4d,
                            e -> !((Wolf) e).isTame() && !isDefending() && !tame()
                    )
            );
            goalSelector.addGoal(4,
                    new AvoidEntityGoal<>(
                            this,
                            PolarBear.class,
                            8.0f,
                            1.6d,
                            1.4d,
                            e -> !isDefending()
                    )
            );

            Goal stalk = (Goal) ReflectionHelper.newInstance(Fox.class, "StalkPreyGoal", this);
            goalSelector.addGoal(5, stalk);
            goalSelector.addGoal(6, new Fox.FoxPounceGoal());

            Goal seekShelter = (Goal) ReflectionHelper.newInstance(Fox.class, "SeekShelterGoal", this, new Argument<>(double.class, 1.25d));
            goalSelector.addGoal(6, seekShelter);
            goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0d, 10.0f, 2.0f));

            Goal meleeAttack = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxMeleeAttackGoal", this, new Argument<>(double.class, 1.2d), new Argument<>(boolean.class, true));
            goalSelector.addGoal(7, meleeAttack);

            Goal sleep = (Goal) ReflectionHelper.newInstance(Fox.class, "SleepGoal", this);
            goalSelector.addGoal(7, sleep);
            goalSelector.addGoal(7, new LieOnBlockGoal(this, 1.1d, 10));
            // goalSelector.addGoal(7, new SitOnBlockGoal(this, 0.8d, 10)); -> nah

            Goal followParent = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxFollowParentGoal", this, new Argument<>(Fox.class, this), new Argument<>(double.class, 1.25d));
            goalSelector.addGoal(8, followParent);

            Goal strollThroughVillage = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxStrollThroughVillageGoal", this, new Argument<>(int.class, 32), new Argument<>(int.class, 200));
            goalSelector.addGoal(9, strollThroughVillage);

            Goal eatBerries = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxEatBerriesGoal", this, new Argument<>(double.class, 1.2d), new Argument<>(int.class, 12), new Argument<>(int.class, 1));
            goalSelector.addGoal(10, eatBerries);
            goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4f));
            goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0d));

            Goal searchForItem = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxSearchForItemsGoal", this);
            goalSelector.addGoal(11, searchForItem);

            Goal lookAtPlayer = (Goal) ReflectionHelper.newInstance(Fox.class, "FoxLookAtPlayerGoal", this, new Argument<>(Mob.class, this), new Argument<>(Class.class, Player.class), new Argument<>(float.class, 24.0f));
            goalSelector.addGoal(12, lookAtPlayer);

            Goal perchAndSearch = (Goal) ReflectionHelper.newInstance(Fox.class, "PerchAndSearchGoal", this);
            goalSelector.addGoal(13, perchAndSearch);

            targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
            targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));

            Goal defendTrusted = (Goal) ReflectionHelper.newInstance(
                    Fox.class,
                    "DefendTrustedTargetGoal",
                    this,
                    new Argument<>(Class.class, net.minecraft.world.entity.LivingEntity.class),
                    new Argument<>(boolean.class, false),
                    new Argument<>(boolean.class, false),
                    new Argument<>(TargetingConditions.Selector.class, (e, w) -> TRUSTED_TARGET_SELECTOR.test(e))
            );
            targetSelector.addGoal(3, defendTrusted);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
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
        return Boolean.TRUE.equals(pdc.get(KEY_SIT, PersistentDataType.BOOLEAN));
    }

    @Override
    public void orderedToSit(boolean o) {
        pdc.set(KEY_SIT, PersistentDataType.BOOLEAN, o);
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
    public boolean ownedBy(@NotNull Player p) {
        return owner().isPresent() && owner().get() == p;
    }

    @Override
    public void tame(@NotNull Player p) {
        owner(p);
        tame(true);

        CraftPlayer cp = (CraftPlayer) p;
        ServerPlayer sp = cp.getHandle();

        CriteriaTriggers.TAME_ANIMAL.trigger(sp, this);
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
            Bukkit.getScheduler().runTask(Foxes.plugin(), () -> {
                Particle.HEART.builder()
                        .location(loc)
                        .offset(.5d, .5d, .5d)
                        .count(14)
                        .extra(0)
                        .spawn();
            });
        } else {
            level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
            Bukkit.getScheduler().runTask(Foxes.plugin(), () -> {
                Particle.SMOKE.builder()
                        .location(loc)
                        .offset(.5d, .5d, .5d)
                        .count(14)
                        .extra(0)
                        .spawn();
            });
        }
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
    public boolean comfortable(BlockData bd) {
        CraftBlockData cbd = (CraftBlockData) bd;
        BlockState bs = cbd.getState();

        return bs.is(Blocks.FURNACE) ? (Boolean) bs.getValue(AbstractFurnaceBlock.LIT) :
                bs.is(BlockTags.BEDS, s -> s.getOptionalValue(BedBlock.PART).map(p -> p != BedPart.HEAD).orElse(true));
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
                ! fox.tame() || fox.ownedBy((Player) o)
                :
                !(t instanceof net.minecraft.world.entity.player.Player player && o instanceof net.minecraft.world.entity.player.Player player1 && !player1.canHarmPlayer(player))
                && !(t instanceof AbstractHorse abstractHorse && abstractHorse.isTamed())
                && !(t instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame());
        }
    }

    @Override
    public InteractionResult mobInteract(net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (item instanceof SpawnEggItem) return super.mobInteract(player, hand);

        if (tame()) {
            if (isFood(itemstack) && getHealth() < getMaxHealth()) {
                itemstack.consume(1, player);

                FoodProperties food = item.components().get(DataComponents.FOOD);
                int nutrition = food == null ? 1 : food.nutrition();
                heal(2.0f * nutrition, EntityRegainHealthEvent.RegainReason.EATING);
                return InteractionResult.SUCCESS;
            }

            InteractionResult result = super.mobInteract(player, hand);
            Player p = (Player) player.getBukkitEntity();

            if (!result.consumesAction() && ownedBy(p) && hand == InteractionHand.MAIN_HAND) {
                orderedToSit(!orderedToSit());

                jumping = false;
                navigation.stop();
                setTarget(null, EntityTargetEvent.TargetReason.FORGOT_TARGET);

                return InteractionResult.SUCCESS.withoutItem();
            }

            return result;
        }

        if (!level().isClientSide() && itemstack.is(Items.CHICKEN)) {
            itemstack.consume(1, player);

            tryToTame(player);
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.mobInteract(player, hand);
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
}
