package pl.olafcio.playclient.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.util.screen.ChatPromptElement;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin extends Screen {
    protected InventoryScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("CTOR_HEAD"), method = "<init>")
    public void construct(PlayerEntity player, CallbackInfo ci) {
        int elHeight = client.textRenderer.fontHeight + 8;
        addDrawableChild(new ChatPromptElement(
                client.textRenderer,
                0, height - elHeight,
                100,
                elHeight,
                Text.of("chat box")
        ));
    }
}
