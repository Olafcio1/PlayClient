package pl.olafcio.playclient.mixin;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoDrawer.class)
public class LogoDrawerMixin {
    @Shadow
    @Final
    private boolean ignoreAlpha;

    @Shadow
    @Final
    private boolean minceraft;

    @Shadow
    @Final
    public static Identifier MINCERAFT_TEXTURE;

    @Shadow
    @Final
    public static Identifier LOGO_TEXTURE;

    @Shadow
    @Final
    public static Identifier EDITION_TEXTURE;

    @Inject(at = @At("HEAD"), method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", cancellable = true)
    public void draw(DrawContext context, int screenWidth, float alpha, int y, CallbackInfo ci) {
        if (ci.isCancelled()) return;
        ci.cancel();

        float f = this.ignoreAlpha ? 1.0F : alpha;

        int i = screenWidth / 2 - 128;
        int j = ColorHelper.getWhite(f);
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                this.minceraft ? MINCERAFT_TEXTURE : LOGO_TEXTURE,
                i, y,
                0.0F, 0.0F,
                256, 64,
                256, 64,
                j
        );

        int k = screenWidth / 2 - 64;
        int l = y + 44 - 7;
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                EDITION_TEXTURE,
                k, l,
                0.0F, 0.0F,
                128, 14,
                128, 16,
                j
        );
    }
}
