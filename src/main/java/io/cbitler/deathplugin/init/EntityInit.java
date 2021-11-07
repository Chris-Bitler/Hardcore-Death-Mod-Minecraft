package io.cbitler.deathplugin.init;

import io.cbitler.deathplugin.DeathPlugin;
import io.cbitler.deathplugin.entities.DeadPlayerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityInit {
    public static final EntityType<DeadPlayerEntity> dead = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(DeathPlugin.MOD_ID, "dead-person"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DeadPlayerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build()
    );
}
