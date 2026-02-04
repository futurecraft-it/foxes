package it.futurecraft.foxes.goals;

import it.futurecraft.foxes.entities.Tamable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;

public class SitWhenOrderedToGoal extends Goal {
    private final Mob entity;

    public  SitWhenOrderedToGoal(@NotNull Tamable entity) {
        this.entity = (Mob) entity;

        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Tamable t = (Tamable) entity;
        boolean sit = t.orderedToSit();

        if(!sit && !t.tame()) return false;
        if(entity.isInWater() || !entity.onGround()) return false;

        Optional<Player> p = t.owner();
        if(p.isEmpty()) return true;

        CraftPlayer cp = (CraftPlayer) p.get();
        LivingEntity owner = cp.getHandle();

        return owner.level() != entity.level() ||
                (entity.distanceToSqr(owner) < 144.0D || owner.getLastHurtByMob() == null) && sit;
    }

    @Override
    public boolean canContinueToUse() {
        Tamable t = (Tamable) entity;
        return t.orderedToSit();
    }

    @Override
    public void start() {
        entity.getNavigation().stop();

        Tamable t = (Tamable) entity;
        t.sit(true);
    }

    @Override
    public void stop() {
        Tamable t = (Tamable) entity;
        t.sit(false);
    }
}
