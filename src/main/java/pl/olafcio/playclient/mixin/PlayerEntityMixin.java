package pl.olafcio.playclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.olafcio.playclient.features.modules.play.InfReach;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), order = 1001, cancellable = true)
    private void modifyBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        if (InfReach.active)
             cir.setReturnValue(Math.max(0, cir.getReturnValue() + Modules.get().get(InfReach.class).blockReach.get()));
    }

    @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), order = 1001, cancellable = true)
    private void modifyEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        if (InfReach.active)
            cir.setReturnValue(Math.max(0, cir.getReturnValue() + Modules.get().get(InfReach.class).entityReach.get()));
    }
}
