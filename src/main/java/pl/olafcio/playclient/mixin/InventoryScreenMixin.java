package pl.olafcio.playclient.mixin;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.util.screen.ChatPromptElement;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends RecipeBookScreen<PlayerScreenHandler> {
    private InventoryScreenMixin(PlayerScreenHandler handler, RecipeBookWidget<?> recipeBook, PlayerInventory inventory, Text title) {
        super(handler, recipeBook, inventory, title);
    }

    @Unique
    private ChatPromptElement cpe;

    @Inject(at = @At("CTOR_HEAD"), method = "<init>")
    public void construct(PlayerEntity player, CallbackInfo ci) {
        addDrawableChild(cpe = new ChatPromptElement(
                client.textRenderer,
                0, 0,
                100,
                client.textRenderer.fontHeight + 8,
                Text.of("chat box")
        ));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (!cpe.isHovered())
            cpe.setFocused(false);

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        return (
                input.key() != GLFW.GLFW_KEY_ESCAPE &&
                cpe.isFocused()
        ) ? cpe.keyPressed(input) : super.keyPressed(input);
    }
}
