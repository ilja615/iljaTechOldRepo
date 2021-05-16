package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.SalmonModel;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SalmonRenderer extends MobRenderer<SalmonEntity, SalmonModel<SalmonEntity>> {
   private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

   public SalmonRenderer(EntityRendererManager p_i48862_1_) {
      super(p_i48862_1_, new SalmonModel<>(), 0.4F);
   }

   public ResourceLocation getTextureLocation(SalmonEntity p_110775_1_) {
      return SALMON_LOCATION;
   }

   protected void setupRotations(SalmonEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
      float f = 1.0F;
      float f1 = 1.0F;
      if (!p_225621_1_.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      float f2 = f * 4.3F * MathHelper.sin(f1 * 0.6F * p_225621_3_);
      p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(f2));
      p_225621_2_.translate(0.0D, 0.0D, (double)-0.4F);
      if (!p_225621_1_.isInWater()) {
         p_225621_2_.translate((double)0.2F, (double)0.1F, 0.0D);
         p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

   }
}
