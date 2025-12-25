package pl.olafcio.playclient.features.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import pl.olafcio.playclient.PlayAddon;

public class TriggerBot extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> inScreens = sgGeneral.add(new BoolSetting.Builder()
            .name("while-in-screens")
            .description("Whether to click while a screen is open.")
            .defaultValue(true)
    .build());

    private final Setting<Mode> leftClickMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode-left")
            .description("The method of clicking for left clicks.")
            .defaultValue(Mode.Press)
    .build());

    private final Setting<Integer> leftClickDelay = sgGeneral.add(new IntSetting.Builder()
            .name("delay-left")
            .description("The amount of delay between left clicks in ticks.")
            .defaultValue(2)
            .min(0)
            .sliderMax(60)
            .visible(() -> leftClickMode.get() == Mode.Press)
    .build());

    private final Setting<Mode> rightClickMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode-right")
            .description("The method of clicking for right clicks.")
            .defaultValue(Mode.Disabled)
    .build());

    private final Setting<Integer> rightClickDelay = sgGeneral.add(new IntSetting.Builder()
            .name("delay-right")
            .description("The amount of delay between right clicks in ticks.")
            .defaultValue(2)
            .min(0)
            .sliderMax(60)
            .visible(() -> rightClickMode.get() == Mode.Press)
    .build());

    private int rightClickTimer, leftClickTimer;

    public TriggerBot() {
        super(PlayAddon.CATEGORY, "TriggerBot", "Automatically clicks on the entity you're looking at.");
    }

    @Override
    public void onActivate() {
        rightClickTimer = 0;
        leftClickTimer = 0;
        mc.options.attackKey.setPressed(false);
        mc.options.useKey.setPressed(false);
    }

    @Override
    public void onDeactivate() {
        mc.options.attackKey.setPressed(false);
        mc.options.useKey.setPressed(false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!inScreens.get() && mc.currentScreen != null) return;
        if (mc.targetedEntity == null) return;

        switch (leftClickMode.get()) {
            case Disabled -> {}
            case Hold -> mc.options.attackKey.setPressed(true);
            case Press -> {
                leftClickTimer++;
                if (leftClickTimer > leftClickDelay.get()) {
                    Utils.leftClick();
                    leftClickTimer = 0;
                }
            }
        }

        switch (rightClickMode.get()) {
            case Disabled -> {}
            case Hold -> mc.options.useKey.setPressed(true);
            case Press -> {
                rightClickTimer++;
                if (rightClickTimer > rightClickDelay.get()) {
                    Utils.rightClick();
                    rightClickTimer = 0;
                }
            }
        }
    }

    public enum Mode {
        Disabled,
        Hold,
        Press
    }
}

