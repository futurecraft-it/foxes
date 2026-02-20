package it.futurecraft.foxes;

import it.futurecraft.foxes.v1_21_10.entities.TamableFox;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper;
import it.futurecraft.foxes.v1_21_10.entities.TamableSlime;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.monster.Slime;

import java.lang.reflect.Field;

public class Foxes_1_21_10_Impl implements Foxes {
    public Foxes_1_21_10_Impl() {}

    @Override
    public void registerFox() {
        try {
            Field f = EntityType.FOX.getClass().getDeclaredField("cf");
            ReflectionHelper.set(f, EntityType.FOX, (EntityType.EntityFactory<Fox>) TamableFox::new);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerSlime() {
        try {
            Field f = EntityType.SLIME.getClass().getDeclaredField("cf");
            ReflectionHelper.set(f, EntityType.SLIME, (EntityType.EntityFactory<Slime>) TamableSlime::new);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
