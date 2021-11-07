package io.cbitler.deathplugin.mixins;

import io.cbitler.deathplugin.init.ConfigInit;
import io.cbitler.deathplugin.interfaces.mixins.IPlayerEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Mixin to add additional functionality to the server version of the player entity
 */
@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements IPlayerEntityMixin {
    private long deathTimestamp = 0;
    private boolean isGhost = false;
    private Vec3d deathPosition;
    private UUID zombieUUID;
    private int freeTicks;
    private boolean isSpectatorTeleporting;
    private Entity spectatorTeleportationTarget;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Read more information from the player's NBT that the plugin needs
     *
     * @param nbt The nbt compound to read from
     */
    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("deathTimestamp")) {
            this.deathTimestamp = nbt.getLong("deathTimestamp");
        }
        if (nbt.contains("isGhost")) {
            this.isGhost = nbt.getBoolean("isGhost");
        }
        if (nbt.contains("zombieUUID")) {
            String zombieUUIDString = nbt.getString("zombieUUID");
            if (zombieUUIDString != null) {
                this.zombieUUID = UUID.fromString(zombieUUIDString);
            }
        }
        if (nbt.contains("deathX") && nbt.contains("deathY") && nbt.contains("deathZ")) {
            double deathX = nbt.getDouble("deathX");
            double deathY = nbt.getDouble("deathY");
            double deathZ = nbt.getDouble("deathZ");
            this.deathPosition = new Vec3d(deathX, deathY, deathZ);
        }
    }

    /**
     * Write more information to the player's NBT that the plugin needs
     *
     * @param nbt The nbt compound to write to
     */
    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        nbt.putLong("deathTimestamp", this.deathTimestamp);
        nbt.putBoolean("isGhost", this.isGhost);
        if (this.zombieUUID != null) {
            nbt.putString("zombieUUID", this.zombieUUID.toString());
        }
        if (this.deathPosition != null) {
            nbt.putDouble("deathX", this.deathPosition.getX());
            nbt.putDouble("deathY", this.deathPosition.getY());
            nbt.putDouble("deathZ", this.deathPosition.getZ());
        }
    }

    /**
     * Add additional handling to player entity ticking
     */
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        // Revive player if they've been dead for long enough
        if (ConfigInit.CONFIG.autoReviveEnabled) {
            if (isGhost) {
                long timeBeforeAutoReviveMillis = ConfigInit.CONFIG.minutesBeforeAutoRevive * 60000L;
                if (System.currentTimeMillis() - deathTimestamp > timeBeforeAutoReviveMillis) {
                    if (!player.world.isClient()) {
                        this.handleRevive(player.world);
                    }
                }
            }
        }

        // Don't allow player to escape either spectating a player or their dead player entity
        if (isGhost) {
            ServerWorld world = player.getServerWorld();
            if (player.getCameraEntity() == player && this.zombieUUID != null) {
                this.freeTicks++;
                if (freeTicks > 5) {
                    Entity zombie = world.getEntity(this.zombieUUID);
                    player.setCameraEntity(zombie);
                }
            } else {
                this.freeTicks = 0;
            }
        }
    }

    /**
     * Set player camera entity after they finish spectator teleport
     * This should prevent it from glitching out if the target isn't loaded on the player's client
     */
    @Inject(method = "onTeleportationDone", at = @At("RETURN"))
    public void onTeleportDoneMixin(CallbackInfo ci) {
        if (this.isSpectatorTeleporting()) {
            this.setSpectatorTeleporting(false);
            if (this.getSpectatorTeleportationTarget() != null) {
                ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
                player.setCameraEntity(player);
            }
        }
    }

    // Getter/setters - these can't be lombok'd because of how mixins work

    public long getDeathTimestamp() {
        return this.deathTimestamp;
    }

    public void setDeathTimestamp(long timestamp) {
        this.deathTimestamp = timestamp;
    }

    public boolean isGhost() {
        return isGhost;
    }

    public void setIsGhost(boolean ghost) {
        this.isGhost = ghost;
    }

    public void setDeathPosition(Vec3d position) {
        this.deathPosition = position;
    }

    public void setZombieUUID(UUID uuid) {
        this.zombieUUID = uuid;
    }

    public void handleRevive(World world) {
        this.handleRevive(world, null);
    }

    public void setSpectatorTeleporting(boolean teleporting) {
        this.isSpectatorTeleporting = teleporting;
    }

    public boolean isSpectatorTeleporting() {
        return this.isSpectatorTeleporting;
    }

    public void setSpectatorTeleportationTarget(Entity entity) {
        this.spectatorTeleportationTarget = entity;
    }

    public Entity getSpectatorTeleportationTarget() {
        return this.spectatorTeleportationTarget;
    }

    /**
     * Handle reviving a player
     *
     * @param world   The world the player is
     * @param reviver The player reviving them - optional as revivals can be automated
     */
    public void handleRevive(World world, @Nullable PlayerEntity reviver) {
        if (world.isClient()) return;
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Reset fall distance so they don't die instantly on revive
        player.fallDistance = 0f;

        player.changeGameMode(GameMode.SURVIVAL);
        player.getAbilities().invulnerable = false;
        player.setInvulnerable(false);
        player.setInvisible(false);
        player.removeStatusEffect(StatusEffects.INVISIBILITY);
        player.setHealth(20);
        this.setIsGhost(false);

        // Restore the player's camera to themselves and set their position to where they died
        player.setCameraEntity(player);
        player.setPos(this.deathPosition.x, this.deathPosition.y, this.deathPosition.z);

        player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1f, 1f);
        if (reviver != null) {
            reviver.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1f, 1f);
        }

        // Update scoreboard information
        Scoreboard scoreboard = player.server.getScoreboard();
        if (scoreboard != null) {
            ScoreboardObjective lives = scoreboard.getObjective("lives");
            if (lives != null) {
                ScoreboardPlayerScore score = scoreboard.getPlayerScore(player.getName().asString(), lives);
                score.setScore(1);
                scoreboard.updateScore(score);
            }
            if (reviver != null) {
                ScoreboardObjective revivesUsed = scoreboard.getObjective("revives_used");
                if (revivesUsed != null) {
                    ScoreboardPlayerScore score = scoreboard.getPlayerScore("dummy", revivesUsed);
                    score.incrementScore();
                    scoreboard.updateScore(score);
                }
            }
        }
        if (reviver != null) {
            player.sendMessage(new TranslatableText(String.format(Formatting.YELLOW + "You have been revived by %s!", reviver.getName().asString())), MessageType.CHAT, player.getUuid());
            ((ServerPlayerEntity) reviver).sendMessage(new TranslatableText(String.format(Formatting.YELLOW + "You have revived %s", player.getName().asString())), MessageType.CHAT, player.getUuid());
            reviver.getMainHandStack().decrement(1);
        } else {
            player.sendMessage(new TranslatableText(Formatting.GOLD + "The gods have smiled upon you and given you another chance."), false);
        }
    }
}
