package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZoglinRenderer extends MobRenderer<ZoglinEntity, BoarModel<ZoglinEntity>> {
   private static final ResourceLocation ZOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/zoglin.png");

   public ZoglinRenderer(EntityRendererManager p_i232474_1_) {
      super(p_i232474_1_, new BoarModel<>(), 0.7F);
   }

   public ResourceLocation getTextureLocation(ZoglinEntity p_110775_1_) {
      return ZOGLIN_LOCATION;
   }
}
