package it.futurecraft.foxes.goals.target;

import it.futurecraft.foxes.entities.Tamable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class OwnerHurtByTargetGoal extends TargetGoal {
    private final Mob entity;

    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public OwnerHurtByTargetGoal(Tamable mob) {
        super((Mob) mob, false);

        this.entity = (Mob) mob;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        Tamable t = (Tamable) entity;
        if (!t.tame() || t.orderedToSit() || t.owner().isEmpty()) return false;

        CraftPlayer cp = (CraftPlayer) t.owner().get();
        LivingEntity owner = cp.getHandle();

        ownerLastHurtBy = owner.getLastHurtByMob();
        int lastHurtByMobTimestamp = owner.getLastHurtByMobTimestamp();

        return lastHurtByMobTimestamp != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && t.wantsToAttack(this.ownerLastHurtBy.getBukkitLivingEntity(), owner.getBukkitLivingEntity());
    }

    public void start() {
        super.mob.setTarget(this.ownerLastHurtBy, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER);

        Tamable t = (Tamable) entity;
        t.owner().ifPresent(p -> {
            CraftPlayer cp = (CraftPlayer) p;
            LivingEntity owner = cp.getHandle();

            this.timestamp = owner.getLastHurtByMobTimestamp();
        });

        super.start();
    }
}