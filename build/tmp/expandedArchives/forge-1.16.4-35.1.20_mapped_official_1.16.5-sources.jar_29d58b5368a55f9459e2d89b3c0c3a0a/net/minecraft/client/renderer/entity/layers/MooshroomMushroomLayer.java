package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MooshroomMushroomLayer<T extends MooshroomEntity> extends LayerRenderer<T, CowModel<T>> {
   public MooshroomMushroomLayer(IEntityRenderer<T, CowModel<T>> p_i50931_1_) {
      super(p_i50931_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (!p_225628_4_.isBaby() && !p_225628_4_.isInvisible()) {
         BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
         BlockState blockstate = p_225628_4_.getMushroomType().getBlockState();
         int i = LivingRenderer.getOverlayCoords(p_225628_4_, 0.0F);
         p_225628_1_.pushPose();
         p_225628_1_.translate((double)0.2F, (double)-0.35F, 0.5D);
         p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(-48.0F));
         p_225628_1_.scale(-1.0F, -1.0F, 1.0F);
         p_225628_1_.translate(-0.5D, -0.5D, -0.5D);
         blockrendererdispatcher.renderSingleBlock(blockstate, p_225628_1_, p_225628_2_, p_225628_3_, i);
         p_225628_1_.popPose();
         p_225628_1_.pushPose();
         p_225628_1_.translate((double)0.2F, (double)-0.35F, 0.5D);
         p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(42.0F));
         p_225628_1_.translate((double)0.1F, 0.0D, (double)-0.6F);
         p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(-48.0F));
         p_225628_1_.scale(-1.0F, -1.0F, 1.0F);
         p_225628_1_.translate(-0.5D, -0.5D, -0.5D);
         blockrendererdispatcher.renderSingleBlock(blockstate, p_225628_1_, p_225628_2_, p_225628_3_, i);
         p_225628_1_.popPose();
         p_225628_1_.pushPose();
         this.getParentModel().getHead().translateAndRotate(p_225628_1_);
         p_225628_1_.translate(0.0D, (double)-0.7F, (double)-0.2F);
         p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(-78.0F));
         p_225628_1_.scale(-1.0F, -1.0F, 1.0F);
         p_225628_1_.translate(-0.5D, -0.5D, -0.5D);
         blockrendererdispatcher.renderSingleBlock(blockstate, p_225628_1_, p_225628_2_, p_225628_3_, i);
         p_225628_1_.popPose();
      }
   }
}
