package it.futurecraft.foxes;

import it.futurecraft.foxes.entities.TamableFox;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Fox;

import java.lang.reflect.Field;

public class Foxes_1_21_R6_Impl implements Foxes {
    public Foxes_1_21_R6_Impl() {}

    @Override
    public void registerFox() {
        try {
            Field f = EntityType.FOX.getClass().getDeclaredField("cf");
            ReflectionHelper.set(f, EntityType.FOX, (EntityType.EntityFactory<Fox>) TamableFox::new);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
