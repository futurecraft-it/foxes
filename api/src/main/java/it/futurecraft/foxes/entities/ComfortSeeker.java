package it.futurecraft.foxes.entities;

import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

/**
 * A mob that searches after "comfortable" blocks to sit on.
 */
public interface ComfortSeeker {
    /**
     * Whether the target block is a comfy block to sit on.
     * @param bd The block to check.
     * @return {@code true} if the block is a valid target, {@code false} otherwise.
     */
    boolean comfortable(BlockData bd);
}
