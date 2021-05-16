package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternTileEntityRenderer extends TileEntityRenderer<LecternTileEntity> {
   private final BookModel bookModel = new BookModel();

   public LecternTileEntityRenderer(TileEntityRendererDispatcher p_i226011_1_) {
      super(p_i226011_1_);
   }

   public void render(LecternTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      BlockState blockstate = p_225616_1_.getBlockState();
      if (blockstate.getValue(LecternBlock.HAS_BOOK)) {
         p_225616_3_.pushPose();
         p_225616_3_.translate(0.5D, 1.0625D, 0.5D);
         float f = blockstate.getValue(LecternBlock.FACING).getClockWise().toYRot();
         p_225616_3_.mulPose(Vector3f.YP.rotationDegrees(-f));
         p_225616_3_.mulPose(Vector3f.ZP.rotationDegrees(67.5F));
         p_225616_3_.translate(0.0D, -0.125D, 0.0D);
         this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
         IVertexBuilder ivertexbuilder = EnchantmentTableTileEntityRenderer.BOOK_LOCATION.buffer(p_225616_4_, RenderType::entitySolid);
         this.bookModel.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_, 1.0F, 1.0F, 1.0F, 1.0F);
         p_225616_3_.popPose();
      }
   }
}
