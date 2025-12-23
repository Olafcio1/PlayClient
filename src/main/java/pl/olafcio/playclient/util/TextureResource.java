package pl.olafcio.playclient.util;

import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Texture;
import net.minecraft.util.Identifier;

public class TextureResource extends Texture {
    public TextureResource(
            int width, int height,
            TextureFormat format,
            FilterMode min, FilterMode mag,
            Identifier resource
    ) {
        super(width, height, format, min, mag);
        var texture = MeteorClient.mc.getTextureManager().getTexture(resource);

        glTexture = texture.getGlTexture();
        sampler = texture.getSampler();

        glTextureView = texture.getGlTextureView();
    }
}
