package io.cbitler.deathplugin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.cbitler.deathplugin.utils.ReviveTimerUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Command to tell the user how long they have left until they are automatically revived, if enabled
 */
public class ReviveTimer implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerCommandSource source = context.getSource();
        if (source != null) {
            ServerPlayerEntity sender = source.getPlayer();
            if (sender != null) {
                ReviveTimerUtils.SEND_TIME_RESULT result = ReviveTimerUtils.sendTimeLeft(sender);
                if (result == ReviveTimerUtils.SEND_TIME_RESULT.ISNT_DEAD) {
                    sender.sendMessage(new TranslatableText(Formatting.RED + "You are not dead!"), false);
                } else if (result == ReviveTimerUtils.SEND_TIME_RESULT.NEGATIVE_TIME_LEFT) {
                    sender.sendMessage(new TranslatableText(Formatting.RED + "You should be revived any moment now"), false);
                }
                return 1;
            }
            return 0;
        }
        return 0;
    }
}
