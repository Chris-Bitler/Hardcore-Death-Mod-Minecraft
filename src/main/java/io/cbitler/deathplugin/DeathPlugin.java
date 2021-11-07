package io.cbitler.deathplugin;

import io.cbitler.deathplugin.init.ConfigInit;
import io.cbitler.deathplugin.init.EntityInit;
import io.cbitler.deathplugin.init.ItemInit;
import io.cbitler.deathplugin.listeners.EntityListeners;
import io.cbitler.deathplugin.listeners.MiscListeners;
import io.cbitler.deathplugin.listeners.ServerListeners;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class DeathPlugin implements ModInitializer {
    public static final String MOD_ID = "hardcore-death-plugin";
    @Override
    public void onInitialize() {
        // Register entity and items
        FabricDefaultAttributeRegistry.register(EntityInit.dead, ZombieEntity.createZombieAttributes());
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "crystal"), ItemInit.CrystalItem);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "revive_crystal"), ItemInit.ResurrectionItem);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "empty_vial"), ItemInit.EmptyLifeVial);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "life_vial"), ItemInit.LifeVial);
        ConfigInit.init();

        // Register various event listeners
        ServerPlayerEvents.ALLOW_DEATH.register(EntityListeners::handlePlayerDeath);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerListeners::handleServerStarted);
        ServerPlayConnectionEvents.JOIN.register(ServerListeners::handlePlayerJoin);
        LootTableLoadingCallback.EVENT.register(MiscListeners::onLootTableLoad);
        CommandRegistrationCallback.EVENT.register(ServerListeners::registerCommands);
    }
}
