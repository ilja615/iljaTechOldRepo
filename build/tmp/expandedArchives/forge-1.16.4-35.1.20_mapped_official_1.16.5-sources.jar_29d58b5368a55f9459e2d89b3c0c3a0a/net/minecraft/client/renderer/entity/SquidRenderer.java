package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.SquidModel;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidRenderer extends MobRenderer<SquidEntity, SquidModel<SquidEntity>> {
   private static final ResourceLocation SQUID_LOCATION = new ResourceLocation("textures/entity/squid.png");

   public SquidRenderer(EntityRendererManager p_i47192_1_) {
      super(p_i47192_1_, new SquidModel<>(), 0.7F);
   }

   public ResourceLocation getTextureLocation(SquidEntity p_110775_1_) {
      return SQUID_LOCATION;
   }

   protected void setupRotations(SquidEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      float f = MathHelper.lerp(p_225621_5_, p_225621_1_.xBodyRotO, p_225621_1_.xBodyRot);
      float f1 = MathHelper.lerp(p_225621_5_, p_225621_1_.zBodyRotO, p_225621_1_.zBodyRot);
      p_225621_2_.translate(0.0D, 0.5D, 0.0D);
      p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_225621_4_));
      p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(f));
      p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(f1));
      p_225621_2_.translate(0.0D, (double)-1.2F, 0.0D);
   }

   protected float getBob(SquidEntity p_77044_1_, float p_77044_2_) {
      return MathHelper.lerp(p_77044_2_, p_77044_1_.oldTentacleAngle, p_77044_1_.tentacleAngle);
   }
}
