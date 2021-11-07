package io.cbitler.deathplugin.mixins;

import io.cbitler.deathplugin.interfaces.mixins.IPlayerEntityMixin;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to the player spectator teleport packet
 * We want this to cause it to set the entity camera to the entity being teleported to
 */
@Mixin(ServerPlayNetworkHandler.class)
public class TeleportToSpecificPlayerSpectatorCommandMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "onSpectatorTeleport", at = @At("RETURN"))
    public void useMixin(SpectatorTeleportC2SPacket packet, CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).player;
        ((IPlayerEntityMixin) player).setSpectatorTeleporting(true);
        for (ServerWorld world : this.server.getWorlds()) {
            if (packet.getTarget(world) != null) {
                // Set the teleportation target so we can force camera after teleport
                ((IPlayerEntityMixin) player).setSpectatorTeleportationTarget(packet.getTarget(world));
                return;
            }
        }
    }
}
