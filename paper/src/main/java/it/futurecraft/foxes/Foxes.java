package it.futurecraft.foxes;

import it.futurecraft.foxes.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Foxes extends JavaPlugin {
    public static final int BSTATS_ID = 1;

    private Version serverVersion;

    @Override
    public void onLoad() {
        serverVersion = Version.getServerVersion();

        switch(serverVersion) {
            case Version.UNKNOWN:
                Bukkit.getPluginManager().disablePlugin(this);
                break;
            case Version.v1_21_R6:
                break;
        }
    }


    public Version getServerVersion() {
        return serverVersion;
    }
}
