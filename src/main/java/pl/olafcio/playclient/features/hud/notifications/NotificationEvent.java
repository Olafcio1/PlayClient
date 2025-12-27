package pl.olafcio.playclient.features.hud.notifications;

import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import pl.olafcio.playclient.util.message.MessageParser;
import pl.olafcio.playclient.util.message.MessageUnparser;

import java.util.UUID;

public record NotificationEvent(UUID uniqueId, @Nullable String prefixTitle, @Nullable Formatting prefixColor, NbtList msg) {
    public NotificationEvent(UUID uniqueId, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
        this(uniqueId, prefixTitle, prefixColor, new MessageUnparser(new MessageParser(msg.getString()).run()).run());
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NotificationEvent && obj.hashCode() == this.hashCode();
    }
}
