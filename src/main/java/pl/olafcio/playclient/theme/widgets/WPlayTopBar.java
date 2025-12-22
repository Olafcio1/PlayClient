package pl.olafcio.playclient.theme.widgets;

import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.utils.render.color.Color;
import pl.olafcio.playclient.theme.PlayWidget;

public class WPlayTopBar extends WTopBar implements PlayWidget {
    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColor.get();
    }
}
