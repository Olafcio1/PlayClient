package pl.olafcio.playclient.features.hud.notifications;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record NotificationEvent(UUID uniqueId, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
}
