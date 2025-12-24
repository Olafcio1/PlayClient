package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
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
        if (!mc.player.isOnGround() && mc.player.getInventory().getStack(EquipmentSlot.BODY.getIndex()).getItem() == Items.ELYTRA) {
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, target.getEntityPos());
            mc.options.forwardKey.setPressed(true);

            just = true;
        } else if (just) {
            just = false;
            mc.options.forwardKey.setPressed(false);
        }
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
