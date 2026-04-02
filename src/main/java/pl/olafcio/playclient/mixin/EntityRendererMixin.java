package pl.olafcio.playclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.olafcio.playclient.features.modules.render.CustomShadow;

import java.awt.*;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState>  {
    @Unique private T entity;
    @Unique private boolean react;

    @Inject(at = @At("HEAD"), method = "updateShadow(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;)V")
    public void updateShadow(T entity, S renderState, CallbackInfo ci) {
        this.entity = entity;
        this.react = false;
    }

    @Inject(at = @At("HEAD"), method = "getShadowRadius", cancellable = true)
    public void getShadowRadius(S state, CallbackInfoReturnable<Float> cir) {
        if (
                entity instanceof PlayerEntity player &&
                Modules.get().isActive(CustomShadow.class)
        ) {
            cir.setReturnValue(player.getAttackRange().getEffectiveMaxRange(player));
            react = true;
        } else {
            react = false;
        }
    }

    @ModifyConstant(constant = {
            @Constant(floatValue = 0.0F, ordinal = 1)
    }, method = "updateShadow(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/world/World;)V")
    public float updateShadow__minCalculatedOpacity(float constant) {
        if (Modules.get().isActive(CustomShadow.class))
            return -32F;
        else return constant;
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"), method = "addShadowPiece")
    public float addShadowPiece__clamp(float value, float min, float max, Operation<Float> original) {
        float ret = original.call(value, min, max);
        if (react)
            return ColorHelper.withAlpha(103, Color.HSBtoRGB(Math.min(1F, (float)entity.squaredDistanceTo(MinecraftClient.getInstance().player) / 10F), .6F, .6F));

        return ColorHelper.getWhite(ret);
    }
}
