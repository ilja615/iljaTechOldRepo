package ilja615.iljatech.client.render;

import ilja615.iljatech.entity.AbstractGasEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GassEntityRender extends EntityRenderer<AbstractGasEntity>
{
    public GassEntityRender(EntityRendererManager manager) {
        super(manager);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(AbstractGasEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}