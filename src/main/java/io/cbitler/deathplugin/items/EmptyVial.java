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
 * Empty life vial item
 */
public class EmptyVial extends Item {
    public EmptyVial(Settings settings) {
        super(settings);
    }

    /**
     * Attempt to store a player life when they use the item
     * @param world The world the item was used in
     * @param user The player who used the item
     * @param hand The hand the player used the item with
     * @return Success if life was stored, fail if it wasn't, or pass if the scoreboard isn't valid
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
                if (score.getScore() > 1) {
                    score.setScore(score.getScore() - 1);
                    scoreboard.updateScore(score);
                    user.sendMessage(new TranslatableText(Formatting.GREEN + "You have bottled a life"), false);
                    ItemStack heartVial = new ItemStack(ItemInit.LifeVial, 1);
                    user.setStackInHand(hand, heartVial);
                    return TypedActionResult.success(heartVial);
                } else {
                    user.sendMessage(new TranslatableText(Formatting.RED + "You cannot bottle your last life"), false);
                    return TypedActionResult.fail(user.getMainHandStack());
                }
            }
        }

        return TypedActionResult.pass(user.getMainHandStack());
    }
}
