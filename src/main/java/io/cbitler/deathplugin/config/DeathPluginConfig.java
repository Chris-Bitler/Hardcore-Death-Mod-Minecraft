package io.cbitler.deathplugin.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

/**
 * Config class for the mod
 */
@Config(name = "hardcore-death-plugin-config")
public class DeathPluginConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
    public float crystalSpawnRate = 0.01f;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
    public float revivalTotemSpawnRate = 0.01f;
    public int minutesBeforeAutoRevive = 60 * 24; // 24 hours
    public boolean autoReviveEnabled = true;
    public boolean autoReviveMessageOnJoin = true;
    public boolean enableSpeedDisableMixin = false;
}
