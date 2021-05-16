package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class StuckInBodyLayer<T extends LivingEntity, M extends PlayerModel<T>> extends LayerRenderer<T, M> {
   public StuckInBodyLayer(LivingRenderer<T, M> p_i226041_1_) {
      super(p_i226041_1_);
   }

   protected abstract int numStuck(T p_225631_1_);

   protected abstract void renderStuckItem(MatrixStack p_225632_1_, IRenderTypeBuffer p_225632_2_, int p_225632_3_, Entity p_225632_4_, float p_225632_5_, float p_225632_6_, float p_225632_7_, float p_225632_8_);

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      int i = this.numStuck(p_225628_4_);
      Random random = new Random((long)p_225628_4_.getId());
      if (i > 0) {
         for(int j = 0; j < i; ++j) {
            p_225628_1_.pushPose();
            ModelRenderer modelrenderer = this.getParentModel().getRandomModelPart(random);
            ModelRenderer.ModelBox modelrenderer$modelbox = modelrenderer.getRandomCube(random);
            modelrenderer.translateAndRotate(p_225628_1_);
            float f = random.nextFloat();
            float f1 = random.nextFloat();
            float f2 = random.nextFloat();
            float f3 = MathHelper.lerp(f, modelrenderer$modelbox.minX, modelrenderer$modelbox.maxX) / 16.0F;
            float f4 = MathHelper.lerp(f1, modelrenderer$modelbox.minY, modelrenderer$modelbox.maxY) / 16.0F;
            float f5 = MathHelper.lerp(f2, modelrenderer$modelbox.minZ, modelrenderer$modelbox.maxZ) / 16.0F;
            p_225628_1_.translate((double)f3, (double)f4, (double)f5);
            f = -1.0F * (f * 2.0F - 1.0F);
            f1 = -1.0F * (f1 * 2.0F - 1.0F);
            f2 = -1.0F * (f2 * 2.0F - 1.0F);
            this.renderStuckItem(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, f, f1, f2, p_225628_7_);
            p_225628_1_.popPose();
         }

      }
   }
}
