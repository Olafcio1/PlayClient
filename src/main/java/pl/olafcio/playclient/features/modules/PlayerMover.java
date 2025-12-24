package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import pl.olafcio.playclient.PlayAddon;

public class PlayerMover extends Module {
    EntityTypeListSetting entities = settings.getDefaultGroup().add(new EntityTypeListSetting.Builder()
            .name("entity-types")
            .description("Entities to trigger the module on.")
            .defaultValue(EntityType.PLAYER)
            .onlyAttackable()
    .build());

    DoubleSetting maxDistance = settings.getDefaultGroup().add(new DoubleSetting.Builder()
            .name("max-distance")
            .description("What distance to unset the target on.")
            .defaultValue(4)
    .build());

    public PlayerMover() {
        super(PlayAddon.CATEGORY, "PlayerMover", "Moves people when you hit them.");
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
                var pos = target.getEntityPos();
                assert mc.player != null;

                mc.player.setPosition(pos);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        pos,
                        mc.player.isOnGround(), mc.player.horizontalCollision
                ));
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (entities.get().contains(event.entity.getType()))
            target = event.entity;
    }

    @Override
    public String getInfoString() {
        return target == null ? null : target.getStringifiedName();
    }
}
