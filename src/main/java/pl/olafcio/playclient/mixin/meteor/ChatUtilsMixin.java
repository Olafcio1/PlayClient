package pl.olafcio.playclient.mixin.meteor;

import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.playclient.features.hud.notifications.NotificationStore;

@Mixin(value = ChatUtils.class, remap = false)
public class ChatUtilsMixin {
    @Inject(at = @At("TAIL"), method = "sendMsg(ILjava/lang/String;Lnet/minecraft/util/Formatting;Lnet/minecraft/text/Text;)V")
    private static void sendMsg(int id, String prefixTitle, Formatting prefixColor, Text msg, CallbackInfo ci) {
        NotificationStore.push(prefixTitle, prefixColor, msg);
    }
}
