package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer extends EntityRenderer<ItemEntity> {
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
   private final Random random = new Random();

   public ItemRenderer(EntityRendererManager p_i46167_1_, net.minecraft.client.renderer.ItemRenderer p_i46167_2_) {
      super(p_i46167_1_);
      this.itemRenderer = p_i46167_2_;
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getRenderAmount(ItemStack p_177078_1_) {
      int i = 1;
      if (p_177078_1_.getCount() > 48) {
         i = 5;
      } else if (p_177078_1_.getCount() > 32) {
         i = 4;
      } else if (p_177078_1_.getCount() > 16) {
         i = 3;
      } else if (p_177078_1_.getCount() > 1) {
         i = 2;
      }

      return i;
   }

   public void render(ItemEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.pushPose();
      ItemStack itemstack = p_225623_1_.getItem();
      int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
      this.random.setSeed((long)i);
      IBakedModel ibakedmodel = this.itemRenderer.getModel(itemstack, p_225623_1_.level, (LivingEntity)null);
      boolean flag = ibakedmodel.isGui3d();
      int j = this.getRenderAmount(itemstack);
      float f = 0.25F;
      float f1 = MathHelper.sin(((float)p_225623_1_.getAge() + p_225623_3_) / 10.0F + p_225623_1_.bobOffs) * 0.1F + 0.1F;
      float f2 = shouldBob() ? ibakedmodel.getTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y() : 0;
      p_225623_4_.translate(0.0D, (double)(f1 + 0.25F * f2), 0.0D);
      float f3 = p_225623_1_.getSpin(p_225623_3_);
      p_225623_4_.mulPose(Vector3f.YP.rotation(f3));
      if (!flag) {
         float f7 = -0.0F * (float)(j - 1) * 0.5F;
         float f8 = -0.0F * (float)(j - 1) * 0.5F;
         float f9 = -0.09375F * (float)(j - 1) * 0.5F;
         p_225623_4_.translate((double)f7, (double)f8, (double)f9);
      }

      for(int k = 0; k < j; ++k) {
         p_225623_4_.pushPose();
         if (k > 0) {
            if (flag) {
               float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               p_225623_4_.translate(shouldSpreadItems() ? f11 : 0, shouldSpreadItems() ? f13 : 0, shouldSpreadItems() ? f10 : 0);
            } else {
               float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               p_225623_4_.translate(shouldSpreadItems() ? f12 : 0, shouldSpreadItems() ? f14 : 0, 0.0D);
            }
         }

         this.itemRenderer.render(itemstack, ItemCameraTransforms.TransformType.GROUND, false, p_225623_4_, p_225623_5_, p_225623_6_, OverlayTexture.NO_OVERLAY, ibakedmodel);
         p_225623_4_.popPose();
         if (!flag) {
            p_225623_4_.translate(0.0, 0.0, 0.09375F);
         }
      }

      p_225623_4_.popPose();
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(ItemEntity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
   
   /*==================================== FORGE START ===========================================*/

   /**
    * @return If items should spread out when rendered in 3D
    */
   public boolean shouldSpreadItems() {
      return true;
   }

   /**
    * @return If items should have a bob effect
    */
   public boolean shouldBob() {
      return true;
   }
   /*==================================== FORGE END =============================================*/
}
