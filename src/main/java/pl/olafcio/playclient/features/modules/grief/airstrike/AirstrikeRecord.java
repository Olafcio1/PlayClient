package pl.olafcio.playclient.features.modules.grief.airstrike;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class AirstrikeRecord {
    public String entityType;
    public String customName;
    public boolean nameVisible;
    public boolean noGravity;
    public boolean noAI;
    public boolean persistenceRequired;

    public AirstrikeRecord(String entityType, @Nullable String customName, boolean nameVisible, boolean noGravity, boolean noAI, boolean persistenceRequired) {
        this.entityType = entityType;
        this.customName = customName == null ? "&aFucked by &bPlay Client&8 | &c@olafcio&4 on YT" : customName;
        this.nameVisible = nameVisible;
        this.noGravity = noGravity;
        this.noAI = noAI;
        this.persistenceRequired = persistenceRequired;
    }

    public static AirstrikeRecord fromNBT(NbtCompound nbt) {
        return new AirstrikeRecord(
                nbt.getString("entity-type").orElseThrow(),
                nbt.getString("custom-name").orElse(null),
                nbt.getBoolean("name-visible", true),
                nbt.getBoolean("no-gravity", false),
                nbt.getBoolean("no-ai", false),
                nbt.getBoolean("persistence-required", false)
        );
    }

    public NbtCompound toNBT() {
        var nbt = new NbtCompound();
        nbt.putString("entity-type", entityType);
        nbt.putString("custom-name", customName);
        nbt.putBoolean("name-visible", nameVisible);
        nbt.putBoolean("no-gravity", noGravity);
        nbt.putBoolean("no-ai", noAI);
        nbt.putBoolean("persistence-required", persistenceRequired);
        return nbt;
    }
}
