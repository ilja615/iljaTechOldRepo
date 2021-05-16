package net.minecraft.world.gen.feature;

import java.util.BitSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;

public class WorldDecoratingHelper {
   private final ISeedReader level;
   private final ChunkGenerator generator;

   public WorldDecoratingHelper(ISeedReader p_i242021_1_, ChunkGenerator p_i242021_2_) {
      this.level = p_i242021_1_;
      this.generator = p_i242021_2_;
   }

   public int getHeight(Heightmap.Type p_242893_1_, int p_242893_2_, int p_242893_3_) {
      return this.level.getHeight(p_242893_1_, p_242893_2_, p_242893_3_);
   }

   public int getGenDepth() {
      return this.generator.getGenDepth();
   }

   public int getSeaLevel() {
      return this.generator.getSeaLevel();
   }

   public BitSet getCarvingMask(ChunkPos p_242892_1_, GenerationStage.Carving p_242892_2_) {
      return ((ChunkPrimer)this.level.getChunk(p_242892_1_.x, p_242892_1_.z)).getOrCreateCarvingMask(p_242892_2_);
   }

   public BlockState getBlockState(BlockPos p_242894_1_) {
      return this.level.getBlockState(p_242894_1_);
   }
}
