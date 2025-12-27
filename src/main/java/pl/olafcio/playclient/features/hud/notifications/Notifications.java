package pl.olafcio.playclient.features.hud.notifications;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;
import pl.olafcio.playclient.PlayAddon;

import java.util.ArrayList;
import java.util.HashMap;

public class Notifications extends HudElement {
    public static final HudElementInfo<Notifications> INFO = new HudElementInfo<>(
            PlayAddon.HUD_GROUP,
            "Notifications",
            "Space for Meteor notifications.",
            Notifications::new
    );

    // TODO: Convert these into settings
    public static final int DURATION = 3000;
    public static final int SHOW_DURATION = 500;
    public static final int HIDE_DURATION = 500;

    protected final HashMap<NotificationEvent, Long> notifications;
    protected final ArrayList<NotificationEvent> toRemove;

    public Notifications() {
        super(INFO);

        notifications = new HashMap<>();
        toRemove = new ArrayList<>();

        NotificationStore.EVENT_BUS.subscribe(this);
    }

    @Override
    public void remove() {
        super.remove();
        NotificationStore.EVENT_BUS.unsubscribe(this);
    }

    protected final SettingGroup sgSize = settings.createGroup("Size");
    protected final IntSetting width = sgSize.add(new IntSetting.Builder()
            .name("width")
            .description("The notification width.")
            .defaultValue(200)
            .min(1)
            .noSlider()
    .build());
    protected final IntSetting height = sgSize.add(new IntSetting.Builder()
            .name("height")
            .description("The notification height.")
            .defaultValue(84)
            .min(1)
            .noSlider()
    .build());

    protected final SettingGroup sgColors = settings.createGroup("Colors");
    protected final ColorSetting background = sgColors.add(new ColorSetting.Builder()
            .name("background")
            .description("The background of a notification.")
            .defaultValue(new Color(19, 59, 98))
    .build());
    protected final ColorSetting lineBackground = sgColors.add(new ColorSetting.Builder()
            .name("line-background")
            .description("The background of the time line.")
            .defaultValue(new Color(39, 79, 118))
    .build());
    protected final ColorSetting textColor = sgColors.add(new ColorSetting.Builder()
            .name("text-color")
            .description("The text color of a notification.")
            .defaultValue(new Color(235, 235, 235))
    .build());

    protected final SettingGroup sgBorder = settings.createGroup("Border");
    protected final IntSetting borderSize = sgBorder.add(new IntSetting.Builder()
            .name("border-size")
            .description("The border size.")
            .defaultValue(1)
            .sliderRange(1, 10)
    .build());
    protected final ColorSetting borderColor = sgBorder.add(new ColorSetting.Builder()
            .name("border-color")
            .description("The border color.")
            .defaultValue(new Color(59, 99, 138))
    .build());

    protected final SettingGroup sgNumbers = settings.createGroup("Numbers");
    protected final DoubleSetting lineHeight = sgNumbers.add(new DoubleSetting.Builder()
            .name("line-height")
            .description("The height of the time line.")
            .defaultValue(2d)
    .build());
    protected final IntSetting gap = sgNumbers.add(new IntSetting.Builder()
            .name("gap")
            .description("The gap between each notification.")
            .defaultValue(8)
    .build());

    protected int id = 0;

    @Override
    public void render(HudRenderer renderer) {
        var width = this.width.get();
        var height = this.height.get();

        setSize(width, height);

        if (!notifications.isEmpty()) {
            var now = System.currentTimeMillis();

            var offset = y;
            var gap = this.gap.get();

            for (var entry : notifications.entrySet()) {
                var notif = entry.getKey();
                var timeEnd = entry.getValue();

                var thisY = offset;

                if (timeEnd - DURATION >= now) {
                    // showing
                    long time = (timeEnd - DURATION) - now;
                    float anim = (float)time / (float)SHOW_DURATION;

                    thisY += (int) ((float)(MeteorClient.mc.getWindow().getHeight() - thisY) * anim);
                } else if (timeEnd >= now) {
                    // rendering
                } else if (timeEnd + HIDE_DURATION >= now) {
                    // hiding
                    long time = (timeEnd + HIDE_DURATION) - now;
                    float anim = (float)time / (float)HIDE_DURATION;

                    thisY += (int) ((float)(MeteorClient.mc.getWindow().getHeight() - thisY) * (1 - anim));
                    offset += (int) ((float)(height + gap) * (1 - anim));
                } else {
                    toRemove.add(notif);
                    continue;
                }

                var anim = (float)(timeEnd - now) / (float)DURATION;
                drawNotification(renderer, thisY, width, height, anim, notif);

                offset -= height + gap;
            }

            removeSelected();
            if (notifications.isEmpty())
                id = 0;
        }
    }

    protected void drawNotification(HudRenderer renderer, int y, int width, int height, float anim, NotificationEvent notif) {
        renderer.drawContext.enableScissor(x, y, x + width, this.y + height);

        renderer.quad(x, y, width, height, background.get());
        renderer.quad(x, y + height - lineHeight.get(), width * Math.min(1, 1 - anim), lineHeight.get(), lineBackground.get());

        var text = new StringBuilder("Meteor");
        if (notif.prefixTitle != null) {
            text.append(" • ");
            text.append(notif.prefixTitle);
        }

        renderer.text(text.toString(), x + 3, y + 3, textColor.get(), true, .8d);
        drawMultiText(renderer, notif.msg, x + 3, y + 3 + renderer.textHeight(true, .7d) + 3, textColor.get(), true, 1d);

        var bSize = borderSize.get();
        var bColor = borderColor.get();

        renderer.quad(x, y, bSize, height, bColor);
        renderer.quad(x + width - bSize, y, bSize, height, bColor);

        renderer.quad(x, y, width, bSize, bColor);
        renderer.quad(x, y + height - bSize, width, bSize, bColor);

        renderer.drawContext.disableScissor();
    }

    protected void drawMultiText(
            HudRenderer renderer,
            NbtList fragments,
            double x,
            double y,
            Color color,
            boolean shadow,
            double scale
    ) {
        for (var token : fragments) {
            var compound = token.asCompound().orElseThrow();
            var text = compound.getString("text").orElseThrow();

            if (compound.contains("color")) {
                var raw = compound.getString("color").orElseThrow();
                int packed;

                if (raw.startsWith("#"))
                    packed = Integer.parseInt(raw.substring(1), 16);
                else packed = Formatting.byName(raw).getColorValue();

                color = new Color(packed);
            }

            drawWrappedText(renderer,
                    text,
                    x, y,
                    width.get(),
                    color,
                    shadow, scale
            );
        }
    }

    protected static double drawWrappedText(
            HudRenderer renderer,
            String text,
            double x, double y,
            double maxWidth,
            Color color,
            boolean shadow, double scale
    ) {
        var characters = text.toCharArray();
        var maxX = x + maxWidth;

        var currentX = x;
        for (var i = 0; i < characters.length; i++) {
            var str = String.valueOf(characters[i]);
            if (str.equals("\n") || str.equals("\r")) {
                currentX = x;
                y += renderer.textHeight(shadow, scale);

                continue;
            } else if (currentX + renderer.textWidth(str, shadow, scale) + 5 >= maxX) {
                renderer.text("-", currentX, y, color, shadow, scale);

                currentX = x;
                y += renderer.textHeight(shadow, scale);

                i--;
                continue;
            }

            currentX = renderer.text(str, currentX, y, color, shadow, scale);
        }

        return y;
    }

    @EventHandler
    public void onNotification(NotificationEvent event) {
        event.hash = id++;

        for (var n : notifications.keySet())
            if (n.repeatCode() == event.repeatCode())
                toRemove.add(n);

        removeSelected();
        notifications.put(event, System.currentTimeMillis() + DURATION + SHOW_DURATION);
    }

    protected void removeSelected() {
        for (var notif : toRemove)
            notifications.remove(notif);

        toRemove.clear();
    }
}
