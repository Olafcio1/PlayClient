package pl.olafcio.playclient;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import pl.olafcio.playclient.features.commands.Payall;
import pl.olafcio.playclient.features.modules.StaffWarner;
import pl.olafcio.playclient.theme.PlayGuiTheme;

public class PlayAddon extends MeteorAddon {
    public static Category CATEGORY = new Category("Play Client");

    @Override
    public void onInitialize() {
        GuiThemes.add(new PlayGuiTheme());
        Modules.get().add(new StaffWarner());
        Commands.add(new Payall());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "pl.olafcio.playclient";
    }
}
