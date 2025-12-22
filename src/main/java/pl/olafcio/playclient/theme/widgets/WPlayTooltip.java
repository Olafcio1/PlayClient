package pl.olafcio.playclient.theme.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;
import pl.olafcio.playclient.theme.PlayWidget;

public class WPlayTooltip extends WTooltip implements PlayWidget {
    public WPlayTooltip(String text) {
        super(text);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(this, theme().backgroundColor.get());
    }
}
