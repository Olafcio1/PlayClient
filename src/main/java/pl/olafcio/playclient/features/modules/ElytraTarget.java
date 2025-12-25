package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import pl.olafcio.playclient.PlayAddon;

public class ElytraTarget extends Module {
    BoolSetting ignoreFriends = settings.getDefaultGroup().add(new BoolSetting.Builder()
            .name("ignore-friends")
    .build());

    public ElytraTarget() {
        super(PlayAddon.CATEGORY, "ElytraTarget", "Flies onto a player with elytra.");
    }

    Entity target;
    boolean just;

    @Override
    public void onActivate() {
        reset();
    }

    @Override
    public void onDeactivate() {
        reset();
    }

    private void reset() {
        target = null;
        just = false;
    }

    @EventHandler(priority = -1)
    public void onTick(TickEvent.Pre event) {
        if (target != null) {
            if (!target.isAlive()) {
                target = null;
                return;
            }
        } else return;

        // 32==9*4==player inventory bars combined
        var inventory = mc.player.getInventory();
        if (
                !mc.player.isOnGround() &&
                inventory.getStack(32 + EquipmentSlot.BODY.getIndex()).getItem() == Items.ELYTRA
        ) {
            var rot = lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, target.getEntityPos());

            mc.options.forwardKey.setPressed(true);
            mc.options.jumpKey.setPressed(true);

            if (mc.player.getVelocity().y < 0) {
                var stacks = inventory.getMainStacks();
                var index = 0;

                for (var stack : stacks) {
                    if (stack.getItem() == Items.FIREWORK_ROCKET) {
                        InvUtils.quickSwap().from(index).to(inventory.getSelectedSlot());

                        mc.interactionManager.sendSequencedPacket(mc.world, sequence -> {
                            return new PlayerInteractItemC2SPacket(
                                    Hand.MAIN_HAND,
                                    sequence,
                                    rot.yaw, rot.pitch
                            );
                        });

                        InvUtils.quickSwap().from(inventory.getSelectedSlot()).to(index);
                        break;
                    }

                    index++;
                }
            }

            just = true;
        } else if (just) {
            just = false;

            mc.options.forwardKey.setPressed(false);
            mc.options.jumpKey.setPressed(false);
        }
    }

    protected record Rotation(float yaw, float pitch) {}

    protected Rotation lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        var self = mc.player;
        var vec3d = anchorPoint.positionAt(self);

        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);

        var pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * (double)(180F / (float)Math.PI))));
        var yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * (double)(180F / (float)Math.PI)) - 90.0F);

        self.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                self.getEntityPos(),
                yaw, pitch,
                self.isOnGround(), self.horizontalCollision
        ));

        return new Rotation(yaw, pitch);
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (event.entity.getType() == EntityType.PLAYER && (
                !ignoreFriends.get() ||
                Friends.get().isFriend((PlayerEntity) event.entity)
        ))
            target = event.entity;
    }

    @Override
    public String getInfoString() {
        return target == null ? null : target.getStringifiedName();
    }
}
