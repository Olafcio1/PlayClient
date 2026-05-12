package pl.olafcio.playclient.mixin;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.olafcio.playclient.util.screen.ChatPromptElement;

@Mixin(HandledScreen.class)
public abstract class InventoryScreenMixin extends Screen {
    private InventoryScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private ChatPromptElement cpe;

    @Inject(at = @At("CTOR_HEAD"), method = "<init>")
    public void construct(ScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        addDrawableChild(cpe = new ChatPromptElement(
                client.textRenderer,
                0, 0,
                100,
                client.textRenderer.fontHeight + 8,
                Text.of("chat box")
        ));
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    public void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        cpe.setFocused(cpe.isHovered());
    }

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    public void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (
                input.key() != GLFW.GLFW_KEY_ESCAPE &&
                cpe.isFocused()
        ) cir.setReturnValue(cpe.keyPressed(input));
    }
}
