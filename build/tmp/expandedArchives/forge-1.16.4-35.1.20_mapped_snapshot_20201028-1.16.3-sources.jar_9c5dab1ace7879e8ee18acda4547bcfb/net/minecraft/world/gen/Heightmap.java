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
   private static final Predicate<BlockState> IS_NOT_AIR = (state) -> {
      return !state.isAir();
   };
   private static final Predicate<BlockState> BLOCKS_MOVEMENT = (state) -> {
      return state.getMaterial().blocksMovement();
   };
   private final BitArray data = new BitArray(9, 256);
   private final Predicate<BlockState> heightLimitPredicate;
   private final IChunk chunk;

   public Heightmap(IChunk chunkIn, Heightmap.Type type) {
      this.heightLimitPredicate = type.getHeightLimitPredicate();
      this.chunk = chunkIn;
   }

   public static void updateChunkHeightmaps(IChunk chunkIn, Set<Heightmap.Type> types) {
      int i = types.size();
      ObjectList<Heightmap> objectlist = new ObjectArrayList<>(i);
      ObjectListIterator<Heightmap> objectlistiterator = objectlist.iterator();
      int j = chunkIn.getTopFilledSegment() + 16;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            for(Heightmap.Type heightmap$type : types) {
               objectlist.add(chunkIn.getHeightmap(heightmap$type));
            }

            for(int i1 = j - 1; i1 >= 0; --i1) {
               blockpos$mutable.setPos(k, i1, l);
               BlockState blockstate = chunkIn.getBlockState(blockpos$mutable);
               if (!blockstate.isIn(Blocks.AIR)) {
                  while(objectlistiterator.hasNext()) {
                     Heightmap heightmap = objectlistiterator.next();
                     if (heightmap.heightLimitPredicate.test(blockstate)) {
                        heightmap.set(k, l, i1 + 1);
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

   public boolean update(int x, int y, int z, BlockState state) {
      int i = this.getHeight(x, z);
      if (y <= i - 2) {
         return false;
      } else {
         if (this.heightLimitPredicate.test(state)) {
            if (y >= i) {
               this.set(x, z, y + 1);
               return true;
            }
         } else if (i - 1 == y) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for(int j = y - 1; j >= 0; --j) {
               blockpos$mutable.setPos(x, j, z);
               if (this.heightLimitPredicate.test(this.chunk.getBlockState(blockpos$mutable))) {
                  this.set(x, z, j + 1);
                  return true;
               }
            }

            this.set(x, z, 0);
            return true;
         }

         return false;
      }
   }

   public int getHeight(int x, int z) {
      return this.getHeight(getDataArrayIndex(x, z));
   }

   private int getHeight(int dataArrayIndex) {
      return this.data.getAt(dataArrayIndex);
   }

   private void set(int x, int z, int value) {
      this.data.setAt(getDataArrayIndex(x, z), value);
   }

   public void setDataArray(long[] dataIn) {
      System.arraycopy(dataIn, 0, this.data.getBackingLongArray(), 0, dataIn.length);
   }

   public long[] getDataArray() {
      return this.data.getBackingLongArray();
   }

   private static int getDataArrayIndex(int x, int z) {
      return x + z * 16;
   }

   public static enum Type implements IStringSerializable {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, Heightmap.IS_NOT_AIR),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.CLIENT, Heightmap.IS_NOT_AIR),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, Heightmap.BLOCKS_MOVEMENT),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, Heightmap.BLOCKS_MOVEMENT),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.CLIENT, (state) -> {
         return state.getMaterial().blocksMovement() || !state.getFluidState().isEmpty();
      }),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, (state) -> {
         return (state.getMaterial().blocksMovement() || !state.getFluidState().isEmpty()) && !(state.getBlock() instanceof LeavesBlock);
      });

      public static final Codec<Heightmap.Type> CODEC = IStringSerializable.createEnumCodec(Heightmap.Type::values, Heightmap.Type::getTypeFromId);
      private final String id;
      private final Heightmap.Usage usage;
      private final Predicate<BlockState> heightLimitPredicate;
      private static final Map<String, Heightmap.Type> BY_ID = Util.make(Maps.newHashMap(), (nameToTypeMap) -> {
         for(Heightmap.Type heightmap$type : values()) {
            nameToTypeMap.put(heightmap$type.id, heightmap$type);
         }

      });

      private Type(String idIn, Heightmap.Usage usageIn, Predicate<BlockState> heightLimitPredicateIn) {
         this.id = idIn;
         this.usage = usageIn;
         this.heightLimitPredicate = heightLimitPredicateIn;
      }

      public String getId() {
         return this.id;
      }

      public boolean isUsageClient() {
         return this.usage == Heightmap.Usage.CLIENT;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isUsageNotWorldgen() {
         return this.usage != Heightmap.Usage.WORLDGEN;
      }

      @Nullable
      public static Heightmap.Type getTypeFromId(String idIn) {
         return BY_ID.get(idIn);
      }

      public Predicate<BlockState> getHeightLimitPredicate() {
         return this.heightLimitPredicate;
      }

      public String getString() {
         return this.id;
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD,
      CLIENT;
   }
}
