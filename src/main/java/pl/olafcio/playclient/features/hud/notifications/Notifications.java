package pl.olafcio.playclient.features.hud.notifications;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.ColorSetting;
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

    protected final ColorSetting background = settings.getDefaultGroup().add(new ColorSetting.Builder()
            .name("background")
            .description("The background of a notification.")
            .defaultValue(new Color(19, 59, 98))
    .build());

    protected final ColorSetting lineBackground = settings.getDefaultGroup().add(new ColorSetting.Builder()
            .name("background")
            .description("The background of a notification.")
            .defaultValue(new Color(39, 79, 118))
    .build());

    protected final ColorSetting textColor = settings.getDefaultGroup().add(new ColorSetting.Builder()
            .name("text-color")
            .description("The text color of a notification.")
            .defaultValue(new Color(235, 235, 235))
    .build());

    protected int id = 0;

    @Override
    public void render(HudRenderer renderer) {
        var width = 200;
        var height = 84;

        setSize(width, height);

        if (!notifications.isEmpty()) {
            var now = System.currentTimeMillis();

            var offset = y;
            var gap = 8;

            for (var entry : notifications.entrySet()) {
                var notif = entry.getKey();
                var timeEnd = entry.getValue();

                var thisY = offset;
                float anim;

                if (timeEnd - DURATION >= now) {
                    // showing
                    long time = (timeEnd - DURATION) - now;
                    anim = (float)time / (float)SHOW_DURATION;

                    thisY += (int) ((float)(MeteorClient.mc.getWindow().getHeight() - thisY) * anim);
                } else if (timeEnd >= now) {
                    // rendering
                } else if (timeEnd + HIDE_DURATION >= now) {
                    // hiding
                    long time = (timeEnd + HIDE_DURATION) - now;
                    anim = (float)time / (float)HIDE_DURATION;

                    thisY += (int) ((float)(MeteorClient.mc.getWindow().getHeight() - thisY) * (1 - anim));
                } else {
                    toRemove.add(notif);
                    continue;
                }

                renderer.quad(x, thisY, width, height, background.get());
                renderer.line(x, thisY + height - 1, x, thisY + height, lineBackground.get());

                renderer.text("Meteor • " + notif.prefixTitle, x + 3, thisY + 3, textColor.get(), true, .8d);
                drawMultiText(renderer, notif.msg, x + 3, thisY + 3 + renderer.textHeight(true, .7d) + 3, textColor.get(), true, 1d);

                offset -= height + gap;
            }

            removeSelected();
            if (notifications.isEmpty())
                id = 0;
        }
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

            renderer.text(
                    text,
                    x, y,
                    color,
                    shadow, scale
            );
        }
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
