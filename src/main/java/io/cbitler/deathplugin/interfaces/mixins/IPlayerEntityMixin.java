package io.cbitler.deathplugin.interfaces.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Interface representing added methods to the ServerPlayerEntity class
 */
public interface IPlayerEntityMixin {
    long getDeathTimestamp();
    void setDeathTimestamp(long timestamp);
    boolean isGhost();
    void setIsGhost(boolean ghost);
    void handleRevive(World world);
    void handleRevive(World world, PlayerEntity reviver);
    void setDeathPosition(Vec3d position);
    void setZombieUUID(UUID zombie);
    void setSpectatorTeleporting(boolean teleporting);
    boolean isSpectatorTeleporting();
    void setSpectatorTeleportationTarget(Entity entity);
    Entity getSpectatorTeleportationTarget();

}
