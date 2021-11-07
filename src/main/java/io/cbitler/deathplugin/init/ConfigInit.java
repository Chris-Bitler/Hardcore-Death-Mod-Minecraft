package io.cbitler.deathplugin.init;

import io.cbitler.deathplugin.config.DeathPluginConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ConfigInit {
    public static DeathPluginConfig CONFIG = new DeathPluginConfig();

    public static void init() {
        AutoConfig.register(DeathPluginConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(DeathPluginConfig.class).getConfig();
    }
}
