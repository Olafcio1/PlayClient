package pl.olafcio.playclient.mixin;

import net.minecraft.client.render.command.ShadowPiecesCommandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShadowPiecesCommandRenderer.class)
public class ShadowPiecesCommandRendererMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;getWhite(F)I"), method = "render")
    public int getWhite(float alpha) {
        return (int)alpha;
    }
}
