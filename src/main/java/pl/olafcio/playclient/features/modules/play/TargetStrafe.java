package pl.olafcio.playclient.features.modules.play;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import pl.olafcio.playclient.PlayAddon;

public class TargetStrafe extends Module {
    EntityTypeListSetting entities = settings.getDefaultGroup().add(new EntityTypeListSetting.Builder()
            .name("entity-types")
            .description("Entities to trigger the module on.")
            .defaultValue(EntityType.PLAYER)
            .onlyAttackable()
    .build());

    DoubleSetting preferredDistance = settings.getDefaultGroup().add(new DoubleSetting.Builder()
            .name("preferred-distance")
            .description("The preferred distance to strafe at.")
            .defaultValue(3)
    .build());

    DoubleSetting maxDistance = settings.getDefaultGroup().add(new DoubleSetting.Builder()
            .name("max-distance")
            .description("Max distance to trigger TargetStrafe in.")
            .defaultValue(3)
    .build());

    IntSetting addition = settings.getDefaultGroup().add(new IntSetting.Builder()
            .name("addition")
            .description("The amount of degrees to add each tick.")
            .range(1, 359)
            .sliderRange(1, 359)
            .defaultValue(4)
    .build());

    BoolSetting ignoreFriends = settings.getDefaultGroup().add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("Disables the module on friends.")
            .defaultValue(true)
    .build());

    public TargetStrafe() {
        super(PlayAddon.CATEGORY, "TargetStrafe", "Makes you strafe around entities when you attack.");
    }

    @Override
    public void onActivate() {
        target = null;
        progress = 0;
    }

    Entity target;
    float progress;

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (target != null) {
            if (target.distanceTo(mc.player) > maxDistance.get() || !target.isAlive()) {
                target = null;
            } else {
                progress += addition.get();
                if (progress >= 180)
                    progress -= 360;

                var headYaw = target.getHeadYaw();
                target.setHeadYaw(progress + 90);

                target.lastYaw = progress + 90;
                target.lastPitch = 0;

                var hit = target.raycast(preferredDistance.get(), 1F, true);
                var pos = hit.getPos();

                pos = new Vec3d(pos.x, mc.player.getY(), pos.z);

                mc.player.setPosition(pos);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        pos,
                        mc.player.isOnGround(), mc.player.horizontalCollision
                ));

                target.setHeadYaw(headYaw);
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (entities.get().contains(event.entity.getType()) && (
                event.entity.getType() != EntityType.PLAYER ||
                !ignoreFriends.get() ||
                !Friends.get().isFriend((PlayerEntity) event.entity)
        ))
            target = event.entity;
    }

    @Override
    public String getInfoString() {
        return target == null ? null : target.getStringifiedName();
    }
}
