package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchHeldItemLayer<T extends LivingEntity> extends CrossedArmsItemLayer<T, WitchModel<T>> {
   public WitchHeldItemLayer(IEntityRenderer<T, WitchModel<T>> p_i50916_1_) {
      super(p_i50916_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      ItemStack itemstack = p_225628_4_.getMainHandItem();
      p_225628_1_.pushPose();
      if (itemstack.getItem() == Items.POTION) {
         this.getParentModel().getHead().translateAndRotate(p_225628_1_);
         this.getParentModel().getNose().translateAndRotate(p_225628_1_);
         p_225628_1_.translate(0.0625D, 0.25D, 0.0D);
         p_225628_1_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         p_225628_1_.mulPose(Vector3f.XP.rotationDegrees(140.0F));
         p_225628_1_.mulPose(Vector3f.ZP.rotationDegrees(10.0F));
         p_225628_1_.translate(0.0D, (double)-0.4F, (double)0.4F);
      }

      super.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
      p_225628_1_.popPose();
   }
}
