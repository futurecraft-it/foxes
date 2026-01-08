package it.futurecraft.foxes.goals.target;

import it.futurecraft.foxes.entities.Tamable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class OwnerHurtTargetGoal extends TargetGoal {
    private final Mob entity;

    private LivingEntity ownerLastHurt;
    private int timestamp;

    public OwnerHurtTargetGoal(@NotNull Tamable mob) {
        super((Mob) mob, false);

        this.entity = (Mob) mob;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Tamable t = (Tamable) entity;
        if (!t.tame() || t.orderedToSit() || t.owner().isEmpty()) return false;

        CraftPlayer cp = (CraftPlayer) t.owner().get();
        LivingEntity owner = cp.getHandle();

        ownerLastHurt = owner.getLastHurtMob();
        int lastTimestamp = owner.getLastHurtMobTimestamp();

        return timestamp != lastTimestamp && canAttack(ownerLastHurt, TargetingConditions.DEFAULT) && t.wantsToAttack(ownerLastHurt.getBukkitLivingEntity(), owner.getBukkitLivingEntity());
    }

    @Override
    public void start() {
        super.mob.setTarget(ownerLastHurt, TargetReason.OWNER_ATTACKED_TARGET);

        Tamable t = (Tamable) entity;
        t.owner().ifPresent(p -> {
            CraftPlayer cp = (CraftPlayer) p;
            LivingEntity owner = cp.getHandle();

            this.timestamp = owner.getLastHurtMobTimestamp();
        });

        super.start();
    }
}
