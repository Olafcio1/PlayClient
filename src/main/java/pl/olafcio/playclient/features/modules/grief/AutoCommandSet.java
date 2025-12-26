package pl.olafcio.playclient.features.modules.grief;

import com.google.common.collect.Lists;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import pl.olafcio.playclient.PlayAddon;

public class AutoCommandSet extends Module {
    BoolSetting chaining = settings.getDefaultGroup().add(new BoolSetting.Builder()
            .name("chaining")
            .description("Makes the command blocks run in order.")
            .defaultValue(false)
    .build());

    BoolSetting trackOutput = settings.getDefaultGroup().add(new BoolSetting.Builder()
            .name("track-output")
            .description("Keeps last command output in each command storer.")
            .defaultValue(false)
    .build());

    StringListSetting commands = settings.getDefaultGroup().add(new StringListSetting.Builder()
            .name("commands")
            .description("The commands to schedule.")
            .defaultValue("gamemode creative Olafcio",
                          "gamerule doImmediateRespawn true",
                          "gamerule immediate_respawn true",
                          "kill @a[name=!Olafcio]",
                          "pardon Olafcio")
    .build());

    public AutoCommandSet() {
        super(PlayAddon.GRIEF, "AutoCommandSet", "Automatically schedules the specified commands in the most efficient way possible.");
    }

    @Override
    public void onActivate() {
        mc.player.networkHandler.sendChatCommand("forceload add 0 0");

        var starter = 0;
        var allType = "repeating";
        if (chaining.get() && commands.get().size() > 1) {
            starter = 1;
            allType = "chain";

            mc.player.networkHandler.sendChatCommand("setblock 0 0 0 repeating_command_block[facing=up]{auto:1,TrackOutput:%d}".formatted(
                    trackOutput.get() == true ? 1 : 0
            ));
        }

        mc.player.networkHandler.sendChatCommand(("fill " +
                                                  "0 %d 0 " +
                                                  "0 %d 0 " +
                                                  "%s_command_block%s{auto:1%s}"
                                                 ).formatted(
                                                         starter,
                                                         commands.get().size() - 1,
                                                         allType,
                                                         chaining.get() ? "[facing=up]" : "",
                                                         trackOutput.get() == true ? "" : ",TrackOutput:0"
                                                 ));
        mc.player.networkHandler.sendChatCommand("setblock 0 0 1 redstone_block");
        mc.player.networkHandler.sendChatCommand("setblock 0 1 1 activator_rail");
        mc.player.networkHandler.sendChatCommand("setblock 0 2 1 bedrock");

        var i = 0;
        for (var cmd : commands.get()) {
            cmd = cmd.replace("\"", "\\\"");

            if (!run(
                    "data merge block 0 %d 0 {Command:\"%s\"}",
                    i, cmd
            ))
                error("[Command %d/Block] No chat space left in chatbox (>255 characters)".formatted(i));

            if (!run(
                    new String[][]{
                            new String[]{
                                    "summon command_block_minecart 0 1 1 {Command:\"%s\"}",
                            },
                            new String[]{
                                    "summon command_block_minecart 0 1 1 {Tags:[\"x\"]}",
                                    "data merge entity @e[tag=x,limit=1] {x:\"%s\"}",
                                    "data modify entity @e[tag=x,limit=1] Command set from entity @e[tag=x,limit=1] x",
                                    "data remove entity @e[tag=x,limit=1] x",
                                    "tag @e[tag=x] remove x"
                            }
                    },
                    cmd
            ))
                error("[Command %d/Minecart] No chat space left in chatbox (>255 characters)".formatted(i));

            i++;
        }

        toggle();
    }

    private boolean run(String command, Object... placeholders) {
        command = command.formatted(placeholders);
        if (command.length() > 255)
            return false;

        mc.player.networkHandler.sendChatCommand(command);
        return true;
    }

    private boolean run(String[][] options, Object... placeholders) {
        for (var opt : options) {
            var ok = true;
            for (var cmd : opt) {
                if (cmd.length() > 255) {
                    ok = false;
                    break;
                }
            }

            if (!ok)
                continue;

            var pList = Lists.newArrayList(placeholders);
            for (var cmd : opt) {
                var used = cmd.split("%").length - 1;

                var own      = pList.subList(0, used);
                var ownArray = own  .toArray(Object[]::new);

                mc.player.networkHandler.sendChatCommand(cmd.formatted(ownArray));
                own.clear();
            }

            return true;
        }

        return false;
    }
}
