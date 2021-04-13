package org.samo_lego.taterzens.api.professions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Profession interface, providing hooks
 * for Taterzen's behaviour.
 *
 * Booleans instead of voids are there to allow you to cancel
 * the base Taterzen method.
 */
public interface TaterzenProfession {

    /**
     * Called on Taterzen entity tick.
     * @return true if you want to cancel the default Taterzen ticking.
     */
    boolean tick();

    /**
     * Called on movement tick.
     * @return true if you want to cancel the default Taterzen movement tick.
     */
    boolean tickMovement();

    /**
     * Called on Taterzen interaction.
     * @param player player that interacted with Taterzen
     * @param pos pos of interaction
     * @param hand player's hand
     * @return PASS to continue with default interaction, SUCCESS or FAIL to stop.
     */
    default ActionResult interactAt(PlayerEntity player, Vec3d pos, Hand hand) {
        return ActionResult.PASS;
    }

    /**
     * Called when Taterzen is attack
     * @param attacker entity that is attacking
     * @return true to cancel the attack, otherwise false
     */
    boolean handleAttack(Entity attacker);

    /**
     * Called onb Taterzen detah / removal.
     */
    void onRemove();

    /**
     * Called on parsing Taterzen data from {@link CompoundTag}.
     * @param tag tag to load profession data from.
     */
    void fromTag(CompoundTag tag);
    /**
     * Called on saving Taterzen data to {@link CompoundTag}.
     * @param tag tag to save profession data to.
     */
    void toTag(CompoundTag tag);
}