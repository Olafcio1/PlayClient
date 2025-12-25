package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import pl.olafcio.playclient.PlayAddon;

public class InfReach extends Module {
    public final Setting<Double> blockReach = new DoubleSetting.Builder()
            .name("extra-block-reach")
            .description("The distance to add to your block reach.")
            .sliderMax(1)
    .build(); // TODO

    public final Setting<Double> entityReach = settings.getDefaultGroup().add(new DoubleSetting.Builder()
            .name("extra-entity-reach")
            .description("The distance to add to your entity reach.")
            .sliderMax(1)
    .build());

    public static boolean active = false;
    private Vec3d previousPos;

    public InfReach() {
        super(PlayAddon.CATEGORY, "InfReach", "Expands your reach infinitely by teleporting during hit-time.");
    }

    @Override
    public void onActivate() {
        active = true;
        previousPos = null;
    }

    @Override
    public void onDeactivate() {
        active = false;
        previousPos = null;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (previousPos != null)
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    previousPos,
                    mc.player.isOnGround(), mc.player.horizontalCollision
            ));
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (event.entity.distanceTo(mc.player) > mc.player.getEntityInteractionRange() - entityReach.get())
            teleportOver(event.entity.getEntityPos());
    }

    private void teleportOver(Vec3d pos) {
        previousPos = mc.player.getEntityPos();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                pos,
                mc.player.isOnGround(), mc.player.horizontalCollision
        ));
    }
}
