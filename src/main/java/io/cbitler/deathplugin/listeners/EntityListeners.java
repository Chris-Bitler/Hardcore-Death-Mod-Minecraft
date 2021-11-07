package io.cbitler.deathplugin.listeners;

import io.cbitler.deathplugin.entities.DeadPlayerEntity;
import io.cbitler.deathplugin.init.EntityInit;
import io.cbitler.deathplugin.interfaces.mixins.IPlayerEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

/**
 * Entity-specific listeners
 */
public class EntityListeners {
    /**
     * Handle a player dying
     * @param player The player that died
     * @param damageSource What killed them (not used)
     * @param damageAmount How much damage they took (not used)
     * @return true if they are actually dead, false otherwise
     */
    public static boolean handlePlayerDeath(ServerPlayerEntity player, DamageSource damageSource, float damageAmount) {
        if (player.world.isClient()) return true;

        // Support void totem as it also prevents the player from dying
        Identifier voidTotem = new Identifier("voidtotem", "totem_of_void_undying");
        if (player.getMainHandStack().getItem() != Items.AIR) {
            if (player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING) || player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING))
                return true;
            if (player.getMainHandStack().getItem() == Registry.ITEM.get(voidTotem) || player.getOffHandStack().getItem() == Registry.ITEM.get(voidTotem))
                return true;
        }
        MinecraftServer server = player.world.getServer();
        Scoreboard scoreboard = server.getScoreboard();
        if (scoreboard != null) {
            ScoreboardObjective lives = scoreboard.getObjective("lives");
            if (lives != null) {
                ScoreboardPlayerScore score = scoreboard.getPlayerScore(player.getName().asString(), lives);
                if (score.getScore() > 1) {
                    // Just remove a life and let the player die
                    score.setScore(score.getScore() - 1);
                    scoreboard.updateScore(score);
                } else {
                    // Set their score to 0
                    score.setScore(score.getScore() - 1);
                    scoreboard.updateScore(score);

                    // Set player entity information needed to know if they are dead and when they died
                    IPlayerEntityMixin playerWithMixin = ((IPlayerEntityMixin) player);
                    playerWithMixin.setDeathTimestamp(System.currentTimeMillis());
                    playerWithMixin.setIsGhost(true);

                    // Create the dead player entity (TODO: disabling AI here doesn't actually work)
                    DeadPlayerEntity entity = new DeadPlayerEntity(EntityInit.dead, player.world);
                    entity.setDeadPlayerName(player.getName().asString());
                    entity.setUuidString(player.getUuidAsString());
                    entity.setPosition(player.getPos());
                    entity.setAiDisabled(true);
                    entity.setInvulnerable(true);
                    player.changeGameMode(GameMode.SPECTATOR);
                    playerWithMixin.setDeathPosition(player.getPos());
                    playerWithMixin.setZombieUUID(entity.getUuid());

                    // This prevents the player from actually dying
                    player.setHealth(1);

                    player.getAbilities().allowFlying = false;
                    player.getAbilities().allowModifyWorld = false;
                    player.getAbilities().flying = false;
                    player.getAbilities().invulnerable = true;
                    player.setInvisible(true);
                    player.setInvulnerable(true);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE));
                    player.sendMessage(new TranslatableText(Formatting.RED + "You have lost your last life!"), false);

                    // TODO: Make this not actually cause fire..
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(player.world);
                    lightning.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0.0F, 0.0F);
                    player.world.spawnEntity(lightning);

                    player.sendAbilitiesUpdate();
                    player.world.spawnEntity(entity);

                    // Don't set the player's camera entity until the entity actually exists
                    player.setCameraEntity(entity);

                    return false;
                }
            }
        }

        return true;
    }
}
