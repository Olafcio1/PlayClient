package pl.olafcio.playclient.theme.screens;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.ModulesScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import pl.olafcio.playclient.theme.widgets.WPlayWindow;

import java.util.ArrayList;
import java.util.List;

public class PlayModulesScreen extends ModulesScreen {
    protected final ArrayList<WPlayWindow.WPlayHeader> headers;
    public PlayModulesScreen(GuiTheme theme) {
        super(theme);
        headers = new ArrayList<>();
    }

    @Override
    protected WWindow createCategory(WContainer c, Category category, List<Module> moduleList) {
        var w = (WPlayWindow) theme.window(category.name);
        w.id = category.name;
        w.padding = 0;
        w.spacing = 0;
        w.onHeaderInit = wContainer -> {
            headers.add(wContainer);

            var div = 360d / headers.size();
            var index = 0d;
            for (var header : headers)
                header.color = Color.fromHsv(div * index++, .6, .4);
        };

        if (theme.categoryIcons())
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.item(category.icon)).pad(2);

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.spacing = 0;

        for (var module : moduleList)
            w.add(theme.module(module)).expandX();

        return w;
    }
}
