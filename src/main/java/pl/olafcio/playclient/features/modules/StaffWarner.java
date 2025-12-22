package pl.olafcio.playclient.features.modules;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import pl.olafcio.playclient.PlayAddon;

public class StaffWarner extends Module {
    enum Mode {
        Nick,
        RegExp
    }

    SettingGroup sgDetection = settings.createGroup("Detection");
    EnumSetting<Mode> mode = sgDetection.add(new EnumSetting.Builder<Mode>()
                            .name("mode")
                            .description("The way of detecting")
                            .defaultValue(Mode.Nick)
                    .build());
    StringListSetting staffList = sgDetection.add(new StringListSetting.Builder()
                            .name("staff-list")
                            .description("Nicks of all staff members")
                            .visible(() -> mode.get() == Mode.Nick)
                    .build());
    StringListSetting regexpList = sgDetection.add(new StringListSetting.Builder()
                            .name("reg-exps")
                            .description("At least one regular expression must match")
                            .visible(() -> mode.get() == Mode.RegExp)
                    .build());

    SettingGroup sgAction = settings.createGroup("Action");
    BoolSetting notify = sgAction.add(new BoolSetting.Builder()
                            .name("notify")
                            .description("Notifies you when a staff member is detected.")
                            .defaultValue(true)
                    .build());
    StringListSetting commandList = sgAction.add(new StringListSetting.Builder()
                            .name("command-list")
                            .description("List of commands/messages/chatcommands to type in chat when a staff member is detected.")
                    .build());

    public StaffWarner() {
        super(PlayAddon.CATEGORY, "StaffWarner", "Warns you when a staff member has been detected.");
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet.getPacketType() == PlayPackets.PLAYER_INFO_UPDATE) {
            var packet = (PlayerListS2CPacket) event.packet;
            var entries = packet.getEntries();

            var nicks = staffList.get();
            var regexps = regexpList.get();

            for (var entry : entries) {
                var profile = entry.profile();
                if (profile == null)
                    continue;

                var nick = profile.name();
                if (switch (mode.get()) {
                    case Nick -> nicks.contains(nick);
                    case RegExp -> regexps.stream().anyMatch(regex -> {
                        var display = entry.displayName();
                        if (display == null)
                            return false;

                        var text = display.getString();
                        return text.matches(regex);
                    });
                }) {
                    if (notify.get()) {
                        var msg = "§c§l[SEVERE]§4 STAFF MEMBER DETECTED: §6" + nick;
                        for (int i = 0; i < 3; i++)
                            info(msg);
                    }

                    var index = 0;
                    for (var cmd : commandList.get()) {
                        if (cmd.startsWith("/")) {
                            mc.player.networkHandler.sendChatCommand(cmd);
                        } else if (cmd.startsWith(".")) {
                            try {
                                Commands.dispatch(cmd.substring(1));
                            } catch (CommandSyntaxException e) {
                                info("Command %d: %s", index, e.getMessage());
                            }
                        } else {
                            mc.player.networkHandler.sendChatMessage(cmd);
                        }

                        index++;
                    }
                }
            }
        }
    }
}
