package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.PolarBearModel;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PolarBearRenderer extends MobRenderer<PolarBearEntity, PolarBearModel<PolarBearEntity>> {
   private static final ResourceLocation BEAR_LOCATION = new ResourceLocation("textures/entity/bear/polarbear.png");

   public PolarBearRenderer(EntityRendererManager p_i47197_1_) {
      super(p_i47197_1_, new PolarBearModel<>(), 0.9F);
   }

   public ResourceLocation getTextureLocation(PolarBearEntity p_110775_1_) {
      return BEAR_LOCATION;
   }

   protected void scale(PolarBearEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      p_225620_2_.scale(1.2F, 1.2F, 1.2F);
      super.scale(p_225620_1_, p_225620_2_, p_225620_3_);
   }
}
