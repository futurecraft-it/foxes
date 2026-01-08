package it.futurecraft.foxes.goals;

import it.futurecraft.foxes.entities.Tamable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class FollowOwnerGoal extends Goal implements SpeedModifier{
    private final Mob entity;
    private final PathNavigation navigation;

    @Nullable
    private LivingEntity owner;

    private final double speedModifier;

    private final float startDistance;
    private final float stopDistance;

    private int timeToRecalcPath;
    private float waterCost;

    public FollowOwnerGoal(@NotNull Tamable entity, double speedModifier, float startDistance, float stopDistance) {
        this.entity = (Mob) entity;
        this.navigation = this.entity.getNavigation();

        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public double speedModifier() {
        return speedModifier;
    }

    @Override
    public boolean canUse() {
        Tamable t = (Tamable) entity;

        Optional<Player> p = t.owner();
        if (p.isEmpty()) return false;

        CraftPlayer cp = (CraftPlayer) p.get();
        owner = cp.getHandle();

        if (!t.canMoveToOwner()) return false;

        return entity.distanceToSqr(owner) >= (double) (this.startDistance * this.startDistance);
    }

    @Override
    public boolean canContinueToUse() {
        if (navigation.isDone() || owner == null) return false;

        Tamable t = (Tamable) entity;
        return t.canMoveToOwner() && entity.distanceToSqr(owner) > (double)(this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        timeToRecalcPath = 0;

        waterCost = entity.getPathfindingMalus(PathType.WATER);
        entity.setPathfindingMalus(PathType.WATER, 0.0f);
    }

    @Override
    public void stop() {
        owner = null;

        navigation.stop();
        entity.setPathfindingMalus(PathType.WATER, waterCost);
    }

    @Override
    public void tick() {
        Tamable t = (Tamable) entity;

        boolean teleport = t.shouldTryToTeleportOwner();
        if (!teleport && entity.distanceToSqr(owner) <= 256.0D) {
            entity.getLookControl().setLookAt(owner, 10F, (float) entity.getMaxHeadXRot());
        }

        if (--timeToRecalcPath < 0) {
            timeToRecalcPath = adjustedTickDelay(10);

            if (teleport) {
                t.tryToTeleportOwner();
            } else {
                navigation.moveTo(owner, speedModifier);
            }
        }
    }
}
