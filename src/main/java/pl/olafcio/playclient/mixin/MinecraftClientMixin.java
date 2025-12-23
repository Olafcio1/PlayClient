package pl.olafcio.playclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.util.IDrawContext;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("TAIL"), method = "setScreen")
    public void setScreen(Screen screen, CallbackInfo ci) {
        screen.drawables.addFirst((ctx, mouseX, mouseY, delta) -> {
                screen.drawables.removeFirst();
                ((IDrawContext) ctx).playclient$showCursor();
        });
    }
}
