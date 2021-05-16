package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelRenderer {
   private final BlockColors blockColors;
   private static final ThreadLocal<BlockModelRenderer.Cache> CACHE = ThreadLocal.withInitial(() -> {
      return new BlockModelRenderer.Cache();
   });

   public BlockModelRenderer(BlockColors p_i46575_1_) {
      this.blockColors = p_i46575_1_;
   }

   @Deprecated //Forge: Model data argument
   public boolean tesselateBlock(IBlockDisplayReader p_228802_1_, IBakedModel p_228802_2_, BlockState p_228802_3_, BlockPos p_228802_4_, MatrixStack p_228802_5_, IVertexBuilder p_228802_6_, boolean p_228802_7_, Random p_228802_8_, long p_228802_9_, int p_228802_11_) {
       return renderModel(p_228802_1_, p_228802_2_, p_228802_3_, p_228802_4_, p_228802_5_, p_228802_6_, p_228802_7_, p_228802_8_, p_228802_9_, p_228802_11_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderModel(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = Minecraft.useAmbientOcclusion() && stateIn.getLightValue(worldIn, posIn) == 0 && modelIn.useAmbientOcclusion();
      Vector3d vector3d = stateIn.getOffset(worldIn, posIn);
      matrixIn.translate(vector3d.x, vector3d.y, vector3d.z);
      modelData = modelIn.getModelData(worldIn, posIn, stateIn, modelData);

      try {
         return flag ? this.renderModelSmooth(worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, modelData) : this.renderModelFlat(worldIn, modelIn, stateIn, posIn, matrixIn, buffer, checkSides, randomIn, rand, combinedOverlayIn, modelData);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block model");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block model being tesselated");
         CrashReportCategory.populateBlockDetails(crashreportcategory, posIn, stateIn);
         crashreportcategory.setDetail("Using AO", flag);
         throw new ReportedException(crashreport);
      }
   }

   @Deprecated //Forge: Model data argument
   public boolean tesselateWithAO(IBlockDisplayReader p_228805_1_, IBakedModel p_228805_2_, BlockState p_228805_3_, BlockPos p_228805_4_, MatrixStack p_228805_5_, IVertexBuilder p_228805_6_, boolean p_228805_7_, Random p_228805_8_, long p_228805_9_, int p_228805_11_) {
       return renderModelSmooth(p_228805_1_, p_228805_2_, p_228805_3_, p_228805_4_, p_228805_5_, p_228805_6_, p_228805_7_, p_228805_8_, p_228805_9_, p_228805_11_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderModelSmooth(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      float[] afloat = new float[Direction.values().length * 2];
      BitSet bitset = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new BlockModelRenderer.AmbientOcclusionFace();

      for(Direction direction : Direction.values()) {
         randomIn.setSeed(rand);
         List<BakedQuad> list = modelIn.getQuads(stateIn, direction, randomIn, modelData);
         if (!list.isEmpty() && (!checkSides || Block.shouldRenderFace(stateIn, worldIn, posIn, direction))) {
            this.renderModelFaceAO(worldIn, stateIn, posIn, matrixStackIn, buffer, list, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlayIn);
            flag = true;
         }
      }

      randomIn.setSeed(rand);
      List<BakedQuad> list1 = modelIn.getQuads(stateIn, (Direction)null, randomIn, modelData);
      if (!list1.isEmpty()) {
         this.renderModelFaceAO(worldIn, stateIn, posIn, matrixStackIn, buffer, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlayIn);
         flag = true;
      }

      return flag;
   }

   @Deprecated //Forge: Model data argument
   public boolean tesselateWithoutAO(IBlockDisplayReader p_228806_1_, IBakedModel p_228806_2_, BlockState p_228806_3_, BlockPos p_228806_4_, MatrixStack p_228806_5_, IVertexBuilder p_228806_6_, boolean p_228806_7_, Random p_228806_8_, long p_228806_9_, int p_228806_11_) {
       return renderModelFlat(p_228806_1_, p_228806_2_, p_228806_3_, p_228806_4_, p_228806_5_, p_228806_6_, p_228806_7_, p_228806_8_, p_228806_9_, p_228806_11_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderModelFlat(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, boolean checkSides, Random randomIn, long rand, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      BitSet bitset = new BitSet(3);

      for(Direction direction : Direction.values()) {
         randomIn.setSeed(rand);
         List<BakedQuad> list = modelIn.getQuads(stateIn, direction, randomIn, modelData);
         if (!list.isEmpty() && (!checkSides || Block.shouldRenderFace(stateIn, worldIn, posIn, direction))) {
            int i = WorldRenderer.getLightColor(worldIn, stateIn, posIn.relative(direction));
            this.renderModelFaceFlat(worldIn, stateIn, posIn, i, combinedOverlayIn, false, matrixStackIn, buffer, list, bitset);
            flag = true;
         }
      }

      randomIn.setSeed(rand);
      List<BakedQuad> list1 = modelIn.getQuads(stateIn, (Direction)null, randomIn, modelData);
      if (!list1.isEmpty()) {
         this.renderModelFaceFlat(worldIn, stateIn, posIn, -1, combinedOverlayIn, true, matrixStackIn, buffer, list1, bitset);
         flag = true;
      }

      return flag;
   }

   private void renderModelFaceAO(IBlockDisplayReader p_228799_1_, BlockState p_228799_2_, BlockPos p_228799_3_, MatrixStack p_228799_4_, IVertexBuilder p_228799_5_, List<BakedQuad> p_228799_6_, float[] p_228799_7_, BitSet p_228799_8_, BlockModelRenderer.AmbientOcclusionFace p_228799_9_, int p_228799_10_) {
      for(BakedQuad bakedquad : p_228799_6_) {
         this.calculateShape(p_228799_1_, p_228799_2_, p_228799_3_, bakedquad.getVertices(), bakedquad.getDirection(), p_228799_7_, p_228799_8_);
         p_228799_9_.calculate(p_228799_1_, p_228799_2_, p_228799_3_, bakedquad.getDirection(), p_228799_7_, p_228799_8_, bakedquad.isShade());
         this.putQuadData(p_228799_1_, p_228799_2_, p_228799_3_, p_228799_5_, p_228799_4_.last(), bakedquad, p_228799_9_.brightness[0], p_228799_9_.brightness[1], p_228799_9_.brightness[2], p_228799_9_.brightness[3], p_228799_9_.lightmap[0], p_228799_9_.lightmap[1], p_228799_9_.lightmap[2], p_228799_9_.lightmap[3], p_228799_10_);
      }

   }

   private void putQuadData(IBlockDisplayReader p_228800_1_, BlockState p_228800_2_, BlockPos p_228800_3_, IVertexBuilder p_228800_4_, MatrixStack.Entry p_228800_5_, BakedQuad p_228800_6_, float p_228800_7_, float p_228800_8_, float p_228800_9_, float p_228800_10_, int p_228800_11_, int p_228800_12_, int p_228800_13_, int p_228800_14_, int p_228800_15_) {
      float f;
      float f1;
      float f2;
      if (p_228800_6_.isTinted()) {
         int i = this.blockColors.getColor(p_228800_2_, p_228800_1_, p_228800_3_, p_228800_6_.getTintIndex());
         f = (float)(i >> 16 & 255) / 255.0F;
         f1 = (float)(i >> 8 & 255) / 255.0F;
         f2 = (float)(i & 255) / 255.0F;
      } else {
         f = 1.0F;
         f1 = 1.0F;
         f2 = 1.0F;
      }

      p_228800_4_.putBulkData(p_228800_5_, p_228800_6_, new float[]{p_228800_7_, p_228800_8_, p_228800_9_, p_228800_10_}, f, f1, f2, new int[]{p_228800_11_, p_228800_12_, p_228800_13_, p_228800_14_}, p_228800_15_, true);
   }

   private void calculateShape(IBlockDisplayReader p_228801_1_, BlockState p_228801_2_, BlockPos p_228801_3_, int[] p_228801_4_, Direction p_228801_5_, @Nullable float[] p_228801_6_, BitSet p_228801_7_) {
      float f = 32.0F;
      float f1 = 32.0F;
      float f2 = 32.0F;
      float f3 = -32.0F;
      float f4 = -32.0F;
      float f5 = -32.0F;

      for(int i = 0; i < 4; ++i) {
         float f6 = Float.intBitsToFloat(p_228801_4_[i * 8]);
         float f7 = Float.intBitsToFloat(p_228801_4_[i * 8 + 1]);
         float f8 = Float.intBitsToFloat(p_228801_4_[i * 8 + 2]);
         f = Math.min(f, f6);
         f1 = Math.min(f1, f7);
         f2 = Math.min(f2, f8);
         f3 = Math.max(f3, f6);
         f4 = Math.max(f4, f7);
         f5 = Math.max(f5, f8);
      }

      if (p_228801_6_ != null) {
         p_228801_6_[Direction.WEST.get3DDataValue()] = f;
         p_228801_6_[Direction.EAST.get3DDataValue()] = f3;
         p_228801_6_[Direction.DOWN.get3DDataValue()] = f1;
         p_228801_6_[Direction.UP.get3DDataValue()] = f4;
         p_228801_6_[Direction.NORTH.get3DDataValue()] = f2;
         p_228801_6_[Direction.SOUTH.get3DDataValue()] = f5;
         int j = Direction.values().length;
         p_228801_6_[Direction.WEST.get3DDataValue() + j] = 1.0F - f;
         p_228801_6_[Direction.EAST.get3DDataValue() + j] = 1.0F - f3;
         p_228801_6_[Direction.DOWN.get3DDataValue() + j] = 1.0F - f1;
         p_228801_6_[Direction.UP.get3DDataValue() + j] = 1.0F - f4;
         p_228801_6_[Direction.NORTH.get3DDataValue() + j] = 1.0F - f2;
         p_228801_6_[Direction.SOUTH.get3DDataValue() + j] = 1.0F - f5;
      }

      float f9 = 1.0E-4F;
      float f10 = 0.9999F;
      switch(p_228801_5_) {
      case DOWN:
         p_228801_7_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         p_228801_7_.set(0, f1 == f4 && (f1 < 1.0E-4F || p_228801_2_.isCollisionShapeFullBlock(p_228801_1_, p_228801_3_)));
         break;
      case UP:
         p_228801_7_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         p_228801_7_.set(0, f1 == f4 && (f4 > 0.9999F || p_228801_2_.isCollisionShapeFullBlock(p_228801_1_, p_228801_3_)));
         break;
      case NORTH:
         p_228801_7_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         p_228801_7_.set(0, f2 == f5 && (f2 < 1.0E-4F || p_228801_2_.isCollisionShapeFullBlock(p_228801_1_, p_228801_3_)));
         break;
      case SOUTH:
         p_228801_7_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         p_228801_7_.set(0, f2 == f5 && (f5 > 0.9999F || p_228801_2_.isCollisionShapeFullBlock(p_228801_1_, p_228801_3_)));
         break;
      case WEST:
         p_228801_7_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         p_228801_7_.set(0, f == f3 && (f < 1.0E-4F || p_228801_2_.isCollisionShapeFullBlock(p_228801_1_, p_228801_3_)));
         break;
      case EAST:
         p_228801_7_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         p_228801_7_.set(0, f == f3 && (f3 > 0.9999F || p_228801_2_.isCollisionShapeFullBlock(p_228801_1_, p_228801_3_)));
      }

   }

   private void renderModelFaceFlat(IBlockDisplayReader p_228798_1_, BlockState p_228798_2_, BlockPos p_228798_3_, int p_228798_4_, int p_228798_5_, boolean p_228798_6_, MatrixStack p_228798_7_, IVertexBuilder p_228798_8_, List<BakedQuad> p_228798_9_, BitSet p_228798_10_) {
      for(BakedQuad bakedquad : p_228798_9_) {
         if (p_228798_6_) {
            this.calculateShape(p_228798_1_, p_228798_2_, p_228798_3_, bakedquad.getVertices(), bakedquad.getDirection(), (float[])null, p_228798_10_);
            BlockPos blockpos = p_228798_10_.get(0) ? p_228798_3_.relative(bakedquad.getDirection()) : p_228798_3_;
            p_228798_4_ = WorldRenderer.getLightColor(p_228798_1_, p_228798_2_, blockpos);
         }

         float f = p_228798_1_.getShade(bakedquad.getDirection(), bakedquad.isShade());
         this.putQuadData(p_228798_1_, p_228798_2_, p_228798_3_, p_228798_8_, p_228798_7_.last(), bakedquad, f, f, f, f, p_228798_4_, p_228798_4_, p_228798_4_, p_228798_4_, p_228798_5_);
      }

   }

   @Deprecated //Forge: Model data argument
   public void renderModel(MatrixStack.Entry p_228804_1_, IVertexBuilder p_228804_2_, @Nullable BlockState p_228804_3_, IBakedModel p_228804_4_, float p_228804_5_, float p_228804_6_, float p_228804_7_, int p_228804_8_, int p_228804_9_) {
      renderModel(p_228804_1_, p_228804_2_, p_228804_3_, p_228804_4_, p_228804_5_, p_228804_6_, p_228804_7_, p_228804_8_, p_228804_9_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderModel(MatrixStack.Entry p_228804_1_, IVertexBuilder p_228804_2_, @Nullable BlockState p_228804_3_, IBakedModel p_228804_4_, float p_228804_5_, float p_228804_6_, float p_228804_7_, int p_228804_8_, int p_228804_9_, net.minecraftforge.client.model.data.IModelData modelData) {
      Random random = new Random();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         random.setSeed(42L);
         renderQuadList(p_228804_1_, p_228804_2_, p_228804_5_, p_228804_6_, p_228804_7_, p_228804_4_.getQuads(p_228804_3_, direction, random, modelData), p_228804_8_, p_228804_9_);
      }

      random.setSeed(42L);
      renderQuadList(p_228804_1_, p_228804_2_, p_228804_5_, p_228804_6_, p_228804_7_, p_228804_4_.getQuads(p_228804_3_, (Direction)null, random, modelData), p_228804_8_, p_228804_9_);
   }

   private static void renderQuadList(MatrixStack.Entry p_228803_0_, IVertexBuilder p_228803_1_, float p_228803_2_, float p_228803_3_, float p_228803_4_, List<BakedQuad> p_228803_5_, int p_228803_6_, int p_228803_7_) {
      for(BakedQuad bakedquad : p_228803_5_) {
         float f;
         float f1;
         float f2;
         if (bakedquad.isTinted()) {
            f = MathHelper.clamp(p_228803_2_, 0.0F, 1.0F);
            f1 = MathHelper.clamp(p_228803_3_, 0.0F, 1.0F);
            f2 = MathHelper.clamp(p_228803_4_, 0.0F, 1.0F);
         } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
         }

         p_228803_1_.putBulkData(p_228803_0_, bakedquad, f, f1, f2, p_228803_6_, p_228803_7_);
      }

   }

   public static void enableCaching() {
      CACHE.get().enable();
   }

   public static void clearCache() {
      CACHE.get().disable();
   }

   @OnlyIn(Dist.CLIENT)
   class AmbientOcclusionFace {
      private final float[] brightness = new float[4];
      private final int[] lightmap = new int[4];

      public AmbientOcclusionFace() {
      }

      public void calculate(IBlockDisplayReader p_239285_1_, BlockState p_239285_2_, BlockPos p_239285_3_, Direction p_239285_4_, float[] p_239285_5_, BitSet p_239285_6_, boolean p_239285_7_) {
         BlockPos blockpos = p_239285_6_.get(0) ? p_239285_3_.relative(p_239285_4_) : p_239285_3_;
         BlockModelRenderer.NeighborInfo blockmodelrenderer$neighborinfo = BlockModelRenderer.NeighborInfo.fromFacing(p_239285_4_);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         BlockModelRenderer.Cache blockmodelrenderer$cache = BlockModelRenderer.CACHE.get();
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0]);
         BlockState blockstate = p_239285_1_.getBlockState(blockpos$mutable);
         int i = blockmodelrenderer$cache.getLightColor(blockstate, p_239285_1_, blockpos$mutable);
         float f = blockmodelrenderer$cache.getShadeBrightness(blockstate, p_239285_1_, blockpos$mutable);
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1]);
         BlockState blockstate1 = p_239285_1_.getBlockState(blockpos$mutable);
         int j = blockmodelrenderer$cache.getLightColor(blockstate1, p_239285_1_, blockpos$mutable);
         float f1 = blockmodelrenderer$cache.getShadeBrightness(blockstate1, p_239285_1_, blockpos$mutable);
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[2]);
         BlockState blockstate2 = p_239285_1_.getBlockState(blockpos$mutable);
         int k = blockmodelrenderer$cache.getLightColor(blockstate2, p_239285_1_, blockpos$mutable);
         float f2 = blockmodelrenderer$cache.getShadeBrightness(blockstate2, p_239285_1_, blockpos$mutable);
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[3]);
         BlockState blockstate3 = p_239285_1_.getBlockState(blockpos$mutable);
         int l = blockmodelrenderer$cache.getLightColor(blockstate3, p_239285_1_, blockpos$mutable);
         float f3 = blockmodelrenderer$cache.getShadeBrightness(blockstate3, p_239285_1_, blockpos$mutable);
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0]).move(p_239285_4_);
         boolean flag = p_239285_1_.getBlockState(blockpos$mutable).getLightBlock(p_239285_1_, blockpos$mutable) == 0;
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1]).move(p_239285_4_);
         boolean flag1 = p_239285_1_.getBlockState(blockpos$mutable).getLightBlock(p_239285_1_, blockpos$mutable) == 0;
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[2]).move(p_239285_4_);
         boolean flag2 = p_239285_1_.getBlockState(blockpos$mutable).getLightBlock(p_239285_1_, blockpos$mutable) == 0;
         blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[3]).move(p_239285_4_);
         boolean flag3 = p_239285_1_.getBlockState(blockpos$mutable).getLightBlock(p_239285_1_, blockpos$mutable) == 0;
         float f4;
         int i1;
         if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
         } else {
            blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate4 = p_239285_1_.getBlockState(blockpos$mutable);
            f4 = blockmodelrenderer$cache.getShadeBrightness(blockstate4, p_239285_1_, blockpos$mutable);
            i1 = blockmodelrenderer$cache.getLightColor(blockstate4, p_239285_1_, blockpos$mutable);
         }

         float f5;
         int j1;
         if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
         } else {
            blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate6 = p_239285_1_.getBlockState(blockpos$mutable);
            f5 = blockmodelrenderer$cache.getShadeBrightness(blockstate6, p_239285_1_, blockpos$mutable);
            j1 = blockmodelrenderer$cache.getLightColor(blockstate6, p_239285_1_, blockpos$mutable);
         }

         float f6;
         int k1;
         if (!flag2 && !flag1) {
            f6 = f;
            k1 = i;
         } else {
            blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate7 = p_239285_1_.getBlockState(blockpos$mutable);
            f6 = blockmodelrenderer$cache.getShadeBrightness(blockstate7, p_239285_1_, blockpos$mutable);
            k1 = blockmodelrenderer$cache.getLightColor(blockstate7, p_239285_1_, blockpos$mutable);
         }

         float f7;
         int l1;
         if (!flag3 && !flag1) {
            f7 = f;
            l1 = i;
         } else {
            blockpos$mutable.setWithOffset(blockpos, blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate8 = p_239285_1_.getBlockState(blockpos$mutable);
            f7 = blockmodelrenderer$cache.getShadeBrightness(blockstate8, p_239285_1_, blockpos$mutable);
            l1 = blockmodelrenderer$cache.getLightColor(blockstate8, p_239285_1_, blockpos$mutable);
         }

         int i3 = blockmodelrenderer$cache.getLightColor(p_239285_2_, p_239285_1_, p_239285_3_);
         blockpos$mutable.setWithOffset(p_239285_3_, p_239285_4_);
         BlockState blockstate5 = p_239285_1_.getBlockState(blockpos$mutable);
         if (p_239285_6_.get(0) || !blockstate5.isSolidRender(p_239285_1_, blockpos$mutable)) {
            i3 = blockmodelrenderer$cache.getLightColor(blockstate5, p_239285_1_, blockpos$mutable);
         }

         float f8 = p_239285_6_.get(0) ? blockmodelrenderer$cache.getShadeBrightness(p_239285_1_.getBlockState(blockpos), p_239285_1_, blockpos) : blockmodelrenderer$cache.getShadeBrightness(p_239285_1_.getBlockState(p_239285_3_), p_239285_1_, p_239285_3_);
         BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.fromFacing(p_239285_4_);
         if (p_239285_6_.get(1) && blockmodelrenderer$neighborinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f31 = (f2 + f + f4 + f8) * 0.25F;
            float f32 = (f2 + f1 + f6 + f8) * 0.25F;
            float f33 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[1].shape];
            float f14 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[3].shape];
            float f15 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[5].shape];
            float f16 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[7].shape];
            float f17 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[1].shape];
            float f18 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[3].shape];
            float f19 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[5].shape];
            float f20 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[7].shape];
            float f21 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[1].shape];
            float f22 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[3].shape];
            float f23 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[5].shape];
            float f24 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[7].shape];
            float f25 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[1].shape];
            float f26 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[3].shape];
            float f27 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[5].shape];
            float f28 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[7].shape];
            this.brightness[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f31 * f14 + f32 * f15 + f33 * f16;
            this.brightness[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f31 * f18 + f32 * f19 + f33 * f20;
            this.brightness[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f31 * f22 + f32 * f23 + f33 * f24;
            this.brightness[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f31 * f26 + f32 * f27 + f33 * f28;
            int i2 = this.blend(l, i, j1, i3);
            int j2 = this.blend(k, i, i1, i3);
            int k2 = this.blend(k, j, k1, i3);
            int l2 = this.blend(l, j, l1, i3);
            this.lightmap[blockmodelrenderer$vertextranslations.vert0] = this.blend(i2, j2, k2, l2, f13, f14, f15, f16);
            this.lightmap[blockmodelrenderer$vertextranslations.vert1] = this.blend(i2, j2, k2, l2, f17, f18, f19, f20);
            this.lightmap[blockmodelrenderer$vertextranslations.vert2] = this.blend(i2, j2, k2, l2, f21, f22, f23, f24);
            this.lightmap[blockmodelrenderer$vertextranslations.vert3] = this.blend(i2, j2, k2, l2, f25, f26, f27, f28);
         } else {
            float f9 = (f3 + f + f5 + f8) * 0.25F;
            float f10 = (f2 + f + f4 + f8) * 0.25F;
            float f11 = (f2 + f1 + f6 + f8) * 0.25F;
            float f12 = (f3 + f1 + f7 + f8) * 0.25F;
            this.lightmap[blockmodelrenderer$vertextranslations.vert0] = this.blend(l, i, j1, i3);
            this.lightmap[blockmodelrenderer$vertextranslations.vert1] = this.blend(k, i, i1, i3);
            this.lightmap[blockmodelrenderer$vertextranslations.vert2] = this.blend(k, j, k1, i3);
            this.lightmap[blockmodelrenderer$vertextranslations.vert3] = this.blend(l, j, l1, i3);
            this.brightness[blockmodelrenderer$vertextranslations.vert0] = f9;
            this.brightness[blockmodelrenderer$vertextranslations.vert1] = f10;
            this.brightness[blockmodelrenderer$vertextranslations.vert2] = f11;
            this.brightness[blockmodelrenderer$vertextranslations.vert3] = f12;
         }

         float f30 = p_239285_1_.getShade(p_239285_4_, p_239285_7_);

         for(int j3 = 0; j3 < this.brightness.length; ++j3) {
            this.brightness[j3] *= f30;
         }

      }

      private int blend(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_) {
         if (p_147778_1_ == 0) {
            p_147778_1_ = p_147778_4_;
         }

         if (p_147778_2_ == 0) {
            p_147778_2_ = p_147778_4_;
         }

         if (p_147778_3_ == 0) {
            p_147778_3_ = p_147778_4_;
         }

         return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 16711935;
      }

      private int blend(int p_178203_1_, int p_178203_2_, int p_178203_3_, int p_178203_4_, float p_178203_5_, float p_178203_6_, float p_178203_7_, float p_178203_8_) {
         int i = (int)((float)(p_178203_1_ >> 16 & 255) * p_178203_5_ + (float)(p_178203_2_ >> 16 & 255) * p_178203_6_ + (float)(p_178203_3_ >> 16 & 255) * p_178203_7_ + (float)(p_178203_4_ >> 16 & 255) * p_178203_8_) & 255;
         int j = (int)((float)(p_178203_1_ & 255) * p_178203_5_ + (float)(p_178203_2_ & 255) * p_178203_6_ + (float)(p_178203_3_ & 255) * p_178203_7_ + (float)(p_178203_4_ & 255) * p_178203_8_) & 255;
         return i << 16 | j;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Cache {
      private boolean enabled;
      private final Long2IntLinkedOpenHashMap colorCache = Util.make(() -> {
         Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         long2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
         return long2intlinkedopenhashmap;
      });
      private final Long2FloatLinkedOpenHashMap brightnessCache = Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
         return long2floatlinkedopenhashmap;
      });

      private Cache() {
      }

      public void enable() {
         this.enabled = true;
      }

      public void disable() {
         this.enabled = false;
         this.colorCache.clear();
         this.brightnessCache.clear();
      }

      public int getLightColor(BlockState p_228810_1_, IBlockDisplayReader p_228810_2_, BlockPos p_228810_3_) {
         long i = p_228810_3_.asLong();
         if (this.enabled) {
            int j = this.colorCache.get(i);
            if (j != Integer.MAX_VALUE) {
               return j;
            }
         }

         int k = WorldRenderer.getLightColor(p_228810_2_, p_228810_1_, p_228810_3_);
         if (this.enabled) {
            if (this.colorCache.size() == 100) {
               this.colorCache.removeFirstInt();
            }

            this.colorCache.put(i, k);
         }

         return k;
      }

      public float getShadeBrightness(BlockState p_228811_1_, IBlockDisplayReader p_228811_2_, BlockPos p_228811_3_) {
         long i = p_228811_3_.asLong();
         if (this.enabled) {
            float f = this.brightnessCache.get(i);
            if (!Float.isNaN(f)) {
               return f;
            }
         }

         float f1 = p_228811_1_.getShadeBrightness(p_228811_2_, p_228811_3_);
         if (this.enabled) {
            if (this.brightnessCache.size() == 100) {
               this.brightnessCache.removeFirstFloat();
            }

            this.brightnessCache.put(i, f1);
         }

         return f1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum NeighborInfo {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      private final Direction[] corners;
      private final boolean doNonCubicWeight;
      private final BlockModelRenderer.Orientation[] vert0Weights;
      private final BlockModelRenderer.Orientation[] vert1Weights;
      private final BlockModelRenderer.Orientation[] vert2Weights;
      private final BlockModelRenderer.Orientation[] vert3Weights;
      private static final BlockModelRenderer.NeighborInfo[] BY_FACING = Util.make(new BlockModelRenderer.NeighborInfo[6], (p_209260_0_) -> {
         p_209260_0_[Direction.DOWN.get3DDataValue()] = DOWN;
         p_209260_0_[Direction.UP.get3DDataValue()] = UP;
         p_209260_0_[Direction.NORTH.get3DDataValue()] = NORTH;
         p_209260_0_[Direction.SOUTH.get3DDataValue()] = SOUTH;
         p_209260_0_[Direction.WEST.get3DDataValue()] = WEST;
         p_209260_0_[Direction.EAST.get3DDataValue()] = EAST;
      });

      private NeighborInfo(Direction[] p_i46236_3_, float p_i46236_4_, boolean p_i46236_5_, BlockModelRenderer.Orientation[] p_i46236_6_, BlockModelRenderer.Orientation[] p_i46236_7_, BlockModelRenderer.Orientation[] p_i46236_8_, BlockModelRenderer.Orientation[] p_i46236_9_) {
         this.corners = p_i46236_3_;
         this.doNonCubicWeight = p_i46236_5_;
         this.vert0Weights = p_i46236_6_;
         this.vert1Weights = p_i46236_7_;
         this.vert2Weights = p_i46236_8_;
         this.vert3Weights = p_i46236_9_;
      }

      public static BlockModelRenderer.NeighborInfo fromFacing(Direction p_178273_0_) {
         return BY_FACING[p_178273_0_.get3DDataValue()];
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Orientation {
      DOWN(Direction.DOWN, false),
      UP(Direction.UP, false),
      NORTH(Direction.NORTH, false),
      SOUTH(Direction.SOUTH, false),
      WEST(Direction.WEST, false),
      EAST(Direction.EAST, false),
      FLIP_DOWN(Direction.DOWN, true),
      FLIP_UP(Direction.UP, true),
      FLIP_NORTH(Direction.NORTH, true),
      FLIP_SOUTH(Direction.SOUTH, true),
      FLIP_WEST(Direction.WEST, true),
      FLIP_EAST(Direction.EAST, true);

      private final int shape;

      private Orientation(Direction p_i46233_3_, boolean p_i46233_4_) {
         this.shape = p_i46233_3_.get3DDataValue() + (p_i46233_4_ ? Direction.values().length : 0);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final BlockModelRenderer.VertexTranslations[] BY_FACING = Util.make(new BlockModelRenderer.VertexTranslations[6], (p_209261_0_) -> {
         p_209261_0_[Direction.DOWN.get3DDataValue()] = DOWN;
         p_209261_0_[Direction.UP.get3DDataValue()] = UP;
         p_209261_0_[Direction.NORTH.get3DDataValue()] = NORTH;
         p_209261_0_[Direction.SOUTH.get3DDataValue()] = SOUTH;
         p_209261_0_[Direction.WEST.get3DDataValue()] = WEST;
         p_209261_0_[Direction.EAST.get3DDataValue()] = EAST;
      });

      private VertexTranslations(int p_i46234_3_, int p_i46234_4_, int p_i46234_5_, int p_i46234_6_) {
         this.vert0 = p_i46234_3_;
         this.vert1 = p_i46234_4_;
         this.vert2 = p_i46234_5_;
         this.vert3 = p_i46234_6_;
      }

      public static BlockModelRenderer.VertexTranslations fromFacing(Direction p_178184_0_) {
         return BY_FACING[p_178184_0_.get3DDataValue()];
      }
   }
}
