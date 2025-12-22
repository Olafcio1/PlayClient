package pl.olafcio.playclient.theme.widgets.pressable;

import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.utils.render.color.Color;
import pl.olafcio.playclient.theme.PlayWidget;

public class WPlayFavorite extends WFavorite implements PlayWidget {
    public WPlayFavorite(boolean checked) {
        super(checked);
    }

    @Override
    protected Color getColor() {
        return theme().favoriteColor.get();
    }
}
