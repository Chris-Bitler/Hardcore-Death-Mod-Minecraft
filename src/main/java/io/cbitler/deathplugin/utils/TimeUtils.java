package io.cbitler.deathplugin.utils;

import net.minecraft.util.Pair;

/**
 * Static utility class to convert minutes to minutes/hours
 */
public class TimeUtils {
    /**
     * Convert a number of minutes into minutes/hours
     * @param minutes The number of minutes
     * @return Pair representing [Hours, Minutes]
     */
    public static Pair<Integer, Integer> minutesToHoursAndMinutes(int minutes) {
        if (minutes < 60) return new Pair<Integer, Integer>(0, minutes);
        double leftOverMinutes = minutes % 60;
        int hours = Double.valueOf(Math.floor(minutes / 60d)).intValue();
        return new Pair<Integer, Integer>(hours, (int) leftOverMinutes);
    }
}
