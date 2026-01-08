package it.futurecraft.foxes.entities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Generic interface for a tamable mob.
 * It implements what's in NMS' TamableMob class.
 */
public interface Tamable {
    /**
     * The owner of this tamable.
     * @return The player, empty otherwise.
     * @see Player
     */
    Optional<Player> owner();

    /**
     * Changes the owner of the tamable.
     * @param p The player to make the new owner, {@code null} to clear.
     */
    void owner(@Nullable Player p);

    /**
     * Whether the tamable is tamed or not.
     * @return {@code true} if it's tamed, {@code false} otherwise.
     */
    boolean tame();

    /**
     * Sets whether this tamable is tamed.
     * @param t The new state.
     */
    void tame(boolean t);

    /**
     * Whether the tamable is ordered to sit.
     * @return {@code true} if it's ordered to sit, {@code false} otherwise.
     */
    boolean orderedToSit();

    /**
     * Changes the sit order of thi tamable.
     * @param o The new sit order.
     */
    void orderedToSit(boolean o);

    /**
     *
     * @param p The player's that's taming the tamable.
     */
    void tame(@NotNull Player p);

    /**
     * Applies (or removes) the effects of the taming process.
     * @param t Whether it's being tamed or not.
     */
    void applyTamingSideEffects(boolean t);


    /**
     * Whether should try to teleport the tamable nearby the owner's location.
     */
    boolean shouldTryToTeleportOwner();

    /**
     * Tries to teleport the tamable nearby the owner's location.
     */
    void tryToTeleportOwner();
}
