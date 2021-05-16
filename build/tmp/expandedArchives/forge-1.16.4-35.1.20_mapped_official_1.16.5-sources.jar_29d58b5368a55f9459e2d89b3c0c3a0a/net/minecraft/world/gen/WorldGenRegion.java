package net.minecraft.world.gen;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.WorldGenTickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements ISeedReader {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<IChunk> cache;
   private final int x;
   private final int z;
   private final int size;
   private final ServerWorld level;
   private final long seed;
   private final IWorldInfo levelData;
   private final Random random;
   private final DimensionType dimensionType;
   private final ITickList<Block> blockTicks = new WorldGenTickList<>((p_205335_1_) -> {
      return this.getChunk(p_205335_1_).getBlockTicks();
   });
   private final ITickList<Fluid> liquidTicks = new WorldGenTickList<>((p_205334_1_) -> {
      return this.getChunk(p_205334_1_).getLiquidTicks();
   });
   private final BiomeManager biomeManager;
   private final ChunkPos firstPos;
   private final ChunkPos lastPos;
   private final StructureManager structureFeatureManager;

   public WorldGenRegion(ServerWorld p_i50698_1_, List<IChunk> p_i50698_2_) {
      int i = MathHelper.floor(Math.sqrt((double)p_i50698_2_.size()));
      if (i * i != p_i50698_2_.size()) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Cache size is not a square."));
      } else {
         ChunkPos chunkpos = p_i50698_2_.get(p_i50698_2_.size() / 2).getPos();
         this.cache = p_i50698_2_;
         this.x = chunkpos.x;
         this.z = chunkpos.z;
         this.size = i;
         this.level = p_i50698_1_;
         this.seed = p_i50698_1_.getSeed();
         this.levelData = p_i50698_1_.getLevelData();
         this.random = p_i50698_1_.getRandom();
         this.dimensionType = p_i50698_1_.dimensionType();
         this.biomeManager = new BiomeManager(this, BiomeManager.obfuscateSeed(this.seed), p_i50698_1_.dimensionType().getBiomeZoomer());
         this.firstPos = p_i50698_2_.get(0).getPos();
         this.lastPos = p_i50698_2_.get(p_i50698_2_.size() - 1).getPos();
         this.structureFeatureManager = p_i50698_1_.structureFeatureManager().forWorldGenRegion(this);
      }
   }

   public int getCenterX() {
      return this.x;
   }

   public int getCenterZ() {
      return this.z;
   }

   public IChunk getChunk(int p_212866_1_, int p_212866_2_) {
      return this.getChunk(p_212866_1_, p_212866_2_, ChunkStatus.EMPTY);
   }

   @Nullable
   public IChunk getChunk(int p_217353_1_, int p_217353_2_, ChunkStatus p_217353_3_, boolean p_217353_4_) {
      IChunk ichunk;
      if (this.hasChunk(p_217353_1_, p_217353_2_)) {
         int i = p_217353_1_ - this.firstPos.x;
         int j = p_217353_2_ - this.firstPos.z;
         ichunk = this.cache.get(i + j * this.size);
         if (ichunk.getStatus().isOrAfter(p_217353_3_)) {
            return ichunk;
         }
      } else {
         ichunk = null;
      }

      if (!p_217353_4_) {
         return null;
      } else {
         LOGGER.error("Requested chunk : {} {}", p_217353_1_, p_217353_2_);
         LOGGER.error("Region bounds : {} {} | {} {}", this.firstPos.x, this.firstPos.z, this.lastPos.x, this.lastPos.z);
         if (ichunk != null) {
            throw (RuntimeException)Util.pauseInIde(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", p_217353_3_, ichunk.getStatus(), p_217353_1_, p_217353_2_)));
         } else {
            throw (RuntimeException)Util.pauseInIde(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", p_217353_1_, p_217353_2_)));
         }
      }
   }

   public boolean hasChunk(int p_217354_1_, int p_217354_2_) {
      return p_217354_1_ >= this.firstPos.x && p_217354_1_ <= this.lastPos.x && p_217354_2_ >= this.firstPos.z && p_217354_2_ <= this.lastPos.z;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      return this.getChunk(p_180495_1_.getX() >> 4, p_180495_1_.getZ() >> 4).getBlockState(p_180495_1_);
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      return this.getChunk(p_204610_1_).getFluidState(p_204610_1_);
   }

   @Nullable
   public PlayerEntity getNearestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_) {
      return null;
   }

   public int getSkyDarken() {
      return 0;
   }

   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
      return this.level.getUncachedNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
      return 1.0F;
   }

   public WorldLightManager getLightEngine() {
      return this.level.getLightEngine();
   }

   public boolean destroyBlock(BlockPos p_241212_1_, boolean p_241212_2_, @Nullable Entity p_241212_3_, int p_241212_4_) {
      BlockState blockstate = this.getBlockState(p_241212_1_);
      if (blockstate.isAir(this, p_241212_1_)) {
         return false;
      } else {
         if (p_241212_2_) {
            TileEntity tileentity = blockstate.hasTileEntity() ? this.getBlockEntity(p_241212_1_) : null;
            Block.dropResources(blockstate, this.level, p_241212_1_, tileentity, p_241212_3_, ItemStack.EMPTY);
         }

         return this.setBlock(p_241212_1_, Blocks.AIR.defaultBlockState(), 3, p_241212_4_);
      }
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      IChunk ichunk = this.getChunk(p_175625_1_);
      TileEntity tileentity = ichunk.getBlockEntity(p_175625_1_);
      if (tileentity != null) {
         return tileentity;
      } else {
         CompoundNBT compoundnbt = ichunk.getBlockEntityNbt(p_175625_1_);
         BlockState blockstate = ichunk.getBlockState(p_175625_1_);
         if (compoundnbt != null) {
            if ("DUMMY".equals(compoundnbt.getString("id"))) {
               Block block = blockstate.getBlock();
               if (!blockstate.hasTileEntity()) {
                  return null;
               }

               tileentity = blockstate.createTileEntity(this.level);
            } else {
               tileentity = TileEntity.loadStatic(blockstate, compoundnbt);
            }

            if (tileentity != null) {
               ichunk.setBlockEntity(p_175625_1_, tileentity);
               return tileentity;
            }
         }

         if (blockstate.hasTileEntity()) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", (Object)p_175625_1_);
         }

         return null;
      }
   }

   public boolean setBlock(BlockPos p_241211_1_, BlockState p_241211_2_, int p_241211_3_, int p_241211_4_) {
      IChunk ichunk = this.getChunk(p_241211_1_);
      BlockState blockstate = ichunk.setBlockState(p_241211_1_, p_241211_2_, false);
      if (blockstate != null) {
         this.level.onBlockStateChange(p_241211_1_, blockstate, p_241211_2_);
      }

      Block block = p_241211_2_.getBlock();
      if (p_241211_2_.hasTileEntity()) {
         if (ichunk.getStatus().getChunkType() == ChunkStatus.Type.LEVELCHUNK) {
            ichunk.setBlockEntity(p_241211_1_, p_241211_2_.createTileEntity(this));
         } else {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putInt("x", p_241211_1_.getX());
            compoundnbt.putInt("y", p_241211_1_.getY());
            compoundnbt.putInt("z", p_241211_1_.getZ());
            compoundnbt.putString("id", "DUMMY");
            ichunk.setBlockEntityNbt(compoundnbt);
         }
      } else if (blockstate != null && blockstate.hasTileEntity()) {
         ichunk.removeBlockEntity(p_241211_1_);
      }

      if (p_241211_2_.hasPostProcess(this, p_241211_1_)) {
         this.markPosForPostprocessing(p_241211_1_);
      }

      return true;
   }

   private void markPosForPostprocessing(BlockPos p_201683_1_) {
      this.getChunk(p_201683_1_).markPosForPostprocessing(p_201683_1_);
   }

   public boolean addFreshEntity(Entity p_217376_1_) {
      int i = MathHelper.floor(p_217376_1_.getX() / 16.0D);
      int j = MathHelper.floor(p_217376_1_.getZ() / 16.0D);
      this.getChunk(i, j).addEntity(p_217376_1_);
      return true;
   }

   public boolean removeBlock(BlockPos p_217377_1_, boolean p_217377_2_) {
      return this.setBlock(p_217377_1_, Blocks.AIR.defaultBlockState(), 3);
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public boolean isClientSide() {
      return false;
   }

   @Deprecated
   public ServerWorld getLevel() {
      return this.level;
   }

   public DynamicRegistries registryAccess() {
      return this.level.registryAccess();
   }

   public IWorldInfo getLevelData() {
      return this.levelData;
   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos p_175649_1_) {
      if (!this.hasChunk(p_175649_1_.getX() >> 4, p_175649_1_.getZ() >> 4)) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.level.getDifficulty(), this.level.getDayTime(), 0L, this.level.getMoonBrightness());
      }
   }

   public AbstractChunkProvider getChunkSource() {
      return this.level.getChunkSource();
   }

   public long getSeed() {
      return this.seed;
   }

   public ITickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public ITickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public int getSeaLevel() {
      return this.level.getSeaLevel();
   }

   public Random getRandom() {
      return this.random;
   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      return this.getChunk(p_201676_2_ >> 4, p_201676_3_ >> 4).getHeight(p_201676_1_, p_201676_2_ & 15, p_201676_3_ & 15) + 1;
   }

   public void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
   }

   public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
   }

   public void levelEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
   }

   public DimensionType dimensionType() {
      return this.dimensionType;
   }

   public boolean isStateAtPosition(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
      return p_217375_2_.test(this.getBlockState(p_217375_1_));
   }

   public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_) {
      return Collections.emptyList();
   }

   public List<Entity> getEntities(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_) {
      return Collections.emptyList();
   }

   public List<PlayerEntity> players() {
      return Collections.emptyList();
   }

   public Stream<? extends StructureStart<?>> startsForFeature(SectionPos p_241827_1_, Structure<?> p_241827_2_) {
      return this.structureFeatureManager.startsForFeature(p_241827_1_, p_241827_2_);
   }
}
