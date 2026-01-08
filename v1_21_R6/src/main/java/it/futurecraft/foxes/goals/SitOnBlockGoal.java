package it.futurecraft.foxes.goals;

import it.futurecraft.foxes.entities.ComfortSeeker;
import it.futurecraft.foxes.entities.Tamable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class SitOnBlockGoal extends MoveToBlockGoal implements SpeedModifier {
    private final ComfortSeeker entity;

    public SitOnBlockGoal(@NotNull ComfortSeeker entity, double speed, int range) {
        super((PathfinderMob) entity, speed, range);

        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        Tamable t = (Tamable) entity;

        return t.tame() && !t.orderedToSit() && super.canUse();
    }

    @Override
    public void stop() {
        super.stop();

        Tamable t = (Tamable) entity;
        t.sit(false);
    }

    @Override
    public void start() {
        super.start();

        Tamable t = (Tamable) entity;
        t.sit(false);
    }

    @Override
    public void tick() {
        super.tick();

        Tamable t = (Tamable) entity;
        t.sit(isReachedTarget());
    }

    @Override
    public double speedModifier() {
        return speedModifier;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos bp) {
        if (!level.isEmptyBlock(bp.above())) return false;

        BlockState bs = level.getBlockState(bp);
        return entity.comfortable(CraftBlockData.fromData(bs));
    }
}
