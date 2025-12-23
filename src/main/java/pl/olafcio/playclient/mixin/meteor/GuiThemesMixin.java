package pl.olafcio.playclient.mixin.meteor;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.theme.PlayGuiTheme;

@Mixin(value = GuiThemes.class, remap = false)
public abstract class GuiThemesMixin {
    @Shadow public static void add(GuiTheme theme) {}
    @Shadow public static void select(String name) {}

    @Inject(at = @At("TAIL"), method = "init")
    private static void init(CallbackInfo ci) {
        add(new PlayGuiTheme());
    }

    @Inject(at = @At(
            value = "INVOKE",
            target = "Lmeteordevelopment/meteorclient/gui/GuiThemes;select(Ljava/lang/String;)V",
            ordinal = 1
    ), method = "postInit", cancellable = true)
    private static void postInit(CallbackInfo ci) {
        ci.cancel();
        select("Play");
    }
}
