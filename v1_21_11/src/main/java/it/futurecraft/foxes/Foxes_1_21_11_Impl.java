package it.futurecraft.foxes;

import it.futurecraft.foxes.entities.Tamable;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper;
import it.futurecraft.foxes.v1_21_11.entities.TamableFox;
import it.futurecraft.foxes.v1_21_11.entities.TamableSlime;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Foxes_1_21_11_Impl implements Foxes {
    public Foxes_1_21_11_Impl() {}

    @Override
    public Class<? extends Tamable> fox() {
        return TamableFox.class;
    }

    @Override
    public Class<? extends Tamable> slime() {
        return TamableSlime.class;
    }

    @Override
    public <T extends Tamable> boolean register(org.bukkit.entity.EntityType type, Class<T> clazz) {
        EntityType<?> nmsType = mapType(type);

        try {
            Field field = nmsType.getClass().getDeclaredField("cj");

            Constructor<T> constructor = clazz.getDeclaredConstructor(EntityType.class, Level.class);
            MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor);

            EntityType.EntityFactory<?> factory = (t, level) -> {
                try {
                    return (Entity) handle.invoke(t, level);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };

            ReflectionHelper.set(field, nmsType, factory);

            return true;
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private EntityType<?> mapType(org.bukkit.entity.EntityType type) {
        return switch (type) {
            case ALLAY -> EntityType.ALLAY;
            case ARMADILLO -> EntityType.ARMADILLO;
            case AXOLOTL -> EntityType.AXOLOTL;
            case BAT -> EntityType.BAT;
            case BEE -> EntityType.BEE;
            case BLAZE -> EntityType.BLAZE;
            case BOGGED -> EntityType.BOGGED;
            case BREEZE -> EntityType.BREEZE;
            case CAMEL -> EntityType.CAMEL;
            case CAMEL_HUSK -> EntityType.CAMEL_HUSK;
            case CAT -> EntityType.CAT;
            case CAVE_SPIDER -> EntityType.CAVE_SPIDER;
            case CHICKEN -> EntityType.CHICKEN;
            case COD -> EntityType.COD;
            case COPPER_GOLEM -> EntityType.COPPER_GOLEM;
            case COW -> EntityType.COW;
            case CREAKING -> EntityType.CREAKING;
            case CREEPER -> EntityType.CREEPER;
            case DOLPHIN -> EntityType.DOLPHIN;
            case DONKEY -> EntityType.DONKEY;
            case DROWNED -> EntityType.DROWNED;
            case ELDER_GUARDIAN -> EntityType.ELDER_GUARDIAN;
            case ENDER_DRAGON -> EntityType.ENDER_DRAGON;
            case ENDERMAN -> EntityType.ENDERMAN;
            case ENDERMITE -> EntityType.ENDERMITE;
            case EVOKER -> EntityType.EVOKER;
            case FOX -> EntityType.FOX;
            case FROG -> EntityType.FROG;
            case GHAST -> EntityType.GHAST;
            case GIANT -> EntityType.GIANT;
            case GLOW_SQUID -> EntityType.GLOW_SQUID;
            case GOAT -> EntityType.GOAT;
            case GUARDIAN -> EntityType.GUARDIAN;
            case HAPPY_GHAST -> EntityType.HAPPY_GHAST;
            case HOGLIN -> EntityType.HOGLIN;
            case HORSE -> EntityType.HORSE;
            case HUSK -> EntityType.HUSK;
            case ILLUSIONER -> EntityType.ILLUSIONER;
            case IRON_GOLEM -> EntityType.IRON_GOLEM;
            case LLAMA -> EntityType.LLAMA;
            case MAGMA_CUBE -> EntityType.MAGMA_CUBE;
            case MOOSHROOM -> EntityType.MOOSHROOM;
            case MULE -> EntityType.MULE;
            case NAUTILUS -> EntityType.NAUTILUS;
            case OCELOT -> EntityType.OCELOT;
            case PANDA -> EntityType.PANDA;
            case PARCHED -> EntityType.PARCHED;
            case PARROT -> EntityType.PARROT;
            case PHANTOM -> EntityType.PHANTOM;
            case PIG -> EntityType.PIG;
            case PIGLIN -> EntityType.PIGLIN;
            case PIGLIN_BRUTE -> EntityType.PIGLIN_BRUTE;
            case PILLAGER -> EntityType.PILLAGER;
            case PLAYER -> EntityType.PLAYER;
            case POLAR_BEAR -> EntityType.POLAR_BEAR;
            case PUFFERFISH -> EntityType.PUFFERFISH;
            case RABBIT -> EntityType.RABBIT;
            case RAVAGER -> EntityType.RAVAGER;
            case SALMON -> EntityType.SALMON;
            case SHEEP -> EntityType.SHEEP;
            case SHULKER -> EntityType.SHULKER;
            case SILVERFISH -> EntityType.SILVERFISH;
            case SKELETON -> EntityType.SKELETON;
            case SKELETON_HORSE -> EntityType.SKELETON_HORSE;
            case SLIME -> EntityType.SLIME;
            case SNIFFER -> EntityType.SNIFFER;
            case SNOW_GOLEM -> EntityType.SNOW_GOLEM;
            case SPIDER -> EntityType.SPIDER;
            case SQUID -> EntityType.SQUID;
            case STRAY -> EntityType.STRAY;
            case STRIDER -> EntityType.STRIDER;
            case TADPOLE -> EntityType.TADPOLE;
            case TRADER_LLAMA -> EntityType.TRADER_LLAMA;
            case TROPICAL_FISH -> EntityType.TROPICAL_FISH;
            case TURTLE -> EntityType.TURTLE;
            case VEX -> EntityType.VEX;
            case VILLAGER -> EntityType.VILLAGER;
            case VINDICATOR -> EntityType.VINDICATOR;
            case WANDERING_TRADER -> EntityType.WANDERING_TRADER;
            case WARDEN -> EntityType.WARDEN;
            case WITCH -> EntityType.WITCH;
            case WITHER -> EntityType.WITHER;
            case WITHER_SKELETON -> EntityType.WITHER_SKELETON;
            case WOLF -> EntityType.WOLF;
            case ZOGLIN -> EntityType.ZOGLIN;
            case ZOMBIE -> EntityType.ZOMBIE;
            case ZOMBIE_HORSE -> EntityType.ZOMBIE_HORSE;
            case ZOMBIE_NAUTILUS -> EntityType.ZOMBIE_NAUTILUS;
            case ZOMBIE_VILLAGER -> EntityType.ZOMBIE_VILLAGER;
            case ZOMBIFIED_PIGLIN -> EntityType.ZOMBIFIED_PIGLIN;

            default -> throw new IllegalArgumentException("Unsupported entity type: " + type);
        };
    }
}
