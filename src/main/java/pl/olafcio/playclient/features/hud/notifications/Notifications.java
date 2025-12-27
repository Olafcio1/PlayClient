package pl.olafcio.playclient.features.hud.notifications;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import pl.olafcio.playclient.PlayAddon;

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
    public Notifications() {
        super(INFO);
        notifications = new HashMap<>();
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

    @Override
    public void render(HudRenderer renderer) {
        var now = System.currentTimeMillis();

        var width = 70;
        var height = 30;

        var offset = y - height;
        var gap = 8;

        setSize(width, height);

        for (var entry : notifications.entrySet()) {
            var notif = entry.getKey();
            var timeEnd = entry.getValue();

            if (timeEnd - DURATION >= now) {
                // showing
            } else if (timeEnd >= now) {
                // rendering
                renderer.quad(x, offset, width, height, background.get());
                renderer.line(x, offset + height - 1, x, offset + height, lineBackground.get());
            } else if (timeEnd + HIDE_DURATION >= now) {
                // hiding
            } else {
                notifications.remove(notif);
                continue;
            }

            renderer.text("Meteor • " + notif.prefixTitle(), x + 3, offset + 3, textColor.get(), true, .7d);
            renderer.text(notif.msg().getString(), x + 3, offset + 3 + renderer.textHeight(true, .7d) + 3, textColor.get(), true);

            offset -= height + gap;
        }
    }

    @EventHandler
    public void onNotification(NotificationEvent event) {
        notifications.put(event, System.currentTimeMillis() + DURATION + SHOW_DURATION);
    }
}
