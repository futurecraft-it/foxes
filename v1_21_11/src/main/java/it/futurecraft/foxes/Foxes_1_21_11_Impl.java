package it.futurecraft.foxes;

import it.futurecraft.foxes.v1_21_11.entities.TamableFox;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper;
import it.futurecraft.foxes.v1_21_11.entities.TamableSlime;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fox.Fox;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class Foxes_1_21_11_Impl implements Foxes {
    public Foxes_1_21_11_Impl() {}

    @Override
    public void registerFox() {
        try {
            Field f = EntityType.FOX.getClass().getDeclaredField("cj");
            ReflectionHelper.set(f, EntityType.FOX, (EntityType.EntityFactory<@NotNull Fox>) TamableFox::new);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerSlime() {
        try {
            Field f = EntityType.SLIME.getClass().getDeclaredField("cj");
            ReflectionHelper.set(f, EntityType.SLIME, (EntityType.EntityFactory<@NotNull TamableSlime>) TamableSlime::new);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
