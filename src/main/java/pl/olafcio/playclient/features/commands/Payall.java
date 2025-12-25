package pl.olafcio.playclient.features.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.util.Random;

public class Payall extends Command {
    public static final Random random = new Random();
    public Payall() {
        super("payall", "Pays all players a divided");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                argument(
                        "money",
                        DoubleArgumentType.doubleArg()
                ).executes(ctx -> {
                    var money = ctx.getArgument("money", double.class);
                    return giveaway(money, 1);
                }).then(
                        argument(
                                "chunk",
                                FloatArgumentType.floatArg()
                        ).executes(ctx -> {
                            var money = ctx.getArgument("money", double.class);
                            var chunk = ctx.getArgument("chunk", float.class);

                            return giveaway(money, chunk);
                        })
                )
        );
    }

    public int giveaway(final double origMoney, final float chunk) {
        new Thread(() -> {
            try {
                var players = mc.player.networkHandler.getPlayerList();
                var money = origMoney / (players.size() / chunk);

                for (var player : players) {
                    if (mc.player == null || mc.player.networkHandler == null)
                        return;

                    mc.execute(() -> {
                        mc.player.networkHandler.sendChatCommand("pay " + player.getProfile().name() + " " + money);
                    });

                    Thread.sleep(500 + random.nextLong(20)-10);
                }

                mc.execute(() -> {
                    info("Paid to %d people", players.size());
                });
            } catch (InterruptedException e) {
                warning("Interrupted");
            }
        }).start();

        return SINGLE_SUCCESS;
    }
}
