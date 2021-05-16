package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxTileEntityRenderer extends TileEntityRenderer<ShulkerBoxTileEntity> {
   private final ShulkerModel<?> model;

   public ShulkerBoxTileEntityRenderer(ShulkerModel<?> p_i226013_1_, TileEntityRendererDispatcher p_i226013_2_) {
      super(p_i226013_2_);
      this.model = p_i226013_1_;
   }

   public void render(ShulkerBoxTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      Direction direction = Direction.UP;
      if (p_225616_1_.hasLevel()) {
         BlockState blockstate = p_225616_1_.getLevel().getBlockState(p_225616_1_.getBlockPos());
         if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
            direction = blockstate.getValue(ShulkerBoxBlock.FACING);
         }
      }

      DyeColor dyecolor = p_225616_1_.getColor();
      RenderMaterial rendermaterial;
      if (dyecolor == null) {
         rendermaterial = Atlases.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         rendermaterial = Atlases.SHULKER_TEXTURE_LOCATION.get(dyecolor.getId());
      }

      p_225616_3_.pushPose();
      p_225616_3_.translate(0.5D, 0.5D, 0.5D);
      float f = 0.9995F;
      p_225616_3_.scale(0.9995F, 0.9995F, 0.9995F);
      p_225616_3_.mulPose(direction.getRotation());
      p_225616_3_.scale(1.0F, -1.0F, -1.0F);
      p_225616_3_.translate(0.0D, -1.0D, 0.0D);
      IVertexBuilder ivertexbuilder = rendermaterial.buffer(p_225616_4_, RenderType::entityCutoutNoCull);
      this.model.getBase().render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
      p_225616_3_.translate(0.0D, (double)(-p_225616_1_.getProgress(p_225616_2_) * 0.5F), 0.0D);
      p_225616_3_.mulPose(Vector3f.YP.rotationDegrees(270.0F * p_225616_1_.getProgress(p_225616_2_)));
      this.model.getLid().render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
      p_225616_3_.popPose();
   }
}
