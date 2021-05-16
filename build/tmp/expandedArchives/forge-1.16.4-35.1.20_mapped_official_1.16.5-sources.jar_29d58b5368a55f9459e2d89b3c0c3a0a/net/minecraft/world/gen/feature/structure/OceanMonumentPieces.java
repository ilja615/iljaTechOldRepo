package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanMonumentPieces {
   public static class DoubleXRoom extends OceanMonumentPieces.Piece {
      public DoubleXRoom(Direction p_i50661_1_, OceanMonumentPieces.RoomDefinition p_i50661_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, p_i50661_1_, p_i50661_2_, 2, 1, 1);
      }

      public DoubleXRoom(TemplateManager p_i50662_1_, CompoundNBT p_i50662_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, p_i50662_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 8, 0, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 4, 1, 7, 4, 6, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 8, 4, 1, 14, 4, 6, BASE_GRAY);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 15, 3, 0, 15, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 0, 15, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 7, 14, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 15, 2, 0, 15, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 0, 15, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 7, 14, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 15, 1, 0, 15, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 0, 15, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 0, 10, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 2, 0, 9, 2, 3, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 3, 0, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 6, 2, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 9, 2, 3, p_230383_5_);
         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 7, 4, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 11, 1, 0, 12, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 11, 1, 7, 12, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 15, 1, 3, 15, 2, 4);
         }

         return true;
      }
   }

   public static class DoubleXYRoom extends OceanMonumentPieces.Piece {
      public DoubleXYRoom(Direction p_i50659_1_, OceanMonumentPieces.RoomDefinition p_i50659_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, 1, p_i50659_1_, p_i50659_2_, 2, 2, 1);
      }

      public DoubleXYRoom(TemplateManager p_i50660_1_, CompoundNBT p_i50660_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, p_i50660_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition2 = oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition3 = oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 8, 0, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition2.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 8, 1, 7, 8, 6, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition3.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 8, 8, 1, 14, 8, 6, BASE_GRAY);
         }

         for(int i = 1; i <= 7; ++i) {
            BlockState blockstate = BASE_LIGHT;
            if (i == 2 || i == 6) {
               blockstate = BASE_GRAY;
            }

            this.generateBox(p_230383_1_, p_230383_5_, 0, i, 0, 0, i, 7, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 15, i, 0, 15, i, 7, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, i, 0, 15, i, 0, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, i, 7, 14, i, 7, blockstate, blockstate, false);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 3, 2, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 2, 4, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 5, 4, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 13, 1, 3, 13, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 11, 1, 2, 12, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 11, 1, 5, 12, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 3, 5, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 1, 3, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 7, 2, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 5, 2, 5, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 5, 2, 10, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 5, 5, 5, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 5, 5, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 6, 6, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 9, 6, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 6, 6, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 9, 6, 5, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 4, 3, 6, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 4, 3, 10, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 5, 4, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 5, 4, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 10, 4, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 10, 4, 5, p_230383_5_);
         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 7, 4, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 11, 1, 0, 12, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 11, 1, 7, 12, 2, 7);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 15, 1, 3, 15, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 5, 0, 4, 6, 0);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 5, 7, 4, 6, 7);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 5, 3, 0, 6, 4);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 11, 5, 0, 12, 6, 0);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 11, 5, 7, 12, 6, 7);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 15, 5, 3, 15, 6, 4);
         }

         return true;
      }
   }

   public static class DoubleYRoom extends OceanMonumentPieces.Piece {
      public DoubleYRoom(Direction p_i50657_1_, OceanMonumentPieces.RoomDefinition p_i50657_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, p_i50657_1_, p_i50657_2_, 1, 2, 1);
      }

      public DoubleYRoom(TemplateManager p_i50658_1_, CompoundNBT p_i50658_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, p_i50658_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.UP.get3DDataValue()];
         if (oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 8, 1, 6, 8, 6, BASE_GRAY);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 4, 0, 0, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 4, 0, 7, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 0, 6, 4, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 7, 6, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 4, 1, 2, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 2, 1, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 4, 1, 5, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 4, 2, 6, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 4, 5, 2, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 5, 1, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 4, 5, 5, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 4, 5, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;

         for(int i = 1; i <= 5; i += 4) {
            int j = 0;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 2, i, j, 2, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 5, i, j, 5, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 3, i + 2, j, 4, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, 0, i, j, 7, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 0, i + 1, j, 7, i + 1, j, BASE_GRAY, BASE_GRAY, false);
            }

            j = 7;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 2, i, j, 2, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 5, i, j, 5, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 3, i + 2, j, 4, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, 0, i, j, 7, i + 2, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 0, i + 1, j, 7, i + 1, j, BASE_GRAY, BASE_GRAY, false);
            }

            int k = 0;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, k, i, 2, k, i + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, k, i, 5, k, i + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, k, i + 2, 3, k, i + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, k, i, 0, k, i + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, k, i + 1, 0, k, i + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            k = 7;
            if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, k, i, 2, k, i + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, k, i, 5, k, i + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, k, i + 2, 3, k, i + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, k, i, 0, k, i + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, k, i + 1, 0, k, i + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            oceanmonumentpieces$roomdefinition1 = oceanmonumentpieces$roomdefinition;
         }

         return true;
      }
   }

   public static class DoubleYZRoom extends OceanMonumentPieces.Piece {
      public DoubleYZRoom(Direction p_i50655_1_, OceanMonumentPieces.RoomDefinition p_i50655_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, 1, p_i50655_1_, p_i50655_2_, 1, 2, 2);
      }

      public DoubleYZRoom(TemplateManager p_i50656_1_, CompoundNBT p_i50656_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, p_i50656_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition2 = oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition3 = oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 8, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition3.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 8, 1, 6, 8, 7, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition2.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 8, 8, 6, 8, 14, BASE_GRAY);
         }

         for(int i = 1; i <= 7; ++i) {
            BlockState blockstate = BASE_LIGHT;
            if (i == 2 || i == 6) {
               blockstate = BASE_GRAY;
            }

            this.generateBox(p_230383_1_, p_230383_5_, 0, i, 0, 0, i, 15, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, i, 0, 7, i, 15, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, i, 0, 6, i, 0, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, i, 15, 6, i, 15, blockstate, blockstate, false);
         }

         for(int j = 1; j <= 7; ++j) {
            BlockState blockstate1 = BASE_BLACK;
            if (j == 2 || j == 6) {
               blockstate1 = LAMP_BLOCK;
            }

            this.generateBox(p_230383_1_, p_230383_5_, 3, j, 7, 4, j, 8, blockstate1, blockstate1, false);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 7, 1, 3, 7, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 15, 4, 2, 15);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 11, 0, 2, 12);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 7, 1, 11, 7, 2, 12);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 5, 0, 4, 6, 0);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 7, 5, 3, 7, 6, 4);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 4, 2, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 2, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 5, 6, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (oceanmonumentpieces$roomdefinition3.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 5, 3, 0, 6, 4);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 2, 2, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 2, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 5, 1, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 5, 15, 4, 6, 15);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 5, 11, 0, 6, 12);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 10, 2, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 10, 1, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 13, 1, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (oceanmonumentpieces$roomdefinition2.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 7, 5, 11, 7, 6, 12);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 4, 10, 6, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 10, 6, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 13, 6, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   public static class DoubleZRoom extends OceanMonumentPieces.Piece {
      public DoubleZRoom(Direction p_i50653_1_, OceanMonumentPieces.RoomDefinition p_i50653_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, p_i50653_1_, p_i50653_2_, 1, 1, 2);
      }

      public DoubleZRoom(TemplateManager p_i50654_1_, CompoundNBT p_i50654_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, p_i50654_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 8, oceanmonumentpieces$roomdefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 0, oceanmonumentpieces$roomdefinition1.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (oceanmonumentpieces$roomdefinition1.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 4, 1, 6, 4, 7, BASE_GRAY);
         }

         if (oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 4, 8, 6, 4, 14, BASE_GRAY);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 0, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 0, 7, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 15, 6, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 0, 7, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 0, 7, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 15, 6, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 0, 0, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 0, 7, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 0, 7, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 15, 6, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 1, 1, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 1, 6, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 1, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 3, 1, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 13, 1, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 13, 6, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 13, 1, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 3, 13, 6, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 6, 2, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 6, 5, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 9, 2, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 9, 5, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 2, 6, 4, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 2, 9, 4, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 2, 7, 2, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 7, 5, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 2, 2, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 5, 2, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 2, 2, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, LAMP_BLOCK, 5, 2, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 2, 3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 5, 3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 2, 3, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 5, 3, 10, p_230383_5_);
         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 7, 1, 3, 7, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition1.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 3, 0, 2, 4);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 15, 4, 2, 15);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 11, 0, 2, 12);
         }

         if (oceanmonumentpieces$roomdefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 7, 1, 11, 7, 2, 12);
         }

         return true;
      }
   }

   public static class EntryRoom extends OceanMonumentPieces.Piece {
      public EntryRoom(Direction p_i45592_1_, OceanMonumentPieces.RoomDefinition p_i45592_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, p_i45592_1_, p_i45592_2_, 1, 1, 1);
      }

      public EntryRoom(TemplateManager p_i50652_1_, CompoundNBT p_i50652_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, p_i50652_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 2, 0, 7, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 0, 2, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 7, 4, 2, 7);
         }

         if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 3, 1, 2, 4);
         }

         if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 6, 1, 3, 7, 2, 4);
         }

         return true;
      }
   }

   static class FitSimpleRoomHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private FitSimpleRoomHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return true;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         return new OceanMonumentPieces.SimpleRoom(p_175968_1_, p_175968_2_, p_175968_3_);
      }
   }

   static class FitSimpleRoomTopHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private FitSimpleRoomTopHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return !p_175969_1_.hasOpening[Direction.WEST.get3DDataValue()] && !p_175969_1_.hasOpening[Direction.EAST.get3DDataValue()] && !p_175969_1_.hasOpening[Direction.NORTH.get3DDataValue()] && !p_175969_1_.hasOpening[Direction.SOUTH.get3DDataValue()] && !p_175969_1_.hasOpening[Direction.UP.get3DDataValue()];
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         return new OceanMonumentPieces.SimpleTopRoom(p_175968_1_, p_175968_2_);
      }
   }

   interface IMonumentRoomFitHelper {
      boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_);

      OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_);
   }

   public static class MonumentBuilding extends OceanMonumentPieces.Piece {
      private OceanMonumentPieces.RoomDefinition sourceRoom;
      private OceanMonumentPieces.RoomDefinition coreRoom;
      private final List<OceanMonumentPieces.Piece> childPieces = Lists.newArrayList();

      public MonumentBuilding(Random p_i45599_1_, int p_i45599_2_, int p_i45599_3_, Direction p_i45599_4_) {
         super(IStructurePieceType.OCEAN_MONUMENT_BUILDING, 0);
         this.setOrientation(p_i45599_4_);
         Direction direction = this.getOrientation();
         if (direction.getAxis() == Direction.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(p_i45599_2_, 39, p_i45599_3_, p_i45599_2_ + 58 - 1, 61, p_i45599_3_ + 58 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(p_i45599_2_, 39, p_i45599_3_, p_i45599_2_ + 58 - 1, 61, p_i45599_3_ + 58 - 1);
         }

         List<OceanMonumentPieces.RoomDefinition> list = this.generateRoomGraph(p_i45599_1_);
         this.sourceRoom.claimed = true;
         this.childPieces.add(new OceanMonumentPieces.EntryRoom(direction, this.sourceRoom));
         this.childPieces.add(new OceanMonumentPieces.MonumentCoreRoom(direction, this.coreRoom));
         List<OceanMonumentPieces.IMonumentRoomFitHelper> list1 = Lists.newArrayList();
         list1.add(new OceanMonumentPieces.XYDoubleRoomFitHelper());
         list1.add(new OceanMonumentPieces.YZDoubleRoomFitHelper());
         list1.add(new OceanMonumentPieces.ZDoubleRoomFitHelper());
         list1.add(new OceanMonumentPieces.XDoubleRoomFitHelper());
         list1.add(new OceanMonumentPieces.YDoubleRoomFitHelper());
         list1.add(new OceanMonumentPieces.FitSimpleRoomTopHelper());
         list1.add(new OceanMonumentPieces.FitSimpleRoomHelper());

         for(OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition : list) {
            if (!oceanmonumentpieces$roomdefinition.claimed && !oceanmonumentpieces$roomdefinition.isSpecial()) {
               for(OceanMonumentPieces.IMonumentRoomFitHelper oceanmonumentpieces$imonumentroomfithelper : list1) {
                  if (oceanmonumentpieces$imonumentroomfithelper.fits(oceanmonumentpieces$roomdefinition)) {
                     this.childPieces.add(oceanmonumentpieces$imonumentroomfithelper.create(direction, oceanmonumentpieces$roomdefinition, p_i45599_1_));
                     break;
                  }
               }
            }
         }

         int j = this.boundingBox.y0;
         int k = this.getWorldX(9, 22);
         int l = this.getWorldZ(9, 22);

         for(OceanMonumentPieces.Piece oceanmonumentpieces$piece : this.childPieces) {
            oceanmonumentpieces$piece.getBoundingBox().move(k, j, l);
         }

         MutableBoundingBox mutableboundingbox1 = MutableBoundingBox.createProper(this.getWorldX(1, 1), this.getWorldY(1), this.getWorldZ(1, 1), this.getWorldX(23, 21), this.getWorldY(8), this.getWorldZ(23, 21));
         MutableBoundingBox mutableboundingbox2 = MutableBoundingBox.createProper(this.getWorldX(34, 1), this.getWorldY(1), this.getWorldZ(34, 1), this.getWorldX(56, 21), this.getWorldY(8), this.getWorldZ(56, 21));
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.createProper(this.getWorldX(22, 22), this.getWorldY(13), this.getWorldZ(22, 22), this.getWorldX(35, 35), this.getWorldY(17), this.getWorldZ(35, 35));
         int i = p_i45599_1_.nextInt();
         this.childPieces.add(new OceanMonumentPieces.WingRoom(direction, mutableboundingbox1, i++));
         this.childPieces.add(new OceanMonumentPieces.WingRoom(direction, mutableboundingbox2, i++));
         this.childPieces.add(new OceanMonumentPieces.Penthouse(direction, mutableboundingbox));
      }

      public MonumentBuilding(TemplateManager p_i50665_1_, CompoundNBT p_i50665_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_BUILDING, p_i50665_2_);
      }

      private List<OceanMonumentPieces.RoomDefinition> generateRoomGraph(Random p_175836_1_) {
         OceanMonumentPieces.RoomDefinition[] aoceanmonumentpieces$roomdefinition = new OceanMonumentPieces.RoomDefinition[75];

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 4; ++j) {
               int k = 0;
               int l = getRoomIndex(i, 0, j);
               aoceanmonumentpieces$roomdefinition[l] = new OceanMonumentPieces.RoomDefinition(l);
            }
         }

         for(int i2 = 0; i2 < 5; ++i2) {
            for(int l2 = 0; l2 < 4; ++l2) {
               int k3 = 1;
               int j4 = getRoomIndex(i2, 1, l2);
               aoceanmonumentpieces$roomdefinition[j4] = new OceanMonumentPieces.RoomDefinition(j4);
            }
         }

         for(int j2 = 1; j2 < 4; ++j2) {
            for(int i3 = 0; i3 < 2; ++i3) {
               int l3 = 2;
               int k4 = getRoomIndex(j2, 2, i3);
               aoceanmonumentpieces$roomdefinition[k4] = new OceanMonumentPieces.RoomDefinition(k4);
            }
         }

         this.sourceRoom = aoceanmonumentpieces$roomdefinition[GRIDROOM_SOURCE_INDEX];

         for(int k2 = 0; k2 < 5; ++k2) {
            for(int j3 = 0; j3 < 5; ++j3) {
               for(int i4 = 0; i4 < 3; ++i4) {
                  int l4 = getRoomIndex(k2, i4, j3);
                  if (aoceanmonumentpieces$roomdefinition[l4] != null) {
                     for(Direction direction : Direction.values()) {
                        int i1 = k2 + direction.getStepX();
                        int j1 = i4 + direction.getStepY();
                        int k1 = j3 + direction.getStepZ();
                        if (i1 >= 0 && i1 < 5 && k1 >= 0 && k1 < 5 && j1 >= 0 && j1 < 3) {
                           int l1 = getRoomIndex(i1, j1, k1);
                           if (aoceanmonumentpieces$roomdefinition[l1] != null) {
                              if (k1 == j3) {
                                 aoceanmonumentpieces$roomdefinition[l4].setConnection(direction, aoceanmonumentpieces$roomdefinition[l1]);
                              } else {
                                 aoceanmonumentpieces$roomdefinition[l4].setConnection(direction.getOpposite(), aoceanmonumentpieces$roomdefinition[l1]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = new OceanMonumentPieces.RoomDefinition(1003);
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition1 = new OceanMonumentPieces.RoomDefinition(1001);
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition2 = new OceanMonumentPieces.RoomDefinition(1002);
         aoceanmonumentpieces$roomdefinition[GRIDROOM_TOP_CONNECT_INDEX].setConnection(Direction.UP, oceanmonumentpieces$roomdefinition);
         aoceanmonumentpieces$roomdefinition[GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, oceanmonumentpieces$roomdefinition1);
         aoceanmonumentpieces$roomdefinition[GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, oceanmonumentpieces$roomdefinition2);
         oceanmonumentpieces$roomdefinition.claimed = true;
         oceanmonumentpieces$roomdefinition1.claimed = true;
         oceanmonumentpieces$roomdefinition2.claimed = true;
         this.sourceRoom.isSource = true;
         this.coreRoom = aoceanmonumentpieces$roomdefinition[getRoomIndex(p_175836_1_.nextInt(4), 0, 2)];
         this.coreRoom.claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         List<OceanMonumentPieces.RoomDefinition> list = Lists.newArrayList();

         for(OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition4 : aoceanmonumentpieces$roomdefinition) {
            if (oceanmonumentpieces$roomdefinition4 != null) {
               oceanmonumentpieces$roomdefinition4.updateOpenings();
               list.add(oceanmonumentpieces$roomdefinition4);
            }
         }

         oceanmonumentpieces$roomdefinition.updateOpenings();
         Collections.shuffle(list, p_175836_1_);
         int i5 = 1;

         for(OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition3 : list) {
            int j5 = 0;
            int k5 = 0;

            while(j5 < 2 && k5 < 5) {
               ++k5;
               int l5 = p_175836_1_.nextInt(6);
               if (oceanmonumentpieces$roomdefinition3.hasOpening[l5]) {
                  int i6 = Direction.from3DDataValue(l5).getOpposite().get3DDataValue();
                  oceanmonumentpieces$roomdefinition3.hasOpening[l5] = false;
                  oceanmonumentpieces$roomdefinition3.connections[l5].hasOpening[i6] = false;
                  if (oceanmonumentpieces$roomdefinition3.findSource(i5++) && oceanmonumentpieces$roomdefinition3.connections[l5].findSource(i5++)) {
                     ++j5;
                  } else {
                     oceanmonumentpieces$roomdefinition3.hasOpening[l5] = true;
                     oceanmonumentpieces$roomdefinition3.connections[l5].hasOpening[i6] = true;
                  }
               }
            }
         }

         list.add(oceanmonumentpieces$roomdefinition);
         list.add(oceanmonumentpieces$roomdefinition1);
         list.add(oceanmonumentpieces$roomdefinition2);
         return list;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         int i = Math.max(p_230383_1_.getSeaLevel(), 64) - this.boundingBox.y0;
         this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 0, 0, 58, i, 58);
         this.generateWing(false, 0, p_230383_1_, p_230383_4_, p_230383_5_);
         this.generateWing(true, 33, p_230383_1_, p_230383_4_, p_230383_5_);
         this.generateEntranceArchs(p_230383_1_, p_230383_4_, p_230383_5_);
         this.generateEntranceWall(p_230383_1_, p_230383_4_, p_230383_5_);
         this.generateRoofPiece(p_230383_1_, p_230383_4_, p_230383_5_);
         this.generateLowerWall(p_230383_1_, p_230383_4_, p_230383_5_);
         this.generateMiddleWall(p_230383_1_, p_230383_4_, p_230383_5_);
         this.generateUpperWall(p_230383_1_, p_230383_4_, p_230383_5_);

         for(int j = 0; j < 7; ++j) {
            int k = 0;

            while(k < 7) {
               if (k == 0 && j == 3) {
                  k = 6;
               }

               int l = j * 9;
               int i1 = k * 9;

               for(int j1 = 0; j1 < 4; ++j1) {
                  for(int k1 = 0; k1 < 4; ++k1) {
                     this.placeBlock(p_230383_1_, BASE_LIGHT, l + j1, 0, i1 + k1, p_230383_5_);
                     this.fillColumnDown(p_230383_1_, BASE_LIGHT, l + j1, -1, i1 + k1, p_230383_5_);
                  }
               }

               if (j != 0 && j != 6) {
                  k += 6;
               } else {
                  ++k;
               }
            }
         }

         for(int l1 = 0; l1 < 5; ++l1) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, -1 - l1, 0 + l1 * 2, -1 - l1, -1 - l1, 23, 58 + l1);
            this.generateWaterBox(p_230383_1_, p_230383_5_, 58 + l1, 0 + l1 * 2, -1 - l1, 58 + l1, 23, 58 + l1);
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0 - l1, 0 + l1 * 2, -1 - l1, 57 + l1, 23, -1 - l1);
            this.generateWaterBox(p_230383_1_, p_230383_5_, 0 - l1, 0 + l1 * 2, 58 + l1, 57 + l1, 23, 58 + l1);
         }

         for(OceanMonumentPieces.Piece oceanmonumentpieces$piece : this.childPieces) {
            if (oceanmonumentpieces$piece.getBoundingBox().intersects(p_230383_5_)) {
               oceanmonumentpieces$piece.postProcess(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
            }
         }

         return true;
      }

      private void generateWing(boolean p_175840_1_, int p_175840_2_, ISeedReader p_175840_3_, Random p_175840_4_, MutableBoundingBox p_175840_5_) {
         int i = 24;
         if (this.chunkIntersects(p_175840_5_, p_175840_2_, 0, p_175840_2_ + 23, 20)) {
            this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 0, 0, 0, p_175840_2_ + 24, 0, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175840_3_, p_175840_5_, p_175840_2_ + 0, 1, 0, p_175840_2_ + 24, 10, 20);

            for(int j = 0; j < 4; ++j) {
               this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + j, j + 1, j, p_175840_2_ + j, j + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + j + 7, j + 5, j + 7, p_175840_2_ + j + 7, j + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 17 - j, j + 5, j + 7, p_175840_2_ + 17 - j, j + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 24 - j, j + 1, j, p_175840_2_ + 24 - j, j + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + j + 1, j + 1, j, p_175840_2_ + 23 - j, j + 1, j, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + j + 8, j + 5, j + 7, p_175840_2_ + 16 - j, j + 5, j + 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 4, 4, 4, p_175840_2_ + 6, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 7, 4, 4, p_175840_2_ + 17, 4, 6, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 18, 4, 4, p_175840_2_ + 20, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 11, 8, 11, p_175840_2_ + 13, 8, 20, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_175840_3_, DOT_DECO_DATA, p_175840_2_ + 12, 9, 12, p_175840_5_);
            this.placeBlock(p_175840_3_, DOT_DECO_DATA, p_175840_2_ + 12, 9, 15, p_175840_5_);
            this.placeBlock(p_175840_3_, DOT_DECO_DATA, p_175840_2_ + 12, 9, 18, p_175840_5_);
            int j1 = p_175840_2_ + (p_175840_1_ ? 19 : 5);
            int k = p_175840_2_ + (p_175840_1_ ? 5 : 19);

            for(int l = 20; l >= 5; l -= 3) {
               this.placeBlock(p_175840_3_, DOT_DECO_DATA, j1, 5, l, p_175840_5_);
            }

            for(int k1 = 19; k1 >= 7; k1 -= 3) {
               this.placeBlock(p_175840_3_, DOT_DECO_DATA, k, 5, k1, p_175840_5_);
            }

            for(int l1 = 0; l1 < 4; ++l1) {
               int i1 = p_175840_1_ ? p_175840_2_ + 24 - (17 - l1 * 3) : p_175840_2_ + 17 - l1 * 3;
               this.placeBlock(p_175840_3_, DOT_DECO_DATA, i1, 5, 5, p_175840_5_);
            }

            this.placeBlock(p_175840_3_, DOT_DECO_DATA, k, 5, 5, p_175840_5_);
            this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 11, 1, 12, p_175840_2_ + 13, 7, 12, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175840_3_, p_175840_5_, p_175840_2_ + 12, 1, 11, p_175840_2_ + 12, 7, 13, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateEntranceArchs(ISeedReader p_175839_1_, Random p_175839_2_, MutableBoundingBox p_175839_3_) {
         if (this.chunkIntersects(p_175839_3_, 22, 5, 35, 17)) {
            this.generateWaterBox(p_175839_1_, p_175839_3_, 25, 0, 0, 32, 8, 20);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_175839_1_, p_175839_3_, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175839_1_, p_175839_3_, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(p_175839_1_, BASE_LIGHT, 25, 5, 5 + i * 4, p_175839_3_);
               this.placeBlock(p_175839_1_, BASE_LIGHT, 26, 6, 5 + i * 4, p_175839_3_);
               this.placeBlock(p_175839_1_, LAMP_BLOCK, 26, 5, 5 + i * 4, p_175839_3_);
               this.generateBox(p_175839_1_, p_175839_3_, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175839_1_, p_175839_3_, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(p_175839_1_, BASE_LIGHT, 32, 5, 5 + i * 4, p_175839_3_);
               this.placeBlock(p_175839_1_, BASE_LIGHT, 31, 6, 5 + i * 4, p_175839_3_);
               this.placeBlock(p_175839_1_, LAMP_BLOCK, 31, 5, 5 + i * 4, p_175839_3_);
               this.generateBox(p_175839_1_, p_175839_3_, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, BASE_GRAY, BASE_GRAY, false);
            }
         }

      }

      private void generateEntranceWall(ISeedReader p_175837_1_, Random p_175837_2_, MutableBoundingBox p_175837_3_) {
         if (this.chunkIntersects(p_175837_3_, 15, 20, 42, 21)) {
            this.generateBox(p_175837_1_, p_175837_3_, 15, 0, 21, 42, 0, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 26, 1, 21, 31, 3, 21);
            this.generateBox(p_175837_1_, p_175837_3_, 21, 12, 21, 36, 12, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 17, 11, 21, 40, 11, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 16, 10, 21, 41, 10, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 15, 7, 21, 42, 9, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 16, 6, 21, 41, 6, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 17, 5, 21, 40, 5, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 21, 4, 21, 36, 4, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 22, 3, 21, 26, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 31, 3, 21, 35, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 23, 2, 21, 25, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 32, 2, 21, 34, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175837_1_, p_175837_3_, 28, 4, 20, 29, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_175837_1_, BASE_LIGHT, 27, 3, 21, p_175837_3_);
            this.placeBlock(p_175837_1_, BASE_LIGHT, 30, 3, 21, p_175837_3_);
            this.placeBlock(p_175837_1_, BASE_LIGHT, 26, 2, 21, p_175837_3_);
            this.placeBlock(p_175837_1_, BASE_LIGHT, 31, 2, 21, p_175837_3_);
            this.placeBlock(p_175837_1_, BASE_LIGHT, 25, 1, 21, p_175837_3_);
            this.placeBlock(p_175837_1_, BASE_LIGHT, 32, 1, 21, p_175837_3_);

            for(int i = 0; i < 7; ++i) {
               this.placeBlock(p_175837_1_, BASE_BLACK, 28 - i, 6 + i, 21, p_175837_3_);
               this.placeBlock(p_175837_1_, BASE_BLACK, 29 + i, 6 + i, 21, p_175837_3_);
            }

            for(int j = 0; j < 4; ++j) {
               this.placeBlock(p_175837_1_, BASE_BLACK, 28 - j, 9 + j, 21, p_175837_3_);
               this.placeBlock(p_175837_1_, BASE_BLACK, 29 + j, 9 + j, 21, p_175837_3_);
            }

            this.placeBlock(p_175837_1_, BASE_BLACK, 28, 12, 21, p_175837_3_);
            this.placeBlock(p_175837_1_, BASE_BLACK, 29, 12, 21, p_175837_3_);

            for(int k = 0; k < 3; ++k) {
               this.placeBlock(p_175837_1_, BASE_BLACK, 22 - k * 2, 8, 21, p_175837_3_);
               this.placeBlock(p_175837_1_, BASE_BLACK, 22 - k * 2, 9, 21, p_175837_3_);
               this.placeBlock(p_175837_1_, BASE_BLACK, 35 + k * 2, 8, 21, p_175837_3_);
               this.placeBlock(p_175837_1_, BASE_BLACK, 35 + k * 2, 9, 21, p_175837_3_);
            }

            this.generateWaterBox(p_175837_1_, p_175837_3_, 15, 13, 21, 42, 15, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 15, 1, 21, 15, 6, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 16, 1, 21, 16, 5, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 17, 1, 21, 20, 4, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 21, 1, 21, 21, 3, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 22, 1, 21, 22, 2, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 23, 1, 21, 24, 1, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 42, 1, 21, 42, 6, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 41, 1, 21, 41, 5, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 37, 1, 21, 40, 4, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 36, 1, 21, 36, 3, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 33, 1, 21, 34, 1, 21);
            this.generateWaterBox(p_175837_1_, p_175837_3_, 35, 1, 21, 35, 2, 21);
         }

      }

      private void generateRoofPiece(ISeedReader p_175841_1_, Random p_175841_2_, MutableBoundingBox p_175841_3_) {
         if (this.chunkIntersects(p_175841_3_, 21, 21, 36, 36)) {
            this.generateBox(p_175841_1_, p_175841_3_, 21, 0, 22, 36, 0, 36, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175841_1_, p_175841_3_, 21, 1, 22, 36, 23, 36);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_175841_1_, p_175841_3_, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175841_1_, p_175841_3_, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175841_1_, p_175841_3_, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_175841_1_, p_175841_3_, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_175841_1_, p_175841_3_, 25, 16, 25, 32, 16, 32, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175841_1_, p_175841_3_, 25, 17, 25, 25, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_175841_1_, p_175841_3_, 32, 17, 25, 32, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_175841_1_, p_175841_3_, 25, 17, 32, 25, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_175841_1_, p_175841_3_, 32, 17, 32, 32, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 26, 20, 26, p_175841_3_);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 27, 21, 27, p_175841_3_);
            this.placeBlock(p_175841_1_, LAMP_BLOCK, 27, 20, 27, p_175841_3_);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 26, 20, 31, p_175841_3_);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 27, 21, 30, p_175841_3_);
            this.placeBlock(p_175841_1_, LAMP_BLOCK, 27, 20, 30, p_175841_3_);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 31, 20, 31, p_175841_3_);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 30, 21, 30, p_175841_3_);
            this.placeBlock(p_175841_1_, LAMP_BLOCK, 30, 20, 30, p_175841_3_);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 31, 20, 26, p_175841_3_);
            this.placeBlock(p_175841_1_, BASE_LIGHT, 30, 21, 27, p_175841_3_);
            this.placeBlock(p_175841_1_, LAMP_BLOCK, 30, 20, 27, p_175841_3_);
            this.generateBox(p_175841_1_, p_175841_3_, 28, 21, 27, 29, 21, 27, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175841_1_, p_175841_3_, 27, 21, 28, 27, 21, 29, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175841_1_, p_175841_3_, 28, 21, 30, 29, 21, 30, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175841_1_, p_175841_3_, 30, 21, 28, 30, 21, 29, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateLowerWall(ISeedReader p_175835_1_, Random p_175835_2_, MutableBoundingBox p_175835_3_) {
         if (this.chunkIntersects(p_175835_3_, 0, 21, 6, 58)) {
            this.generateBox(p_175835_1_, p_175835_3_, 0, 0, 21, 6, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175835_1_, p_175835_3_, 0, 1, 21, 6, 7, 57);
            this.generateBox(p_175835_1_, p_175835_3_, 4, 4, 21, 6, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_175835_1_, p_175835_3_, i, i + 1, 21, i, i + 1, 57 - i, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j = 23; j < 53; j += 3) {
               this.placeBlock(p_175835_1_, DOT_DECO_DATA, 5, 5, j, p_175835_3_);
            }

            this.placeBlock(p_175835_1_, DOT_DECO_DATA, 5, 5, 52, p_175835_3_);

            for(int k = 0; k < 4; ++k) {
               this.generateBox(p_175835_1_, p_175835_3_, k, k + 1, 21, k, k + 1, 57 - k, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_175835_1_, p_175835_3_, 4, 1, 52, 6, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175835_1_, p_175835_3_, 5, 1, 51, 5, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if (this.chunkIntersects(p_175835_3_, 51, 21, 58, 58)) {
            this.generateBox(p_175835_1_, p_175835_3_, 51, 0, 21, 57, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175835_1_, p_175835_3_, 51, 1, 21, 57, 7, 57);
            this.generateBox(p_175835_1_, p_175835_3_, 51, 4, 21, 53, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(int l = 0; l < 4; ++l) {
               this.generateBox(p_175835_1_, p_175835_3_, 57 - l, l + 1, 21, 57 - l, l + 1, 57 - l, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int i1 = 23; i1 < 53; i1 += 3) {
               this.placeBlock(p_175835_1_, DOT_DECO_DATA, 52, 5, i1, p_175835_3_);
            }

            this.placeBlock(p_175835_1_, DOT_DECO_DATA, 52, 5, 52, p_175835_3_);
            this.generateBox(p_175835_1_, p_175835_3_, 51, 1, 52, 53, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175835_1_, p_175835_3_, 52, 1, 51, 52, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if (this.chunkIntersects(p_175835_3_, 0, 51, 57, 57)) {
            this.generateBox(p_175835_1_, p_175835_3_, 7, 0, 51, 50, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175835_1_, p_175835_3_, 7, 1, 51, 50, 10, 57);

            for(int j1 = 0; j1 < 4; ++j1) {
               this.generateBox(p_175835_1_, p_175835_3_, j1 + 1, j1 + 1, 57 - j1, 56 - j1, j1 + 1, 57 - j1, BASE_LIGHT, BASE_LIGHT, false);
            }
         }

      }

      private void generateMiddleWall(ISeedReader p_175842_1_, Random p_175842_2_, MutableBoundingBox p_175842_3_) {
         if (this.chunkIntersects(p_175842_3_, 7, 21, 13, 50)) {
            this.generateBox(p_175842_1_, p_175842_3_, 7, 0, 21, 13, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175842_1_, p_175842_3_, 7, 1, 21, 13, 10, 50);
            this.generateBox(p_175842_1_, p_175842_3_, 11, 8, 21, 13, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_175842_1_, p_175842_3_, i + 7, i + 5, 21, i + 7, i + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j = 21; j <= 45; j += 3) {
               this.placeBlock(p_175842_1_, DOT_DECO_DATA, 12, 9, j, p_175842_3_);
            }
         }

         if (this.chunkIntersects(p_175842_3_, 44, 21, 50, 54)) {
            this.generateBox(p_175842_1_, p_175842_3_, 44, 0, 21, 50, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175842_1_, p_175842_3_, 44, 1, 21, 50, 10, 50);
            this.generateBox(p_175842_1_, p_175842_3_, 44, 8, 21, 46, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(int k = 0; k < 4; ++k) {
               this.generateBox(p_175842_1_, p_175842_3_, 50 - k, k + 5, 21, 50 - k, k + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int l = 21; l <= 45; l += 3) {
               this.placeBlock(p_175842_1_, DOT_DECO_DATA, 45, 9, l, p_175842_3_);
            }
         }

         if (this.chunkIntersects(p_175842_3_, 8, 44, 49, 54)) {
            this.generateBox(p_175842_1_, p_175842_3_, 14, 0, 44, 43, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175842_1_, p_175842_3_, 14, 1, 44, 43, 10, 50);

            for(int i1 = 12; i1 <= 45; i1 += 3) {
               this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 9, 45, p_175842_3_);
               this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 9, 52, p_175842_3_);
               if (i1 == 12 || i1 == 18 || i1 == 24 || i1 == 33 || i1 == 39 || i1 == 45) {
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 9, 47, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 9, 50, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 10, 45, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 10, 46, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 10, 51, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 10, 52, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 11, 47, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 11, 50, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 12, 48, p_175842_3_);
                  this.placeBlock(p_175842_1_, DOT_DECO_DATA, i1, 12, 49, p_175842_3_);
               }
            }

            for(int j1 = 0; j1 < 3; ++j1) {
               this.generateBox(p_175842_1_, p_175842_3_, 8 + j1, 5 + j1, 54, 49 - j1, 5 + j1, 54, BASE_GRAY, BASE_GRAY, false);
            }

            this.generateBox(p_175842_1_, p_175842_3_, 11, 8, 54, 46, 8, 54, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_175842_1_, p_175842_3_, 14, 8, 44, 43, 8, 53, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateUpperWall(ISeedReader p_175838_1_, Random p_175838_2_, MutableBoundingBox p_175838_3_) {
         if (this.chunkIntersects(p_175838_3_, 14, 21, 20, 43)) {
            this.generateBox(p_175838_1_, p_175838_3_, 14, 0, 21, 20, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175838_1_, p_175838_3_, 14, 1, 22, 20, 14, 43);
            this.generateBox(p_175838_1_, p_175838_3_, 18, 12, 22, 20, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175838_1_, p_175838_3_, 18, 12, 21, 20, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_175838_1_, p_175838_3_, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j = 23; j <= 39; j += 3) {
               this.placeBlock(p_175838_1_, DOT_DECO_DATA, 19, 13, j, p_175838_3_);
            }
         }

         if (this.chunkIntersects(p_175838_3_, 37, 21, 43, 43)) {
            this.generateBox(p_175838_1_, p_175838_3_, 37, 0, 21, 43, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175838_1_, p_175838_3_, 37, 1, 22, 43, 14, 43);
            this.generateBox(p_175838_1_, p_175838_3_, 37, 12, 22, 39, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175838_1_, p_175838_3_, 37, 12, 21, 39, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int k = 0; k < 4; ++k) {
               this.generateBox(p_175838_1_, p_175838_3_, 43 - k, k + 9, 21, 43 - k, k + 9, 43 - k, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int l = 23; l <= 39; l += 3) {
               this.placeBlock(p_175838_1_, DOT_DECO_DATA, 38, 13, l, p_175838_3_);
            }
         }

         if (this.chunkIntersects(p_175838_3_, 15, 37, 42, 43)) {
            this.generateBox(p_175838_1_, p_175838_3_, 21, 0, 37, 36, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(p_175838_1_, p_175838_3_, 21, 1, 37, 36, 14, 43);
            this.generateBox(p_175838_1_, p_175838_3_, 21, 12, 37, 36, 12, 39, BASE_GRAY, BASE_GRAY, false);

            for(int i1 = 0; i1 < 4; ++i1) {
               this.generateBox(p_175838_1_, p_175838_3_, 15 + i1, i1 + 9, 43 - i1, 42 - i1, i1 + 9, 43 - i1, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int j1 = 21; j1 <= 36; j1 += 3) {
               this.placeBlock(p_175838_1_, DOT_DECO_DATA, j1, 13, 38, p_175838_3_);
            }
         }

      }
   }

   public static class MonumentCoreRoom extends OceanMonumentPieces.Piece {
      public MonumentCoreRoom(Direction p_i50663_1_, OceanMonumentPieces.RoomDefinition p_i50663_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, p_i50663_1_, p_i50663_2_, 2, 2, 2);
      }

      public MonumentCoreRoom(TemplateManager p_i50664_1_, CompoundNBT p_i50664_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_CORE_ROOM, p_i50664_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 8, 0, 14, 8, 14, BASE_GRAY);
         int i = 7;
         BlockState blockstate = BASE_LIGHT;
         this.generateBox(p_230383_1_, p_230383_5_, 0, 7, 0, 0, 7, 15, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 15, 7, 0, 15, 7, 15, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 7, 0, 15, 7, 0, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 7, 15, 14, 7, 15, blockstate, blockstate, false);

         for(int k = 1; k <= 6; ++k) {
            blockstate = BASE_LIGHT;
            if (k == 2 || k == 6) {
               blockstate = BASE_GRAY;
            }

            for(int j = 0; j <= 15; j += 15) {
               this.generateBox(p_230383_1_, p_230383_5_, j, k, 0, j, k, 1, blockstate, blockstate, false);
               this.generateBox(p_230383_1_, p_230383_5_, j, k, 6, j, k, 9, blockstate, blockstate, false);
               this.generateBox(p_230383_1_, p_230383_5_, j, k, 14, j, k, 15, blockstate, blockstate, false);
            }

            this.generateBox(p_230383_1_, p_230383_5_, 1, k, 0, 1, k, 0, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, k, 0, 9, k, 0, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 14, k, 0, 14, k, 0, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, k, 15, 14, k, 15, blockstate, blockstate, false);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 6, 3, 6, 9, 6, 9, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.GOLD_BLOCK.defaultBlockState(), false);

         for(int l = 3; l <= 6; l += 3) {
            for(int i1 = 6; i1 <= 9; i1 += 3) {
               this.placeBlock(p_230383_1_, LAMP_BLOCK, i1, l, 6, p_230383_5_);
               this.placeBlock(p_230383_1_, LAMP_BLOCK, i1, l, 9, p_230383_5_);
            }
         }

         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 6, 5, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 9, 5, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 1, 6, 10, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 1, 9, 10, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 5, 6, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 1, 5, 9, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 10, 6, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 1, 10, 9, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 5, 5, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 10, 5, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 2, 5, 10, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 2, 10, 10, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 7, 1, 5, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 7, 1, 10, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 7, 9, 5, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 7, 9, 10, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 7, 5, 6, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 7, 10, 6, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 7, 5, 14, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 7, 10, 14, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 2, 2, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 2, 3, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 13, 1, 2, 13, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 12, 1, 2, 12, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 12, 2, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 13, 3, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 13, 1, 12, 13, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 12, 1, 13, 12, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         return true;
      }
   }

   public static class Penthouse extends OceanMonumentPieces.Piece {
      public Penthouse(Direction p_i45591_1_, MutableBoundingBox p_i45591_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_PENTHOUSE, p_i45591_1_, p_i45591_2_);
      }

      public Penthouse(TemplateManager p_i50651_1_, CompoundNBT p_i50651_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_PENTHOUSE, p_i50651_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 2, -1, 2, 11, -1, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, -1, 0, 1, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 12, -1, 0, 13, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, -1, 0, 11, -1, 1, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, -1, 12, 11, -1, 13, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 0, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 13, 0, 0, 13, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 0, 0, 12, 0, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 0, 13, 12, 0, 13, BASE_LIGHT, BASE_LIGHT, false);

         for(int i = 2; i <= 11; i += 3) {
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 0, 0, i, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 13, 0, i, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, i, 0, 0, p_230383_5_);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 2, 0, 3, 4, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 0, 3, 11, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 0, 9, 9, 0, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 5, 0, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 8, 0, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 10, 0, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, BASE_LIGHT, 3, 0, 10, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 0, 3, 3, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 0, 3, 10, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 0, 10, 7, 0, 10, BASE_BLACK, BASE_BLACK, false);
         int l = 3;

         for(int j = 0; j < 2; ++j) {
            for(int k = 2; k <= 8; k += 3) {
               this.generateBox(p_230383_1_, p_230383_5_, l, 0, k, l, 2, k, BASE_LIGHT, BASE_LIGHT, false);
            }

            l = 10;
         }

         this.generateBox(p_230383_1_, p_230383_5_, 5, 0, 10, 5, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 0, 10, 8, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, -1, 7, 7, -1, 8, BASE_BLACK, BASE_BLACK, false);
         this.generateWaterBox(p_230383_1_, p_230383_5_, 6, -1, 3, 7, -1, 4);
         this.spawnElder(p_230383_1_, p_230383_5_, 6, 1, 6);
         return true;
      }
   }

   public abstract static class Piece extends StructurePiece {
      protected static final BlockState BASE_GRAY = Blocks.PRISMARINE.defaultBlockState();
      protected static final BlockState BASE_LIGHT = Blocks.PRISMARINE_BRICKS.defaultBlockState();
      protected static final BlockState BASE_BLACK = Blocks.DARK_PRISMARINE.defaultBlockState();
      protected static final BlockState DOT_DECO_DATA = BASE_LIGHT;
      protected static final BlockState LAMP_BLOCK = Blocks.SEA_LANTERN.defaultBlockState();
      protected static final BlockState FILL_BLOCK = Blocks.WATER.defaultBlockState();
      protected static final Set<Block> FILL_KEEP = ImmutableSet.<Block>builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(FILL_BLOCK.getBlock()).build();
      protected static final int GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
      protected static final int GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
      protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
      protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);
      protected OceanMonumentPieces.RoomDefinition roomDefinition;

      protected static final int getRoomIndex(int p_175820_0_, int p_175820_1_, int p_175820_2_) {
         return p_175820_1_ * 25 + p_175820_2_ * 5 + p_175820_0_;
      }

      public Piece(IStructurePieceType p_i50647_1_, int p_i50647_2_) {
         super(p_i50647_1_, p_i50647_2_);
      }

      public Piece(IStructurePieceType p_i50648_1_, Direction p_i50648_2_, MutableBoundingBox p_i50648_3_) {
         super(p_i50648_1_, 1);
         this.setOrientation(p_i50648_2_);
         this.boundingBox = p_i50648_3_;
      }

      protected Piece(IStructurePieceType p_i50649_1_, int p_i50649_2_, Direction p_i50649_3_, OceanMonumentPieces.RoomDefinition p_i50649_4_, int p_i50649_5_, int p_i50649_6_, int p_i50649_7_) {
         super(p_i50649_1_, p_i50649_2_);
         this.setOrientation(p_i50649_3_);
         this.roomDefinition = p_i50649_4_;
         int i = p_i50649_4_.index;
         int j = i % 5;
         int k = i / 5 % 5;
         int l = i / 25;
         if (p_i50649_3_ != Direction.NORTH && p_i50649_3_ != Direction.SOUTH) {
            this.boundingBox = new MutableBoundingBox(0, 0, 0, p_i50649_7_ * 8 - 1, p_i50649_6_ * 4 - 1, p_i50649_5_ * 8 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(0, 0, 0, p_i50649_5_ * 8 - 1, p_i50649_6_ * 4 - 1, p_i50649_7_ * 8 - 1);
         }

         switch(p_i50649_3_) {
         case NORTH:
            this.boundingBox.move(j * 8, l * 4, -(k + p_i50649_7_) * 8 + 1);
            break;
         case SOUTH:
            this.boundingBox.move(j * 8, l * 4, k * 8);
            break;
         case WEST:
            this.boundingBox.move(-(k + p_i50649_7_) * 8 + 1, l * 4, j * 8);
            break;
         default:
            this.boundingBox.move(k * 8, l * 4, j * 8);
         }

      }

      public Piece(IStructurePieceType p_i50650_1_, CompoundNBT p_i50650_2_) {
         super(p_i50650_1_, p_i50650_2_);
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      }

      protected void generateWaterBox(ISeedReader p_209179_1_, MutableBoundingBox p_209179_2_, int p_209179_3_, int p_209179_4_, int p_209179_5_, int p_209179_6_, int p_209179_7_, int p_209179_8_) {
         for(int i = p_209179_4_; i <= p_209179_7_; ++i) {
            for(int j = p_209179_3_; j <= p_209179_6_; ++j) {
               for(int k = p_209179_5_; k <= p_209179_8_; ++k) {
                  BlockState blockstate = this.getBlock(p_209179_1_, j, i, k, p_209179_2_);
                  if (!FILL_KEEP.contains(blockstate.getBlock())) {
                     if (this.getWorldY(i) >= p_209179_1_.getSeaLevel() && blockstate != FILL_BLOCK) {
                        this.placeBlock(p_209179_1_, Blocks.AIR.defaultBlockState(), j, i, k, p_209179_2_);
                     } else {
                        this.placeBlock(p_209179_1_, FILL_BLOCK, j, i, k, p_209179_2_);
                     }
                  }
               }
            }
         }

      }

      protected void generateDefaultFloor(ISeedReader p_175821_1_, MutableBoundingBox p_175821_2_, int p_175821_3_, int p_175821_4_, boolean p_175821_5_) {
         if (p_175821_5_) {
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 0, 0, p_175821_4_ + 0, p_175821_3_ + 2, 0, p_175821_4_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 5, 0, p_175821_4_ + 0, p_175821_3_ + 8 - 1, 0, p_175821_4_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 0, p_175821_3_ + 4, 0, p_175821_4_ + 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 5, p_175821_3_ + 4, 0, p_175821_4_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 2, p_175821_3_ + 4, 0, p_175821_4_ + 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 5, p_175821_3_ + 4, 0, p_175821_4_ + 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 2, 0, p_175821_4_ + 3, p_175821_3_ + 2, 0, p_175821_4_ + 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 5, 0, p_175821_4_ + 3, p_175821_3_ + 5, 0, p_175821_4_ + 4, BASE_LIGHT, BASE_LIGHT, false);
         } else {
            this.generateBox(p_175821_1_, p_175821_2_, p_175821_3_ + 0, 0, p_175821_4_ + 0, p_175821_3_ + 8 - 1, 0, p_175821_4_ + 8 - 1, BASE_GRAY, BASE_GRAY, false);
         }

      }

      protected void generateBoxOnFillOnly(ISeedReader p_175819_1_, MutableBoundingBox p_175819_2_, int p_175819_3_, int p_175819_4_, int p_175819_5_, int p_175819_6_, int p_175819_7_, int p_175819_8_, BlockState p_175819_9_) {
         for(int i = p_175819_4_; i <= p_175819_7_; ++i) {
            for(int j = p_175819_3_; j <= p_175819_6_; ++j) {
               for(int k = p_175819_5_; k <= p_175819_8_; ++k) {
                  if (this.getBlock(p_175819_1_, j, i, k, p_175819_2_) == FILL_BLOCK) {
                     this.placeBlock(p_175819_1_, p_175819_9_, j, i, k, p_175819_2_);
                  }
               }
            }
         }

      }

      protected boolean chunkIntersects(MutableBoundingBox p_175818_1_, int p_175818_2_, int p_175818_3_, int p_175818_4_, int p_175818_5_) {
         int i = this.getWorldX(p_175818_2_, p_175818_3_);
         int j = this.getWorldZ(p_175818_2_, p_175818_3_);
         int k = this.getWorldX(p_175818_4_, p_175818_5_);
         int l = this.getWorldZ(p_175818_4_, p_175818_5_);
         return p_175818_1_.intersects(Math.min(i, k), Math.min(j, l), Math.max(i, k), Math.max(j, l));
      }

      protected boolean spawnElder(ISeedReader p_175817_1_, MutableBoundingBox p_175817_2_, int p_175817_3_, int p_175817_4_, int p_175817_5_) {
         int i = this.getWorldX(p_175817_3_, p_175817_5_);
         int j = this.getWorldY(p_175817_4_);
         int k = this.getWorldZ(p_175817_3_, p_175817_5_);
         if (p_175817_2_.isInside(new BlockPos(i, j, k))) {
            ElderGuardianEntity elderguardianentity = EntityType.ELDER_GUARDIAN.create(p_175817_1_.getLevel());
            elderguardianentity.heal(elderguardianentity.getMaxHealth());
            elderguardianentity.moveTo((double)i + 0.5D, (double)j, (double)k + 0.5D, 0.0F, 0.0F);
            elderguardianentity.finalizeSpawn(p_175817_1_, p_175817_1_.getCurrentDifficultyAt(elderguardianentity.blockPosition()), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            p_175817_1_.addFreshEntityWithPassengers(elderguardianentity);
            return true;
         } else {
            return false;
         }
      }
   }

   static class RoomDefinition {
      private final int index;
      private final OceanMonumentPieces.RoomDefinition[] connections = new OceanMonumentPieces.RoomDefinition[6];
      private final boolean[] hasOpening = new boolean[6];
      private boolean claimed;
      private boolean isSource;
      private int scanIndex;

      public RoomDefinition(int p_i45584_1_) {
         this.index = p_i45584_1_;
      }

      public void setConnection(Direction p_175957_1_, OceanMonumentPieces.RoomDefinition p_175957_2_) {
         this.connections[p_175957_1_.get3DDataValue()] = p_175957_2_;
         p_175957_2_.connections[p_175957_1_.getOpposite().get3DDataValue()] = this;
      }

      public void updateOpenings() {
         for(int i = 0; i < 6; ++i) {
            this.hasOpening[i] = this.connections[i] != null;
         }

      }

      public boolean findSource(int p_175959_1_) {
         if (this.isSource) {
            return true;
         } else {
            this.scanIndex = p_175959_1_;

            for(int i = 0; i < 6; ++i) {
               if (this.connections[i] != null && this.hasOpening[i] && this.connections[i].scanIndex != p_175959_1_ && this.connections[i].findSource(p_175959_1_)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean isSpecial() {
         return this.index >= 75;
      }

      public int countOpenings() {
         int i = 0;

         for(int j = 0; j < 6; ++j) {
            if (this.hasOpening[j]) {
               ++i;
            }
         }

         return i;
      }
   }

   public static class SimpleRoom extends OceanMonumentPieces.Piece {
      private int mainDesign;

      public SimpleRoom(Direction p_i45587_1_, OceanMonumentPieces.RoomDefinition p_i45587_2_, Random p_i45587_3_) {
         super(IStructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, p_i45587_1_, p_i45587_2_, 1, 1, 1);
         this.mainDesign = p_i45587_3_.nextInt(3);
      }

      public SimpleRoom(TemplateManager p_i50646_1_, CompoundNBT p_i50646_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, p_i50646_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         boolean flag = this.mainDesign != 0 && p_230383_4_.nextBoolean() && !this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()] && !this.roomDefinition.hasOpening[Direction.UP.get3DDataValue()] && this.roomDefinition.countOpenings() > 1;
         if (this.mainDesign == 0) {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 0, 2, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 0, 2, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 1, 2, 1, p_230383_5_);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 0, 7, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 3, 0, 7, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 0, 7, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 6, 2, 1, p_230383_5_);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 5, 2, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 5, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 5, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 7, 2, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 1, 2, 6, p_230383_5_);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 5, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 3, 5, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 5, 7, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 6, 2, 6, p_230383_5_);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 3, 3, 0, 4, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, 3, 3, 0, 4, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 3, 2, 0, 4, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 1, 1, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 3, 3, 7, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, 3, 3, 6, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 3, 2, 7, 4, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 6, 4, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 3, 0, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 3, 1, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 3, 0, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 3, 1, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(p_230383_1_, p_230383_5_, 6, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 3, 7, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 3, 7, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.mainDesign == 1) {
            this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 2, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 5, 2, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 5, 5, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 5, 1, 2, 5, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 2, 2, 2, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 2, 2, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 5, 2, 5, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 5, 2, 2, p_230383_5_);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 0, 1, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 1, 0, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 7, 1, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 6, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 6, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 1, 7, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(p_230383_1_, BASE_GRAY, 1, 2, 0, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_GRAY, 0, 2, 1, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_GRAY, 1, 2, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_GRAY, 0, 2, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_GRAY, 6, 2, 7, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_GRAY, 7, 2, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_GRAY, 6, 2, 0, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_GRAY, 7, 2, 1, p_230383_5_);
            if (!this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 1, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 1, 0, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 1, 0, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 1, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 1, 7, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 1, 7, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.mainDesign == 2) {
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 7, 4, 2, 7);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateWaterBox(p_230383_1_, p_230383_5_, 0, 1, 3, 0, 2, 4);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateWaterBox(p_230383_1_, p_230383_5_, 7, 1, 3, 7, 2, 4);
            }
         }

         if (flag) {
            this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 3, 4, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 3, 2, 3, 4, 2, 4, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(p_230383_1_, p_230383_5_, 3, 3, 3, 4, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   public static class SimpleTopRoom extends OceanMonumentPieces.Piece {
      public SimpleTopRoom(Direction p_i50644_1_, OceanMonumentPieces.RoomDefinition p_i50644_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, p_i50644_1_, p_i50644_2_, 1, 1, 1);
      }

      public SimpleTopRoom(TemplateManager p_i50645_1_, CompoundNBT p_i50645_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, p_i50645_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_230383_1_, p_230383_5_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(p_230383_1_, p_230383_5_, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         for(int i = 1; i <= 6; ++i) {
            for(int j = 1; j <= 6; ++j) {
               if (p_230383_4_.nextInt(3) != 0) {
                  int k = 2 + (p_230383_4_.nextInt(4) == 0 ? 0 : 1);
                  BlockState blockstate = Blocks.WET_SPONGE.defaultBlockState();
                  this.generateBox(p_230383_1_, p_230383_5_, i, k, j, i, 3, j, blockstate, blockstate, false);
               }
            }
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
         if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(p_230383_1_, p_230383_5_, 3, 1, 0, 4, 2, 0);
         }

         return true;
      }
   }

   public static class WingRoom extends OceanMonumentPieces.Piece {
      private int mainDesign;

      public WingRoom(Direction p_i45585_1_, MutableBoundingBox p_i45585_2_, int p_i45585_3_) {
         super(IStructurePieceType.OCEAN_MONUMENT_WING_ROOM, p_i45585_1_, p_i45585_2_);
         this.mainDesign = p_i45585_3_ & 1;
      }

      public WingRoom(TemplateManager p_i50643_1_, CompoundNBT p_i50643_2_) {
         super(IStructurePieceType.OCEAN_MONUMENT_WING_ROOM, p_i50643_2_);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         if (this.mainDesign == 0) {
            for(int i = 0; i < 4; ++i) {
               this.generateBox(p_230383_1_, p_230383_5_, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(p_230383_1_, p_230383_5_, 7, 0, 6, 15, 0, 16, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 0, 6, 6, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 16, 0, 6, 16, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 7, 7, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 15, 1, 7, 15, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 6, 9, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 13, 1, 6, 15, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 8, 1, 7, 9, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 13, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 9, 0, 5, 13, 0, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 10, 0, 7, 12, 0, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 8, 0, 10, 8, 0, 12, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 14, 0, 10, 14, 0, 12, BASE_BLACK, BASE_BLACK, false);

            for(int i1 = 18; i1 >= 7; i1 -= 3) {
               this.placeBlock(p_230383_1_, LAMP_BLOCK, 6, 3, i1, p_230383_5_);
               this.placeBlock(p_230383_1_, LAMP_BLOCK, 16, 3, i1, p_230383_5_);
            }

            this.placeBlock(p_230383_1_, LAMP_BLOCK, 10, 0, 10, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 12, 0, 10, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 10, 0, 12, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 12, 0, 12, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 8, 3, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 14, 3, 6, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 4, 2, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 4, 1, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 4, 0, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 18, 2, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 18, 1, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 18, 0, 4, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 4, 2, 18, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 4, 1, 18, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 4, 0, 18, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 18, 2, 18, p_230383_5_);
            this.placeBlock(p_230383_1_, LAMP_BLOCK, 18, 1, 18, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 18, 0, 18, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 9, 7, 20, p_230383_5_);
            this.placeBlock(p_230383_1_, BASE_LIGHT, 13, 7, 20, p_230383_5_);
            this.generateBox(p_230383_1_, p_230383_5_, 6, 0, 21, 7, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 15, 0, 21, 16, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.spawnElder(p_230383_1_, p_230383_5_, 11, 2, 16);
         } else if (this.mainDesign == 1) {
            this.generateBox(p_230383_1_, p_230383_5_, 9, 3, 18, 13, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 9, 0, 18, 9, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(p_230383_1_, p_230383_5_, 13, 0, 18, 13, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            int j1 = 9;
            int j = 20;
            int k = 5;

            for(int l = 0; l < 2; ++l) {
               this.placeBlock(p_230383_1_, BASE_LIGHT, j1, 6, 20, p_230383_5_);
               this.placeBlock(p_230383_1_, LAMP_BLOCK, j1, 5, 20, p_230383_5_);
               this.placeBlock(p_230383_1_, BASE_LIGHT, j1, 4, 20, p_230383_5_);
               j1 = 13;
            }

            this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 7, 15, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
            j1 = 10;

            for(int k1 = 0; k1 < 2; ++k1) {
               this.generateBox(p_230383_1_, p_230383_5_, j1, 0, 10, j1, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, j1, 0, 12, j1, 6, 12, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(p_230383_1_, LAMP_BLOCK, j1, 0, 10, p_230383_5_);
               this.placeBlock(p_230383_1_, LAMP_BLOCK, j1, 0, 12, p_230383_5_);
               this.placeBlock(p_230383_1_, LAMP_BLOCK, j1, 4, 10, p_230383_5_);
               this.placeBlock(p_230383_1_, LAMP_BLOCK, j1, 4, 12, p_230383_5_);
               j1 = 12;
            }

            j1 = 8;

            for(int l1 = 0; l1 < 2; ++l1) {
               this.generateBox(p_230383_1_, p_230383_5_, j1, 0, 7, j1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(p_230383_1_, p_230383_5_, j1, 0, 14, j1, 2, 14, BASE_LIGHT, BASE_LIGHT, false);
               j1 = 14;
            }

            this.generateBox(p_230383_1_, p_230383_5_, 8, 3, 8, 8, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(p_230383_1_, p_230383_5_, 14, 3, 8, 14, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.spawnElder(p_230383_1_, p_230383_5_, 11, 5, 13);
         }

         return true;
      }
   }

   static class XDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private XDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return p_175969_1_.hasOpening[Direction.EAST.get3DDataValue()] && !p_175969_1_.connections[Direction.EAST.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.EAST.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.DoubleXRoom(p_175968_1_, p_175968_2_);
      }
   }

   static class XYDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private XYDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         if (p_175969_1_.hasOpening[Direction.EAST.get3DDataValue()] && !p_175969_1_.connections[Direction.EAST.get3DDataValue()].claimed && p_175969_1_.hasOpening[Direction.UP.get3DDataValue()] && !p_175969_1_.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = p_175969_1_.connections[Direction.EAST.get3DDataValue()];
            return oceanmonumentpieces$roomdefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.EAST.get3DDataValue()].claimed = true;
         p_175968_2_.connections[Direction.UP.get3DDataValue()].claimed = true;
         p_175968_2_.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.DoubleXYRoom(p_175968_1_, p_175968_2_);
      }
   }

   static class YDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private YDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return p_175969_1_.hasOpening[Direction.UP.get3DDataValue()] && !p_175969_1_.connections[Direction.UP.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.DoubleYRoom(p_175968_1_, p_175968_2_);
      }
   }

   static class YZDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private YZDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         if (p_175969_1_.hasOpening[Direction.NORTH.get3DDataValue()] && !p_175969_1_.connections[Direction.NORTH.get3DDataValue()].claimed && p_175969_1_.hasOpening[Direction.UP.get3DDataValue()] && !p_175969_1_.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = p_175969_1_.connections[Direction.NORTH.get3DDataValue()];
            return oceanmonumentpieces$roomdefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanmonumentpieces$roomdefinition.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         p_175968_2_.connections[Direction.UP.get3DDataValue()].claimed = true;
         p_175968_2_.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.DoubleYZRoom(p_175968_1_, p_175968_2_);
      }
   }

   static class ZDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private ZDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return p_175969_1_.hasOpening[Direction.NORTH.get3DDataValue()] && !p_175969_1_.connections[Direction.NORTH.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         OceanMonumentPieces.RoomDefinition oceanmonumentpieces$roomdefinition = p_175968_2_;
         if (!p_175968_2_.hasOpening[Direction.NORTH.get3DDataValue()] || p_175968_2_.connections[Direction.NORTH.get3DDataValue()].claimed) {
            oceanmonumentpieces$roomdefinition = p_175968_2_.connections[Direction.SOUTH.get3DDataValue()];
         }

         oceanmonumentpieces$roomdefinition.claimed = true;
         oceanmonumentpieces$roomdefinition.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.DoubleZRoom(p_175968_1_, oceanmonumentpieces$roomdefinition);
      }
   }
}
