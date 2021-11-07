package io.cbitler.deathplugin.items;

import io.cbitler.deathplugin.init.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Class representing a filled life vial
 */
public class LifeVial extends Item {
    public LifeVial(Settings settings) {
        super(settings);
    }

    /**
     * Attempt to allow the player to gain a life from using it
     * @param world The world it was used in
     * @param user The player using it
     * @param hand The hand the player used it in
     * @return success if used, pass otherwise
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) return TypedActionResult.pass(user.getMainHandStack());
        MinecraftServer server = world.getServer();
        Scoreboard scoreboard = server.getScoreboard();
        if (scoreboard != null) {
            ScoreboardObjective lives = scoreboard.getObjective("lives");
            if (lives != null) {
                ScoreboardPlayerScore score = scoreboard.getPlayerScore(user.getName().asString(), lives);
                score.setScore(score.getScore() + 1);
                scoreboard.updateScore(score);
                user.sendMessage(new TranslatableText(Formatting.GREEN + "You have used the bottled life"), false);
                ItemStack itemStack = new ItemStack(ItemInit.EmptyLifeVial, 1);
                user.setStackInHand(hand, itemStack);
                return TypedActionResult.success(itemStack);
            }
        }

        return TypedActionResult.pass(user.getMainHandStack());
    }
}
