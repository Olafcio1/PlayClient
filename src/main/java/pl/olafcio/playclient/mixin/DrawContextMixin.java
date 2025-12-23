package pl.olafcio.playclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.Cursor;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.util.IDrawContext;

@Mixin(DrawContext.class)
public class DrawContextMixin implements IDrawContext {
    @Unique
    private static ClickableWidget hideBecause = null;

    @Unique
    public void playclient$hideCursorFor(ClickableWidget widget) {
        hideBecause = widget;
        GLFW.glfwSetInputMode(
                MeteorClient.mc.getWindow().getHandle(),
                GLFW.GLFW_CURSOR,
                GLFW.GLFW_CURSOR_HIDDEN
        );
    }

    @Override
    public ClickableWidget playclient$cursorhider() {
        return hideBecause;
    }

    @Override
    public void playclient$showCursor() {
        hideBecause = null;
        GLFW.glfwSetInputMode(
                MeteorClient.mc.getWindow().getHandle(),
                GLFW.GLFW_CURSOR,
                GLFW.GLFW_CURSOR_NORMAL
        );
    }

    @Inject(at = @At("HEAD"), method = "applyCursorTo", cancellable = true)
    public void applyCursorTo(Window window, CallbackInfo ci) {
        if (hideBecause != null)
            if (hideBecause.isHovered())
                ci.cancel();
            else playclient$showCursor();
    }

    @Inject(at = @At("TAIL"), method = "setCursor")
    public void setCursor(Cursor cursor, CallbackInfo ci) {
        playclient$showCursor();
    }
}
