package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BellTileEntityRenderer extends TileEntityRenderer<BellTileEntity> {
   public static final RenderMaterial BELL_RESOURCE_LOCATION = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/bell/bell_body"));
   private final ModelRenderer bellBody = new ModelRenderer(32, 32, 0, 0);

   public BellTileEntityRenderer(TileEntityRendererDispatcher p_i226005_1_) {
      super(p_i226005_1_);
      this.bellBody.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
      this.bellBody.setPos(8.0F, 12.0F, 8.0F);
      ModelRenderer modelrenderer = new ModelRenderer(32, 32, 0, 13);
      modelrenderer.addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
      modelrenderer.setPos(-8.0F, -12.0F, -8.0F);
      this.bellBody.addChild(modelrenderer);
   }

   public void render(BellTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      float f = (float)p_225616_1_.ticks + p_225616_2_;
      float f1 = 0.0F;
      float f2 = 0.0F;
      if (p_225616_1_.shaking) {
         float f3 = MathHelper.sin(f / (float)Math.PI) / (4.0F + f / 3.0F);
         if (p_225616_1_.clickDirection == Direction.NORTH) {
            f1 = -f3;
         } else if (p_225616_1_.clickDirection == Direction.SOUTH) {
            f1 = f3;
         } else if (p_225616_1_.clickDirection == Direction.EAST) {
            f2 = -f3;
         } else if (p_225616_1_.clickDirection == Direction.WEST) {
            f2 = f3;
         }
      }

      this.bellBody.xRot = f1;
      this.bellBody.zRot = f2;
      IVertexBuilder ivertexbuilder = BELL_RESOURCE_LOCATION.buffer(p_225616_4_, RenderType::entitySolid);
      this.bellBody.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
   }
}
