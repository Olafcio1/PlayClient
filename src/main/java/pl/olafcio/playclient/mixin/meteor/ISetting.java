package pl.olafcio.playclient.mixin.meteor;

import meteordevelopment.meteorclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Setting.class, remap = false)
public interface ISetting {
    @Accessor(value = "value")
    void playclient$value(Object value);

    @Accessor(value = "defaultValue")
    @Mutable
    void playclient$defaultValue(Object value);
}
