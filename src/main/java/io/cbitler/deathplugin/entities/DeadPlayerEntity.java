package io.cbitler.deathplugin.entities;

import io.cbitler.deathplugin.init.ItemInit;
import io.cbitler.deathplugin.interfaces.mixins.IPlayerEntityMixin;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.MessageType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

/**
 * Entity representing a dead player
 */
public class DeadPlayerEntity extends ZombieEntity {
    Random rand = new Random();

    @Getter @Setter
    String deadPlayerName;
    @Getter @Setter
    String uuidString;

    public DeadPlayerEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
        this.setCustomNameVisible(true);
        this.goalSelector.clear();
        this.targetSelector.clear();
    }

    /**
     * Show particles to differentiate it from a normal zombie, and remove it's AI goals, as setting noAI isn't enough
     * Also handle removing if the player it was meant for has been revived
     */
    @Override
    public void tick() {
        super.tick();
        int xMod = rand.nextInt(2) == 0 ? -1 : 1;
        int zMod = rand.nextInt(2) == 0 ? -1 : 1;
        for (int i = 0; i < 10; i++) {
            this.world.addParticle(ParticleTypes.DAMAGE_INDICATOR, this.getX(), this.getY(), this.getZ(), rand.nextFloat()*0.3*xMod, 0.1D, rand.nextFloat()*0.3*zMod);
        }
        this.goalSelector.clear();
        this.targetSelector.clear();
        this.handleRemoveIfRevived(world);
    }

    /**
     * Track the player name and UUID
     * @param nbt The NBT compound to write to
     */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (deadPlayerName != null) {
            nbt.put("deadPlayerName", NbtString.of(deadPlayerName));
        }
        if (uuidString != null) {
            nbt.put("deadPlayerUuid", NbtString.of(uuidString));
        }
        super.writeCustomDataToNbt(nbt);
    }

    /**
     * Read the player name and UUID from the NBT
     * @param nbt The NBT to read from
     */
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        String name = nbt.getString("deadPlayerName");
        String uuid = nbt.getString("deadPlayerUuid");
        if (!name.isEmpty()) {
            this.deadPlayerName = name;
            this.setCustomName(new TranslatableText(deadPlayerName));
        }
        if (!uuid.isEmpty()) {
            this.uuidString = uuid;
        }
    }

    @Override
    public boolean burnsInDaylight() {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    /**
     * If the player associated is revived, remove this entity
     * @param world The world the entity is in
     */
    public void handleRemoveIfRevived(World world) {
        if (world.isClient()) return;
        // See if the player is online
        Optional<ServerPlayerEntity> playerSearch = PlayerLookup.all(world.getServer())
                .stream()
                .filter((p) -> p.getName().asString().equalsIgnoreCase(this.deadPlayerName))
                .findFirst();
        if (playerSearch.isPresent()) {
            ServerPlayerEntity serverPlayer = playerSearch.get();
            IPlayerEntityMixin playerEntityWithMixin = ((IPlayerEntityMixin) serverPlayer);
            if (!playerEntityWithMixin.isGhost()) {
                // Remove if the dead player isn't a ghost
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }
}
