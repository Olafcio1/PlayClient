package pl.olafcio.playclient;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.ArrayUtils;
import pl.olafcio.playclient.features.commands.Payall;
import pl.olafcio.playclient.features.modules.PlayerMover;
import pl.olafcio.playclient.features.modules.StaffWarner;
import pl.olafcio.playclient.theme.PlayGuiTheme;

public class PlayAddon extends MeteorAddon {
    public static Category CATEGORY = new Category("Play Client");

    @Override
    public void onInitialize() {
        if (!ArrayUtils.contains(GuiThemes.getNames(), "Play"))
            GuiThemes.add(new PlayGuiTheme());

        Modules.get().add(new StaffWarner());
        Modules.get().add(new PlayerMover());

        Commands.add(new Payall());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getWebsite() {
        return FabricLoader.getInstance()
                           .getModContainer("playclient").orElseThrow()
                           .getMetadata()
                           .getContact()
                           .get("homepage").orElseThrow();
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("Olafcio1", "PlayClient");
    }

    @Override
    public String getPackage() {
        return "pl.olafcio.playclient";
    }
}
