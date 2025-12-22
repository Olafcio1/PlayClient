package pl.olafcio.playclient.theme.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.utils.render.color.Color;
import pl.olafcio.playclient.theme.PlayWidget;

import java.util.Objects;
import java.util.function.Consumer;

public class WPlayWindow extends WWindow implements PlayWidget {
    public Consumer<WPlayHeader> onHeaderInit;
    public WPlayWindow(WWidget icon, String title) {
        super(icon, title);
    }

    @Override
    protected WHeader header(WWidget icon) {
        var header = new WPlayHeader(icon);
        if (onHeaderInit != null)
            onHeaderInit.accept(header);

        return header;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animProgress > 0)
            renderer.quad(x, y + header.height, width, height - header.height, theme().backgroundColor.get());
    }

    public class WPlayHeader extends WHeader {
        public Color color = null;
        public WPlayHeader(WWidget icon) {
            super(icon);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            var color = Objects.requireNonNullElse(this.color, theme().accentColor.get());
            var color2 = new Color(color);

            color2.r -= 15;
            color2.g -= 15;
            color2.b -= 15;

            color2.validate();

            renderer.quad(
                    x, y,
                    width, height,
                    color, color,
                    color2, color2
            );
        }
    }
}
