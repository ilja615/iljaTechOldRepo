package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectralArrowRenderer extends ArrowRenderer<SpectralArrowEntity> {
   public static final ResourceLocation SPECTRAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowRenderer(EntityRendererManager p_i46549_1_) {
      super(p_i46549_1_);
   }

   public ResourceLocation getTextureLocation(SpectralArrowEntity p_110775_1_) {
      return SPECTRAL_ARROW_LOCATION;
   }
}
