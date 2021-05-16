package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DebugChunkGenerator extends ChunkGenerator {
   public static final Codec<DebugChunkGenerator> CODEC = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(DebugChunkGenerator::new, DebugChunkGenerator::biomes).stable().codec();
   private static List<BlockState> ALL_BLOCKS = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((p_236067_0_) -> {
      return p_236067_0_.getStateDefinition().getPossibleStates().stream();
   }).collect(Collectors.toList());
   private static int GRID_WIDTH = MathHelper.ceil(MathHelper.sqrt((float)ALL_BLOCKS.size()));
   private static int GRID_HEIGHT = MathHelper.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
   protected static final BlockState BARRIER = Blocks.BARRIER.defaultBlockState();
   private final Registry<Biome> biomes;

   public DebugChunkGenerator(Registry<Biome> p_i241974_1_) {
      super(new SingleBiomeProvider(p_i241974_1_.getOrThrow(Biomes.PLAINS)), new DimensionStructuresSettings(false));
      this.biomes = p_i241974_1_;
   }

   public Registry<Biome> biomes() {
      return this.biomes;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public ChunkGenerator withSeed(long p_230349_1_) {
      return this;
   }

   public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
   }

   public void applyCarvers(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_) {
   }

   public void applyBiomeDecoration(WorldGenRegion p_230351_1_, StructureManager p_230351_2_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      int i = p_230351_1_.getCenterX();
      int j = p_230351_1_.getCenterZ();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            int i1 = (i << 4) + k;
            int j1 = (j << 4) + l;
            p_230351_1_.setBlock(blockpos$mutable.set(i1, 60, j1), BARRIER, 2);
            BlockState blockstate = getBlockStateFor(i1, j1);
            if (blockstate != null) {
               p_230351_1_.setBlock(blockpos$mutable.set(i1, 70, j1), blockstate, 2);
            }
         }
      }

   }

   public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
   }

   public int getBaseHeight(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      return 0;
   }

   public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
      return new Blockreader(new BlockState[0]);
   }

   public static BlockState getBlockStateFor(int p_177461_0_, int p_177461_1_) {
      BlockState blockstate = AIR;
      if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0) {
         p_177461_0_ = p_177461_0_ / 2;
         p_177461_1_ = p_177461_1_ / 2;
         if (p_177461_0_ <= GRID_WIDTH && p_177461_1_ <= GRID_HEIGHT) {
            int i = MathHelper.abs(p_177461_0_ * GRID_WIDTH + p_177461_1_);
            if (i < ALL_BLOCKS.size()) {
               blockstate = ALL_BLOCKS.get(i);
            }
         }
      }

      return blockstate;
   }
   
   public static void initValidStates() {
      ALL_BLOCKS = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
      GRID_WIDTH = MathHelper.ceil(MathHelper.sqrt(ALL_BLOCKS.size()));
      GRID_HEIGHT = MathHelper.ceil((float) (ALL_BLOCKS.size() / GRID_WIDTH));
   }
}
