package net.minecraft.world;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;

public interface IBiomeReader extends IEntityReader, IWorldReader, IWorldGenerationReader {
   default Stream<VoxelShape> getEntityCollisions(@Nullable Entity p_230318_1_, AxisAlignedBB p_230318_2_, Predicate<Entity> p_230318_3_) {
      return IEntityReader.super.getEntityCollisions(p_230318_1_, p_230318_2_, p_230318_3_);
   }

   default boolean isUnobstructed(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      return IEntityReader.super.isUnobstructed(p_195585_1_, p_195585_2_);
   }

   default BlockPos getHeightmapPos(Heightmap.Type p_205770_1_, BlockPos p_205770_2_) {
      return IWorldReader.super.getHeightmapPos(p_205770_1_, p_205770_2_);
   }

   DynamicRegistries registryAccess();

   default Optional<RegistryKey<Biome>> getBiomeName(BlockPos p_242406_1_) {
      return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(this.getBiome(p_242406_1_));
   }
}
