package pl.olafcio.playclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.util.IDrawContext;

import java.awt.*;
import java.util.function.Supplier;

@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin extends ClickableWidget.InactivityIndicatingWidget {
    @Shadow
    private @Nullable Supplier<Boolean> focusOverride;

    public PressableWidgetMixin(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    @Inject(at = @At("HEAD"), method = "drawButton", cancellable = true)
    protected final void drawButton(DrawContext context, CallbackInfo ci) {
        ci.cancel();

        // TODO: Focus state
        //if (this.active || this.focusOverride != null ? this.focusOverride.get() : this.isSelected())

        int x = this.getX();
        int y = this.getY();
        int x2 = x + this.getWidth() - 1;
        int y2 = y + this.getHeight();

        for (int ny = y; ny < y2; ny++)
            context.drawHorizontalLine(x, x2, ny, ColorHelper.withAlpha(alpha, Color.HSBtoRGB(
                    (float) ny /
                                 context.getScaledWindowHeight() /
                                 MeteorClient.mc.options.getGuiScale().getValue() *
                                 2F,
                    .3f,
                    .4f
            )));

        var mouseX = MeteorClient.mc.mouse.getScaledX(MeteorClient.mc.getWindow());
        var mouseY = MeteorClient.mc.mouse.getScaledY(MeteorClient.mc.getWindow());

        if (isHovered()) {
            var alpha = .75F;
            var radius = 6;

            setAt(context, mouseX, mouseY, ColorHelper.getWhite(alpha));

            for (int r = 0; r < radius; r++) {
                var iterA = alpha - (alpha / radius) * r;
                var color = ColorHelper.getWhite(iterA);

                for (int i = 0; i < r; i++) {
                    setAt(context, mouseX - i, mouseY - r, color);
                    setAt(context, mouseX - r, mouseY - i, color);
                    setAt(context, mouseX + i, mouseY + r, color);
                    setAt(context, mouseX + r, mouseY + i, color);
                }
            }
        }

        context.drawStrokedRectangle(x, y, width, height, ColorHelper.getArgb(
                Math.max(0, ColorHelper.channelFromFloat(alpha) - 30),
                0, 0, 0
        ));
    }

    @Unique
    private void setAt(DrawContext context, double x, double y, int color) {
        context.fill((int) x, (int) y, (int) x+1, (int) y+1, color);
    }

    @Override
    protected void setCursor(DrawContext context) {
        if (this.isHovered())
            ((IDrawContext) context).playclient$hideCursorFor(this);
    }
}
