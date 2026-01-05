package it.futurecraft.foxes.events;

import it.futurecraft.foxes.Foxes;
import it.futurecraft.foxes.TamableFox;
import it.futurecraft.foxes.goals.FoxSitWhenOrderedToGoal;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractFoxEvent extends EventListener {
    public PlayerInteractFoxEvent(Foxes plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if(e.getHand() != EquipmentSlot.HAND) return;

        Entity entity = e.getRightClicked();
        Player p = e.getPlayer();

        if (!TamableFox.isFox(entity)) return;

        TamableFox f = new TamableFox(plugin, (Fox) entity);

        if (!f.isTame()) {
            EquipmentSlot slot = e.getHand();

            ItemStack item = p.getInventory().getItem(slot);

            if (item.getType() != Material.CHICKEN) return;

            if (f.getRand().nextInt(3) == 0) {
                f.tame(p);

                Particle.HEART.builder()
                        .location(f.getLocation())
                        .offset(.5d, .5d, .5d)
                        .count(14)
                        .extra(0)
                        .spawn();
            } else {
                Particle.SMOKE.builder()
                        .location(f.getLocation())
                        .offset(.5d, .5d, .5d)
                        .count(14)
                        .extra(0)
                        .spawn();
            }

            if(p.getGameMode() != GameMode.CREATIVE) {
                item.subtract();
            }
        } else {
            f.setOrderedToSit(!f.isOrderedToSit());
        }

    }
}
