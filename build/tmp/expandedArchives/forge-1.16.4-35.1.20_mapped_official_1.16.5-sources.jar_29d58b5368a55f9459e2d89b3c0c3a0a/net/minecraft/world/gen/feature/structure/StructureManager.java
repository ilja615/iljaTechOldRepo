package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.DataFixUtils;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

public class StructureManager {
   private final IWorld level;
   private final DimensionGeneratorSettings worldGenSettings;

   public StructureManager(IWorld p_i231626_1_, DimensionGeneratorSettings p_i231626_2_) {
      this.level = p_i231626_1_;
      this.worldGenSettings = p_i231626_2_;
   }

   public StructureManager forWorldGenRegion(WorldGenRegion p_241464_1_) {
      if (p_241464_1_.getLevel() != this.level) {
         throw new IllegalStateException("Using invalid feature manager (source level: " + p_241464_1_.getLevel() + ", region: " + p_241464_1_);
      } else {
         return new StructureManager(p_241464_1_, this.worldGenSettings);
      }
   }

   public Stream<? extends StructureStart<?>> startsForFeature(SectionPos p_235011_1_, Structure<?> p_235011_2_) {
      return this.level.getChunk(p_235011_1_.x(), p_235011_1_.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(p_235011_2_).stream().map((p_235015_0_) -> {
         return SectionPos.of(new ChunkPos(p_235015_0_), 0);
      }).map((p_235006_2_) -> {
         return this.getStartForFeature(p_235006_2_, p_235011_2_, this.level.getChunk(p_235006_2_.x(), p_235006_2_.z(), ChunkStatus.STRUCTURE_STARTS));
      }).filter((p_235007_0_) -> {
         return p_235007_0_ != null && p_235007_0_.isValid();
      });
   }

   @Nullable
   public StructureStart<?> getStartForFeature(SectionPos p_235013_1_, Structure<?> p_235013_2_, IStructureReader p_235013_3_) {
      return p_235013_3_.getStartForFeature(p_235013_2_);
   }

   public void setStartForFeature(SectionPos p_235014_1_, Structure<?> p_235014_2_, StructureStart<?> p_235014_3_, IStructureReader p_235014_4_) {
      p_235014_4_.setStartForFeature(p_235014_2_, p_235014_3_);
   }

   public void addReferenceForFeature(SectionPos p_235012_1_, Structure<?> p_235012_2_, long p_235012_3_, IStructureReader p_235012_5_) {
      p_235012_5_.addReferenceForFeature(p_235012_2_, p_235012_3_);
   }

   public boolean shouldGenerateFeatures() {
      return this.worldGenSettings.generateFeatures();
   }

   public StructureStart<?> getStructureAt(BlockPos p_235010_1_, boolean p_235010_2_, Structure<?> p_235010_3_) {
      return DataFixUtils.orElse(this.startsForFeature(SectionPos.of(p_235010_1_), p_235010_3_).filter((p_235009_1_) -> {
         return p_235009_1_.getBoundingBox().isInside(p_235010_1_);
      }).filter((p_235016_2_) -> {
         return !p_235010_2_ || p_235016_2_.getPieces().stream().anyMatch((p_235008_1_) -> {
            return p_235008_1_.getBoundingBox().isInside(p_235010_1_);
         });
      }).findFirst(), StructureStart.INVALID_START);
   }
}
