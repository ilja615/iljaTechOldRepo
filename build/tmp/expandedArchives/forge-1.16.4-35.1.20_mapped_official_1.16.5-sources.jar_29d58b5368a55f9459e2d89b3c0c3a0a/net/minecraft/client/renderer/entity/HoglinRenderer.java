package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HoglinRenderer extends MobRenderer<HoglinEntity, BoarModel<HoglinEntity>> {
   private static final ResourceLocation HOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/hoglin.png");

   public HoglinRenderer(EntityRendererManager p_i232470_1_) {
      super(p_i232470_1_, new BoarModel<>(), 0.7F);
   }

   public ResourceLocation getTextureLocation(HoglinEntity p_110775_1_) {
      return HOGLIN_LOCATION;
   }

   protected boolean isShaking(HoglinEntity p_230495_1_) {
      return p_230495_1_.isConverting();
   }
}
