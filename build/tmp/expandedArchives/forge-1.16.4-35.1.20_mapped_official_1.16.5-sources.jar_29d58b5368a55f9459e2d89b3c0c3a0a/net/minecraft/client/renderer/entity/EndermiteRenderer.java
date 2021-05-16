package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.EndermiteModel;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermiteRenderer extends MobRenderer<EndermiteEntity, EndermiteModel<EndermiteEntity>> {
   private static final ResourceLocation ENDERMITE_LOCATION = new ResourceLocation("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRendererManager p_i46181_1_) {
      super(p_i46181_1_, new EndermiteModel<>(), 0.3F);
   }

   protected float getFlipDegrees(EndermiteEntity p_77037_1_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(EndermiteEntity p_110775_1_) {
      return ENDERMITE_LOCATION;
   }
}
