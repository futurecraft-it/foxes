package it.futurecraft.foxes;

import it.futurecraft.foxes.entities.Tamable;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public interface Foxes {
    class _SingletonHelper {
        private static JavaPlugin INSTANCE;

        public static void plugin(JavaPlugin plugin) {
            INSTANCE = plugin;
        }
    }

    static JavaPlugin plugin() {
        return Foxes._SingletonHelper.INSTANCE;
    }

    /**
     * Gets the class of the tamable fox entity.
     * @return the class of the tamable fox entity.
     */
    Class<? extends Tamable> fox();

    /**
     * Gets the class of the tamable slime entity.
     * @return the class of the tamable slime entity.
     */
    Class<? extends Tamable> slime();

    /**
     * Registers a new tamable entity type.
     * @param type The entity type to register.
     * @param clazz The class of the tamable entity.
     * @return true if the entity type was successfully registered, throws otherwise.
     * @param <T> The type of the tamable entity.
     */
    <T extends Tamable> boolean register(EntityType type, Class<T> clazz);
}
