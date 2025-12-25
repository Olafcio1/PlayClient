package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
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
            .description("Disables the module on friends.")
            .defaultValue(true)
    .build());

    public ElytraTarget() {
        super(PlayAddon.CATEGORY, "ElytraTarget", "Flies onto a player with elytra.");
    }

    Entity target;
    boolean just;
    Float yaw, pitch;
    float realYaw, realPitch;

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
        yaw = null;
        pitch = null;
    }

    @EventHandler(priority = -1)
    public void onTickPre(TickEvent.Pre event) {
        if (target != null) {
            if (!target.isAlive()) {
                target = null;
                reset();
                return;
            }
        } else return;

        // 32==9*4==player inventory bars combined
        var inventory = mc.player.getInventory();
        if (
                !mc.player.isOnGround() &&
                !mc.player.isInFluid() &&
                !mc.player.getAbilities().flying &&
                inventory.getStack(32 + EquipmentSlot.BODY.getIndex()).getItem() == Items.ELYTRA
        ) {
            var rot = lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, target.getEntityPos());
            if (mc.player.getVelocity().y < 0) {
                var stacks = inventory.getMainStacks();
                var index = 0;

                for (var stack : stacks) {
                    if (stack.getItem() == Items.FIREWORK_ROCKET) {
                        InvUtils.move().from(index).to(inventory.getSelectedSlot());

                        mc.interactionManager.sendSequencedPacket(mc.world, sequence -> {
                            return new PlayerInteractItemC2SPacket(
                                    Hand.MAIN_HAND,
                                    sequence,
                                    rot.yaw, rot.pitch
                            );
                        });

                        InvUtils.move().from(inventory.getSelectedSlot()).to(index);
                        break;
                    }

                    index++;
                }
            }

            just = true;
        } else if (just) {
            just = false;
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                    mc.player.getYaw(), mc.player.getPitch(),
                    mc.player.isOnGround(), mc.player.horizontalCollision
            ));
        }
    }

    @EventHandler
    public void onTickPost(TickEvent.Post event) {
        if (just) {
            mc.player.setPitch(realPitch);
            mc.player.setYaw(realYaw);
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

        realPitch = self.getPitch();
        realYaw = self.getYaw();

        pitch = self.lastPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * (double)(180F / (float)Math.PI))));
        yaw = self.lastYaw = self.lastHeadYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * (double)(180F / (float)Math.PI)) - 90.0F);

        self.setPitch(pitch);
        self.setYaw(yaw);
        self.setHeadYaw(yaw + 90);

        self.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                yaw, pitch,
                self.isOnGround(), self.horizontalCollision
        ));

        return new Rotation(yaw, pitch);
    }

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        if (event.entity.getType() == EntityType.PLAYER && (
                !ignoreFriends.get() ||
                !Friends.get().isFriend((PlayerEntity) event.entity)
        ))
            target = event.entity;
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (
                event.packet instanceof PlayerMoveC2SPacket packet &&
                just
        ) {
            packet.yaw = yaw;
            packet.pitch = pitch;
        }
    }

    @Override
    public String getInfoString() {
        return target == null ? null : target.getStringifiedName();
    }
}
