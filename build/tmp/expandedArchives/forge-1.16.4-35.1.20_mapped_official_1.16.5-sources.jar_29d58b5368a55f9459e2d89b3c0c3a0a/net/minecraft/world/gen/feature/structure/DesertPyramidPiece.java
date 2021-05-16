package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DesertPyramidPiece extends ScatteredStructurePiece {
   private final boolean[] hasPlacedChest = new boolean[4];

   public DesertPyramidPiece(Random p_i48658_1_, int p_i48658_2_, int p_i48658_3_) {
      super(IStructurePieceType.DESERT_PYRAMID_PIECE, p_i48658_1_, p_i48658_2_, 64, p_i48658_3_, 21, 15, 21);
   }

   public DesertPyramidPiece(TemplateManager p_i51351_1_, CompoundNBT p_i51351_2_) {
      super(IStructurePieceType.DESERT_PYRAMID_PIECE, p_i51351_2_);
      this.hasPlacedChest[0] = p_i51351_2_.getBoolean("hasPlacedChest0");
      this.hasPlacedChest[1] = p_i51351_2_.getBoolean("hasPlacedChest1");
      this.hasPlacedChest[2] = p_i51351_2_.getBoolean("hasPlacedChest2");
      this.hasPlacedChest[3] = p_i51351_2_.getBoolean("hasPlacedChest3");
   }

   protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      super.addAdditionalSaveData(p_143011_1_);
      p_143011_1_.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
      p_143011_1_.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
      p_143011_1_.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
      p_143011_1_.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
   }

   public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
      this.generateBox(p_230383_1_, p_230383_5_, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);

      for(int i = 1; i <= 9; ++i) {
         this.generateBox(p_230383_1_, p_230383_5_, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      }

      for(int k1 = 0; k1 < this.width; ++k1) {
         for(int j = 0; j < this.depth; ++j) {
            int k = -5;
            this.fillColumnDown(p_230383_1_, Blocks.SANDSTONE.defaultBlockState(), k1, -5, j, p_230383_5_);
         }
      }

      BlockState blockstate1 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.NORTH);
      BlockState blockstate2 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.SOUTH);
      BlockState blockstate3 = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.EAST);
      BlockState blockstate = Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.WEST);
      this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, blockstate1, 2, 10, 0, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate2, 2, 10, 4, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate3, 0, 10, 2, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate, 4, 10, 2, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, blockstate1, this.width - 3, 10, 0, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate2, this.width - 3, 10, 4, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate3, this.width - 5, 10, 2, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate, this.width - 1, 10, 2, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 5, 5, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 5, 6, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 6, 6, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, blockstate1, 2, 4, 5, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate1, 2, 3, 4, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate1, this.width - 3, 4, 5, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate1, this.width - 3, 3, 4, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate, 2, 1, 2, p_230383_5_);
      this.placeBlock(p_230383_1_, blockstate3, this.width - 3, 1, 2, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

      for(int l = 5; l <= 17; l += 2) {
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, l, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, l, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, l, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, l, p_230383_5_);
      }

      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, p_230383_5_);

      for(int l1 = 0; l1 <= this.width - 1; l1 += this.width - 1) {
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 2, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 2, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 2, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 3, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 3, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 3, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 4, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), l1, 4, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 4, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 5, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 5, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 5, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 6, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), l1, 6, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 6, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), l1, 7, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), l1, 8, 3, p_230383_5_);
      }

      for(int i2 = 2; i2 <= this.width - 3; i2 += this.width - 3 - 2) {
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 2, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 2, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 2, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 3, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 3, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 3, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 4, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), i2, 4, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 4, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 5, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 5, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 5, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 6, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), i2, 6, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 6, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 - 1, 7, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2, 7, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), i2 + 1, 7, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 - 1, 8, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2, 8, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), i2 + 1, 8, 0, p_230383_5_);
      }

      this.generateBox(p_230383_1_, p_230383_5_, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 8, 6, 0, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 12, 6, 0, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(p_230383_1_, p_230383_5_, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, p_230383_5_);
      this.generateBox(p_230383_1_, p_230383_5_, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 8, -11, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 8, -10, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 12, -11, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 12, -10, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 10, -11, 8, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 10, -10, 8, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 10, -11, 12, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 10, -10, 12, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, p_230383_5_);
      this.placeBlock(p_230383_1_, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, p_230383_5_);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (!this.hasPlacedChest[direction.get2DDataValue()]) {
            int i1 = direction.getStepX() * 2;
            int j1 = direction.getStepZ() * 2;
            this.hasPlacedChest[direction.get2DDataValue()] = this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 10 + i1, -11, 10 + j1, LootTables.DESERT_PYRAMID);
         }
      }

      return true;
   }
}
