package it.futurecraft.foxes.goals;

import it.futurecraft.foxes.TamableFox;
import me.gamercoder215.mobchip.ai.goal.CustomPathfinder;
import me.gamercoder215.mobchip.ai.goal.Pathfinder;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class FoxSleepWithOwnerGoal extends CustomPathfinder {
    private final TamableFox fox;

    public FoxSleepWithOwnerGoal(@NotNull TamableFox fox) {
        super(fox.getFox());

        this.fox = fox;
    }

    @Override
    public @NotNull Pathfinder.PathfinderFlag[] getFlags() {
        return new Pathfinder.PathfinderFlag[0];
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void tick() {

    }
}
