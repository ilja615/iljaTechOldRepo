package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatRenderer extends MobRenderer<BatEntity, BatModel> {
   private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

   public BatRenderer(EntityRendererManager p_i46192_1_) {
      super(p_i46192_1_, new BatModel(), 0.25F);
   }

   public ResourceLocation getTextureLocation(BatEntity p_110775_1_) {
      return BAT_LOCATION;
   }

   protected void scale(BatEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      p_225620_2_.scale(0.35F, 0.35F, 0.35F);
   }

   protected void setupRotations(BatEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      if (p_225621_1_.isResting()) {
         p_225621_2_.translate(0.0D, (double)-0.1F, 0.0D);
      } else {
         p_225621_2_.translate(0.0D, (double)(MathHelper.cos(p_225621_3_ * 0.3F) * 0.1F), 0.0D);
      }

      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
   }
}
