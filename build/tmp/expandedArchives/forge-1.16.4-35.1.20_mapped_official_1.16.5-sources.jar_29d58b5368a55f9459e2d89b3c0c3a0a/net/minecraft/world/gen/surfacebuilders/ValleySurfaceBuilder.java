package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;

public abstract class ValleySurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   private long seed;
   private ImmutableMap<BlockState, OctavesNoiseGenerator> floorNoises = ImmutableMap.of();
   private ImmutableMap<BlockState, OctavesNoiseGenerator> ceilingNoises = ImmutableMap.of();
   private OctavesNoiseGenerator patchNoise;

   public ValleySurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232130_1_) {
      super(p_i232130_1_);
   }

   public void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int i = p_205610_11_ + 1;
      int j = p_205610_4_ & 15;
      int k = p_205610_5_ & 15;
      int l = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      int i1 = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      double d0 = 0.03125D;
      boolean flag = this.patchNoise.getValue((double)p_205610_4_ * 0.03125D, 109.0D, (double)p_205610_5_ * 0.03125D) * 75.0D + p_205610_1_.nextDouble() > 0.0D;
      BlockState blockstate = this.ceilingNoises.entrySet().stream().max(Comparator.comparing((p_237176_3_) -> {
         return p_237176_3_.getValue().getValue((double)p_205610_4_, (double)p_205610_11_, (double)p_205610_5_);
      })).get().getKey();
      BlockState blockstate1 = this.floorNoises.entrySet().stream().max(Comparator.comparing((p_237174_3_) -> {
         return p_237174_3_.getValue().getValue((double)p_205610_4_, (double)p_205610_11_, (double)p_205610_5_);
      })).get().getKey();
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      BlockState blockstate2 = p_205610_2_.getBlockState(blockpos$mutable.set(j, 128, k));

      for(int j1 = 127; j1 >= 0; --j1) {
         blockpos$mutable.set(j, j1, k);
         BlockState blockstate3 = p_205610_2_.getBlockState(blockpos$mutable);
         if (blockstate2.is(p_205610_9_.getBlock()) && (blockstate3.isAir() || blockstate3 == p_205610_10_)) {
            for(int k1 = 0; k1 < l; ++k1) {
               blockpos$mutable.move(Direction.UP);
               if (!p_205610_2_.getBlockState(blockpos$mutable).is(p_205610_9_.getBlock())) {
                  break;
               }

               p_205610_2_.setBlockState(blockpos$mutable, blockstate, false);
            }

            blockpos$mutable.set(j, j1, k);
         }

         if ((blockstate2.isAir() || blockstate2 == p_205610_10_) && blockstate3.is(p_205610_9_.getBlock())) {
            for(int l1 = 0; l1 < i1 && p_205610_2_.getBlockState(blockpos$mutable).is(p_205610_9_.getBlock()); ++l1) {
               if (flag && j1 >= i - 4 && j1 <= i + 1) {
                  p_205610_2_.setBlockState(blockpos$mutable, this.getPatchBlockState(), false);
               } else {
                  p_205610_2_.setBlockState(blockpos$mutable, blockstate1, false);
               }

               blockpos$mutable.move(Direction.DOWN);
            }
         }

         blockstate2 = blockstate3;
      }

   }

   public void initNoise(long p_205548_1_) {
      if (this.seed != p_205548_1_ || this.patchNoise == null || this.floorNoises.isEmpty() || this.ceilingNoises.isEmpty()) {
         this.floorNoises = initPerlinNoises(this.getFloorBlockStates(), p_205548_1_);
         this.ceilingNoises = initPerlinNoises(this.getCeilingBlockStates(), p_205548_1_ + (long)this.floorNoises.size());
         this.patchNoise = new OctavesNoiseGenerator(new SharedSeedRandom(p_205548_1_ + (long)this.floorNoises.size() + (long)this.ceilingNoises.size()), ImmutableList.of(0));
      }

      this.seed = p_205548_1_;
   }

   private static ImmutableMap<BlockState, OctavesNoiseGenerator> initPerlinNoises(ImmutableList<BlockState> p_237175_0_, long p_237175_1_) {
      Builder<BlockState, OctavesNoiseGenerator> builder = new Builder<>();

      for(BlockState blockstate : p_237175_0_) {
         builder.put(blockstate, new OctavesNoiseGenerator(new SharedSeedRandom(p_237175_1_), ImmutableList.of(-4)));
         ++p_237175_1_;
      }

      return builder.build();
   }

   protected abstract ImmutableList<BlockState> getFloorBlockStates();

   protected abstract ImmutableList<BlockState> getCeilingBlockStates();

   protected abstract BlockState getPatchBlockState();
}
