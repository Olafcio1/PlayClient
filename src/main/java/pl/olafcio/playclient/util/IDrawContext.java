package pl.olafcio.playclient.util;

import net.minecraft.client.gui.widget.ClickableWidget;

public interface IDrawContext {
    void playclient$hideCursorFor(ClickableWidget widget);
    void playclient$showCursor();

    ClickableWidget playclient$cursorhider();
}
