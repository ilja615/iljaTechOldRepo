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
   private final IWorld world;
   private final DimensionGeneratorSettings settings;

   public StructureManager(IWorld world, DimensionGeneratorSettings settings) {
      this.world = world;
      this.settings = settings;
   }

   public StructureManager getStructureManager(WorldGenRegion region) {
      if (region.getWorld() != this.world) {
         throw new IllegalStateException("Using invalid feature manager (source level: " + region.getWorld() + ", region: " + region);
      } else {
         return new StructureManager(region, this.settings);
      }
   }

   public Stream<? extends StructureStart<?>> func_235011_a_(SectionPos sectionPos, Structure<?> structure) {
      return this.world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).func_230346_b_(structure).stream().map((chunkPos) -> {
         return SectionPos.from(new ChunkPos(chunkPos), 0);
      }).map((pos) -> {
         return this.getStructureStart(pos, structure, this.world.getChunk(pos.getSectionX(), pos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS));
      }).filter((start) -> {
         return start != null && start.isValid();
      });
   }

   @Nullable
   public StructureStart<?> getStructureStart(SectionPos sectionPos, Structure<?> structure, IStructureReader reader) {
      return reader.func_230342_a_(structure);
   }

   public void addStructureStart(SectionPos sectionPos, Structure<?> structure, StructureStart<?> start, IStructureReader reader) {
      reader.func_230344_a_(structure, start);
   }

   public void addReference(SectionPos sectionPos, Structure<?> structure, long p_235012_3_, IStructureReader reader) {
      reader.func_230343_a_(structure, p_235012_3_);
   }

   public boolean canGenerateFeatures() {
      return this.settings.doesGenerateFeatures();
   }

   public StructureStart<?> getStructureStart(BlockPos pos, boolean p_235010_2_, Structure<?> structure) {
      return DataFixUtils.orElse(this.func_235011_a_(SectionPos.from(pos), structure).filter((start) -> {
         return start.getBoundingBox().isVecInside(pos);
      }).filter((start) -> {
         return !p_235010_2_ || start.getComponents().stream().anyMatch((piece) -> {
            return piece.getBoundingBox().isVecInside(pos);
         });
      }).findFirst(), StructureStart.DUMMY);
   }
}
