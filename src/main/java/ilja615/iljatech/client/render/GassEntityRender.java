package ilja615.iljatech.client.render;

import ilja615.iljatech.entity.AbstractGasCloud;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GassEntityRender extends EntityRenderer<AbstractGasCloud>
{
    public GassEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(AbstractGasCloud entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}