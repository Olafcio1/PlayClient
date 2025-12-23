package pl.olafcio.playclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SplashTextRenderer.class)
public class SplashTextRendererMixin {
    @WrapOperation(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/DrawnTextConsumer$Transformation;withPose(Lorg/joml/Matrix3x2fc;)Lnet/minecraft/client/font/DrawnTextConsumer$Transformation;"
            ),
            method = "render"
    )
    public DrawnTextConsumer.Transformation withPose(DrawnTextConsumer.Transformation instance, Matrix3x2fc pose, Operation<DrawnTextConsumer.Transformation> original) {
        return original.call(instance, pose.translate(0f, 5f, new Matrix3x2f()));
    }
}
