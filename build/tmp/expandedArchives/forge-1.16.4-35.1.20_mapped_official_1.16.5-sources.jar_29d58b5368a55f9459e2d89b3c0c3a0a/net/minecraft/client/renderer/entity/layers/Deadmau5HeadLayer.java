package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Deadmau5HeadLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public Deadmau5HeadLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50945_1_) {
      super(p_i50945_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, AbstractClientPlayerEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if ("deadmau5".equals(p_225628_4_.getName().getString()) && p_225628_4_.isSkinLoaded() && !p_225628_4_.isInvisible()) {
         IVertexBuilder ivertexbuilder = p_225628_2_.getBuffer(RenderType.entitySolid(p_225628_4_.getSkinTextureLocation()));
         int i = LivingRenderer.getOverlayCoords(p_225628_4_, 0.0F);

         for(int j = 0; j < 2; ++j) {
            float f = MathHelper.lerp(p_225628_7_, p_225628_4_.yRotO, p_225628_4_.yRot) - MathHelper.lerp(p_225628_7_, p_225628_4_.yBodyRotO, p_225628_4_.yBodyRot);
            float f1 = MathHelper.lerp(p_225628_7_, p_225628_4_.xRotO, p_225628_4_.xRot);
            p_225628_1_.pushPose();
            p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(f));
            p_225628_1_.mulPose(Vector3f.XP.rotationDegrees(f1));
            p_225628_1_.translate((double)(0.375F * (float)(j * 2 - 1)), 0.0D, 0.0D);
            p_225628_1_.translate(0.0D, -0.375D, 0.0D);
            p_225628_1_.mulPose(Vector3f.XP.rotationDegrees(-f1));
            p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(-f));
            float f2 = 1.3333334F;
            p_225628_1_.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.getParentModel().renderEars(p_225628_1_, ivertexbuilder, p_225628_3_, i);
            p_225628_1_.popPose();
         }

      }
   }
}
