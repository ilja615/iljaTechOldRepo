package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AreaEffectCloudRenderer extends EntityRenderer<AreaEffectCloudEntity> {
   public AreaEffectCloudRenderer(EntityRendererManager p_i46554_1_) {
      super(p_i46554_1_);
   }

   public ResourceLocation getTextureLocation(AreaEffectCloudEntity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
}
