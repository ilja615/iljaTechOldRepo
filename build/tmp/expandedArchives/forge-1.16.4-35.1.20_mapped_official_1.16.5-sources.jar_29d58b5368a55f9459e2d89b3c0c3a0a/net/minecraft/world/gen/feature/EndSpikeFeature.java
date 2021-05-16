package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class EndSpikeFeature extends Feature<EndSpikeFeatureConfig> {
   private static final LoadingCache<Long, List<EndSpikeFeature.EndSpike>> SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new EndSpikeFeature.EndSpikeCacheLoader());

   public EndSpikeFeature(Codec<EndSpikeFeatureConfig> p_i231994_1_) {
      super(p_i231994_1_);
   }

   public static List<EndSpikeFeature.EndSpike> getSpikesForLevel(ISeedReader p_236356_0_) {
      Random random = new Random(p_236356_0_.getSeed());
      long i = random.nextLong() & 65535L;
      return SPIKE_CACHE.getUnchecked(i);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, EndSpikeFeatureConfig p_241855_5_) {
      List<EndSpikeFeature.EndSpike> list = p_241855_5_.getSpikes();
      if (list.isEmpty()) {
         list = getSpikesForLevel(p_241855_1_);
      }

      for(EndSpikeFeature.EndSpike endspikefeature$endspike : list) {
         if (endspikefeature$endspike.isCenterWithinChunk(p_241855_4_)) {
            this.placeSpike(p_241855_1_, p_241855_3_, p_241855_5_, endspikefeature$endspike);
         }
      }

      return true;
   }

   private void placeSpike(IServerWorld p_214553_1_, Random p_214553_2_, EndSpikeFeatureConfig p_214553_3_, EndSpikeFeature.EndSpike p_214553_4_) {
      int i = p_214553_4_.getRadius();

      for(BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(p_214553_4_.getCenterX() - i, 0, p_214553_4_.getCenterZ() - i), new BlockPos(p_214553_4_.getCenterX() + i, p_214553_4_.getHeight() + 10, p_214553_4_.getCenterZ() + i))) {
         if (blockpos.distSqr((double)p_214553_4_.getCenterX(), (double)blockpos.getY(), (double)p_214553_4_.getCenterZ(), false) <= (double)(i * i + 1) && blockpos.getY() < p_214553_4_.getHeight()) {
            this.setBlock(p_214553_1_, blockpos, Blocks.OBSIDIAN.defaultBlockState());
         } else if (blockpos.getY() > 65) {
            this.setBlock(p_214553_1_, blockpos, Blocks.AIR.defaultBlockState());
         }
      }

      if (p_214553_4_.isGuarded()) {
         int j1 = -2;
         int k1 = 2;
         int j = 3;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = -2; k <= 2; ++k) {
            for(int l = -2; l <= 2; ++l) {
               for(int i1 = 0; i1 <= 3; ++i1) {
                  boolean flag = MathHelper.abs(k) == 2;
                  boolean flag1 = MathHelper.abs(l) == 2;
                  boolean flag2 = i1 == 3;
                  if (flag || flag1 || flag2) {
                     boolean flag3 = k == -2 || k == 2 || flag2;
                     boolean flag4 = l == -2 || l == 2 || flag2;
                     BlockState blockstate = Blocks.IRON_BARS.defaultBlockState().setValue(PaneBlock.NORTH, Boolean.valueOf(flag3 && l != -2)).setValue(PaneBlock.SOUTH, Boolean.valueOf(flag3 && l != 2)).setValue(PaneBlock.WEST, Boolean.valueOf(flag4 && k != -2)).setValue(PaneBlock.EAST, Boolean.valueOf(flag4 && k != 2));
                     this.setBlock(p_214553_1_, blockpos$mutable.set(p_214553_4_.getCenterX() + k, p_214553_4_.getHeight() + i1, p_214553_4_.getCenterZ() + l), blockstate);
                  }
               }
            }
         }
      }

      EnderCrystalEntity endercrystalentity = EntityType.END_CRYSTAL.create(p_214553_1_.getLevel());
      endercrystalentity.setBeamTarget(p_214553_3_.getCrystalBeamTarget());
      endercrystalentity.setInvulnerable(p_214553_3_.isCrystalInvulnerable());
      endercrystalentity.moveTo((double)p_214553_4_.getCenterX() + 0.5D, (double)(p_214553_4_.getHeight() + 1), (double)p_214553_4_.getCenterZ() + 0.5D, p_214553_2_.nextFloat() * 360.0F, 0.0F);
      p_214553_1_.addFreshEntity(endercrystalentity);
      this.setBlock(p_214553_1_, new BlockPos(p_214553_4_.getCenterX(), p_214553_4_.getHeight(), p_214553_4_.getCenterZ()), Blocks.BEDROCK.defaultBlockState());
   }

   public static class EndSpike {
      public static final Codec<EndSpikeFeature.EndSpike> CODEC = RecordCodecBuilder.create((p_236359_0_) -> {
         return p_236359_0_.group(Codec.INT.fieldOf("centerX").orElse(0).forGetter((p_236363_0_) -> {
            return p_236363_0_.centerX;
         }), Codec.INT.fieldOf("centerZ").orElse(0).forGetter((p_236362_0_) -> {
            return p_236362_0_.centerZ;
         }), Codec.INT.fieldOf("radius").orElse(0).forGetter((p_236361_0_) -> {
            return p_236361_0_.radius;
         }), Codec.INT.fieldOf("height").orElse(0).forGetter((p_236360_0_) -> {
            return p_236360_0_.height;
         }), Codec.BOOL.fieldOf("guarded").orElse(false).forGetter((p_236358_0_) -> {
            return p_236358_0_.guarded;
         })).apply(p_236359_0_, EndSpikeFeature.EndSpike::new);
      });
      private final int centerX;
      private final int centerZ;
      private final int radius;
      private final int height;
      private final boolean guarded;
      private final AxisAlignedBB topBoundingBox;

      public EndSpike(int p_i47020_1_, int p_i47020_2_, int p_i47020_3_, int p_i47020_4_, boolean p_i47020_5_) {
         this.centerX = p_i47020_1_;
         this.centerZ = p_i47020_2_;
         this.radius = p_i47020_3_;
         this.height = p_i47020_4_;
         this.guarded = p_i47020_5_;
         this.topBoundingBox = new AxisAlignedBB((double)(p_i47020_1_ - p_i47020_3_), 0.0D, (double)(p_i47020_2_ - p_i47020_3_), (double)(p_i47020_1_ + p_i47020_3_), 256.0D, (double)(p_i47020_2_ + p_i47020_3_));
      }

      public boolean isCenterWithinChunk(BlockPos p_186154_1_) {
         return p_186154_1_.getX() >> 4 == this.centerX >> 4 && p_186154_1_.getZ() >> 4 == this.centerZ >> 4;
      }

      public int getCenterX() {
         return this.centerX;
      }

      public int getCenterZ() {
         return this.centerZ;
      }

      public int getRadius() {
         return this.radius;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean isGuarded() {
         return this.guarded;
      }

      public AxisAlignedBB getTopBoundingBox() {
         return this.topBoundingBox;
      }
   }

   static class EndSpikeCacheLoader extends CacheLoader<Long, List<EndSpikeFeature.EndSpike>> {
      private EndSpikeCacheLoader() {
      }

      public List<EndSpikeFeature.EndSpike> load(Long p_load_1_) {
         List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
         Collections.shuffle(list, new Random(p_load_1_));
         List<EndSpikeFeature.EndSpike> list1 = Lists.newArrayList();

         for(int i = 0; i < 10; ++i) {
            int j = MathHelper.floor(42.0D * Math.cos(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
            int k = MathHelper.floor(42.0D * Math.sin(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
            int l = list.get(i);
            int i1 = 2 + l / 3;
            int j1 = 76 + l * 3;
            boolean flag = l == 1 || l == 2;
            list1.add(new EndSpikeFeature.EndSpike(j, k, i1, j1, flag));
         }

         return list1;
      }
   }
}
