package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TippedArrowRenderer extends ArrowRenderer<ArrowEntity> {
   public static final ResourceLocation NORMAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png");
   public static final ResourceLocation TIPPED_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

   public TippedArrowRenderer(EntityRendererManager p_i46547_1_) {
      super(p_i46547_1_);
   }

   public ResourceLocation getTextureLocation(ArrowEntity p_110775_1_) {
      return p_110775_1_.getColor() > 0 ? TIPPED_ARROW_LOCATION : NORMAL_ARROW_LOCATION;
   }
}
