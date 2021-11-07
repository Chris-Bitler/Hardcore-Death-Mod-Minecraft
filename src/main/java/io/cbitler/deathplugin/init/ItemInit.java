package io.cbitler.deathplugin.init;

import io.cbitler.deathplugin.items.EmptyVial;
import io.cbitler.deathplugin.items.LifeCrystal;
import io.cbitler.deathplugin.items.LifeVial;
import io.cbitler.deathplugin.items.ResurrectionCrystal;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ItemInit {
    public static final Item CrystalItem = new LifeCrystal(new FabricItemSettings().group(ItemGroup.COMBAT));
    public static final Item EmptyLifeVial = new EmptyVial(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item LifeVial = new LifeVial(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    public static final Item ResurrectionItem = new ResurrectionCrystal(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
}
