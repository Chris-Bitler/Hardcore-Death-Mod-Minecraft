package io.cbitler.deathplugin.items;

import io.cbitler.deathplugin.entities.DeadPlayerEntity;
import io.cbitler.deathplugin.init.ItemInit;
import io.cbitler.deathplugin.interfaces.mixins.IPlayerEntityMixin;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.Optional;

/**
 * Class representing a resurrection item
 */
public class ResurrectionCrystal extends Item {
    public ResurrectionCrystal(Settings settings) {
        super(settings);
    }

    /**
     * Handle a player interacting with this mob. Used to check for revive items used on it
     * @param stack The itemstack used
     * @param player The player interacting
     * @param entity The entity the item was used on
     * @param hand The hand that they interacted with
     * @return Pass unless exception is thrown
     */
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (player.world.isClient()) return ActionResult.PASS;
        if (stack.isOf(ItemInit.ResurrectionItem)) {
            if (entity instanceof DeadPlayerEntity deadPlayerEntity) {
                String deadPlayerName = deadPlayerEntity.getDeadPlayerName();
                Optional<ServerPlayerEntity> playerSearch = PlayerLookup.all(player.world.getServer())
                        .stream()
                        .filter((p) -> p.getName().asString().equalsIgnoreCase(deadPlayerName))
                        .findFirst();
                if (playerSearch.isPresent()) {
                    ServerPlayerEntity serverPlayer = playerSearch.get();
                    IPlayerEntityMixin playerEntityMixin = (IPlayerEntityMixin) serverPlayer;
                    if (playerEntityMixin.isGhost()) {
                        // Delegate handling the revive to the player mixin
                        playerEntityMixin.handleRevive(player.getEntityWorld(), player);
                    } else {
                        // Remove since this isn't needed anymore
                        deadPlayerEntity.remove(Entity.RemovalReason.DISCARDED);
                    }
                } else {
                    player.sendMessage(new TranslatableText(Formatting.RED + "The dead player is not online"), false);
                }
            }
        }

        return ActionResult.PASS;
    }
}
