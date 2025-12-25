package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
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
    public InfReach() {
        super(PlayAddon.CATEGORY, "InfReach", "Expands your reach infinitely by teleporting during hit-time.");
    }

    @Override
    public void onActivate() {
        reset(true);
    }

    private void reset(boolean active) {
        InfReach.active = active;
        processing = false;
    }

    @Override
    public void onDeactivate() {
        reset(false);
    }

    boolean processing;

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (!processing && event.entity.distanceTo(mc.player) > mc.player.getEntityInteractionRange() - entityReach.get()) {
            event.cancel();
            applyAt(event.entity.getEntityPos(), () -> {
                mc.interactionManager.attackEntity(mc.player, event.entity);
            });
        }
    }

    @FunctionalInterface
    private interface VoidFunction {
        void apply();
    }

    private void applyAt(Vec3d destination, final VoidFunction callback) {
        processing = true;

        final var previousPos = mc.player.getEntityPos();
        final var ref = new Vec3d[]{destination};

        new Thread(() -> {
            try {
                var pos = ref[0];

                var xDiff = pos.x - previousPos.x;
                var yDiff = pos.y - previousPos.y;
                var zDiff = pos.z - previousPos.z;

                var maxPC = 1.5;
                var xTimes = Math.abs(Math.floor(xDiff / maxPC));
                var yTimes = Math.abs(Math.floor(yDiff / maxPC));
                var zTimes = Math.abs(Math.floor(zDiff / maxPC));

                teleportChunked(xTimes, yTimes, zTimes, pos, maxPC, xDiff, yDiff, zDiff);
                mc.execute(callback::apply);
                teleportChunked(xTimes, yTimes, zTimes, previousPos, maxPC, -xDiff, -yDiff, -zDiff);

                processing = false;
            } catch (InterruptedException e) {
                warning("[InfReach] Interrupted");
            }
        }).start();
    }

    private void teleportChunked(
            double xTimes, double yTimes, double zTimes,
            Vec3d pos, double maxPC,
            double xDiff, double yDiff, double zDiff
    ) throws InterruptedException {
        for (var t = 0; t < Math.max(xTimes, Math.max(yTimes, zTimes)); t++) {
            if (t < xTimes - 1)
                pos = pos.add(maxPC, 0, 0);
            else if (t == xTimes - 1)
                pos = pos.add(xDiff % maxPC, 0, 0);

            if (t < yTimes - 1)
                pos = pos.add(0, maxPC, 0);
            else if (t == yTimes - 1)
                pos = pos.add(0, yDiff % maxPC, 0);

            if (t < zTimes - 1)
                pos = pos.add(0, 0, maxPC);
            else if (t == zTimes - 1)
                pos = pos.add(0, 0, zDiff % maxPC);

            final var forwardedPos = pos;
            mc.execute(() -> {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        forwardedPos,
                        mc.player.isOnGround(), mc.player.horizontalCollision
                ));
            });

            Thread.sleep(100);
        }
    }
}
