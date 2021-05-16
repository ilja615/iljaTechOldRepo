package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentTableTileEntityRenderer extends TileEntityRenderer<EnchantingTableTileEntity> {
   public static final RenderMaterial BOOK_LOCATION = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
   private final BookModel bookModel = new BookModel();

   public EnchantmentTableTileEntityRenderer(TileEntityRendererDispatcher p_i226010_1_) {
      super(p_i226010_1_);
   }

   public void render(EnchantingTableTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      p_225616_3_.pushPose();
      p_225616_3_.translate(0.5D, 0.75D, 0.5D);
      float f = (float)p_225616_1_.time + p_225616_2_;
      p_225616_3_.translate(0.0D, (double)(0.1F + MathHelper.sin(f * 0.1F) * 0.01F), 0.0D);

      float f1;
      for(f1 = p_225616_1_.rot - p_225616_1_.oRot; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
      }

      while(f1 < -(float)Math.PI) {
         f1 += ((float)Math.PI * 2F);
      }

      float f2 = p_225616_1_.oRot + f1 * p_225616_2_;
      p_225616_3_.mulPose(Vector3f.YP.rotation(-f2));
      p_225616_3_.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
      float f3 = MathHelper.lerp(p_225616_2_, p_225616_1_.oFlip, p_225616_1_.flip);
      float f4 = MathHelper.frac(f3 + 0.25F) * 1.6F - 0.3F;
      float f5 = MathHelper.frac(f3 + 0.75F) * 1.6F - 0.3F;
      float f6 = MathHelper.lerp(p_225616_2_, p_225616_1_.oOpen, p_225616_1_.open);
      this.bookModel.setupAnim(f, MathHelper.clamp(f4, 0.0F, 1.0F), MathHelper.clamp(f5, 0.0F, 1.0F), f6);
      IVertexBuilder ivertexbuilder = BOOK_LOCATION.buffer(p_225616_4_, RenderType::entitySolid);
      this.bookModel.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_, 1.0F, 1.0F, 1.0F, 1.0F);
      p_225616_3_.popPose();
   }
}
