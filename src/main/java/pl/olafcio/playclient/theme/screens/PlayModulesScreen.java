package pl.olafcio.playclient.theme.screens;

import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.ModulesScreen;
import meteordevelopment.meteorclient.gui.widgets.WTexture;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Identifier;
import pl.olafcio.playclient.theme.widgets.WPlayWindow;
import pl.olafcio.playclient.util.TextureResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayModulesScreen extends ModulesScreen {
    protected final ArrayList<WPlayWindow.WPlayHeader> headers;
    protected static final String[] have_icons;

    static {
        Arrays.sort(have_icons = new String[]{
                "combat",
                "misc",
                "movement",
                "playclient",
                "player",
                "render",
                "search",
                "world"
        });
    }

    public PlayModulesScreen(GuiTheme theme) {
        super(theme);
        headers = new ArrayList<>();
    }

    @Override
    protected WWindow createCategory(WContainer c, Category category, List<Module> moduleList) {
        WPlayWindow w;
        WTexture icon;

        String id;
        if (theme.categoryIcons() && Arrays.binarySearch(have_icons, id = category.name.toLowerCase().replace(" ", "")) >= 0) {
            icon = theme.texture(
                    20, 20,
                    0,
                    new TextureResource(
                            256, 256,
                            TextureFormat.RGBA8,
                            FilterMode.LINEAR, FilterMode.LINEAR,
                            Identifier.of("playclient", "category/%s.png".formatted(id))
                    )
            );

            w = (WPlayWindow) theme.window(icon, category.name);
            w.beforeHeaderInit = wContainer -> wContainer.add(icon).pad(2);
        } else
            w = (WPlayWindow) theme.window(category.name);

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

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.spacing = 0;

        for (var module : moduleList)
            w.add(theme.module(module)).expandX();

        return w;
    }
}
