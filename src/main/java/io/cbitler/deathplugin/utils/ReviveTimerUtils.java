package io.cbitler.deathplugin.utils;

import io.cbitler.deathplugin.init.ConfigInit;
import io.cbitler.deathplugin.interfaces.mixins.IPlayerEntityMixin;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

/**
 * Static utility class to calculate and send the time the user has left until they are revived
 */
public class ReviveTimerUtils {
    public enum SEND_TIME_RESULT {
        ISNT_DEAD,
        NEGATIVE_TIME_LEFT,
        MESSAGE_SENT
    }

    /**
     * Set the player a message with how long they have left until they are automatically revived
     * @param player The player to send the message to
     * @return ISNT_DEAD if the player isn't dead,
     * NEGATIVE_TIME_LEFT if the time left is negative,
     * MESSAGE_SENT if the message is sent
     */
    public static SEND_TIME_RESULT sendTimeLeft(ServerPlayerEntity player) {
        IPlayerEntityMixin playerEntityMixin = (IPlayerEntityMixin) player;
        if (playerEntityMixin.isGhost()) {
            long timeSinceDeath = System.currentTimeMillis() - playerEntityMixin.getDeathTimestamp();
            long minutesSinceDeath = Math.floorDiv(timeSinceDeath, 60000L);
            int timeLeftTillRevive = ConfigInit.CONFIG.minutesBeforeAutoRevive - (int)minutesSinceDeath;
            if (timeLeftTillRevive > 0) {
                Pair<Integer, Integer> hoursAndMinutesTillRevive = TimeUtils.minutesToHoursAndMinutes(timeLeftTillRevive);
                String message = String.format(Formatting.YELLOW + "You have %d minutes left until you are automatically revived.", timeLeftTillRevive);
                if (hoursAndMinutesTillRevive.getLeft() > 0) {
                    message = String.format(Formatting.YELLOW + "You have %d hours and %d minutes left until you are automatically revived.", hoursAndMinutesTillRevive.getLeft(), hoursAndMinutesTillRevive.getRight());
                }
                player.sendMessage(
                        new TranslatableText(
                                message
                        ), false
                );
                return SEND_TIME_RESULT.MESSAGE_SENT;
            } else {
                return SEND_TIME_RESULT.NEGATIVE_TIME_LEFT;
            }
        } else {
            return SEND_TIME_RESULT.ISNT_DEAD;
        }
    }
}
