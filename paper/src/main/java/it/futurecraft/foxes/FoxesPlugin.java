package it.futurecraft.foxes;

import it.futurecraft.foxes.events.PlayerInteractEntity;
import it.futurecraft.foxes.utils.Version;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class FoxesPlugin extends JavaPlugin implements Listener {
    public static final int BSTATS_ID = 29314;

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
            case Version.v1_21_10:
                foxes = new Foxes_1_21_10_Impl();
                break;
            case Version.v1_21_11:
                foxes = new Foxes_1_21_11_Impl();
                break;
        }

        foxes.register(EntityType.FOX, foxes.fox());
        foxes.register(EntityType.SLIME, foxes.slime());
    }

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, BSTATS_ID);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEntity(this), this);

        getServer().getServicesManager().register(Foxes.class, foxes, this, ServicePriority.High);
    }

    public Version getServerVersion() {
        return serverVersion;
    }
}
