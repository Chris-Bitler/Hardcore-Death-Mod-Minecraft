package io.cbitler.deathplugin.client;

import io.cbitler.deathplugin.DeathPlugin;
import io.cbitler.deathplugin.init.EntityInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.ZombieEntityRenderer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class DeathPluginClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityInit.dead, ZombieEntityRenderer::new);
    }
}
