package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class MobRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {
   public MobRenderer(EntityRendererManager p_i50961_1_, M p_i50961_2_, float p_i50961_3_) {
      super(p_i50961_1_, p_i50961_2_, p_i50961_3_);
   }

   protected boolean shouldShowName(T p_177070_1_) {
      return super.shouldShowName(p_177070_1_) && (p_177070_1_.shouldShowName() || p_177070_1_.hasCustomName() && p_177070_1_ == this.entityRenderDispatcher.crosshairPickEntity);
   }

   public boolean shouldRender(T p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      if (super.shouldRender(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_)) {
         return true;
      } else {
         Entity entity = p_225626_1_.getLeashHolder();
         return entity != null ? p_225626_2_.isVisible(entity.getBoundingBoxForCulling()) : false;
      }
   }

   public void render(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      Entity entity = p_225623_1_.getLeashHolder();
      if (entity != null) {
         this.renderLeash(p_225623_1_, p_225623_3_, p_225623_4_, p_225623_5_, entity);
      }
   }

   private <E extends Entity> void renderLeash(T p_229118_1_, float p_229118_2_, MatrixStack p_229118_3_, IRenderTypeBuffer p_229118_4_, E p_229118_5_) {
      p_229118_3_.pushPose();
      Vector3d vector3d = p_229118_5_.getRopeHoldPosition(p_229118_2_);
      double d0 = (double)(MathHelper.lerp(p_229118_2_, p_229118_1_.yBodyRot, p_229118_1_.yBodyRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
      Vector3d vector3d1 = p_229118_1_.getLeashOffset();
      double d1 = Math.cos(d0) * vector3d1.z + Math.sin(d0) * vector3d1.x;
      double d2 = Math.sin(d0) * vector3d1.z - Math.cos(d0) * vector3d1.x;
      double d3 = MathHelper.lerp((double)p_229118_2_, p_229118_1_.xo, p_229118_1_.getX()) + d1;
      double d4 = MathHelper.lerp((double)p_229118_2_, p_229118_1_.yo, p_229118_1_.getY()) + vector3d1.y;
      double d5 = MathHelper.lerp((double)p_229118_2_, p_229118_1_.zo, p_229118_1_.getZ()) + d2;
      p_229118_3_.translate(d1, vector3d1.y, d2);
      float f = (float)(vector3d.x - d3);
      float f1 = (float)(vector3d.y - d4);
      float f2 = (float)(vector3d.z - d5);
      float f3 = 0.025F;
      IVertexBuilder ivertexbuilder = p_229118_4_.getBuffer(RenderType.leash());
      Matrix4f matrix4f = p_229118_3_.last().pose();
      float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
      float f5 = f2 * f4;
      float f6 = f * f4;
      BlockPos blockpos = new BlockPos(p_229118_1_.getEyePosition(p_229118_2_));
      BlockPos blockpos1 = new BlockPos(p_229118_5_.getEyePosition(p_229118_2_));
      int i = this.getBlockLightLevel(p_229118_1_, blockpos);
      int j = this.entityRenderDispatcher.getRenderer(p_229118_5_).getBlockLightLevel(p_229118_5_, blockpos1);
      int k = p_229118_1_.level.getBrightness(LightType.SKY, blockpos);
      int l = p_229118_1_.level.getBrightness(LightType.SKY, blockpos1);
      renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6);
      renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6);
      p_229118_3_.popPose();
   }

   public static void renderSide(IVertexBuilder p_229119_0_, Matrix4f p_229119_1_, float p_229119_2_, float p_229119_3_, float p_229119_4_, int p_229119_5_, int p_229119_6_, int p_229119_7_, int p_229119_8_, float p_229119_9_, float p_229119_10_, float p_229119_11_, float p_229119_12_) {
      int i = 24;

      for(int j = 0; j < 24; ++j) {
         float f = (float)j / 23.0F;
         int k = (int)MathHelper.lerp(f, (float)p_229119_5_, (float)p_229119_6_);
         int l = (int)MathHelper.lerp(f, (float)p_229119_7_, (float)p_229119_8_);
         int i1 = LightTexture.pack(k, l);
         addVertexPair(p_229119_0_, p_229119_1_, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j, false, p_229119_11_, p_229119_12_);
         addVertexPair(p_229119_0_, p_229119_1_, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j + 1, true, p_229119_11_, p_229119_12_);
      }

   }

   public static void addVertexPair(IVertexBuilder p_229120_0_, Matrix4f p_229120_1_, int p_229120_2_, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_, float p_229120_11_, float p_229120_12_) {
      float f = 0.5F;
      float f1 = 0.4F;
      float f2 = 0.3F;
      if (p_229120_9_ % 2 == 0) {
         f *= 0.7F;
         f1 *= 0.7F;
         f2 *= 0.7F;
      }

      float f3 = (float)p_229120_9_ / (float)p_229120_8_;
      float f4 = p_229120_3_ * f3;
      float f5 = p_229120_4_ > 0.0F ? p_229120_4_ * f3 * f3 : p_229120_4_ - p_229120_4_ * (1.0F - f3) * (1.0F - f3);
      float f6 = p_229120_5_ * f3;
      if (!p_229120_10_) {
         p_229120_0_.vertex(p_229120_1_, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
      }

      p_229120_0_.vertex(p_229120_1_, f4 - p_229120_11_, f5 + p_229120_7_, f6 + p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
      if (p_229120_10_) {
         p_229120_0_.vertex(p_229120_1_, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).uv2(p_229120_2_).endVertex();
      }

   }
}
