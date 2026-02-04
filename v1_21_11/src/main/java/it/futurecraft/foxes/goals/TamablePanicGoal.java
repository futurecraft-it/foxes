package it.futurecraft.foxes.goals;

import it.futurecraft.foxes.entities.Tamable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import org.jetbrains.annotations.NotNull;

public class TamablePanicGoal extends PanicGoal implements SpeedModifier {
    private final Tamable entity;

    public TamablePanicGoal(@NotNull Tamable entity, final double speedModifier, final TagKey<DamageType> panicCausingDamageTypes) {
        super((PathfinderMob) entity, speedModifier, panicCausingDamageTypes);
        this.entity = entity;
    }

    public TamablePanicGoal(@NotNull Tamable entity, final double speedModifier) {
        super((PathfinderMob) entity, speedModifier);
        this.entity = entity;
    }

    @Override
    public double speedModifier() {
        return speedModifier;
    }

    public void tick() {
        if (!entity.canMoveToOwner() && entity.shouldTryTeleportToOwner()) {
            entity.tryToTeleportToOwner();
        }

        super.tick();
    }
}
