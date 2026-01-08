package it.futurecraft.foxes.entities;

import org.bukkit.entity.LivingEntity;
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
     * Whether the tamable is sitting or not.
     * @return {@code true} if it's sitting, {@code false} otherwise.
     */
    boolean sit();

    /**
     * Sets whether the tamable has to sit.
     * @param s The new sit state.
     */
    void sit(boolean s);

    /**
     * Whether the tamable is lying or not.
     * @return {@code true} if it's lying, {@code false} otherwise.
     */
    boolean lie();

    /**
     * Sets whether the tamable has to lie.
     * @param l The new lie state.
     */
    void lie(boolean l);

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
    boolean shouldTryTeleportToOwner();

    /**
     * Tries to teleport the tamable nearby the owner's location.
     */
    void tryToTeleportToOwner();

    /**
     * Whether the tamable can move to the owner's location.
     * @return {@code true} if the tamable can move there, {@code false} otherwise.
     */
    boolean canMoveToOwner();

    /**
     * Whether the mob wants to attack the entity.
     * @param target The target to attack.
     * @param owner The owner of the tamable.
     * @return {@code true} if the mob wants to attack the entity, {@code false} otherwise.
     */
    boolean wantsToAttack(@NotNull LivingEntity target, @NotNull LivingEntity owner);
}
