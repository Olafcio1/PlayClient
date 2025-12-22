package pl.olafcio.playclient;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import pl.olafcio.playclient.features.commands.Payall;

public class PlayAddon extends MeteorAddon {
    @Override
    public void onInitialize() {
        Commands.add(new Payall());
    }

    @Override
    public String getPackage() {
        return "pl.olafcio.playclient";
    }
}
