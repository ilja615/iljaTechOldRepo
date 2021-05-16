package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BedTileEntityRenderer extends TileEntityRenderer<BedTileEntity> {
   private final ModelRenderer headPiece;
   private final ModelRenderer footPiece;
   private final ModelRenderer[] legs = new ModelRenderer[4];

   public BedTileEntityRenderer(TileEntityRendererDispatcher p_i226004_1_) {
      super(p_i226004_1_);
      this.headPiece = new ModelRenderer(64, 64, 0, 0);
      this.headPiece.addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
      this.footPiece = new ModelRenderer(64, 64, 0, 22);
      this.footPiece.addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
      this.legs[0] = new ModelRenderer(64, 64, 50, 0);
      this.legs[1] = new ModelRenderer(64, 64, 50, 6);
      this.legs[2] = new ModelRenderer(64, 64, 50, 12);
      this.legs[3] = new ModelRenderer(64, 64, 50, 18);
      this.legs[0].addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
      this.legs[1].addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
      this.legs[2].addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
      this.legs[3].addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
      this.legs[0].xRot = ((float)Math.PI / 2F);
      this.legs[1].xRot = ((float)Math.PI / 2F);
      this.legs[2].xRot = ((float)Math.PI / 2F);
      this.legs[3].xRot = ((float)Math.PI / 2F);
      this.legs[0].zRot = 0.0F;
      this.legs[1].zRot = ((float)Math.PI / 2F);
      this.legs[2].zRot = ((float)Math.PI * 1.5F);
      this.legs[3].zRot = (float)Math.PI;
   }

   public void render(BedTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      RenderMaterial rendermaterial = Atlases.BED_TEXTURES[p_225616_1_.getColor().getId()];
      World world = p_225616_1_.getLevel();
      if (world != null) {
         BlockState blockstate = p_225616_1_.getBlockState();
         TileEntityMerger.ICallbackWrapper<? extends BedTileEntity> icallbackwrapper = TileEntityMerger.combineWithNeigbour(TileEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, blockstate, world, p_225616_1_.getBlockPos(), (p_228846_0_, p_228846_1_) -> {
            return false;
         });
         int i = icallbackwrapper.<Int2IntFunction>apply(new DualBrightnessCallback<>()).get(p_225616_5_);
         this.renderPiece(p_225616_3_, p_225616_4_, blockstate.getValue(BedBlock.PART) == BedPart.HEAD, blockstate.getValue(BedBlock.FACING), rendermaterial, i, p_225616_6_, false);
      } else {
         this.renderPiece(p_225616_3_, p_225616_4_, true, Direction.SOUTH, rendermaterial, p_225616_5_, p_225616_6_, false);
         this.renderPiece(p_225616_3_, p_225616_4_, false, Direction.SOUTH, rendermaterial, p_225616_5_, p_225616_6_, true);
      }

   }

   private void renderPiece(MatrixStack p_228847_1_, IRenderTypeBuffer p_228847_2_, boolean p_228847_3_, Direction p_228847_4_, RenderMaterial p_228847_5_, int p_228847_6_, int p_228847_7_, boolean p_228847_8_) {
      this.headPiece.visible = p_228847_3_;
      this.footPiece.visible = !p_228847_3_;
      this.legs[0].visible = !p_228847_3_;
      this.legs[1].visible = p_228847_3_;
      this.legs[2].visible = !p_228847_3_;
      this.legs[3].visible = p_228847_3_;
      p_228847_1_.pushPose();
      p_228847_1_.translate(0.0D, 0.5625D, p_228847_8_ ? -1.0D : 0.0D);
      p_228847_1_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      p_228847_1_.translate(0.5D, 0.5D, 0.5D);
      p_228847_1_.mulPose(Vector3f.ZP.rotationDegrees(180.0F + p_228847_4_.toYRot()));
      p_228847_1_.translate(-0.5D, -0.5D, -0.5D);
      IVertexBuilder ivertexbuilder = p_228847_5_.buffer(p_228847_2_, RenderType::entitySolid);
      this.headPiece.render(p_228847_1_, ivertexbuilder, p_228847_6_, p_228847_7_);
      this.footPiece.render(p_228847_1_, ivertexbuilder, p_228847_6_, p_228847_7_);
      this.legs[0].render(p_228847_1_, ivertexbuilder, p_228847_6_, p_228847_7_);
      this.legs[1].render(p_228847_1_, ivertexbuilder, p_228847_6_, p_228847_7_);
      this.legs[2].render(p_228847_1_, ivertexbuilder, p_228847_6_, p_228847_7_);
      this.legs[3].render(p_228847_1_, ivertexbuilder, p_228847_6_, p_228847_7_);
      p_228847_1_.popPose();
   }
}
