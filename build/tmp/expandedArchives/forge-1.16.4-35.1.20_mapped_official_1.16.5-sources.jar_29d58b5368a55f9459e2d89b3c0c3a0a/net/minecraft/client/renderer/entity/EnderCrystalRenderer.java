package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderCrystalRenderer extends EntityRenderer<EnderCrystalEntity> {
   private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
   private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));
   private final ModelRenderer cube;
   private final ModelRenderer glass;
   private final ModelRenderer base;

   public EnderCrystalRenderer(EntityRendererManager p_i46184_1_) {
      super(p_i46184_1_);
      this.shadowRadius = 0.5F;
      this.glass = new ModelRenderer(64, 32, 0, 0);
      this.glass.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.cube = new ModelRenderer(64, 32, 32, 0);
      this.cube.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.base = new ModelRenderer(64, 32, 0, 16);
      this.base.addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
   }

   public void render(EnderCrystalEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.pushPose();
      float f = getY(p_225623_1_, p_225623_3_);
      float f1 = ((float)p_225623_1_.time + p_225623_3_) * 3.0F;
      IVertexBuilder ivertexbuilder = p_225623_5_.getBuffer(RENDER_TYPE);
      p_225623_4_.pushPose();
      p_225623_4_.scale(2.0F, 2.0F, 2.0F);
      p_225623_4_.translate(0.0D, -0.5D, 0.0D);
      int i = OverlayTexture.NO_OVERLAY;
      if (p_225623_1_.showsBottom()) {
         this.base.render(p_225623_4_, ivertexbuilder, p_225623_6_, i);
      }

      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(f1));
      p_225623_4_.translate(0.0D, (double)(1.5F + f / 2.0F), 0.0D);
      p_225623_4_.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      this.glass.render(p_225623_4_, ivertexbuilder, p_225623_6_, i);
      float f2 = 0.875F;
      p_225623_4_.scale(0.875F, 0.875F, 0.875F);
      p_225623_4_.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.glass.render(p_225623_4_, ivertexbuilder, p_225623_6_, i);
      p_225623_4_.scale(0.875F, 0.875F, 0.875F);
      p_225623_4_.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.cube.render(p_225623_4_, ivertexbuilder, p_225623_6_, i);
      p_225623_4_.popPose();
      p_225623_4_.popPose();
      BlockPos blockpos = p_225623_1_.getBeamTarget();
      if (blockpos != null) {
         float f3 = (float)blockpos.getX() + 0.5F;
         float f4 = (float)blockpos.getY() + 0.5F;
         float f5 = (float)blockpos.getZ() + 0.5F;
         float f6 = (float)((double)f3 - p_225623_1_.getX());
         float f7 = (float)((double)f4 - p_225623_1_.getY());
         float f8 = (float)((double)f5 - p_225623_1_.getZ());
         p_225623_4_.translate((double)f6, (double)f7, (double)f8);
         EnderDragonRenderer.renderCrystalBeams(-f6, -f7 + f, -f8, p_225623_3_, p_225623_1_.time, p_225623_4_, p_225623_5_, p_225623_6_);
      }

      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public static float getY(EnderCrystalEntity p_229051_0_, float p_229051_1_) {
      float f = (float)p_229051_0_.time + p_229051_1_;
      float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
      f1 = (f1 * f1 + f1) * 0.4F;
      return f1 - 1.4F;
   }

   public ResourceLocation getTextureLocation(EnderCrystalEntity p_110775_1_) {
      return END_CRYSTAL_LOCATION;
   }

   public boolean shouldRender(EnderCrystalEntity p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      return super.shouldRender(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_) || p_225626_1_.getBeamTarget() != null;
   }
}
