package it.futurecraft.foxes.goals;

import it.futurecraft.foxes.TamableFox;
import me.gamercoder215.mobchip.ai.SpeedModifier;
import me.gamercoder215.mobchip.ai.goal.CustomPathfinder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FoxFollowOwnerGoal extends CustomPathfinder implements SpeedModifier {
    private final TamableFox fox;

    private int timeToRecalcPath;

    private double startDistance;
    private float stopDistance;
    private double speedModifier;

    private Player owner;

    public FoxFollowOwnerGoal(@NotNull TamableFox fox, double startDistance, float stopDistance, double speedModifier) {
        super(fox.getFox());

        this.fox = fox;

        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.speedModifier = speedModifier;
    }

    public double getStartDistance() {
        return startDistance;
    }

    public void setStartDistance(double startDistance) {
        this.startDistance = startDistance;
    }

    public float getStopDistance() {
        return stopDistance;
    }

    public void setStopDistance(float stopDistance) {
        this.stopDistance = stopDistance;
    }

    @Override
    public double getSpeedModifier() {
        return speedModifier;
    }

    @Override
    public void setSpeedModifier(double v) {
        this.speedModifier = v;
    }

    @Override
    public @NotNull PathfinderFlag[] getFlags() {
        return new PathfinderFlag[]{PathfinderFlag.MOVEMENT, PathfinderFlag.LOOKING};
    }

    @Override
    public boolean canStart() {
        Player owner = fox.getOwner();
        if (owner == null || !owner.isOnline() || owner.getGameMode() == GameMode.SPECTATOR) return false;
        if (fox.isOrderedToSit()) return false;

        Location loc = fox.getLocation();
        if (loc.distanceSquared(owner.getLocation()) < startDistance * startDistance) return false;

        this.owner = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (fox.isOrderedToSit()) return false;

        Location loc = fox.getLocation();
        return loc.distanceSquared(owner.getLocation()) > (double) (stopDistance * stopDistance);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.owner = null;
        this.fox.getNavigation().stop();
    }

    @Override
    public void tick() {
        boolean shouldTeleport = fox.shouldTryTeleportToOwner();

        if (!shouldTeleport) {
            fox.getNavigation().lookAt(owner);
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;

            if (shouldTeleport) {
                fox.tryToTeleportOwner();
            } else {
                fox.getNavigation().moveTo(owner, speedModifier);
            }
        }
    }
}
