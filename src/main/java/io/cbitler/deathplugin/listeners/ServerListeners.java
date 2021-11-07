package io.cbitler.deathplugin.listeners;

import com.mojang.brigadier.CommandDispatcher;
import io.cbitler.deathplugin.commands.ReviveTimer;
import io.cbitler.deathplugin.init.ConfigInit;
import io.cbitler.deathplugin.interfaces.mixins.IPlayerEntityMixin;
import io.cbitler.deathplugin.utils.ReviveTimerUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

/**
 * Server specific listeners
 */
public class ServerListeners {
    /**
     * Register the commands used by this mod
     * @param dispatcher The command dispatcher
     * @param dedicated if the server is dedicated (only register on true)
     */
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        if (dedicated) {
            dispatcher.register(CommandManager.literal("reviveTimer").executes(new ReviveTimer()));
        }
    }

    /**
     * Init scoreboard related things when the server starts
     * @param server The minecraft server starting
     */
    public static void handleServerStarted(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective lives = scoreboard.getObjective("lives");
        if (lives == null) {
            ScoreboardObjective createdLives = scoreboard.addObjective("lives", ScoreboardCriterion.DUMMY, new TranslatableText("Lives"), ScoreboardCriterion.RenderType.INTEGER);
            scoreboard.setObjectiveSlot(Scoreboard.SIDEBAR_DISPLAY_SLOT_ID, createdLives);
        }
        ScoreboardObjective deaths = scoreboard.getObjective("total_deaths");
        if (deaths == null) {
            ScoreboardObjective createdDeaths = scoreboard.addObjective("total_deaths", ScoreboardCriterion.DUMMY, new TranslatableText("Deaths"), ScoreboardCriterion.RenderType.INTEGER);
            ScoreboardPlayerScore score = scoreboard.getPlayerScore("dummy", createdDeaths);
            score.setScore(0);
            scoreboard.updateScore(score);
        }
        ScoreboardObjective resurrectionsUsed = scoreboard.getObjective("revives_used");
        if (resurrectionsUsed == null) {
            ScoreboardObjective createdResurrectionsUsed = scoreboard.addObjective("revives_used", ScoreboardCriterion.DUMMY, new TranslatableText("Revives used"), ScoreboardCriterion.RenderType.INTEGER);
            ScoreboardPlayerScore score = scoreboard.getPlayerScore("dummy", createdResurrectionsUsed);
            score.setScore(0);
            scoreboard.updateScore(score);
        }
    }

    /**
     * Handle a player joining
     * @param handler The player network join packet
     * @param send The packet sender (not used)
     * @param server The minecraft server
     */
    public static void handlePlayerJoin(ServerPlayNetworkHandler handler, PacketSender send, MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective lives = scoreboard.getObjective("lives");
        // Set a player's initial lives
        if (!scoreboard.playerHasObjective(handler.player.getName().asString(), lives)) {
            ScoreboardPlayerScore score = scoreboard.getPlayerScore(handler.player.getName().asString(), lives);
            score.setScore(3);
            scoreboard.updateScore(score);
        }
        if (ConfigInit.CONFIG.autoReviveMessageOnJoin) {
            ServerPlayerEntity player = handler.getPlayer();
            IPlayerEntityMixin playerEntityMixin = (IPlayerEntityMixin) player;
            if (playerEntityMixin.isGhost()) {
                // Calculate the remaining time
                long timeSinceDeath = System.currentTimeMillis() - playerEntityMixin.getDeathTimestamp();
                long minutesSinceDeath = Math.floorDiv(timeSinceDeath, 60000L);
                int timeLeftTillRevive = ConfigInit.CONFIG.minutesBeforeAutoRevive - (int)minutesSinceDeath;

                // Don't send the on join message to players who are going to revived soon
                if (timeLeftTillRevive > 0) {
                    ReviveTimerUtils.sendTimeLeft(player);
                }
            }
        }
    }
}
