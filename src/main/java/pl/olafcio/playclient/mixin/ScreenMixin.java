package pl.olafcio.playclient.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.util.IDrawContext;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow
    @Final
    private List<Drawable> drawables;

    @Unique
    private final Drawable prependedDrawable = (ctx, mouseX, mouseY, delta) -> {
        var ctx1 = (IDrawContext) ctx;
        var hider = ctx1.playclient$cursorhider();
        if (hider != null && !hider.isHovered())
            ctx1.playclient$showCursor();
    };

    @Inject(at = @At("HEAD"), method = "init(II)V")
    protected void init(CallbackInfo ci) {
        drawables.remove(prependedDrawable);
        drawables.addFirst(prependedDrawable);
    }
}
