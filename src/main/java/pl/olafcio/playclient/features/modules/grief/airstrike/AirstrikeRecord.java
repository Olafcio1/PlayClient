package pl.olafcio.playclient.features.modules.grief.airstrike;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class AirstrikeRecord {
    public String entityType;
    public String customName;
    public boolean noGravity;
    public boolean noAI;

    public AirstrikeRecord(String entityType, @Nullable String customName, boolean noGravity, boolean noAI) {
        this.entityType = entityType;
        this.customName = customName == null ? "&aFucked by &bPlay Client&8 | &c@olafcio&4 on YT" : customName;
        this.noGravity = noGravity;
        this.noAI = noAI;
    }

    public static AirstrikeRecord fromNBT(NbtCompound nbt) {
        return new AirstrikeRecord(
                nbt.getString("entity-type").orElseThrow(),
                nbt.getString("custom-name").orElse(null),
                nbt.getBoolean("no-gravity", false),
                nbt.getBoolean("no-ai", false)
        );
    }

    public NbtCompound toNBT() {
        var nbt = new NbtCompound();
        nbt.putString("entity-type", entityType);
        nbt.putString("custom-name", customName);
        nbt.putBoolean("no-gravity", noGravity);
        nbt.putBoolean("no-ai", noGravity);
        return nbt;
    }
}
