package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IronGolenFlowerLayer extends LayerRenderer<IronGolemEntity, IronGolemModel<IronGolemEntity>> {
   public IronGolenFlowerLayer(IEntityRenderer<IronGolemEntity, IronGolemModel<IronGolemEntity>> p_i50935_1_) {
      super(p_i50935_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, IronGolemEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (p_225628_4_.getOfferFlowerTick() != 0) {
         p_225628_1_.pushPose();
         ModelRenderer modelrenderer = this.getParentModel().getFlowerHoldingArm();
         modelrenderer.translateAndRotate(p_225628_1_);
         p_225628_1_.translate(-1.1875D, 1.0625D, -0.9375D);
         p_225628_1_.translate(0.5D, 0.5D, 0.5D);
         float f = 0.5F;
         p_225628_1_.scale(0.5F, 0.5F, 0.5F);
         p_225628_1_.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
         p_225628_1_.translate(-0.5D, -0.5D, -0.5D);
         Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POPPY.defaultBlockState(), p_225628_1_, p_225628_2_, p_225628_3_, OverlayTexture.NO_OVERLAY);
         p_225628_1_.popPose();
      }
   }
}
