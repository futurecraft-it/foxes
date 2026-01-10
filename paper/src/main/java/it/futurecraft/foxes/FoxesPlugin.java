package it.futurecraft.foxes;

import it.futurecraft.foxes.events.PlayerInteractEntity;
import it.futurecraft.foxes.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class FoxesPlugin extends JavaPlugin implements Listener {
    public static final int BSTATS_ID = 1;

    private Version serverVersion;
    private Foxes foxes;

    @Override
    public void onLoad() {
        Foxes._SingletonHelper.plugin(this);

        serverVersion = Version.getServerVersion();

        switch(serverVersion) {
            case Version.UNKNOWN:
                Bukkit.getPluginManager().disablePlugin(this);
                break;
            case Version.v1_21_R6:
                foxes = new Foxes_1_21_R6_Impl();
                break;
        }

        foxes.registerFox();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEntity(this), this);
    }

    public Version getServerVersion() {
        return serverVersion;
    }
}
