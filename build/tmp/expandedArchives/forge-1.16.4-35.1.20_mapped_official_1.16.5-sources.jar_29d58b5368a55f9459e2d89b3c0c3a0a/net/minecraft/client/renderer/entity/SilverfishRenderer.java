package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.SilverfishModel;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilverfishRenderer extends MobRenderer<SilverfishEntity, SilverfishModel<SilverfishEntity>> {
   private static final ResourceLocation SILVERFISH_LOCATION = new ResourceLocation("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRendererManager p_i46144_1_) {
      super(p_i46144_1_, new SilverfishModel<>(), 0.3F);
   }

   protected float getFlipDegrees(SilverfishEntity p_77037_1_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(SilverfishEntity p_110775_1_) {
      return SILVERFISH_LOCATION;
   }
}
