package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutPiece extends ScatteredStructurePiece {
   private boolean spawnedWitch;
   private boolean spawnedCat;

   public SwampHutPiece(Random p_i48652_1_, int p_i48652_2_, int p_i48652_3_) {
      super(IStructurePieceType.SWAMPLAND_HUT, p_i48652_1_, p_i48652_2_, 64, p_i48652_3_, 7, 7, 9);
   }

   public SwampHutPiece(TemplateManager p_i51340_1_, CompoundNBT p_i51340_2_) {
      super(IStructurePieceType.SWAMPLAND_HUT, p_i51340_2_);
      this.spawnedWitch = p_i51340_2_.getBoolean("Witch");
      this.spawnedCat = p_i51340_2_.getBoolean("Cat");
   }

   protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      super.addAdditionalSaveData(p_143011_1_);
      p_143011_1_.putBoolean("Witch", this.spawnedWitch);
      p_143011_1_.putBoolean("Cat", this.spawnedCat);
   }

   public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
      if (!this.updateAverageGroundHeight(p_230383_1_, p_230383_5_, 0)) {
         return false;
      } else {
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 1, 3, 4, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 5, 3, 4, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 5, 3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, p_230383_5_);
         BlockState blockstate = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.NORTH);
         BlockState blockstate1 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.EAST);
         BlockState blockstate2 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.WEST);
         BlockState blockstate3 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.SOUTH);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 4, 1, 6, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 4, 2, 0, 4, 7, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 4, 2, 6, 4, 7, blockstate2, blockstate2, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 4, 8, 6, 4, 8, blockstate3, blockstate3, false);
         this.placeBlock(p_230383_1_, blockstate.setValue(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate.setValue(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate3.setValue(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate3.setValue(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, p_230383_5_);

         for(int i = 2; i <= 7; i += 5) {
            for(int j = 1; j <= 5; j += 4) {
               this.fillColumnDown(p_230383_1_, Blocks.OAK_LOG.defaultBlockState(), j, -1, i, p_230383_5_);
            }
         }

         if (!this.spawnedWitch) {
            int l = this.getWorldX(2, 5);
            int i1 = this.getWorldY(2);
            int k = this.getWorldZ(2, 5);
            if (p_230383_5_.isInside(new BlockPos(l, i1, k))) {
               this.spawnedWitch = true;
               WitchEntity witchentity = EntityType.WITCH.create(p_230383_1_.getLevel());
               witchentity.setPersistenceRequired();
               witchentity.moveTo((double)l + 0.5D, (double)i1, (double)k + 0.5D, 0.0F, 0.0F);
               witchentity.finalizeSpawn(p_230383_1_, p_230383_1_.getCurrentDifficultyAt(new BlockPos(l, i1, k)), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
               p_230383_1_.addFreshEntityWithPassengers(witchentity);
            }
         }

         this.spawnCat(p_230383_1_, p_230383_5_);
         return true;
      }
   }

   private void spawnCat(IServerWorld p_214821_1_, MutableBoundingBox p_214821_2_) {
      if (!this.spawnedCat) {
         int i = this.getWorldX(2, 5);
         int j = this.getWorldY(2);
         int k = this.getWorldZ(2, 5);
         if (p_214821_2_.isInside(new BlockPos(i, j, k))) {
            this.spawnedCat = true;
            CatEntity catentity = EntityType.CAT.create(p_214821_1_.getLevel());
            catentity.setPersistenceRequired();
            catentity.moveTo((double)i + 0.5D, (double)j, (double)k + 0.5D, 0.0F, 0.0F);
            catentity.finalizeSpawn(p_214821_1_, p_214821_1_.getCurrentDifficultyAt(new BlockPos(i, j, k)), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            p_214821_1_.addFreshEntityWithPassengers(catentity);
         }
      }

   }
}
