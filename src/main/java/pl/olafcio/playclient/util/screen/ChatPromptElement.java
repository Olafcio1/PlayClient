package pl.olafcio.playclient.util.screen;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ChatPromptElement extends TextFieldWidget {
    public ChatPromptElement(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        try {
            if (input.key() == GLFW.GLFW_KEY_ENTER) {
                var text = getText();
                if (text.startsWith("."))
                    Commands.dispatch(text.substring(1));
                else ChatUtils.sendPlayerMsg(text);
            } else {
                return super.keyPressed(input);
            }
        } catch (CommandSyntaxException e) {
            MeteorClient.LOG.error(e.getMessage());
        }

        return true;
    }
}
