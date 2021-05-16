package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class StrongholdPieces {
   private static final StrongholdPieces.PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new StrongholdPieces.PieceWeight[]{new StrongholdPieces.PieceWeight(StrongholdPieces.Straight.class, 40, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.Prison.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.LeftTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RightTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RoomCrossing.class, 10, 6), new StrongholdPieces.PieceWeight(StrongholdPieces.StairsStraight.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.Stairs.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.Crossing.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.ChestCorridor.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.Library.class, 10, 2) {
      public boolean doPlace(int p_75189_1_) {
         return super.doPlace(p_75189_1_) && p_75189_1_ > 4;
      }
   }, new StrongholdPieces.PieceWeight(StrongholdPieces.PortalRoom.class, 20, 1) {
      public boolean doPlace(int p_75189_1_) {
         return super.doPlace(p_75189_1_) && p_75189_1_ > 5;
      }
   }};
   private static List<StrongholdPieces.PieceWeight> currentPieces;
   private static Class<? extends StrongholdPieces.Stronghold> imposedPiece;
   private static int totalWeight;
   private static final StrongholdPieces.Stones SMOOTH_STONE_SELECTOR = new StrongholdPieces.Stones();

   public static void resetPieces() {
      currentPieces = Lists.newArrayList();

      for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : STRONGHOLD_PIECE_WEIGHTS) {
         strongholdpieces$pieceweight.placeCount = 0;
         currentPieces.add(strongholdpieces$pieceweight);
      }

      imposedPiece = null;
   }

   private static boolean updatePieceWeight() {
      boolean flag = false;
      totalWeight = 0;

      for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : currentPieces) {
         if (strongholdpieces$pieceweight.maxPlaceCount > 0 && strongholdpieces$pieceweight.placeCount < strongholdpieces$pieceweight.maxPlaceCount) {
            flag = true;
         }

         totalWeight += strongholdpieces$pieceweight.weight;
      }

      return flag;
   }

   private static StrongholdPieces.Stronghold findAndCreatePieceFactory(Class<? extends StrongholdPieces.Stronghold> p_175954_0_, List<StructurePiece> p_175954_1_, Random p_175954_2_, int p_175954_3_, int p_175954_4_, int p_175954_5_, @Nullable Direction p_175954_6_, int p_175954_7_) {
      StrongholdPieces.Stronghold strongholdpieces$stronghold = null;
      if (p_175954_0_ == StrongholdPieces.Straight.class) {
         strongholdpieces$stronghold = StrongholdPieces.Straight.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.Prison.class) {
         strongholdpieces$stronghold = StrongholdPieces.Prison.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.LeftTurn.class) {
         strongholdpieces$stronghold = StrongholdPieces.LeftTurn.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.RightTurn.class) {
         strongholdpieces$stronghold = StrongholdPieces.RightTurn.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.RoomCrossing.class) {
         strongholdpieces$stronghold = StrongholdPieces.RoomCrossing.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.StairsStraight.class) {
         strongholdpieces$stronghold = StrongholdPieces.StairsStraight.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.Stairs.class) {
         strongholdpieces$stronghold = StrongholdPieces.Stairs.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.Crossing.class) {
         strongholdpieces$stronghold = StrongholdPieces.Crossing.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.ChestCorridor.class) {
         strongholdpieces$stronghold = StrongholdPieces.ChestCorridor.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.Library.class) {
         strongholdpieces$stronghold = StrongholdPieces.Library.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      } else if (p_175954_0_ == StrongholdPieces.PortalRoom.class) {
         strongholdpieces$stronghold = StrongholdPieces.PortalRoom.createPiece(p_175954_1_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
      }

      return strongholdpieces$stronghold;
   }

   private static StrongholdPieces.Stronghold generatePieceFromSmallDoor(StrongholdPieces.Stairs2 p_175955_0_, List<StructurePiece> p_175955_1_, Random p_175955_2_, int p_175955_3_, int p_175955_4_, int p_175955_5_, Direction p_175955_6_, int p_175955_7_) {
      if (!updatePieceWeight()) {
         return null;
      } else {
         if (imposedPiece != null) {
            StrongholdPieces.Stronghold strongholdpieces$stronghold = findAndCreatePieceFactory(imposedPiece, p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_, p_175955_7_);
            imposedPiece = null;
            if (strongholdpieces$stronghold != null) {
               return strongholdpieces$stronghold;
            }
         }

         int j = 0;

         while(j < 5) {
            ++j;
            int i = p_175955_2_.nextInt(totalWeight);

            for(StrongholdPieces.PieceWeight strongholdpieces$pieceweight : currentPieces) {
               i -= strongholdpieces$pieceweight.weight;
               if (i < 0) {
                  if (!strongholdpieces$pieceweight.doPlace(p_175955_7_) || strongholdpieces$pieceweight == p_175955_0_.previousPiece) {
                     break;
                  }

                  StrongholdPieces.Stronghold strongholdpieces$stronghold1 = findAndCreatePieceFactory(strongholdpieces$pieceweight.pieceClass, p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_, p_175955_7_);
                  if (strongholdpieces$stronghold1 != null) {
                     ++strongholdpieces$pieceweight.placeCount;
                     p_175955_0_.previousPiece = strongholdpieces$pieceweight;
                     if (!strongholdpieces$pieceweight.isValid()) {
                        currentPieces.remove(strongholdpieces$pieceweight);
                     }

                     return strongholdpieces$stronghold1;
                  }
               }
            }
         }

         MutableBoundingBox mutableboundingbox = StrongholdPieces.Corridor.findPieceBox(p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_);
         return mutableboundingbox != null && mutableboundingbox.y0 > 1 ? new StrongholdPieces.Corridor(p_175955_7_, mutableboundingbox, p_175955_6_) : null;
      }
   }

   private static StructurePiece generateAndAddPiece(StrongholdPieces.Stairs2 p_175953_0_, List<StructurePiece> p_175953_1_, Random p_175953_2_, int p_175953_3_, int p_175953_4_, int p_175953_5_, @Nullable Direction p_175953_6_, int p_175953_7_) {
      if (p_175953_7_ > 50) {
         return null;
      } else if (Math.abs(p_175953_3_ - p_175953_0_.getBoundingBox().x0) <= 112 && Math.abs(p_175953_5_ - p_175953_0_.getBoundingBox().z0) <= 112) {
         StructurePiece structurepiece = generatePieceFromSmallDoor(p_175953_0_, p_175953_1_, p_175953_2_, p_175953_3_, p_175953_4_, p_175953_5_, p_175953_6_, p_175953_7_ + 1);
         if (structurepiece != null) {
            p_175953_1_.add(structurepiece);
            p_175953_0_.pendingChildren.add(structurepiece);
         }

         return structurepiece;
      } else {
         return null;
      }
   }

   public static class ChestCorridor extends StrongholdPieces.Stronghold {
      private boolean hasPlacedChest;

      public ChestCorridor(int p_i45582_1_, Random p_i45582_2_, MutableBoundingBox p_i45582_3_, Direction p_i45582_4_) {
         super(IStructurePieceType.STRONGHOLD_CHEST_CORRIDOR, p_i45582_1_);
         this.setOrientation(p_i45582_4_);
         this.entryDoor = this.randomSmallDoor(p_i45582_2_);
         this.boundingBox = p_i45582_3_;
      }

      public ChestCorridor(TemplateManager p_i50140_1_, CompoundNBT p_i50140_2_) {
         super(IStructurePieceType.STRONGHOLD_CHEST_CORRIDOR, p_i50140_2_);
         this.hasPlacedChest = p_i50140_2_.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Chest", this.hasPlacedChest);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateSmallDoorChildForward((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
      }

      public static StrongholdPieces.ChestCorridor createPiece(List<StructurePiece> p_175868_0_, Random p_175868_1_, int p_175868_2_, int p_175868_3_, int p_175868_4_, Direction p_175868_5_, int p_175868_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175868_2_, p_175868_3_, p_175868_4_, -1, -1, 0, 5, 5, 7, p_175868_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175868_0_, mutableboundingbox) == null ? new StrongholdPieces.ChestCorridor(p_175868_6_, p_175868_1_, mutableboundingbox, p_175868_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 4, 6, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, p_230383_5_);

         for(int i = 2; i <= 4; ++i) {
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, i, p_230383_5_);
         }

         if (!this.hasPlacedChest && p_230383_5_.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
            this.hasPlacedChest = true;
            this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR);
         }

         return true;
      }
   }

   public static class Corridor extends StrongholdPieces.Stronghold {
      private final int steps;

      public Corridor(int p_i50137_1_, MutableBoundingBox p_i50137_2_, Direction p_i50137_3_) {
         super(IStructurePieceType.STRONGHOLD_FILLER_CORRIDOR, p_i50137_1_);
         this.setOrientation(p_i50137_3_);
         this.boundingBox = p_i50137_2_;
         this.steps = p_i50137_3_ != Direction.NORTH && p_i50137_3_ != Direction.SOUTH ? p_i50137_2_.getXSpan() : p_i50137_2_.getZSpan();
      }

      public Corridor(TemplateManager p_i50138_1_, CompoundNBT p_i50138_2_) {
         super(IStructurePieceType.STRONGHOLD_FILLER_CORRIDOR, p_i50138_2_);
         this.steps = p_i50138_2_.getInt("Steps");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putInt("Steps", this.steps);
      }

      public static MutableBoundingBox findPieceBox(List<StructurePiece> p_175869_0_, Random p_175869_1_, int p_175869_2_, int p_175869_3_, int p_175869_4_, Direction p_175869_5_) {
         int i = 3;
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, 4, p_175869_5_);
         StructurePiece structurepiece = StructurePiece.findCollisionPiece(p_175869_0_, mutableboundingbox);
         if (structurepiece == null) {
            return null;
         } else {
            if (structurepiece.getBoundingBox().y0 == mutableboundingbox.y0) {
               for(int j = 3; j >= 1; --j) {
                  mutableboundingbox = MutableBoundingBox.orientBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, j - 1, p_175869_5_);
                  if (!structurepiece.getBoundingBox().intersects(mutableboundingbox)) {
                     return MutableBoundingBox.orientBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, j, p_175869_5_);
                  }
               }
            }

            return null;
         }
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         for(int i = 0; i < this.steps; ++i) {
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, i, p_230383_5_);

            for(int j = 1; j <= 3; ++j) {
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 0, j, i, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.CAVE_AIR.defaultBlockState(), 1, j, i, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.CAVE_AIR.defaultBlockState(), 2, j, i, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.CAVE_AIR.defaultBlockState(), 3, j, i, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 4, j, i, p_230383_5_);
            }

            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, i, p_230383_5_);
         }

         return true;
      }
   }

   public static class Crossing extends StrongholdPieces.Stronghold {
      private final boolean leftLow;
      private final boolean leftHigh;
      private final boolean rightLow;
      private final boolean rightHigh;

      public Crossing(int p_i45580_1_, Random p_i45580_2_, MutableBoundingBox p_i45580_3_, Direction p_i45580_4_) {
         super(IStructurePieceType.STRONGHOLD_FIVE_CROSSING, p_i45580_1_);
         this.setOrientation(p_i45580_4_);
         this.entryDoor = this.randomSmallDoor(p_i45580_2_);
         this.boundingBox = p_i45580_3_;
         this.leftLow = p_i45580_2_.nextBoolean();
         this.leftHigh = p_i45580_2_.nextBoolean();
         this.rightLow = p_i45580_2_.nextBoolean();
         this.rightHigh = p_i45580_2_.nextInt(3) > 0;
      }

      public Crossing(TemplateManager p_i50136_1_, CompoundNBT p_i50136_2_) {
         super(IStructurePieceType.STRONGHOLD_FIVE_CROSSING, p_i50136_2_);
         this.leftLow = p_i50136_2_.getBoolean("leftLow");
         this.leftHigh = p_i50136_2_.getBoolean("leftHigh");
         this.rightLow = p_i50136_2_.getBoolean("rightLow");
         this.rightHigh = p_i50136_2_.getBoolean("rightHigh");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("leftLow", this.leftLow);
         p_143011_1_.putBoolean("leftHigh", this.leftHigh);
         p_143011_1_.putBoolean("rightLow", this.rightLow);
         p_143011_1_.putBoolean("rightHigh", this.rightHigh);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = 3;
         int j = 5;
         Direction direction = this.getOrientation();
         if (direction == Direction.WEST || direction == Direction.NORTH) {
            i = 8 - i;
            j = 8 - j;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 5, 1);
         if (this.leftLow) {
            this.generateSmallDoorChildLeft((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, i, 1);
         }

         if (this.leftHigh) {
            this.generateSmallDoorChildLeft((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, j, 7);
         }

         if (this.rightLow) {
            this.generateSmallDoorChildRight((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, i, 1);
         }

         if (this.rightHigh) {
            this.generateSmallDoorChildRight((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, j, 7);
         }

      }

      public static StrongholdPieces.Crossing createPiece(List<StructurePiece> p_175866_0_, Random p_175866_1_, int p_175866_2_, int p_175866_3_, int p_175866_4_, Direction p_175866_5_, int p_175866_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175866_2_, p_175866_3_, p_175866_4_, -4, -3, 0, 10, 9, 11, p_175866_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175866_0_, mutableboundingbox) == null ? new StrongholdPieces.Crossing(p_175866_6_, p_175866_1_, mutableboundingbox, p_175866_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 9, 8, 10, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 4, 3, 0);
         if (this.leftLow) {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightLow) {
            this.generateBox(p_230383_1_, p_230383_5_, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.leftHigh) {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightHigh) {
            this.generateBox(p_230383_1_, p_230383_5_, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 1, 8, 2, 6, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 5, 4, 4, 9, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 1, 5, 8, 4, 9, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 7, 3, 4, 9, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 5, 3, 3, 6, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 7, 7, 1, 8, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 5, 7, 7, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
         this.placeBlock(p_230383_1_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, p_230383_5_);
         return true;
      }
   }

   public static class LeftTurn extends StrongholdPieces.Turn {
      public LeftTurn(int p_i45579_1_, Random p_i45579_2_, MutableBoundingBox p_i45579_3_, Direction p_i45579_4_) {
         super(IStructurePieceType.STRONGHOLD_LEFT_TURN, p_i45579_1_);
         this.setOrientation(p_i45579_4_);
         this.entryDoor = this.randomSmallDoor(p_i45579_2_);
         this.boundingBox = p_i45579_3_;
      }

      public LeftTurn(TemplateManager p_i50134_1_, CompoundNBT p_i50134_2_) {
         super(IStructurePieceType.STRONGHOLD_LEFT_TURN, p_i50134_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateSmallDoorChildRight((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
         } else {
            this.generateSmallDoorChildLeft((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
         }

      }

      public static StrongholdPieces.LeftTurn createPiece(List<StructurePiece> p_175867_0_, Random p_175867_1_, int p_175867_2_, int p_175867_3_, int p_175867_4_, Direction p_175867_5_, int p_175867_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175867_2_, p_175867_3_, p_175867_4_, -1, -1, 0, 5, 5, 5, p_175867_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175867_0_, mutableboundingbox) == null ? new StrongholdPieces.LeftTurn(p_175867_6_, p_175867_1_, mutableboundingbox, p_175867_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 4, 4, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 1, 1, 0);
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class Library extends StrongholdPieces.Stronghold {
      private final boolean isTall;

      public Library(int p_i45578_1_, Random p_i45578_2_, MutableBoundingBox p_i45578_3_, Direction p_i45578_4_) {
         super(IStructurePieceType.STRONGHOLD_LIBRARY, p_i45578_1_);
         this.setOrientation(p_i45578_4_);
         this.entryDoor = this.randomSmallDoor(p_i45578_2_);
         this.boundingBox = p_i45578_3_;
         this.isTall = p_i45578_3_.getYSpan() > 6;
      }

      public Library(TemplateManager p_i50133_1_, CompoundNBT p_i50133_2_) {
         super(IStructurePieceType.STRONGHOLD_LIBRARY, p_i50133_2_);
         this.isTall = p_i50133_2_.getBoolean("Tall");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Tall", this.isTall);
      }

      public static StrongholdPieces.Library createPiece(List<StructurePiece> p_175864_0_, Random p_175864_1_, int p_175864_2_, int p_175864_3_, int p_175864_4_, Direction p_175864_5_, int p_175864_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175864_2_, p_175864_3_, p_175864_4_, -4, -1, 0, 14, 11, 15, p_175864_5_);
         if (!isOkBox(mutableboundingbox) || StructurePiece.findCollisionPiece(p_175864_0_, mutableboundingbox) != null) {
            mutableboundingbox = MutableBoundingBox.orientBox(p_175864_2_, p_175864_3_, p_175864_4_, -4, -1, 0, 14, 6, 15, p_175864_5_);
            if (!isOkBox(mutableboundingbox) || StructurePiece.findCollisionPiece(p_175864_0_, mutableboundingbox) != null) {
               return null;
            }
         }

         return new StrongholdPieces.Library(p_175864_6_, p_175864_1_, mutableboundingbox, p_175864_5_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         int i = 11;
         if (!this.isTall) {
            i = 6;
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 13, i - 1, 14, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 4, 1, 0);
         this.generateMaybeBox(p_230383_1_, p_230383_5_, p_230383_4_, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false);
         int j = 1;
         int k = 12;

         for(int l = 1; l <= 13; ++l) {
            if ((l - 1) % 4 == 0) {
               this.generateBox(p_230383_1_, p_230383_5_, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.generateBox(p_230383_1_, p_230383_5_, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.placeBlock(p_230383_1_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, p_230383_5_);
               if (this.isTall) {
                  this.generateBox(p_230383_1_, p_230383_5_, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                  this.generateBox(p_230383_1_, p_230383_5_, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               }
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               this.generateBox(p_230383_1_, p_230383_5_, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               if (this.isTall) {
                  this.generateBox(p_230383_1_, p_230383_5_, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                  this.generateBox(p_230383_1_, p_230383_5_, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               }
            }
         }

         for(int l1 = 3; l1 < 12; l1 += 2) {
            this.generateBox(p_230383_1_, p_230383_5_, 3, 1, l1, 4, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 1, l1, 7, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 9, 1, l1, 10, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
         }

         if (this.isTall) {
            this.generateBox(p_230383_1_, p_230383_5_, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, p_230383_5_);
            BlockState blockstate5 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
            BlockState blockstate = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
            this.generateBox(p_230383_1_, p_230383_5_, 3, 6, 3, 3, 6, 11, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 10, 6, 3, 10, 6, 9, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 4, 6, 2, 9, 6, 2, blockstate5, blockstate5, false);
            this.generateBox(p_230383_1_, p_230383_5_, 4, 6, 12, 7, 6, 12, blockstate5, blockstate5, false);
            this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 2, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 12, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 10, 6, 2, p_230383_5_);

            for(int i1 = 0; i1 <= 2; ++i1) {
               this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 8 + i1, 6, 12 - i1, p_230383_5_);
               if (i1 != 2) {
                  this.placeBlock(p_230383_1_, Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 8 + i1, 6, 11 - i1, p_230383_5_);
               }
            }

            BlockState blockstate6 = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
            this.placeBlock(p_230383_1_, blockstate6, 10, 1, 13, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate6, 10, 2, 13, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate6, 10, 3, 13, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate6, 10, 4, 13, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate6, 10, 5, 13, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate6, 10, 6, 13, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate6, 10, 7, 13, p_230383_5_);
            int j1 = 7;
            int k1 = 7;
            BlockState blockstate1 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true));
            this.placeBlock(p_230383_1_, blockstate1, 6, 9, 7, p_230383_5_);
            BlockState blockstate2 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true));
            this.placeBlock(p_230383_1_, blockstate2, 7, 9, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate1, 6, 8, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate2, 7, 8, 7, p_230383_5_);
            BlockState blockstate3 = blockstate.setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
            this.placeBlock(p_230383_1_, blockstate3, 6, 7, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate3, 7, 7, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate1, 5, 7, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate2, 8, 7, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate1.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate1.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate2.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate2.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, p_230383_5_);
            BlockState blockstate4 = Blocks.TORCH.defaultBlockState();
            this.placeBlock(p_230383_1_, blockstate4, 5, 8, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate4, 8, 8, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate4, 6, 8, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate4, 6, 8, 8, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate4, 7, 8, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate4, 7, 8, 8, p_230383_5_);
         }

         this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY);
         if (this.isTall) {
            this.placeBlock(p_230383_1_, CAVE_AIR, 12, 9, 1, p_230383_5_);
            this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY);
         }

         return true;
      }
   }

   static class PieceWeight {
      public final Class<? extends StrongholdPieces.Stronghold> pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;

      public PieceWeight(Class<? extends StrongholdPieces.Stronghold> p_i2076_1_, int p_i2076_2_, int p_i2076_3_) {
         this.pieceClass = p_i2076_1_;
         this.weight = p_i2076_2_;
         this.maxPlaceCount = p_i2076_3_;
      }

      public boolean doPlace(int p_75189_1_) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }

   public static class PortalRoom extends StrongholdPieces.Stronghold {
      private boolean hasPlacedSpawner;

      public PortalRoom(int p_i50131_1_, MutableBoundingBox p_i50131_2_, Direction p_i50131_3_) {
         super(IStructurePieceType.STRONGHOLD_PORTAL_ROOM, p_i50131_1_);
         this.setOrientation(p_i50131_3_);
         this.boundingBox = p_i50131_2_;
      }

      public PortalRoom(TemplateManager p_i50132_1_, CompoundNBT p_i50132_2_) {
         super(IStructurePieceType.STRONGHOLD_PORTAL_ROOM, p_i50132_2_);
         this.hasPlacedSpawner = p_i50132_2_.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         if (p_74861_1_ != null) {
            ((StrongholdPieces.Stairs2)p_74861_1_).portalRoomPiece = this;
         }

      }

      public static StrongholdPieces.PortalRoom createPiece(List<StructurePiece> p_175865_0_, int p_175865_1_, int p_175865_2_, int p_175865_3_, Direction p_175865_4_, int p_175865_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175865_1_, p_175865_2_, p_175865_3_, -4, -1, 0, 11, 8, 16, p_175865_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175865_0_, mutableboundingbox) == null ? new StrongholdPieces.PortalRoom(p_175865_5_, mutableboundingbox, p_175865_4_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 10, 7, 15, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, StrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         int i = 6;
         this.generateBox(p_230383_1_, p_230383_5_, 1, i, 1, 1, i, 14, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 9, i, 1, 9, i, 14, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, i, 1, 8, i, 2, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, i, 14, 8, i, 14, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 1, 2, 1, 4, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 1, 1, 9, 1, 4, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 8, 7, 1, 12, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         BlockState blockstate = Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.WEST, Boolean.valueOf(true)).setValue(PaneBlock.EAST, Boolean.valueOf(true));

         for(int j = 3; j < 14; j += 2) {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 3, j, 0, 4, j, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 10, 3, j, 10, 4, j, blockstate, blockstate, false);
         }

         for(int i1 = 2; i1 < 9; i1 += 2) {
            this.generateBox(p_230383_1_, p_230383_5_, i1, 3, 15, i1, 4, 15, blockstate1, blockstate1, false);
         }

         BlockState blockstate5 = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.NORTH);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 5, 6, 1, 7, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 6, 6, 2, 7, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 7, 6, 3, 7, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int k = 4; k <= 6; ++k) {
            this.placeBlock(p_230383_1_, blockstate5, k, 1, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate5, k, 2, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate5, k, 3, 6, p_230383_5_);
         }

         BlockState blockstate6 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
         BlockState blockstate2 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
         BlockState blockstate3 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
         BlockState blockstate4 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
         boolean flag = true;
         boolean[] aboolean = new boolean[12];

         for(int l = 0; l < aboolean.length; ++l) {
            aboolean[l] = p_230383_4_.nextFloat() > 0.9F;
            flag &= aboolean[l];
         }

         this.placeBlock(p_230383_1_, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[0])), 4, 3, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[1])), 5, 3, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[2])), 6, 3, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[3])), 4, 3, 12, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[4])), 5, 3, 12, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[5])), 6, 3, 12, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[6])), 3, 3, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[7])), 3, 3, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[8])), 3, 3, 11, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[9])), 7, 3, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[10])), 7, 3, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[11])), 7, 3, 11, p_230383_5_);
         if (flag) {
            BlockState blockstate7 = Blocks.END_PORTAL.defaultBlockState();
            this.placeBlock(p_230383_1_, blockstate7, 4, 3, 9, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 5, 3, 9, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 6, 3, 9, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 4, 3, 10, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 5, 3, 10, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 6, 3, 10, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 4, 3, 11, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 5, 3, 11, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate7, 6, 3, 11, p_230383_5_);
         }

         if (!this.hasPlacedSpawner) {
            i = this.getWorldY(3);
            BlockPos blockpos = new BlockPos(this.getWorldX(5, 6), i, this.getWorldZ(5, 6));
            if (p_230383_5_.isInside(blockpos)) {
               this.hasPlacedSpawner = true;
               p_230383_1_.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
               TileEntity tileentity = p_230383_1_.getBlockEntity(blockpos);
               if (tileentity instanceof MobSpawnerTileEntity) {
                  ((MobSpawnerTileEntity)tileentity).getSpawner().setEntityId(EntityType.SILVERFISH);
               }
            }
         }

         return true;
      }
   }

   public static class Prison extends StrongholdPieces.Stronghold {
      public Prison(int p_i45576_1_, Random p_i45576_2_, MutableBoundingBox p_i45576_3_, Direction p_i45576_4_) {
         super(IStructurePieceType.STRONGHOLD_PRISON_HALL, p_i45576_1_);
         this.setOrientation(p_i45576_4_);
         this.entryDoor = this.randomSmallDoor(p_i45576_2_);
         this.boundingBox = p_i45576_3_;
      }

      public Prison(TemplateManager p_i50130_1_, CompoundNBT p_i50130_2_) {
         super(IStructurePieceType.STRONGHOLD_PRISON_HALL, p_i50130_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateSmallDoorChildForward((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
      }

      public static StrongholdPieces.Prison createPiece(List<StructurePiece> p_175860_0_, Random p_175860_1_, int p_175860_2_, int p_175860_3_, int p_175860_4_, Direction p_175860_5_, int p_175860_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175860_2_, p_175860_3_, p_175860_4_, -1, -1, 0, 9, 5, 11, p_175860_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175860_0_, mutableboundingbox) == null ? new StrongholdPieces.Prison(p_175860_6_, p_175860_1_, mutableboundingbox, p_175860_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 8, 4, 10, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 1, 1, 0);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 1, 4, 3, 1, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 3, 4, 3, 3, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 7, 4, 3, 7, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 9, 4, 3, 9, false, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int i = 1; i <= 3; ++i) {
            this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, i, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true)).setValue(PaneBlock.EAST, Boolean.valueOf(true)), 4, i, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, i, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.WEST, Boolean.valueOf(true)).setValue(PaneBlock.EAST, Boolean.valueOf(true)), 5, i, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.WEST, Boolean.valueOf(true)).setValue(PaneBlock.EAST, Boolean.valueOf(true)), 6, i, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.WEST, Boolean.valueOf(true)).setValue(PaneBlock.EAST, Boolean.valueOf(true)), 7, i, 5, p_230383_5_);
         }

         this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(true)).setValue(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 8, p_230383_5_);
         BlockState blockstate1 = Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
         BlockState blockstate = Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
         this.placeBlock(p_230383_1_, blockstate1, 4, 1, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate, 4, 2, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 4, 1, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate, 4, 2, 8, p_230383_5_);
         return true;
      }
   }

   public static class RightTurn extends StrongholdPieces.Turn {
      public RightTurn(int p_i50127_1_, Random p_i50127_2_, MutableBoundingBox p_i50127_3_, Direction p_i50127_4_) {
         super(IStructurePieceType.STRONGHOLD_RIGHT_TURN, p_i50127_1_);
         this.setOrientation(p_i50127_4_);
         this.entryDoor = this.randomSmallDoor(p_i50127_2_);
         this.boundingBox = p_i50127_3_;
      }

      public RightTurn(TemplateManager p_i50128_1_, CompoundNBT p_i50128_2_) {
         super(IStructurePieceType.STRONGHOLD_RIGHT_TURN, p_i50128_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateSmallDoorChildLeft((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
         } else {
            this.generateSmallDoorChildRight((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
         }

      }

      public static StrongholdPieces.RightTurn createPiece(List<StructurePiece> p_214824_0_, Random p_214824_1_, int p_214824_2_, int p_214824_3_, int p_214824_4_, Direction p_214824_5_, int p_214824_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_214824_2_, p_214824_3_, p_214824_4_, -1, -1, 0, 5, 5, 5, p_214824_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_214824_0_, mutableboundingbox) == null ? new StrongholdPieces.RightTurn(p_214824_6_, p_214824_1_, mutableboundingbox, p_214824_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 4, 4, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 1, 1, 0);
         Direction direction = this.getOrientation();
         if (direction != Direction.NORTH && direction != Direction.EAST) {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class RoomCrossing extends StrongholdPieces.Stronghold {
      protected final int type;

      public RoomCrossing(int p_i45575_1_, Random p_i45575_2_, MutableBoundingBox p_i45575_3_, Direction p_i45575_4_) {
         super(IStructurePieceType.STRONGHOLD_ROOM_CROSSING, p_i45575_1_);
         this.setOrientation(p_i45575_4_);
         this.entryDoor = this.randomSmallDoor(p_i45575_2_);
         this.boundingBox = p_i45575_3_;
         this.type = p_i45575_2_.nextInt(5);
      }

      public RoomCrossing(TemplateManager p_i50125_1_, CompoundNBT p_i50125_2_) {
         super(IStructurePieceType.STRONGHOLD_ROOM_CROSSING, p_i50125_2_);
         this.type = p_i50125_2_.getInt("Type");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putInt("Type", this.type);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateSmallDoorChildForward((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 4, 1);
         this.generateSmallDoorChildLeft((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 4);
         this.generateSmallDoorChildRight((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 4);
      }

      public static StrongholdPieces.RoomCrossing createPiece(List<StructurePiece> p_175859_0_, Random p_175859_1_, int p_175859_2_, int p_175859_3_, int p_175859_4_, Direction p_175859_5_, int p_175859_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175859_2_, p_175859_3_, p_175859_4_, -4, -1, 0, 11, 7, 11, p_175859_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175859_0_, mutableboundingbox) == null ? new StrongholdPieces.RoomCrossing(p_175859_6_, p_175859_1_, mutableboundingbox, p_175859_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 10, 6, 10, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 4, 1, 0);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
         switch(this.type) {
         case 0:
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, p_230383_5_);
            break;
         case 1:
            for(int i1 = 0; i1 < 5; ++i1) {
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + i1, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + i1, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i1, 1, 3, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i1, 1, 7, p_230383_5_);
            }

            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.WATER.defaultBlockState(), 5, 4, 5, p_230383_5_);
            break;
         case 2:
            for(int i = 1; i <= 9; ++i) {
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, i, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, i, p_230383_5_);
            }

            for(int j = 1; j <= 9; ++j) {
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), j, 3, 1, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), j, 3, 9, p_230383_5_);
            }

            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, p_230383_5_);

            for(int k = 1; k <= 3; ++k) {
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 4, k, 4, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 6, k, 4, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 4, k, 6, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.COBBLESTONE.defaultBlockState(), 6, k, 6, p_230383_5_);
            }

            this.placeBlock(p_230383_1_, Blocks.TORCH.defaultBlockState(), 5, 3, 5, p_230383_5_);

            for(int l = 2; l <= 8; ++l) {
               this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, l, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, l, p_230383_5_);
               if (l <= 3 || l >= 7) {
                  this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, l, p_230383_5_);
                  this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, l, p_230383_5_);
                  this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, l, p_230383_5_);
               }

               this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, l, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, l, p_230383_5_);
            }

            BlockState blockstate = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
            this.placeBlock(p_230383_1_, blockstate, 9, 1, 3, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate, 9, 2, 3, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate, 9, 3, 3, p_230383_5_);
            this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 3, 4, 8, LootTables.STRONGHOLD_CROSSING);
         }

         return true;
      }
   }

   public static class Stairs extends StrongholdPieces.Stronghold {
      private final boolean isSource;

      public Stairs(IStructurePieceType p_i50120_1_, int p_i50120_2_, Random p_i50120_3_, int p_i50120_4_, int p_i50120_5_) {
         super(p_i50120_1_, p_i50120_2_);
         this.isSource = true;
         this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(p_i50120_3_));
         this.entryDoor = StrongholdPieces.Stronghold.Door.OPENING;
         if (this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(p_i50120_4_, 64, p_i50120_5_, p_i50120_4_ + 5 - 1, 74, p_i50120_5_ + 5 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(p_i50120_4_, 64, p_i50120_5_, p_i50120_4_ + 5 - 1, 74, p_i50120_5_ + 5 - 1);
         }

      }

      public Stairs(int p_i45574_1_, Random p_i45574_2_, MutableBoundingBox p_i45574_3_, Direction p_i45574_4_) {
         super(IStructurePieceType.STRONGHOLD_STAIRS_DOWN, p_i45574_1_);
         this.isSource = false;
         this.setOrientation(p_i45574_4_);
         this.entryDoor = this.randomSmallDoor(p_i45574_2_);
         this.boundingBox = p_i45574_3_;
      }

      public Stairs(IStructurePieceType p_i50121_1_, CompoundNBT p_i50121_2_) {
         super(p_i50121_1_, p_i50121_2_);
         this.isSource = p_i50121_2_.getBoolean("Source");
      }

      public Stairs(TemplateManager p_i50122_1_, CompoundNBT p_i50122_2_) {
         this(IStructurePieceType.STRONGHOLD_STAIRS_DOWN, p_i50122_2_);
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Source", this.isSource);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         if (this.isSource) {
            StrongholdPieces.imposedPiece = StrongholdPieces.Crossing.class;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
      }

      public static StrongholdPieces.Stairs createPiece(List<StructurePiece> p_175863_0_, Random p_175863_1_, int p_175863_2_, int p_175863_3_, int p_175863_4_, Direction p_175863_5_, int p_175863_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175863_2_, p_175863_3_, p_175863_4_, -1, -7, 0, 5, 11, 5, p_175863_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175863_0_, mutableboundingbox) == null ? new StrongholdPieces.Stairs(p_175863_6_, p_175863_1_, mutableboundingbox, p_175863_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 10, 4, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, p_230383_5_);
         return true;
      }
   }

   public static class Stairs2 extends StrongholdPieces.Stairs {
      public StrongholdPieces.PieceWeight previousPiece;
      @Nullable
      public StrongholdPieces.PortalRoom portalRoomPiece;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public Stairs2(Random p_i50117_1_, int p_i50117_2_, int p_i50117_3_) {
         super(IStructurePieceType.STRONGHOLD_START, 0, p_i50117_1_, p_i50117_2_, p_i50117_3_);
      }

      public Stairs2(TemplateManager p_i50118_1_, CompoundNBT p_i50118_2_) {
         super(IStructurePieceType.STRONGHOLD_START, p_i50118_2_);
      }
   }

   public static class StairsStraight extends StrongholdPieces.Stronghold {
      public StairsStraight(int p_i45572_1_, Random p_i45572_2_, MutableBoundingBox p_i45572_3_, Direction p_i45572_4_) {
         super(IStructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, p_i45572_1_);
         this.setOrientation(p_i45572_4_);
         this.entryDoor = this.randomSmallDoor(p_i45572_2_);
         this.boundingBox = p_i45572_3_;
      }

      public StairsStraight(TemplateManager p_i50113_1_, CompoundNBT p_i50113_2_) {
         super(IStructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, p_i50113_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateSmallDoorChildForward((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
      }

      public static StrongholdPieces.StairsStraight createPiece(List<StructurePiece> p_175861_0_, Random p_175861_1_, int p_175861_2_, int p_175861_3_, int p_175861_4_, Direction p_175861_5_, int p_175861_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175861_2_, p_175861_3_, p_175861_4_, -1, -7, 0, 5, 11, 8, p_175861_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175861_0_, mutableboundingbox) == null ? new StrongholdPieces.StairsStraight(p_175861_6_, p_175861_1_, mutableboundingbox, p_175861_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 10, 7, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
         BlockState blockstate = Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.SOUTH);

         for(int i = 0; i < 6; ++i) {
            this.placeBlock(p_230383_1_, blockstate, 1, 6 - i, 1 + i, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate, 2, 6 - i, 1 + i, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate, 3, 6 - i, 1 + i, p_230383_5_);
            if (i < 5) {
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - i, 1 + i, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - i, 1 + i, p_230383_5_);
               this.placeBlock(p_230383_1_, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - i, 1 + i, p_230383_5_);
            }
         }

         return true;
      }
   }

   static class Stones extends StructurePiece.BlockSelector {
      private Stones() {
      }

      public void next(Random p_75062_1_, int p_75062_2_, int p_75062_3_, int p_75062_4_, boolean p_75062_5_) {
         if (p_75062_5_) {
            float f = p_75062_1_.nextFloat();
            if (f < 0.2F) {
               this.next = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            } else if (f < 0.5F) {
               this.next = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            } else if (f < 0.55F) {
               this.next = Blocks.INFESTED_STONE_BRICKS.defaultBlockState();
            } else {
               this.next = Blocks.STONE_BRICKS.defaultBlockState();
            }
         } else {
            this.next = Blocks.CAVE_AIR.defaultBlockState();
         }

      }
   }

   public static class Straight extends StrongholdPieces.Stronghold {
      private final boolean leftChild;
      private final boolean rightChild;

      public Straight(int p_i45573_1_, Random p_i45573_2_, MutableBoundingBox p_i45573_3_, Direction p_i45573_4_) {
         super(IStructurePieceType.STRONGHOLD_STRAIGHT, p_i45573_1_);
         this.setOrientation(p_i45573_4_);
         this.entryDoor = this.randomSmallDoor(p_i45573_2_);
         this.boundingBox = p_i45573_3_;
         this.leftChild = p_i45573_2_.nextInt(2) == 0;
         this.rightChild = p_i45573_2_.nextInt(2) == 0;
      }

      public Straight(TemplateManager p_i50115_1_, CompoundNBT p_i50115_2_) {
         super(IStructurePieceType.STRONGHOLD_STRAIGHT, p_i50115_2_);
         this.leftChild = p_i50115_2_.getBoolean("Left");
         this.rightChild = p_i50115_2_.getBoolean("Right");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Left", this.leftChild);
         p_143011_1_.putBoolean("Right", this.rightChild);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateSmallDoorChildForward((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 1);
         if (this.leftChild) {
            this.generateSmallDoorChildLeft((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 2);
         }

         if (this.rightChild) {
            this.generateSmallDoorChildRight((StrongholdPieces.Stairs2)p_74861_1_, p_74861_2_, p_74861_3_, 1, 2);
         }

      }

      public static StrongholdPieces.Straight createPiece(List<StructurePiece> p_175862_0_, Random p_175862_1_, int p_175862_2_, int p_175862_3_, int p_175862_4_, Direction p_175862_5_, int p_175862_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175862_2_, p_175862_3_, p_175862_4_, -1, -1, 0, 5, 5, 7, p_175862_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175862_0_, mutableboundingbox) == null ? new StrongholdPieces.Straight(p_175862_6_, p_175862_1_, mutableboundingbox, p_175862_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 4, 6, true, p_230383_4_, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(p_230383_1_, p_230383_4_, p_230383_5_, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         BlockState blockstate = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
         BlockState blockstate1 = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
         this.maybeGenerateBlock(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 1, 2, 1, blockstate);
         this.maybeGenerateBlock(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 3, 2, 1, blockstate1);
         this.maybeGenerateBlock(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 1, 2, 5, blockstate);
         this.maybeGenerateBlock(p_230383_1_, p_230383_5_, p_230383_4_, 0.1F, 3, 2, 5, blockstate1);
         if (this.leftChild) {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightChild) {
            this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   abstract static class Stronghold extends StructurePiece {
      protected StrongholdPieces.Stronghold.Door entryDoor = StrongholdPieces.Stronghold.Door.OPENING;

      protected Stronghold(IStructurePieceType p_i50110_1_, int p_i50110_2_) {
         super(p_i50110_1_, p_i50110_2_);
      }

      public Stronghold(IStructurePieceType p_i50111_1_, CompoundNBT p_i50111_2_) {
         super(p_i50111_1_, p_i50111_2_);
         this.entryDoor = StrongholdPieces.Stronghold.Door.valueOf(p_i50111_2_.getString("EntryDoor"));
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         p_143011_1_.putString("EntryDoor", this.entryDoor.name());
      }

      protected void generateSmallDoor(ISeedReader p_242917_1_, Random p_242917_2_, MutableBoundingBox p_242917_3_, StrongholdPieces.Stronghold.Door p_242917_4_, int p_242917_5_, int p_242917_6_, int p_242917_7_) {
         switch(p_242917_4_) {
         case OPENING:
            this.generateBox(p_242917_1_, p_242917_3_, p_242917_5_, p_242917_6_, p_242917_7_, p_242917_5_ + 3 - 1, p_242917_6_ + 3 - 1, p_242917_7_, CAVE_AIR, CAVE_AIR, false);
            break;
         case WOOD_DOOR:
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 1, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 2, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 2, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 2, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.OAK_DOOR.defaultBlockState(), p_242917_5_ + 1, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), p_242917_5_ + 1, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            break;
         case GRATES:
            this.placeBlock(p_242917_1_, Blocks.CAVE_AIR.defaultBlockState(), p_242917_5_ + 1, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.CAVE_AIR.defaultBlockState(), p_242917_5_ + 1, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.WEST, Boolean.valueOf(true)), p_242917_5_, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.WEST, Boolean.valueOf(true)), p_242917_5_, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.EAST, Boolean.valueOf(true)).setValue(PaneBlock.WEST, Boolean.valueOf(true)), p_242917_5_, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.EAST, Boolean.valueOf(true)).setValue(PaneBlock.WEST, Boolean.valueOf(true)), p_242917_5_ + 1, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.EAST, Boolean.valueOf(true)).setValue(PaneBlock.WEST, Boolean.valueOf(true)), p_242917_5_ + 2, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.EAST, Boolean.valueOf(true)), p_242917_5_ + 2, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.EAST, Boolean.valueOf(true)), p_242917_5_ + 2, p_242917_6_, p_242917_7_, p_242917_3_);
            break;
         case IRON_DOOR:
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 1, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 2, p_242917_6_ + 2, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 2, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BRICKS.defaultBlockState(), p_242917_5_ + 2, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_DOOR.defaultBlockState(), p_242917_5_ + 1, p_242917_6_, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), p_242917_5_ + 1, p_242917_6_ + 1, p_242917_7_, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BUTTON.defaultBlockState().setValue(AbstractButtonBlock.FACING, Direction.NORTH), p_242917_5_ + 2, p_242917_6_ + 1, p_242917_7_ + 1, p_242917_3_);
            this.placeBlock(p_242917_1_, Blocks.STONE_BUTTON.defaultBlockState().setValue(AbstractButtonBlock.FACING, Direction.SOUTH), p_242917_5_ + 2, p_242917_6_ + 1, p_242917_7_ - 1, p_242917_3_);
         }

      }

      protected StrongholdPieces.Stronghold.Door randomSmallDoor(Random p_74988_1_) {
         int i = p_74988_1_.nextInt(5);
         switch(i) {
         case 0:
         case 1:
         default:
            return StrongholdPieces.Stronghold.Door.OPENING;
         case 2:
            return StrongholdPieces.Stronghold.Door.WOOD_DOOR;
         case 3:
            return StrongholdPieces.Stronghold.Door.GRATES;
         case 4:
            return StrongholdPieces.Stronghold.Door.IRON_DOOR;
         }
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildForward(StrongholdPieces.Stairs2 p_74986_1_, List<StructurePiece> p_74986_2_, Random p_74986_3_, int p_74986_4_, int p_74986_5_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.x0 + p_74986_4_, this.boundingBox.y0 + p_74986_5_, this.boundingBox.z0 - 1, direction, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.x0 + p_74986_4_, this.boundingBox.y0 + p_74986_5_, this.boundingBox.z1 + 1, direction, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + p_74986_5_, this.boundingBox.z0 + p_74986_4_, direction, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + p_74986_5_, this.boundingBox.z0 + p_74986_4_, direction, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildLeft(StrongholdPieces.Stairs2 p_74989_1_, List<StructurePiece> p_74989_2_, Random p_74989_3_, int p_74989_4_, int p_74989_5_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + p_74989_4_, this.boundingBox.z0 + p_74989_5_, Direction.WEST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + p_74989_4_, this.boundingBox.z0 + p_74989_5_, Direction.WEST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.x0 + p_74989_5_, this.boundingBox.y0 + p_74989_4_, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.x0 + p_74989_5_, this.boundingBox.y0 + p_74989_4_, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildRight(StrongholdPieces.Stairs2 p_74987_1_, List<StructurePiece> p_74987_2_, Random p_74987_3_, int p_74987_4_, int p_74987_5_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + p_74987_4_, this.boundingBox.z0 + p_74987_5_, Direction.EAST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + p_74987_4_, this.boundingBox.z0 + p_74987_5_, Direction.EAST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.x0 + p_74987_5_, this.boundingBox.y0 + p_74987_4_, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.x0 + p_74987_5_, this.boundingBox.y0 + p_74987_4_, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
            }
         }

         return null;
      }

      protected static boolean isOkBox(MutableBoundingBox p_74991_0_) {
         return p_74991_0_ != null && p_74991_0_.y0 > 10;
      }

      public static enum Door {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }

   public abstract static class Turn extends StrongholdPieces.Stronghold {
      protected Turn(IStructurePieceType p_i50108_1_, int p_i50108_2_) {
         super(p_i50108_1_, p_i50108_2_);
      }

      public Turn(IStructurePieceType p_i50109_1_, CompoundNBT p_i50109_2_) {
         super(p_i50109_1_, p_i50109_2_);
      }
   }
}
