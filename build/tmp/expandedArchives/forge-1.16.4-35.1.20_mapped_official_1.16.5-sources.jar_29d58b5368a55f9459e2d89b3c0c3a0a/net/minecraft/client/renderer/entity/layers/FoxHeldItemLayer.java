package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeldItemLayer extends LayerRenderer<FoxEntity, FoxModel<FoxEntity>> {
   public FoxHeldItemLayer(IEntityRenderer<FoxEntity, FoxModel<FoxEntity>> p_i50938_1_) {
      super(p_i50938_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, FoxEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      boolean flag = p_225628_4_.isSleeping();
      boolean flag1 = p_225628_4_.isBaby();
      p_225628_1_.pushPose();
      if (flag1) {
         float f = 0.75F;
         p_225628_1_.scale(0.75F, 0.75F, 0.75F);
         p_225628_1_.translate(0.0D, 0.5D, (double)0.209375F);
      }

      p_225628_1_.translate((double)((this.getParentModel()).head.x / 16.0F), (double)((this.getParentModel()).head.y / 16.0F), (double)((this.getParentModel()).head.z / 16.0F));
      float f1 = p_225628_4_.getHeadRollAngle(p_225628_7_);
      p_225628_1_.mulPose(Vector3f.ZP.rotation(f1));
      p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(p_225628_9_));
      p_225628_1_.mulPose(Vector3f.XP.rotationDegrees(p_225628_10_));
      if (p_225628_4_.isBaby()) {
         if (flag) {
            p_225628_1_.translate((double)0.4F, (double)0.26F, (double)0.15F);
         } else {
            p_225628_1_.translate((double)0.06F, (double)0.26F, -0.5D);
         }
      } else if (flag) {
         p_225628_1_.translate((double)0.46F, (double)0.26F, (double)0.22F);
      } else {
         p_225628_1_.translate((double)0.06F, (double)0.27F, -0.5D);
      }

      p_225628_1_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      if (flag) {
         p_225628_1_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

      ItemStack itemstack = p_225628_4_.getItemBySlot(EquipmentSlotType.MAINHAND);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(p_225628_4_, itemstack, ItemCameraTransforms.TransformType.GROUND, false, p_225628_1_, p_225628_2_, p_225628_3_);
      p_225628_1_.popPose();
   }
}
