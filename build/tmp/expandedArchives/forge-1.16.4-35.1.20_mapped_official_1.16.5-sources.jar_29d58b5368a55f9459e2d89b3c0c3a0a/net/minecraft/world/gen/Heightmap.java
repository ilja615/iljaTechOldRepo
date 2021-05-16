package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.BitArray;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Heightmap {
   private static final Predicate<BlockState> NOT_AIR = (p_222688_0_) -> {
      return !p_222688_0_.isAir();
   };
   private static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING = (p_222689_0_) -> {
      return p_222689_0_.getMaterial().blocksMotion();
   };
   private final BitArray data = new BitArray(9, 256);
   private final Predicate<BlockState> isOpaque;
   private final IChunk chunk;

   public Heightmap(IChunk p_i48695_1_, Heightmap.Type p_i48695_2_) {
      this.isOpaque = p_i48695_2_.isOpaque();
      this.chunk = p_i48695_1_;
   }

   public static void primeHeightmaps(IChunk p_222690_0_, Set<Heightmap.Type> p_222690_1_) {
      int i = p_222690_1_.size();
      ObjectList<Heightmap> objectlist = new ObjectArrayList<>(i);
      ObjectListIterator<Heightmap> objectlistiterator = objectlist.iterator();
      int j = p_222690_0_.getHighestSectionPosition() + 16;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            for(Heightmap.Type heightmap$type : p_222690_1_) {
               objectlist.add(p_222690_0_.getOrCreateHeightmapUnprimed(heightmap$type));
            }

            for(int i1 = j - 1; i1 >= 0; --i1) {
               blockpos$mutable.set(k, i1, l);
               BlockState blockstate = p_222690_0_.getBlockState(blockpos$mutable);
               if (!blockstate.is(Blocks.AIR)) {
                  while(objectlistiterator.hasNext()) {
                     Heightmap heightmap = objectlistiterator.next();
                     if (heightmap.isOpaque.test(blockstate)) {
                        heightmap.setHeight(k, l, i1 + 1);
                        objectlistiterator.remove();
                     }
                  }

                  if (objectlist.isEmpty()) {
                     break;
                  }

                  objectlistiterator.back(i);
               }
            }
         }
      }

   }

   public boolean update(int p_202270_1_, int p_202270_2_, int p_202270_3_, BlockState p_202270_4_) {
      int i = this.getFirstAvailable(p_202270_1_, p_202270_3_);
      if (p_202270_2_ <= i - 2) {
         return false;
      } else {
         if (this.isOpaque.test(p_202270_4_)) {
            if (p_202270_2_ >= i) {
               this.setHeight(p_202270_1_, p_202270_3_, p_202270_2_ + 1);
               return true;
            }
         } else if (i - 1 == p_202270_2_) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for(int j = p_202270_2_ - 1; j >= 0; --j) {
               blockpos$mutable.set(p_202270_1_, j, p_202270_3_);
               if (this.isOpaque.test(this.chunk.getBlockState(blockpos$mutable))) {
                  this.setHeight(p_202270_1_, p_202270_3_, j + 1);
                  return true;
               }
            }

            this.setHeight(p_202270_1_, p_202270_3_, 0);
            return true;
         }

         return false;
      }
   }

   public int getFirstAvailable(int p_202273_1_, int p_202273_2_) {
      return this.getFirstAvailable(getIndex(p_202273_1_, p_202273_2_));
   }

   private int getFirstAvailable(int p_202274_1_) {
      return this.data.get(p_202274_1_);
   }

   private void setHeight(int p_202272_1_, int p_202272_2_, int p_202272_3_) {
      this.data.set(getIndex(p_202272_1_, p_202272_2_), p_202272_3_);
   }

   public void setRawData(long[] p_202268_1_) {
      System.arraycopy(p_202268_1_, 0, this.data.getRaw(), 0, p_202268_1_.length);
   }

   public long[] getRawData() {
      return this.data.getRaw();
   }

   private static int getIndex(int p_202267_0_, int p_202267_1_) {
      return p_202267_0_ + p_202267_1_ * 16;
   }

   public static enum Type implements IStringSerializable {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, Heightmap.NOT_AIR),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.CLIENT, Heightmap.NOT_AIR),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, Heightmap.MATERIAL_MOTION_BLOCKING),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, Heightmap.MATERIAL_MOTION_BLOCKING),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.CLIENT, (p_222680_0_) -> {
         return p_222680_0_.getMaterial().blocksMotion() || !p_222680_0_.getFluidState().isEmpty();
      }),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, (p_222682_0_) -> {
         return (p_222682_0_.getMaterial().blocksMotion() || !p_222682_0_.getFluidState().isEmpty()) && !(p_222682_0_.getBlock() instanceof LeavesBlock);
      });

      public static final Codec<Heightmap.Type> CODEC = IStringSerializable.fromEnum(Heightmap.Type::values, Heightmap.Type::getFromKey);
      private final String serializationKey;
      private final Heightmap.Usage usage;
      private final Predicate<BlockState> isOpaque;
      private static final Map<String, Heightmap.Type> REVERSE_LOOKUP = Util.make(Maps.newHashMap(), (p_222679_0_) -> {
         for(Heightmap.Type heightmap$type : values()) {
            p_222679_0_.put(heightmap$type.serializationKey, heightmap$type);
         }

      });

      private Type(String p_i50821_3_, Heightmap.Usage p_i50821_4_, Predicate<BlockState> p_i50821_5_) {
         this.serializationKey = p_i50821_3_;
         this.usage = p_i50821_4_;
         this.isOpaque = p_i50821_5_;
      }

      public String getSerializationKey() {
         return this.serializationKey;
      }

      public boolean sendToClient() {
         return this.usage == Heightmap.Usage.CLIENT;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean keepAfterWorldgen() {
         return this.usage != Heightmap.Usage.WORLDGEN;
      }

      @Nullable
      public static Heightmap.Type getFromKey(String p_203501_0_) {
         return REVERSE_LOOKUP.get(p_203501_0_);
      }

      public Predicate<BlockState> isOpaque() {
         return this.isOpaque;
      }

      public String getSerializedName() {
         return this.serializationKey;
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD,
      CLIENT;
   }
}
