package pl.olafcio.playclient.features.hud.notifications;

import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import pl.olafcio.playclient.util.message.MessageParser;
import pl.olafcio.playclient.util.message.MessageUnparser;

public class NotificationEvent {
    public final @Nullable String prefixTitle;
    public final @Nullable Formatting prefixColor;
    public final NbtList msg;

    public int hash = 0;
    public NotificationEvent(@Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
        this.prefixTitle = prefixTitle;
        this.prefixColor = prefixColor;
        this.msg = new MessageUnparser(new MessageParser(msg.getString()).run()).run();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public int repeatCode() {
        var code = msg.hashCode();

        if (prefixTitle != null) code += prefixTitle.hashCode();
        if (prefixColor != null) code += prefixColor.hashCode();

        return code;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NotificationEvent notif && notif.repeatCode() == this.repeatCode();
    }
}
