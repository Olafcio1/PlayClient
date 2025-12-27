package pl.olafcio.playclient.features.hud.notifications;

import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

public final class NotificationStore {
    private NotificationStore() {
        throw new AssertionError("Tried to construct an utility class");
    }

    public static final IEventBus EVENT_BUS;
    static {
        EVENT_BUS = new EventBus();
        EVENT_BUS.registerLambdaFactory("pl.olafcio.playclient", (lookupInMethod, klass) -> {
            return (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup());
        });
    }

    public static void add(@Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
        EVENT_BUS.post(new NotificationEvent(UUID.randomUUID(), prefixTitle, prefixColor, msg));
    }
}
