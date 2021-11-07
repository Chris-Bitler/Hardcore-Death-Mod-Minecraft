package io.cbitler.deathplugin.items;

import io.cbitler.deathplugin.init.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Class representing a life crystal that will give a player a life
 */
public class LifeCrystal extends Item {
    public LifeCrystal(Settings settings) {
        super(settings);
    }

    /**
     * Attempt to give the player a life when they use the crystal
     * @param world The world the item was used in
     * @param player The player using the item
     * @param hand The hand the player had the item in
     * @return Success if item is used up, pass otherwise
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) return TypedActionResult.pass(player.getMainHandStack());
        if (player.getMainHandStack().isOf(ItemInit.CrystalItem)) {
            MinecraftServer server = world.getServer();
            Scoreboard scoreboard = server.getScoreboard();
            if (scoreboard != null) {
                ScoreboardObjective lives = scoreboard.getObjective("lives");
                if (lives != null) {
                    ScoreboardPlayerScore score = scoreboard.getPlayerScore(player.getName().asString(), lives);
                    score.incrementScore();
                    scoreboard.updateScore(score);
                    player.getMainHandStack().decrement(1);
                    return TypedActionResult.success(player.getMainHandStack());
                }
            }
        }

        return TypedActionResult.pass(player.getMainHandStack());
    }
}
