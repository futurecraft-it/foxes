package it.futurecraft.foxes;

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

    void registerFox();
}
