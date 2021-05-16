package net.minecraft.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
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

public class ShipwreckPieces {
   private static final BlockPos PIVOT = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
   private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};

   public static void addPieces(TemplateManager p_204760_0_, BlockPos p_204760_1_, Rotation p_204760_2_, List<StructurePiece> p_204760_3_, Random p_204760_4_, ShipwreckConfig p_204760_5_) {
      ResourceLocation resourcelocation = Util.getRandom(p_204760_5_.isBeached ? STRUCTURE_LOCATION_BEACHED : STRUCTURE_LOCATION_OCEAN, p_204760_4_);
      p_204760_3_.add(new ShipwreckPieces.Piece(p_204760_0_, resourcelocation, p_204760_1_, p_204760_2_, p_204760_5_.isBeached));
   }

   public static class Piece extends TemplateStructurePiece {
      private final Rotation rotation;
      private final ResourceLocation templateLocation;
      private final boolean isBeached;

      public Piece(TemplateManager p_i48904_1_, ResourceLocation p_i48904_2_, BlockPos p_i48904_3_, Rotation p_i48904_4_, boolean p_i48904_5_) {
         super(IStructurePieceType.SHIPWRECK_PIECE, 0);
         this.templatePosition = p_i48904_3_;
         this.rotation = p_i48904_4_;
         this.templateLocation = p_i48904_2_;
         this.isBeached = p_i48904_5_;
         this.loadTemplate(p_i48904_1_);
      }

      public Piece(TemplateManager p_i50445_1_, CompoundNBT p_i50445_2_) {
         super(IStructurePieceType.SHIPWRECK_PIECE, p_i50445_2_);
         this.templateLocation = new ResourceLocation(p_i50445_2_.getString("Template"));
         this.isBeached = p_i50445_2_.getBoolean("isBeached");
         this.rotation = Rotation.valueOf(p_i50445_2_.getString("Rot"));
         this.loadTemplate(p_i50445_1_);
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putString("Template", this.templateLocation.toString());
         p_143011_1_.putBoolean("isBeached", this.isBeached);
         p_143011_1_.putString("Rot", this.rotation.name());
      }

      private void loadTemplate(TemplateManager p_204754_1_) {
         Template template = p_204754_1_.getOrCreate(this.templateLocation);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot(ShipwreckPieces.PIVOT).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
         this.setup(template, this.templatePosition, placementsettings);
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IServerWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if ("map_chest".equals(p_186175_1_)) {
            LockableLootTileEntity.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.below(), LootTables.SHIPWRECK_MAP);
         } else if ("treasure_chest".equals(p_186175_1_)) {
            LockableLootTileEntity.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.below(), LootTables.SHIPWRECK_TREASURE);
         } else if ("supply_chest".equals(p_186175_1_)) {
            LockableLootTileEntity.setLootTable(p_186175_3_, p_186175_4_, p_186175_2_.below(), LootTables.SHIPWRECK_SUPPLY);
         }

      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         int i = 256;
         int j = 0;
         BlockPos blockpos = this.template.getSize();
         Heightmap.Type heightmap$type = this.isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
         int k = blockpos.getX() * blockpos.getZ();
         if (k == 0) {
            j = p_230383_1_.getHeight(heightmap$type, this.templatePosition.getX(), this.templatePosition.getZ());
         } else {
            BlockPos blockpos1 = this.templatePosition.offset(blockpos.getX() - 1, 0, blockpos.getZ() - 1);

            for(BlockPos blockpos2 : BlockPos.betweenClosed(this.templatePosition, blockpos1)) {
               int l = p_230383_1_.getHeight(heightmap$type, blockpos2.getX(), blockpos2.getZ());
               j += l;
               i = Math.min(i, l);
            }

            j = j / k;
         }

         int i1 = this.isBeached ? i - blockpos.getY() / 2 - p_230383_4_.nextInt(3) : j;
         this.templatePosition = new BlockPos(this.templatePosition.getX(), i1, this.templatePosition.getZ());
         return super.postProcess(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
      }
   }
}
