package pl.olafcio.playclient.features.modules.render;

import meteordevelopment.meteorclient.systems.modules.Module;
import pl.olafcio.playclient.PlayAddon;

public class CustomShadow extends Module {
    public CustomShadow() {
        super(PlayAddon.RENDER, "CustomShadow", "Modifies player shadows.");
    }
}
