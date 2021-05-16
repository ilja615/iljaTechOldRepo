package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class IglooPieces {
   private static final ResourceLocation STRUCTURE_LOCATION_IGLOO = new ResourceLocation("igloo/top");
   private static final ResourceLocation STRUCTURE_LOCATION_LADDER = new ResourceLocation("igloo/middle");
   private static final ResourceLocation STRUCTURE_LOCATION_LABORATORY = new ResourceLocation("igloo/bottom");
   private static final Map<ResourceLocation, BlockPos> PIVOTS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, new BlockPos(3, 5, 5), STRUCTURE_LOCATION_LADDER, new BlockPos(1, 3, 1), STRUCTURE_LOCATION_LABORATORY, new BlockPos(3, 6, 7));
   private static final Map<ResourceLocation, BlockPos> OFFSETS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, BlockPos.ZERO, STRUCTURE_LOCATION_LADDER, new BlockPos(2, -3, 4), STRUCTURE_LOCATION_LABORATORY, new BlockPos(0, -3, -2));

   public static void addPieces(TemplateManager p_236991_0_, BlockPos p_236991_1_, Rotation p_236991_2_, List<StructurePiece> p_236991_3_, Random p_236991_4_) {
      if (p_236991_4_.nextDouble() < 0.5D) {
         int i = p_236991_4_.nextInt(8) + 4;
         p_236991_3_.add(new IglooPieces.Piece(p_236991_0_, STRUCTURE_LOCATION_LABORATORY, p_236991_1_, p_236991_2_, i * 3));

         for(int j = 0; j < i - 1; ++j) {
            p_236991_3_.add(new IglooPieces.Piece(p_236991_0_, STRUCTURE_LOCATION_LADDER, p_236991_1_, p_236991_2_, j * 3));
         }
      }

      p_236991_3_.add(new IglooPieces.Piece(p_236991_0_, STRUCTURE_LOCATION_IGLOO, p_236991_1_, p_236991_2_, 0));
   }

   public static class Piece extends TemplateStructurePiece {
      private final ResourceLocation templateLocation;
      private final Rotation rotation;

      public Piece(TemplateManager p_i49313_1_, ResourceLocation p_i49313_2_, BlockPos p_i49313_3_, Rotation p_i49313_4_, int p_i49313_5_) {
         super(IStructurePieceType.IGLOO, 0);
         this.templateLocation = p_i49313_2_;
         BlockPos blockpos = IglooPieces.OFFSETS.get(p_i49313_2_);
         this.templatePosition = p_i49313_3_.offset(blockpos.getX(), blockpos.getY() - p_i49313_5_, blockpos.getZ());
         this.rotation = p_i49313_4_;
         this.loadTemplate(p_i49313_1_);
      }

      public Piece(TemplateManager p_i50566_1_, CompoundNBT p_i50566_2_) {
         super(IStructurePieceType.IGLOO, p_i50566_2_);
         this.templateLocation = new ResourceLocation(p_i50566_2_.getString("Template"));
         this.rotation = Rotation.valueOf(p_i50566_2_.getString("Rot"));
         this.loadTemplate(p_i50566_1_);
      }

      private void loadTemplate(TemplateManager p_207614_1_) {
         Template template = p_207614_1_.getOrCreate(this.templateLocation);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot(IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
         this.setup(template, this.templatePosition, placementsettings);
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putString("Template", this.templateLocation.toString());
         p_143011_1_.putString("Rot", this.rotation.name());
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IServerWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if ("chest".equals(p_186175_1_)) {
            p_186175_3_.setBlock(p_186175_2_, Blocks.AIR.defaultBlockState(), 3);
            TileEntity tileentity = p_186175_3_.getBlockEntity(p_186175_2_.below());
            if (tileentity instanceof ChestTileEntity) {
               ((ChestTileEntity)tileentity).setLootTable(LootTables.IGLOO_CHEST, p_186175_4_.nextLong());
            }

         }
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot(IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
         BlockPos blockpos = IglooPieces.OFFSETS.get(this.templateLocation);
         BlockPos blockpos1 = this.templatePosition.offset(Template.calculateRelativePosition(placementsettings, new BlockPos(3 - blockpos.getX(), 0, 0 - blockpos.getZ())));
         int i = p_230383_1_.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
         BlockPos blockpos2 = this.templatePosition;
         this.templatePosition = this.templatePosition.offset(0, i - 90 - 1, 0);
         boolean flag = super.postProcess(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
         if (this.templateLocation.equals(IglooPieces.STRUCTURE_LOCATION_IGLOO)) {
            BlockPos blockpos3 = this.templatePosition.offset(Template.calculateRelativePosition(placementsettings, new BlockPos(3, 0, 5)));
            BlockState blockstate = p_230383_1_.getBlockState(blockpos3.below());
            if (!blockstate.isAir() && !blockstate.is(Blocks.LADDER)) {
               p_230383_1_.setBlock(blockpos3, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
            }
         }

         this.templatePosition = blockpos2;
         return flag;
      }
   }
}
