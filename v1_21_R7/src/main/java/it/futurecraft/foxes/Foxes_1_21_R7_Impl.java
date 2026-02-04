package it.futurecraft.foxes;

import it.futurecraft.foxes.entities.TamableFox;
import it.futurecraft.foxes.utils.reflection.ReflectionHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fox.Fox;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class Foxes_1_21_R7_Impl implements Foxes {
    public Foxes_1_21_R7_Impl() {}

    @Override
    public void registerFox() {
        try {
            Field f = EntityType.FOX.getClass().getDeclaredField("cj");
            ReflectionHelper.set(f, EntityType.FOX, (EntityType.EntityFactory<@NotNull Fox>) TamableFox::new);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
