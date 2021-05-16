package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;

public class EmptyChunk extends Chunk {
   private static final Biome[] BIOMES = Util.make(new Biome[BiomeContainer.BIOMES_SIZE], (p_203406_0_) -> {
      Arrays.fill(p_203406_0_, BiomeRegistry.PLAINS);
   });

   public EmptyChunk(World p_i49950_1_, ChunkPos p_i49950_2_) {
      super(p_i49950_1_, p_i49950_2_, new BiomeContainer(p_i49950_1_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), BIOMES));
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      return Blocks.VOID_AIR.defaultBlockState();
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
      return null;
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Nullable
   public WorldLightManager getLightEngine() {
      return null;
   }

   public int getLightEmission(BlockPos p_217298_1_) {
      return 0;
   }

   public void addEntity(Entity p_76612_1_) {
   }

   public void removeEntity(Entity p_76622_1_) {
   }

   public void removeEntity(Entity p_76608_1_, int p_76608_2_) {
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_177424_1_, Chunk.CreateEntityType p_177424_2_) {
      return null;
   }

   public void addBlockEntity(TileEntity p_150813_1_) {
   }

   public void setBlockEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
   }

   public void removeBlockEntity(BlockPos p_177425_1_) {
   }

   public void markUnsaved() {
   }

   public void getEntities(@Nullable Entity p_177414_1_, AxisAlignedBB p_177414_2_, List<Entity> p_177414_3_, Predicate<? super Entity> p_177414_4_) {
   }

   public <T extends Entity> void getEntitiesOfClass(Class<? extends T> p_177430_1_, AxisAlignedBB p_177430_2_, List<T> p_177430_3_, Predicate<? super T> p_177430_4_) {
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isYSpaceEmpty(int p_76606_1_, int p_76606_2_) {
      return true;
   }

   public ChunkHolder.LocationType getFullStatus() {
      return ChunkHolder.LocationType.BORDER;
   }
}
