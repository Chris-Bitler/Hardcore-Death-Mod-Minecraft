package io.cbitler.deathplugin.listeners;

import io.cbitler.deathplugin.init.ConfigInit;
import io.cbitler.deathplugin.init.ItemInit;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

/**
 * Miscellaneous listeners that don't fit into other listener types
 */
public class MiscListeners {
    /**
     * Add the life crystal and revival totem to every possible chest loot table
     *
     * @param resourceManager Minecraft resource manager, not used here
     * @param manager         Loot table manager - used to get loot table type
     * @param id              The id of the loot table
     * @param supplier        The fabric loot table supplier that we add to
     * @param setter          The loot table setter, not used
     */
    public static void onLootTableLoad(
            ResourceManager resourceManager,
            LootManager manager,
            Identifier id,
            FabricLootSupplierBuilder supplier,
            LootTableLoadingCallback.LootTableSetter setter
    ) {
        LootTable table = manager.getTable(id);
        if (table.getType() == LootContextTypes.CHEST) {
            FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                    .rolls(ConstantLootNumberProvider.create(1)).withCondition(RandomChanceLootCondition.builder(ConfigInit.CONFIG.crystalSpawnRate).build())
                    .with(ItemEntry.builder(ItemInit.CrystalItem));
            FabricLootPoolBuilder poolBuilder2 = FabricLootPoolBuilder.builder()
                    .rolls(ConstantLootNumberProvider.create(1)).withCondition(RandomChanceLootCondition.builder(ConfigInit.CONFIG.revivalTotemSpawnRate).build())
                    .with(ItemEntry.builder(ItemInit.ResurrectionItem));
            supplier.pool(poolBuilder);
            supplier.pool(poolBuilder2);
        }
    }
}
