package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.model.StriderModel;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StriderRenderer extends MobRenderer<StriderEntity, StriderModel<StriderEntity>> {
   private static final ResourceLocation STRIDER_LOCATION = new ResourceLocation("textures/entity/strider/strider.png");
   private static final ResourceLocation COLD_LOCATION = new ResourceLocation("textures/entity/strider/strider_cold.png");

   public StriderRenderer(EntityRendererManager p_i232473_1_) {
      super(p_i232473_1_, new StriderModel<>(), 0.5F);
      this.addLayer(new SaddleLayer<>(this, new StriderModel<>(), new ResourceLocation("textures/entity/strider/strider_saddle.png")));
   }

   public ResourceLocation getTextureLocation(StriderEntity p_110775_1_) {
      return p_110775_1_.isSuffocating() ? COLD_LOCATION : STRIDER_LOCATION;
   }

   protected void scale(StriderEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      if (p_225620_1_.isBaby()) {
         p_225620_2_.scale(0.5F, 0.5F, 0.5F);
         this.shadowRadius = 0.25F;
      } else {
         this.shadowRadius = 0.5F;
      }

   }

   protected boolean isShaking(StriderEntity p_230495_1_) {
      return p_230495_1_.isSuffocating();
   }
}
