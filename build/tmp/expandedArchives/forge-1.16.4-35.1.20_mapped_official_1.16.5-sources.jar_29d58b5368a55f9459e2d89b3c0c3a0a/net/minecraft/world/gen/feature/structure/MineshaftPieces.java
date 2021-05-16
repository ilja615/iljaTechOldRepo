package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class MineshaftPieces {
   private static MineshaftPieces.Piece createRandomShaftPiece(List<StructurePiece> p_189940_0_, Random p_189940_1_, int p_189940_2_, int p_189940_3_, int p_189940_4_, @Nullable Direction p_189940_5_, int p_189940_6_, MineshaftStructure.Type p_189940_7_) {
      int i = p_189940_1_.nextInt(100);
      if (i >= 80) {
         MutableBoundingBox mutableboundingbox = MineshaftPieces.Cross.findCrossing(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox != null) {
            return new MineshaftPieces.Cross(p_189940_6_, mutableboundingbox, p_189940_5_, p_189940_7_);
         }
      } else if (i >= 70) {
         MutableBoundingBox mutableboundingbox1 = MineshaftPieces.Stairs.findStairs(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox1 != null) {
            return new MineshaftPieces.Stairs(p_189940_6_, mutableboundingbox1, p_189940_5_, p_189940_7_);
         }
      } else {
         MutableBoundingBox mutableboundingbox2 = MineshaftPieces.Corridor.findCorridorSize(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox2 != null) {
            return new MineshaftPieces.Corridor(p_189940_6_, p_189940_1_, mutableboundingbox2, p_189940_5_, p_189940_7_);
         }
      }

      return null;
   }

   private static MineshaftPieces.Piece generateAndAddPiece(StructurePiece p_189938_0_, List<StructurePiece> p_189938_1_, Random p_189938_2_, int p_189938_3_, int p_189938_4_, int p_189938_5_, Direction p_189938_6_, int p_189938_7_) {
      if (p_189938_7_ > 8) {
         return null;
      } else if (Math.abs(p_189938_3_ - p_189938_0_.getBoundingBox().x0) <= 80 && Math.abs(p_189938_5_ - p_189938_0_.getBoundingBox().z0) <= 80) {
         MineshaftStructure.Type mineshaftstructure$type = ((MineshaftPieces.Piece)p_189938_0_).type;
         MineshaftPieces.Piece mineshaftpieces$piece = createRandomShaftPiece(p_189938_1_, p_189938_2_, p_189938_3_, p_189938_4_, p_189938_5_, p_189938_6_, p_189938_7_ + 1, mineshaftstructure$type);
         if (mineshaftpieces$piece != null) {
            p_189938_1_.add(mineshaftpieces$piece);
            mineshaftpieces$piece.addChildren(p_189938_0_, p_189938_1_, p_189938_2_);
         }

         return mineshaftpieces$piece;
      } else {
         return null;
      }
   }

   public static class Corridor extends MineshaftPieces.Piece {
      private final boolean hasRails;
      private final boolean spiderCorridor;
      private boolean hasPlacedSpider;
      private final int numSections;

      public Corridor(TemplateManager p_i50456_1_, CompoundNBT p_i50456_2_) {
         super(IStructurePieceType.MINE_SHAFT_CORRIDOR, p_i50456_2_);
         this.hasRails = p_i50456_2_.getBoolean("hr");
         this.spiderCorridor = p_i50456_2_.getBoolean("sc");
         this.hasPlacedSpider = p_i50456_2_.getBoolean("hps");
         this.numSections = p_i50456_2_.getInt("Num");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("hr", this.hasRails);
         p_143011_1_.putBoolean("sc", this.spiderCorridor);
         p_143011_1_.putBoolean("hps", this.hasPlacedSpider);
         p_143011_1_.putInt("Num", this.numSections);
      }

      public Corridor(int p_i47140_1_, Random p_i47140_2_, MutableBoundingBox p_i47140_3_, Direction p_i47140_4_, MineshaftStructure.Type p_i47140_5_) {
         super(IStructurePieceType.MINE_SHAFT_CORRIDOR, p_i47140_1_, p_i47140_5_);
         this.setOrientation(p_i47140_4_);
         this.boundingBox = p_i47140_3_;
         this.hasRails = p_i47140_2_.nextInt(3) == 0;
         this.spiderCorridor = !this.hasRails && p_i47140_2_.nextInt(23) == 0;
         if (this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.numSections = p_i47140_3_.getZSpan() / 5;
         } else {
            this.numSections = p_i47140_3_.getXSpan() / 5;
         }

      }

      public static MutableBoundingBox findCorridorSize(List<StructurePiece> p_175814_0_, Random p_175814_1_, int p_175814_2_, int p_175814_3_, int p_175814_4_, Direction p_175814_5_) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_175814_2_, p_175814_3_, p_175814_4_, p_175814_2_, p_175814_3_ + 3 - 1, p_175814_4_);

         int i;
         for(i = p_175814_1_.nextInt(3) + 2; i > 0; --i) {
            int j = i * 5;
            switch(p_175814_5_) {
            case NORTH:
            default:
               mutableboundingbox.x1 = p_175814_2_ + 3 - 1;
               mutableboundingbox.z0 = p_175814_4_ - (j - 1);
               break;
            case SOUTH:
               mutableboundingbox.x1 = p_175814_2_ + 3 - 1;
               mutableboundingbox.z1 = p_175814_4_ + j - 1;
               break;
            case WEST:
               mutableboundingbox.x0 = p_175814_2_ - (j - 1);
               mutableboundingbox.z1 = p_175814_4_ + 3 - 1;
               break;
            case EAST:
               mutableboundingbox.x1 = p_175814_2_ + j - 1;
               mutableboundingbox.z1 = p_175814_4_ + 3 - 1;
            }

            if (StructurePiece.findCollisionPiece(p_175814_0_, mutableboundingbox) == null) {
               break;
            }
         }

         return i > 0 ? mutableboundingbox : null;
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getGenDepth();
         int j = p_74861_3_.nextInt(4);
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z0 - 1, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z0, Direction.WEST, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z0, Direction.EAST, i);
               }
               break;
            case SOUTH:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z1 + 1, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z1 - 3, Direction.WEST, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z1 - 3, Direction.EAST, i);
               }
               break;
            case WEST:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z0, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, i);
               }
               break;
            case EAST:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z0, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + p_74861_3_.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, i);
               }
            }
         }

         if (i < 8) {
            if (direction != Direction.NORTH && direction != Direction.SOUTH) {
               for(int i1 = this.boundingBox.x0 + 3; i1 + 3 <= this.boundingBox.x1; i1 += 5) {
                  int j1 = p_74861_3_.nextInt(5);
                  if (j1 == 0) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, i1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, i + 1);
                  } else if (j1 == 1) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, i1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, i + 1);
                  }
               }
            } else {
               for(int k = this.boundingBox.z0 + 3; k + 3 <= this.boundingBox.z1; k += 5) {
                  int l = p_74861_3_.nextInt(5);
                  if (l == 0) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0, k, Direction.WEST, i + 1);
                  } else if (l == 1) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0, k, Direction.EAST, i + 1);
                  }
               }
            }
         }

      }

      protected boolean createChest(ISeedReader p_186167_1_, MutableBoundingBox p_186167_2_, Random p_186167_3_, int p_186167_4_, int p_186167_5_, int p_186167_6_, ResourceLocation p_186167_7_) {
         BlockPos blockpos = new BlockPos(this.getWorldX(p_186167_4_, p_186167_6_), this.getWorldY(p_186167_5_), this.getWorldZ(p_186167_4_, p_186167_6_));
         if (p_186167_2_.isInside(blockpos) && p_186167_1_.getBlockState(blockpos).isAir(p_186167_1_, blockpos) && !p_186167_1_.getBlockState(blockpos.below()).isAir(p_186167_1_, blockpos.below())) {
            BlockState blockstate = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, p_186167_3_.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.placeBlock(p_186167_1_, blockstate, p_186167_4_, p_186167_5_, p_186167_6_, p_186167_2_);
            ChestMinecartEntity chestminecartentity = new ChestMinecartEntity(p_186167_1_.getLevel(), (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
            chestminecartentity.setLootTable(p_186167_7_, p_186167_3_.nextLong());
            p_186167_1_.addFreshEntity(chestminecartentity);
            return true;
         } else {
            return false;
         }
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.edgesLiquid(p_230383_1_, p_230383_5_)) {
            return false;
         } else {
            int i = 0;
            int j = 2;
            int k = 0;
            int l = 2;
            int i1 = this.numSections * 5 - 1;
            BlockState blockstate = this.getPlanksBlock();
            this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 2, 1, i1, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(p_230383_1_, p_230383_5_, p_230383_4_, 0.8F, 0, 2, 0, 2, 2, i1, CAVE_AIR, CAVE_AIR, false, false);
            if (this.spiderCorridor) {
               this.generateMaybeBox(p_230383_1_, p_230383_5_, p_230383_4_, 0.6F, 0, 0, 0, 2, 1, i1, Blocks.COBWEB.defaultBlockState(), CAVE_AIR, false, true);
            }

            for(int j1 = 0; j1 < this.numSections; ++j1) {
               int k1 = 2 + j1 * 5;
               this.placeSupport(p_230383_1_, p_230383_5_, 0, 0, k1, 2, 2, p_230383_4_);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 0, 2, k1 - 1);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 2, 2, k1 - 1);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 0, 2, k1 + 1);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 2, 2, k1 + 1);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.05F, 0, 2, k1 - 2);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.05F, 2, 2, k1 - 2);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.05F, 0, 2, k1 + 2);
               this.placeCobWeb(p_230383_1_, p_230383_5_, p_230383_4_, 0.05F, 2, 2, k1 + 2);
               if (p_230383_4_.nextInt(100) == 0) {
                  this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 2, 0, k1 - 1, LootTables.ABANDONED_MINESHAFT);
               }

               if (p_230383_4_.nextInt(100) == 0) {
                  this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 0, 0, k1 + 1, LootTables.ABANDONED_MINESHAFT);
               }

               if (this.spiderCorridor && !this.hasPlacedSpider) {
                  int l1 = this.getWorldY(0);
                  int i2 = k1 - 1 + p_230383_4_.nextInt(3);
                  int j2 = this.getWorldX(1, i2);
                  int k2 = this.getWorldZ(1, i2);
                  BlockPos blockpos = new BlockPos(j2, l1, k2);
                  if (p_230383_5_.isInside(blockpos) && this.isInterior(p_230383_1_, 1, 0, i2, p_230383_5_)) {
                     this.hasPlacedSpider = true;
                     p_230383_1_.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
                     TileEntity tileentity = p_230383_1_.getBlockEntity(blockpos);
                     if (tileentity instanceof MobSpawnerTileEntity) {
                        ((MobSpawnerTileEntity)tileentity).getSpawner().setEntityId(EntityType.CAVE_SPIDER);
                     }
                  }
               }
            }

            for(int l2 = 0; l2 <= 2; ++l2) {
               for(int i3 = 0; i3 <= i1; ++i3) {
                  int k3 = -1;
                  BlockState blockstate3 = this.getBlock(p_230383_1_, l2, -1, i3, p_230383_5_);
                  if (blockstate3.isAir() && this.isInterior(p_230383_1_, l2, -1, i3, p_230383_5_)) {
                     int l3 = -1;
                     this.placeBlock(p_230383_1_, blockstate, l2, -1, i3, p_230383_5_);
                  }
               }
            }

            if (this.hasRails) {
               BlockState blockstate1 = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

               for(int j3 = 0; j3 <= i1; ++j3) {
                  BlockState blockstate2 = this.getBlock(p_230383_1_, 1, -1, j3, p_230383_5_);
                  if (!blockstate2.isAir() && blockstate2.isSolidRender(p_230383_1_, new BlockPos(this.getWorldX(1, j3), this.getWorldY(-1), this.getWorldZ(1, j3)))) {
                     float f = this.isInterior(p_230383_1_, 1, 0, j3, p_230383_5_) ? 0.7F : 0.9F;
                     this.maybeGenerateBlock(p_230383_1_, p_230383_5_, p_230383_4_, f, 1, 0, j3, blockstate1);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupport(ISeedReader p_189921_1_, MutableBoundingBox p_189921_2_, int p_189921_3_, int p_189921_4_, int p_189921_5_, int p_189921_6_, int p_189921_7_, Random p_189921_8_) {
         if (this.isSupportingBox(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_7_, p_189921_6_, p_189921_5_)) {
            BlockState blockstate = this.getPlanksBlock();
            BlockState blockstate1 = this.getFenceBlock();
            this.generateBox(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_4_, p_189921_5_, p_189921_3_, p_189921_6_ - 1, p_189921_5_, blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true)), CAVE_AIR, false);
            this.generateBox(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_4_, p_189921_5_, p_189921_7_, p_189921_6_ - 1, p_189921_5_, blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true)), CAVE_AIR, false);
            if (p_189921_8_.nextInt(4) == 0) {
               this.generateBox(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_3_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
               this.generateBox(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
            } else {
               this.generateBox(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
               this.maybeGenerateBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ - 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH));
               this.maybeGenerateBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ + 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH));
            }

         }
      }

      private void placeCobWeb(ISeedReader p_189922_1_, MutableBoundingBox p_189922_2_, Random p_189922_3_, float p_189922_4_, int p_189922_5_, int p_189922_6_, int p_189922_7_) {
         if (this.isInterior(p_189922_1_, p_189922_5_, p_189922_6_, p_189922_7_, p_189922_2_)) {
            this.maybeGenerateBlock(p_189922_1_, p_189922_2_, p_189922_3_, p_189922_4_, p_189922_5_, p_189922_6_, p_189922_7_, Blocks.COBWEB.defaultBlockState());
         }

      }
   }

   public static class Cross extends MineshaftPieces.Piece {
      private final Direction direction;
      private final boolean isTwoFloored;

      public Cross(TemplateManager p_i50454_1_, CompoundNBT p_i50454_2_) {
         super(IStructurePieceType.MINE_SHAFT_CROSSING, p_i50454_2_);
         this.isTwoFloored = p_i50454_2_.getBoolean("tf");
         this.direction = Direction.from2DDataValue(p_i50454_2_.getInt("D"));
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("tf", this.isTwoFloored);
         p_143011_1_.putInt("D", this.direction.get2DDataValue());
      }

      public Cross(int p_i50455_1_, MutableBoundingBox p_i50455_2_, @Nullable Direction p_i50455_3_, MineshaftStructure.Type p_i50455_4_) {
         super(IStructurePieceType.MINE_SHAFT_CROSSING, p_i50455_1_, p_i50455_4_);
         this.direction = p_i50455_3_;
         this.boundingBox = p_i50455_2_;
         this.isTwoFloored = p_i50455_2_.getYSpan() > 3;
      }

      public static MutableBoundingBox findCrossing(List<StructurePiece> p_175813_0_, Random p_175813_1_, int p_175813_2_, int p_175813_3_, int p_175813_4_, Direction p_175813_5_) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_175813_2_, p_175813_3_, p_175813_4_, p_175813_2_, p_175813_3_ + 3 - 1, p_175813_4_);
         if (p_175813_1_.nextInt(4) == 0) {
            mutableboundingbox.y1 += 4;
         }

         switch(p_175813_5_) {
         case NORTH:
         default:
            mutableboundingbox.x0 = p_175813_2_ - 1;
            mutableboundingbox.x1 = p_175813_2_ + 3;
            mutableboundingbox.z0 = p_175813_4_ - 4;
            break;
         case SOUTH:
            mutableboundingbox.x0 = p_175813_2_ - 1;
            mutableboundingbox.x1 = p_175813_2_ + 3;
            mutableboundingbox.z1 = p_175813_4_ + 3 + 1;
            break;
         case WEST:
            mutableboundingbox.x0 = p_175813_2_ - 4;
            mutableboundingbox.z0 = p_175813_4_ - 1;
            mutableboundingbox.z1 = p_175813_4_ + 3;
            break;
         case EAST:
            mutableboundingbox.x1 = p_175813_2_ + 3 + 1;
            mutableboundingbox.z0 = p_175813_4_ - 1;
            mutableboundingbox.z1 = p_175813_4_ + 3;
         }

         return StructurePiece.findCollisionPiece(p_175813_0_, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getGenDepth();
         switch(this.direction) {
         case NORTH:
         default:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, i);
            break;
         case SOUTH:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, i);
            break;
         case WEST:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, i);
            break;
         case EAST:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, i);
         }

         if (this.isTwoFloored) {
            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 - 1, Direction.NORTH, i);
            }

            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.WEST, i);
            }

            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.EAST, i);
            }

            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z1 + 1, Direction.SOUTH, i);
            }
         }

      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.edgesLiquid(p_230383_1_, p_230383_5_)) {
            return false;
         } else {
            BlockState blockstate = this.getPlanksBlock();
            if (this.isTwoFloored) {
               this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0 + 1, this.boundingBox.y1 - 2, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0, this.boundingBox.y1 - 2, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3, this.boundingBox.z0 + 1, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            }

            this.placeSupportPillar(p_230383_1_, p_230383_5_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(p_230383_1_, p_230383_5_, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            this.placeSupportPillar(p_230383_1_, p_230383_5_, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(p_230383_1_, p_230383_5_, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);

            for(int i = this.boundingBox.x0; i <= this.boundingBox.x1; ++i) {
               for(int j = this.boundingBox.z0; j <= this.boundingBox.z1; ++j) {
                  if (this.getBlock(p_230383_1_, i, this.boundingBox.y0 - 1, j, p_230383_5_).isAir() && this.isInterior(p_230383_1_, i, this.boundingBox.y0 - 1, j, p_230383_5_)) {
                     this.placeBlock(p_230383_1_, blockstate, i, this.boundingBox.y0 - 1, j, p_230383_5_);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupportPillar(ISeedReader p_189923_1_, MutableBoundingBox p_189923_2_, int p_189923_3_, int p_189923_4_, int p_189923_5_, int p_189923_6_) {
         if (!this.getBlock(p_189923_1_, p_189923_3_, p_189923_6_ + 1, p_189923_5_, p_189923_2_).isAir()) {
            this.generateBox(p_189923_1_, p_189923_2_, p_189923_3_, p_189923_4_, p_189923_5_, p_189923_3_, p_189923_6_, p_189923_5_, this.getPlanksBlock(), CAVE_AIR, false);
         }

      }
   }

   abstract static class Piece extends StructurePiece {
      protected MineshaftStructure.Type type;

      public Piece(IStructurePieceType p_i50452_1_, int p_i50452_2_, MineshaftStructure.Type p_i50452_3_) {
         super(p_i50452_1_, p_i50452_2_);
         this.type = p_i50452_3_;
      }

      public Piece(IStructurePieceType p_i50453_1_, CompoundNBT p_i50453_2_) {
         super(p_i50453_1_, p_i50453_2_);
         this.type = MineshaftStructure.Type.byId(p_i50453_2_.getInt("MST"));
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         p_143011_1_.putInt("MST", this.type.ordinal());
      }

      protected BlockState getPlanksBlock() {
         switch(this.type) {
         case NORMAL:
         default:
            return Blocks.OAK_PLANKS.defaultBlockState();
         case MESA:
            return Blocks.DARK_OAK_PLANKS.defaultBlockState();
         }
      }

      protected BlockState getFenceBlock() {
         switch(this.type) {
         case NORMAL:
         default:
            return Blocks.OAK_FENCE.defaultBlockState();
         case MESA:
            return Blocks.DARK_OAK_FENCE.defaultBlockState();
         }
      }

      protected boolean isSupportingBox(IBlockReader p_189918_1_, MutableBoundingBox p_189918_2_, int p_189918_3_, int p_189918_4_, int p_189918_5_, int p_189918_6_) {
         for(int i = p_189918_3_; i <= p_189918_4_; ++i) {
            if (this.getBlock(p_189918_1_, i, p_189918_5_ + 1, p_189918_6_, p_189918_2_).isAir()) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Room extends MineshaftPieces.Piece {
      private final List<MutableBoundingBox> childEntranceBoxes = Lists.newLinkedList();

      public Room(int p_i47137_1_, Random p_i47137_2_, int p_i47137_3_, int p_i47137_4_, MineshaftStructure.Type p_i47137_5_) {
         super(IStructurePieceType.MINE_SHAFT_ROOM, p_i47137_1_, p_i47137_5_);
         this.type = p_i47137_5_;
         this.boundingBox = new MutableBoundingBox(p_i47137_3_, 50, p_i47137_4_, p_i47137_3_ + 7 + p_i47137_2_.nextInt(6), 54 + p_i47137_2_.nextInt(6), p_i47137_4_ + 7 + p_i47137_2_.nextInt(6));
      }

      public Room(TemplateManager p_i50451_1_, CompoundNBT p_i50451_2_) {
         super(IStructurePieceType.MINE_SHAFT_ROOM, p_i50451_2_);
         ListNBT listnbt = p_i50451_2_.getList("Entrances", 11);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.childEntranceBoxes.add(new MutableBoundingBox(listnbt.getIntArray(i)));
         }

      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getGenDepth();
         int j = this.boundingBox.getYSpan() - 3 - 1;
         if (j <= 0) {
            j = 1;
         }

         int k;
         for(k = 0; k < this.boundingBox.getXSpan(); k = k + 4) {
            k = k + p_74861_3_.nextInt(this.boundingBox.getXSpan());
            if (k + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineshaftPieces.Piece mineshaftpieces$piece = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + k, this.boundingBox.y0 + p_74861_3_.nextInt(j) + 1, this.boundingBox.z0 - 1, Direction.NORTH, i);
            if (mineshaftpieces$piece != null) {
               MutableBoundingBox mutableboundingbox = mineshaftpieces$piece.getBoundingBox();
               this.childEntranceBoxes.add(new MutableBoundingBox(mutableboundingbox.x0, mutableboundingbox.y0, this.boundingBox.z0, mutableboundingbox.x1, mutableboundingbox.y1, this.boundingBox.z0 + 1));
            }
         }

         for(k = 0; k < this.boundingBox.getXSpan(); k = k + 4) {
            k = k + p_74861_3_.nextInt(this.boundingBox.getXSpan());
            if (k + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineshaftPieces.Piece mineshaftpieces$piece1 = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 + k, this.boundingBox.y0 + p_74861_3_.nextInt(j) + 1, this.boundingBox.z1 + 1, Direction.SOUTH, i);
            if (mineshaftpieces$piece1 != null) {
               MutableBoundingBox mutableboundingbox1 = mineshaftpieces$piece1.getBoundingBox();
               this.childEntranceBoxes.add(new MutableBoundingBox(mutableboundingbox1.x0, mutableboundingbox1.y0, this.boundingBox.z1 - 1, mutableboundingbox1.x1, mutableboundingbox1.y1, this.boundingBox.z1));
            }
         }

         for(k = 0; k < this.boundingBox.getZSpan(); k = k + 4) {
            k = k + p_74861_3_.nextInt(this.boundingBox.getZSpan());
            if (k + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            MineshaftPieces.Piece mineshaftpieces$piece2 = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + p_74861_3_.nextInt(j) + 1, this.boundingBox.z0 + k, Direction.WEST, i);
            if (mineshaftpieces$piece2 != null) {
               MutableBoundingBox mutableboundingbox2 = mineshaftpieces$piece2.getBoundingBox();
               this.childEntranceBoxes.add(new MutableBoundingBox(this.boundingBox.x0, mutableboundingbox2.y0, mutableboundingbox2.z0, this.boundingBox.x0 + 1, mutableboundingbox2.y1, mutableboundingbox2.z1));
            }
         }

         for(k = 0; k < this.boundingBox.getZSpan(); k = k + 4) {
            k = k + p_74861_3_.nextInt(this.boundingBox.getZSpan());
            if (k + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            StructurePiece structurepiece = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + p_74861_3_.nextInt(j) + 1, this.boundingBox.z0 + k, Direction.EAST, i);
            if (structurepiece != null) {
               MutableBoundingBox mutableboundingbox3 = structurepiece.getBoundingBox();
               this.childEntranceBoxes.add(new MutableBoundingBox(this.boundingBox.x1 - 1, mutableboundingbox3.y0, mutableboundingbox3.z0, this.boundingBox.x1, mutableboundingbox3.y1, mutableboundingbox3.z1));
            }
         }

      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.edgesLiquid(p_230383_1_, p_230383_5_)) {
            return false;
         } else {
            this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y0, this.boundingBox.z1, Blocks.DIRT.defaultBlockState(), CAVE_AIR, true);
            this.generateBox(p_230383_1_, p_230383_5_, this.boundingBox.x0, this.boundingBox.y0 + 1, this.boundingBox.z0, this.boundingBox.x1, Math.min(this.boundingBox.y0 + 3, this.boundingBox.y1), this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);

            for(MutableBoundingBox mutableboundingbox : this.childEntranceBoxes) {
               this.generateBox(p_230383_1_, p_230383_5_, mutableboundingbox.x0, mutableboundingbox.y1 - 2, mutableboundingbox.z0, mutableboundingbox.x1, mutableboundingbox.y1, mutableboundingbox.z1, CAVE_AIR, CAVE_AIR, false);
            }

            this.generateUpperHalfSphere(p_230383_1_, p_230383_5_, this.boundingBox.x0, this.boundingBox.y0 + 4, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, false);
            return true;
         }
      }

      public void move(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
         super.move(p_181138_1_, p_181138_2_, p_181138_3_);

         for(MutableBoundingBox mutableboundingbox : this.childEntranceBoxes) {
            mutableboundingbox.move(p_181138_1_, p_181138_2_, p_181138_3_);
         }

      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         ListNBT listnbt = new ListNBT();

         for(MutableBoundingBox mutableboundingbox : this.childEntranceBoxes) {
            listnbt.add(mutableboundingbox.createTag());
         }

         p_143011_1_.put("Entrances", listnbt);
      }
   }

   public static class Stairs extends MineshaftPieces.Piece {
      public Stairs(int p_i50449_1_, MutableBoundingBox p_i50449_2_, Direction p_i50449_3_, MineshaftStructure.Type p_i50449_4_) {
         super(IStructurePieceType.MINE_SHAFT_STAIRS, p_i50449_1_, p_i50449_4_);
         this.setOrientation(p_i50449_3_);
         this.boundingBox = p_i50449_2_;
      }

      public Stairs(TemplateManager p_i50450_1_, CompoundNBT p_i50450_2_) {
         super(IStructurePieceType.MINE_SHAFT_STAIRS, p_i50450_2_);
      }

      public static MutableBoundingBox findStairs(List<StructurePiece> p_175812_0_, Random p_175812_1_, int p_175812_2_, int p_175812_3_, int p_175812_4_, Direction p_175812_5_) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_175812_2_, p_175812_3_ - 5, p_175812_4_, p_175812_2_, p_175812_3_ + 3 - 1, p_175812_4_);
         switch(p_175812_5_) {
         case NORTH:
         default:
            mutableboundingbox.x1 = p_175812_2_ + 3 - 1;
            mutableboundingbox.z0 = p_175812_4_ - 8;
            break;
         case SOUTH:
            mutableboundingbox.x1 = p_175812_2_ + 3 - 1;
            mutableboundingbox.z1 = p_175812_4_ + 8;
            break;
         case WEST:
            mutableboundingbox.x0 = p_175812_2_ - 8;
            mutableboundingbox.z1 = p_175812_4_ + 3 - 1;
            break;
         case EAST:
            mutableboundingbox.x1 = p_175812_2_ + 8;
            mutableboundingbox.z1 = p_175812_4_ + 3 - 1;
         }

         return StructurePiece.findCollisionPiece(p_175812_0_, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getGenDepth();
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, i);
               break;
            case SOUTH:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, i);
               break;
            case WEST:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0, Direction.WEST, i);
               break;
            case EAST:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0, Direction.EAST, i);
            }
         }

      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.edgesLiquid(p_230383_1_, p_230383_5_)) {
            return false;
         } else {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);

            for(int i = 0; i < 5; ++i) {
               this.generateBox(p_230383_1_, p_230383_5_, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, CAVE_AIR, CAVE_AIR, false);
            }

            return true;
         }
      }
   }
}
