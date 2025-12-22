package pl.olafcio.playclient.features.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class Payall extends Command {
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

    public int giveaway(double money, float chunk) {
        var players = mc.player.networkHandler.getPlayerList();
        money /= players.size() / chunk;

        for (var player : players)
            mc.player.networkHandler.sendChatCommand("pay " + player.getProfile().name() + " " + money);

        return SINGLE_SUCCESS;
    }
}
