package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlatChunkGenerator extends ChunkGenerator {
   public static final Codec<FlatChunkGenerator> CODEC = FlatGenerationSettings.CODEC.fieldOf("settings").xmap(FlatChunkGenerator::new, FlatChunkGenerator::settings).codec();
   private final FlatGenerationSettings settings;

   public FlatChunkGenerator(FlatGenerationSettings p_i231902_1_) {
      super(new SingleBiomeProvider(p_i231902_1_.getBiomeFromSettings()), new SingleBiomeProvider(p_i231902_1_.getBiome()), p_i231902_1_.structureSettings(), 0L);
      this.settings = p_i231902_1_;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public ChunkGenerator withSeed(long p_230349_1_) {
      return this;
   }

   public FlatGenerationSettings settings() {
      return this.settings;
   }

   public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
   }

   public int getSpawnHeight() {
      BlockState[] ablockstate = this.settings.getLayers();

      for(int i = 0; i < ablockstate.length; ++i) {
         BlockState blockstate = ablockstate[i] == null ? Blocks.AIR.defaultBlockState() : ablockstate[i];
         if (!Heightmap.Type.MOTION_BLOCKING.isOpaque().test(blockstate)) {
            return i - 1;
         }
      }

      return ablockstate.length;
   }

   public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
      BlockState[] ablockstate = this.settings.getLayers();
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      Heightmap heightmap = p_230352_3_.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
      Heightmap heightmap1 = p_230352_3_.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

      for(int i = 0; i < ablockstate.length; ++i) {
         BlockState blockstate = ablockstate[i];
         if (blockstate != null) {
            for(int j = 0; j < 16; ++j) {
               for(int k = 0; k < 16; ++k) {
                  p_230352_3_.setBlockState(blockpos$mutable.set(j, i, k), blockstate, false);
                  heightmap.update(j, i, k, blockstate);
                  heightmap1.update(j, i, k, blockstate);
               }
            }
         }
      }

   }

   public int getBaseHeight(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      BlockState[] ablockstate = this.settings.getLayers();

      for(int i = ablockstate.length - 1; i >= 0; --i) {
         BlockState blockstate = ablockstate[i];
         if (blockstate != null && p_222529_3_.isOpaque().test(blockstate)) {
            return i + 1;
         }
      }

      return 0;
   }

   public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
      return new Blockreader(Arrays.stream(this.settings.getLayers()).map((p_236072_0_) -> {
         return p_236072_0_ == null ? Blocks.AIR.defaultBlockState() : p_236072_0_;
      }).toArray((p_236071_0_) -> {
         return new BlockState[p_236071_0_];
      }));
   }
}
