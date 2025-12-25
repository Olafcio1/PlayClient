package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import pl.olafcio.playclient.PlayAddon;

import java.util.ArrayList;

public class PacketDelay extends Module {
    private enum Mode {
        Whitelist(true),
        Blacklist(false);

        public final boolean expects;
        Mode(boolean expects) {
            this.expects = expects;
        }
    }

    private final SettingGroup sgC2S = settings.createGroup("C2S");
    private final EnumSetting<Mode> c2sMode = sgC2S.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("Packet listing mode")
            .defaultValue(Mode.Whitelist)
    .build());
    private final PacketListSetting c2sPackets = sgC2S.add(new PacketListSetting.Builder()
            .name("packets")
            .description("The list of packets")
            .filter(packet -> packet.getPackageName().contains("c2s"))
    .build());

    private final SettingGroup sgS2C = settings.createGroup("S2C");
    private final EnumSetting<Mode> s2cMode = sgS2C.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("Packet listing mode")
            .defaultValue(Mode.Whitelist)
    .build());
    private final PacketListSetting s2cPackets = sgS2C.add(new PacketListSetting.Builder()
            .name("packets")
            .description("The list of packets")
            .filter(packet -> packet.getPackageName().contains("s2c"))
    .build());

    private final ArrayList<Packet<?>> c2sBuffer;
    private final ArrayList<Packet<?>> s2cBuffer;

    public PacketDelay() {
        super(PlayAddon.CATEGORY, "PacketDelay", "Delays chosen packets until module deactivation.");

        c2sBuffer = new ArrayList<>();
        s2cBuffer = new ArrayList<>();
    }

    @Override
    public void onDeactivate() {
        for (var packet : c2sBuffer)
            mc.player.networkHandler.sendPacket(packet);

        for (var packet : s2cBuffer)
            mc.player.networkHandler.accepts(packet);

        c2sBuffer.clear();
        s2cBuffer.clear();
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (s2cPackets.get().contains(event.packet.getClass()) == s2cMode.get().expects) {
            event.cancel();
            s2cBuffer.add(event.packet);
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (c2sPackets.get().contains(event.packet.getClass()) == c2sMode.get().expects) {
            event.cancel();
            c2sBuffer.add(event.packet);
        }
    }
}
