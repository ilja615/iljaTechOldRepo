package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TripWireBlock;
import net.minecraft.block.TripWireHookBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class JunglePyramidPiece extends ScatteredStructurePiece {
   private boolean placedMainChest;
   private boolean placedHiddenChest;
   private boolean placedTrap1;
   private boolean placedTrap2;
   private static final JunglePyramidPiece.Selector STONE_SELECTOR = new JunglePyramidPiece.Selector();

   public JunglePyramidPiece(Random p_i48656_1_, int p_i48656_2_, int p_i48656_3_) {
      super(IStructurePieceType.JUNGLE_PYRAMID_PIECE, p_i48656_1_, p_i48656_2_, 64, p_i48656_3_, 12, 10, 15);
   }

   public JunglePyramidPiece(TemplateManager p_i51350_1_, CompoundNBT p_i51350_2_) {
      super(IStructurePieceType.JUNGLE_PYRAMID_PIECE, p_i51350_2_);
      this.placedMainChest = p_i51350_2_.getBoolean("placedMainChest");
      this.placedHiddenChest = p_i51350_2_.getBoolean("placedHiddenChest");
      this.placedTrap1 = p_i51350_2_.getBoolean("placedTrap1");
      this.placedTrap2 = p_i51350_2_.getBoolean("placedTrap2");
   }

   protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      super.addAdditionalSaveData(p_143011_1_);
      p_143011_1_.putBoolean("placedMainChest", this.placedMainChest);
      p_143011_1_.putBoolean("placedHiddenChest", this.placedHiddenChest);
      p_143011_1_.putBoolean("placedTrap1", this.placedTrap1);
      p_143011_1_.putBoolean("placedTrap2", this.placedTrap2);
   }

   public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
      if (!this.updateAverageGroundHeight(p_230383_1_, p_230383_5_, 0)) {
         return false;
      } else {
         this.generateBox(p_230383_1_, p_230383_5_, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 2, 9, 2, 2, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 12, 9, 2, 12, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 1, 3, 2, 2, 11, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 1, 3, 9, 2, 11, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 1, 10, 6, 1, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 13, 10, 6, 13, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 2, 1, 6, 12, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 10, 3, 2, 10, 6, 12, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 3, 2, 9, 3, 12, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 6, 2, 9, 6, 12, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 7, 3, 8, 7, 11, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 8, 4, 7, 8, 10, false, p_230383_4_, STONE_SELECTOR);
         this.generateAirBox(p_230383_1_, p_230383_5_, 3, 1, 3, 8, 2, 11);
         this.generateAirBox(p_230383_1_, p_230383_5_, 4, 3, 6, 7, 3, 9);
         this.generateAirBox(p_230383_1_, p_230383_5_, 2, 4, 2, 9, 5, 12);
         this.generateAirBox(p_230383_1_, p_230383_5_, 4, 6, 5, 7, 6, 9);
         this.generateAirBox(p_230383_1_, p_230383_5_, 5, 7, 6, 6, 7, 8);
         this.generateAirBox(p_230383_1_, p_230383_5_, 5, 1, 2, 6, 2, 2);
         this.generateAirBox(p_230383_1_, p_230383_5_, 5, 2, 12, 6, 2, 12);
         this.generateAirBox(p_230383_1_, p_230383_5_, 5, 5, 1, 6, 5, 1);
         this.generateAirBox(p_230383_1_, p_230383_5_, 5, 5, 13, 6, 5, 13);
         this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 1, 5, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 10, 5, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 1, 5, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.AIR.defaultBlockState(), 10, 5, 9, p_230383_5_);

         for(int i = 0; i <= 14; i += 14) {
            this.generateBox(p_230383_1_, p_230383_5_, 2, 4, i, 2, 5, i, false, p_230383_4_, STONE_SELECTOR);
            this.generateBox(p_230383_1_, p_230383_5_, 4, 4, i, 4, 5, i, false, p_230383_4_, STONE_SELECTOR);
            this.generateBox(p_230383_1_, p_230383_5_, 7, 4, i, 7, 5, i, false, p_230383_4_, STONE_SELECTOR);
            this.generateBox(p_230383_1_, p_230383_5_, 9, 4, i, 9, 5, i, false, p_230383_4_, STONE_SELECTOR);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 5, 6, 0, 6, 6, 0, false, p_230383_4_, STONE_SELECTOR);

         for(int l = 0; l <= 11; l += 11) {
            for(int j = 2; j <= 12; j += 2) {
               this.generateBox(p_230383_1_, p_230383_5_, l, 4, j, l, 5, j, false, p_230383_4_, STONE_SELECTOR);
            }

            this.generateBox(p_230383_1_, p_230383_5_, l, 6, 5, l, 6, 5, false, p_230383_4_, STONE_SELECTOR);
            this.generateBox(p_230383_1_, p_230383_5_, l, 6, 9, l, 6, 9, false, p_230383_4_, STONE_SELECTOR);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 2, 7, 2, 2, 9, 2, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 7, 2, 9, 9, 2, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 7, 12, 2, 9, 12, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 7, 12, 9, 9, 12, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 9, 4, 4, 9, 4, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 9, 4, 7, 9, 4, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 9, 10, 4, 9, 10, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 9, 10, 7, 9, 10, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 9, 7, 6, 9, 7, false, p_230383_4_, STONE_SELECTOR);
         BlockState blockstate3 = Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.EAST);
         BlockState blockstate4 = Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.WEST);
         BlockState blockstate = Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.SOUTH);
         BlockState blockstate1 = Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.NORTH);
         this.placeBlock(p_230383_1_, blockstate1, 5, 9, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 6, 9, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate, 5, 9, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate, 6, 9, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 4, 0, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 5, 0, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 6, 0, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 7, 0, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 4, 1, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 4, 2, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 4, 3, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 7, 1, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 7, 2, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate1, 7, 3, 10, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 9, 4, 1, 9, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 1, 9, 7, 1, 9, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 10, 7, 2, 10, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 4, 5, 6, 4, 5, false, p_230383_4_, STONE_SELECTOR);
         this.placeBlock(p_230383_1_, blockstate3, 4, 4, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate4, 7, 4, 5, p_230383_5_);

         for(int k = 0; k < 4; ++k) {
            this.placeBlock(p_230383_1_, blockstate, 5, 0 - k, 6 + k, p_230383_5_);
            this.placeBlock(p_230383_1_, blockstate, 6, 0 - k, 6 + k, p_230383_5_);
            this.generateAirBox(p_230383_1_, p_230383_5_, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
         }

         this.generateAirBox(p_230383_1_, p_230383_5_, 1, -3, 12, 10, -1, 13);
         this.generateAirBox(p_230383_1_, p_230383_5_, 1, -3, 1, 3, -1, 13);
         this.generateAirBox(p_230383_1_, p_230383_5_, 1, -3, 1, 9, -1, 5);

         for(int i1 = 1; i1 <= 13; i1 += 2) {
            this.generateBox(p_230383_1_, p_230383_5_, 1, -3, i1, 1, -2, i1, false, p_230383_4_, STONE_SELECTOR);
         }

         for(int j1 = 2; j1 <= 12; j1 += 2) {
            this.generateBox(p_230383_1_, p_230383_5_, 1, -1, j1, 3, -1, j1, false, p_230383_4_, STONE_SELECTOR);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 2, -2, 1, 5, -2, 1, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 7, -2, 1, 9, -2, 1, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 6, -3, 1, 6, -3, 1, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 6, -1, 1, 6, -1, 1, false, p_230383_4_, STONE_SELECTOR);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.EAST).setValue(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 1, -3, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.WEST).setValue(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 4, -3, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, Boolean.valueOf(true)).setValue(TripWireBlock.WEST, Boolean.valueOf(true)).setValue(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 2, -3, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, Boolean.valueOf(true)).setValue(TripWireBlock.WEST, Boolean.valueOf(true)).setValue(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 3, -3, 8, p_230383_5_);
         BlockState blockstate5 = Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedstoneWireBlock.NORTH, RedstoneSide.SIDE).setValue(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE);
         this.placeBlock(p_230383_1_, blockstate5, 5, -3, 7, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 5, -3, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 5, -3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 5, -3, 4, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 5, -3, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 5, -3, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedstoneWireBlock.NORTH, RedstoneSide.SIDE).setValue(RedstoneWireBlock.WEST, RedstoneSide.SIDE), 5, -3, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedstoneWireBlock.EAST, RedstoneSide.SIDE).setValue(RedstoneWireBlock.WEST, RedstoneSide.SIDE), 4, -3, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3, -3, 1, p_230383_5_);
         if (!this.placedTrap1) {
            this.placedTrap1 = this.createDispenser(p_230383_1_, p_230383_5_, p_230383_4_, 3, -2, 1, Direction.NORTH, LootTables.JUNGLE_TEMPLE_DISPENSER);
         }

         this.placeBlock(p_230383_1_, Blocks.VINE.defaultBlockState().setValue(VineBlock.SOUTH, Boolean.valueOf(true)), 3, -2, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.NORTH).setValue(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.SOUTH).setValue(TripWireHookBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, Boolean.valueOf(true)).setValue(TripWireBlock.SOUTH, Boolean.valueOf(true)).setValue(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, Boolean.valueOf(true)).setValue(TripWireBlock.SOUTH, Boolean.valueOf(true)).setValue(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, Boolean.valueOf(true)).setValue(TripWireBlock.SOUTH, Boolean.valueOf(true)).setValue(TripWireBlock.ATTACHED, Boolean.valueOf(true)), 7, -3, 4, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedstoneWireBlock.EAST, RedstoneSide.SIDE).setValue(RedstoneWireBlock.WEST, RedstoneSide.SIDE), 8, -3, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedstoneWireBlock.WEST, RedstoneSide.SIDE).setValue(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE), 9, -3, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedstoneWireBlock.NORTH, RedstoneSide.SIDE).setValue(RedstoneWireBlock.SOUTH, RedstoneSide.UP), 9, -3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 4, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 9, -2, 4, p_230383_5_);
         if (!this.placedTrap2) {
            this.placedTrap2 = this.createDispenser(p_230383_1_, p_230383_5_, p_230383_4_, 9, -2, 3, Direction.WEST, LootTables.JUNGLE_TEMPLE_DISPENSER);
         }

         this.placeBlock(p_230383_1_, Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, Boolean.valueOf(true)), 8, -1, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, Boolean.valueOf(true)), 8, -2, 3, p_230383_5_);
         if (!this.placedMainChest) {
            this.placedMainChest = this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 8, -3, 3, LootTables.JUNGLE_TEMPLE);
         }

         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 2, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 1, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 4, -3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -2, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -1, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 6, -3, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -2, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -1, 5, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 5, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 9, -1, 1, 9, -1, 5, false, p_230383_4_, STONE_SELECTOR);
         this.generateAirBox(p_230383_1_, p_230383_5_, 8, -3, 8, 10, -1, 10);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 8, -2, 11, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 9, -2, 11, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 10, -2, 11, p_230383_5_);
         BlockState blockstate2 = Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.NORTH).setValue(LeverBlock.FACE, AttachFace.WALL);
         this.placeBlock(p_230383_1_, blockstate2, 8, -2, 12, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate2, 9, -2, 12, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate2, 10, -2, 12, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 8, -3, 8, 8, -3, 10, false, p_230383_4_, STONE_SELECTOR);
         this.generateBox(p_230383_1_, p_230383_5_, 10, -3, 8, 10, -3, 10, false, p_230383_4_, STONE_SELECTOR);
         this.placeBlock(p_230383_1_, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -2, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 8, -2, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate5, 8, -2, 10, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedstoneWireBlock.NORTH, RedstoneSide.SIDE).setValue(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE).setValue(RedstoneWireBlock.EAST, RedstoneSide.SIDE).setValue(RedstoneWireBlock.WEST, RedstoneSide.SIDE), 10, -1, 9, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBlock.FACING, Direction.UP), 9, -2, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBlock.FACING, Direction.WEST), 10, -2, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBlock.FACING, Direction.WEST), 10, -1, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.REPEATER.defaultBlockState().setValue(RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, p_230383_5_);
         if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 9, -3, 10, LootTables.JUNGLE_TEMPLE);
         }

         return true;
      }
   }

   static class Selector extends StructurePiece.BlockSelector {
      private Selector() {
      }

      public void next(Random p_75062_1_, int p_75062_2_, int p_75062_3_, int p_75062_4_, boolean p_75062_5_) {
         if (p_75062_1_.nextFloat() < 0.4F) {
            this.next = Blocks.COBBLESTONE.defaultBlockState();
         } else {
            this.next = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
         }

      }
   }
}
