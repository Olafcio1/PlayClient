package pl.olafcio.playclient.mixin.meteor;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.config.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Config.class, remap = false)
public class ConfigMixin {
    @Shadow
    @Final
    public Setting<Boolean> customWindowTitle;

    @Shadow
    @Final
    public Setting<String> customWindowTitleText;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(CallbackInfo ci) {
        ((ISetting) customWindowTitle).playclient$defaultValue(true);
        ((ISetting) customWindowTitle).playclient$value(true);

        ((ISetting) customWindowTitleText).playclient$defaultValue("Play {mc_version}");
        ((ISetting) customWindowTitleText).playclient$value("Play {mc_version}");
    }
}
