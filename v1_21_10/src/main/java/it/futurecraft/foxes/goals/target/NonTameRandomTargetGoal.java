package it.futurecraft.foxes.goals.target;

import it.futurecraft.foxes.entities.Tamable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;

public class NonTameRandomTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final Mob entity;

    public NonTameRandomTargetGoal(Tamable entity, Class<T> targetType, boolean mustSee, @Nullable TargetingConditions.Selector selector) {
        super((Mob) entity, targetType, 10, mustSee, false, selector);
        this.entity = (Mob) entity;
    }

    public boolean canUse() {
        Tamable t = (Tamable) entity;
        return !t.tame() && super.canUse();
    }

    public boolean canContinueToUse() {
        return super.targetConditions != null ? super.targetConditions.test(getServerLevel(super.mob), super.mob, super.target) : super.canContinueToUse();
    }
}
