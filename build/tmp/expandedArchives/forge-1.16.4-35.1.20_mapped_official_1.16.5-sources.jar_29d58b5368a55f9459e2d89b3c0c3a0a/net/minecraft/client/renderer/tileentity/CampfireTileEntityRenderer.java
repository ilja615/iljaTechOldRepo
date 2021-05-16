package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CampfireTileEntityRenderer extends TileEntityRenderer<CampfireTileEntity> {
   public CampfireTileEntityRenderer(TileEntityRendererDispatcher p_i226007_1_) {
      super(p_i226007_1_);
   }

   public void render(CampfireTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      Direction direction = p_225616_1_.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList<ItemStack> nonnulllist = p_225616_1_.getItems();

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = nonnulllist.get(i);
         if (itemstack != ItemStack.EMPTY) {
            p_225616_3_.pushPose();
            p_225616_3_.translate(0.5D, 0.44921875D, 0.5D);
            Direction direction1 = Direction.from2DDataValue((i + direction.get2DDataValue()) % 4);
            float f = -direction1.toYRot();
            p_225616_3_.mulPose(Vector3f.YP.rotationDegrees(f));
            p_225616_3_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            p_225616_3_.translate(-0.3125D, -0.3125D, 0.0D);
            p_225616_3_.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.FIXED, p_225616_5_, p_225616_6_, p_225616_3_, p_225616_4_);
            p_225616_3_.popPose();
         }
      }

   }
}
