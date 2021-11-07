package io.cbitler.deathplugin.init;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class TagInit {
    public static final Tag<EntityType<?>> DEATH_MOBS = TagFactory.ENTITY_TYPE.create(IdInit.DEATH_MOB_ID);
    public static final Tag<Item> LIFE_TYPES = TagFactory.ITEM.create(IdInit.LIFE_TYPES);
}
