package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.SectionDistanceGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public abstract class SectionLightStorage<M extends LightDataMap<M>> extends SectionDistanceGraph {
   protected static final NibbleArray EMPTY_DATA = new NibbleArray();
   private static final Direction[] DIRECTIONS = Direction.values();
   private final LightType layer;
   private final IChunkLightProvider chunkSource;
   protected final LongSet dataSectionSet = new LongOpenHashSet();
   protected final LongSet toMarkNoData = new LongOpenHashSet();
   protected final LongSet toMarkData = new LongOpenHashSet();
   protected volatile M visibleSectionData;
   protected final M updatingSectionData;
   protected final LongSet changedSections = new LongOpenHashSet();
   protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
   protected final Long2ObjectMap<NibbleArray> queuedSections = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
   private final LongSet untrustedSections = new LongOpenHashSet();
   private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
   private final LongSet toRemove = new LongOpenHashSet();
   protected volatile boolean hasToRemove;

   protected SectionLightStorage(LightType p_i51291_1_, IChunkLightProvider p_i51291_2_, M p_i51291_3_) {
      super(3, 16, 256);
      this.layer = p_i51291_1_;
      this.chunkSource = p_i51291_2_;
      this.updatingSectionData = p_i51291_3_;
      this.visibleSectionData = p_i51291_3_.copy();
      this.visibleSectionData.disableCache();
   }

   protected boolean storingLightForSection(long p_215518_1_) {
      return this.getDataLayer(p_215518_1_, true) != null;
   }

   @Nullable
   protected NibbleArray getDataLayer(long p_215520_1_, boolean p_215520_3_) {
      return this.getDataLayer((M)(p_215520_3_ ? this.updatingSectionData : this.visibleSectionData), p_215520_1_);
   }

   @Nullable
   protected NibbleArray getDataLayer(M p_215531_1_, long p_215531_2_) {
      return p_215531_1_.getLayer(p_215531_2_);
   }

   @Nullable
   public NibbleArray getDataLayerData(long p_222858_1_) {
      NibbleArray nibblearray = this.queuedSections.get(p_222858_1_);
      return nibblearray != null ? nibblearray : this.getDataLayer(p_222858_1_, false);
   }

   protected abstract int getLightValue(long p_215525_1_);

   protected int getStoredLevel(long p_215521_1_) {
      long i = SectionPos.blockToSection(p_215521_1_);
      NibbleArray nibblearray = this.getDataLayer(i, true);
      return nibblearray.get(SectionPos.sectionRelative(BlockPos.getX(p_215521_1_)), SectionPos.sectionRelative(BlockPos.getY(p_215521_1_)), SectionPos.sectionRelative(BlockPos.getZ(p_215521_1_)));
   }

   protected void setStoredLevel(long p_215517_1_, int p_215517_3_) {
      long i = SectionPos.blockToSection(p_215517_1_);
      if (this.changedSections.add(i)) {
         this.updatingSectionData.copyDataLayer(i);
      }

      NibbleArray nibblearray = this.getDataLayer(i, true);
      nibblearray.set(SectionPos.sectionRelative(BlockPos.getX(p_215517_1_)), SectionPos.sectionRelative(BlockPos.getY(p_215517_1_)), SectionPos.sectionRelative(BlockPos.getZ(p_215517_1_)), p_215517_3_);

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            for(int l = -1; l <= 1; ++l) {
               this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(p_215517_1_, k, l, j)));
            }
         }
      }

   }

   protected int getLevel(long p_215471_1_) {
      if (p_215471_1_ == Long.MAX_VALUE) {
         return 2;
      } else if (this.dataSectionSet.contains(p_215471_1_)) {
         return 0;
      } else {
         return !this.toRemove.contains(p_215471_1_) && this.updatingSectionData.hasLayer(p_215471_1_) ? 1 : 2;
      }
   }

   protected int getLevelFromSource(long p_215516_1_) {
      if (this.toMarkNoData.contains(p_215516_1_)) {
         return 2;
      } else {
         return !this.dataSectionSet.contains(p_215516_1_) && !this.toMarkData.contains(p_215516_1_) ? 2 : 0;
      }
   }

   protected void setLevel(long p_215476_1_, int p_215476_3_) {
      int i = this.getLevel(p_215476_1_);
      if (i != 0 && p_215476_3_ == 0) {
         this.dataSectionSet.add(p_215476_1_);
         this.toMarkData.remove(p_215476_1_);
      }

      if (i == 0 && p_215476_3_ != 0) {
         this.dataSectionSet.remove(p_215476_1_);
         this.toMarkNoData.remove(p_215476_1_);
      }

      if (i >= 2 && p_215476_3_ != 2) {
         if (this.toRemove.contains(p_215476_1_)) {
            this.toRemove.remove(p_215476_1_);
         } else {
            this.updatingSectionData.setLayer(p_215476_1_, this.createDataLayer(p_215476_1_));
            this.changedSections.add(p_215476_1_);
            this.onNodeAdded(p_215476_1_);

            for(int j = -1; j <= 1; ++j) {
               for(int k = -1; k <= 1; ++k) {
                  for(int l = -1; l <= 1; ++l) {
                     this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(p_215476_1_, k, l, j)));
                  }
               }
            }
         }
      }

      if (i != 2 && p_215476_3_ >= 2) {
         this.toRemove.add(p_215476_1_);
      }

      this.hasToRemove = !this.toRemove.isEmpty();
   }

   protected NibbleArray createDataLayer(long p_215530_1_) {
      NibbleArray nibblearray = this.queuedSections.get(p_215530_1_);
      return nibblearray != null ? nibblearray : new NibbleArray();
   }

   protected void clearQueuedSectionBlocks(LightEngine<?, ?> p_215528_1_, long p_215528_2_) {
      if (p_215528_1_.getQueueSize() < 8192) {
         p_215528_1_.removeIf((p_227469_2_) -> {
            return SectionPos.blockToSection(p_227469_2_) == p_215528_2_;
         });
      } else {
         int i = SectionPos.sectionToBlockCoord(SectionPos.x(p_215528_2_));
         int j = SectionPos.sectionToBlockCoord(SectionPos.y(p_215528_2_));
         int k = SectionPos.sectionToBlockCoord(SectionPos.z(p_215528_2_));

         for(int l = 0; l < 16; ++l) {
            for(int i1 = 0; i1 < 16; ++i1) {
               for(int j1 = 0; j1 < 16; ++j1) {
                  long k1 = BlockPos.asLong(i + l, j + i1, k + j1);
                  p_215528_1_.removeFromQueue(k1);
               }
            }
         }

      }
   }

   protected boolean hasInconsistencies() {
      return this.hasToRemove;
   }

   protected void markNewInconsistencies(LightEngine<M, ?> p_215522_1_, boolean p_215522_2_, boolean p_215522_3_) {
      if (this.hasInconsistencies() || !this.queuedSections.isEmpty()) {
         for(long i : this.toRemove) {
            this.clearQueuedSectionBlocks(p_215522_1_, i);
            NibbleArray nibblearray = this.queuedSections.remove(i);
            NibbleArray nibblearray1 = this.updatingSectionData.removeLayer(i);
            if (this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode(i))) {
               if (nibblearray != null) {
                  this.queuedSections.put(i, nibblearray);
               } else if (nibblearray1 != null) {
                  this.queuedSections.put(i, nibblearray1);
               }
            }
         }

         this.updatingSectionData.clearCache();

         for(long k : this.toRemove) {
            this.onNodeRemoved(k);
         }

         this.toRemove.clear();
         this.hasToRemove = false;

         for(Entry<NibbleArray> entry : this.queuedSections.long2ObjectEntrySet()) {
            long j = entry.getLongKey();
            if (this.storingLightForSection(j)) {
               NibbleArray nibblearray2 = entry.getValue();
               if (this.updatingSectionData.getLayer(j) != nibblearray2) {
                  this.clearQueuedSectionBlocks(p_215522_1_, j);
                  this.updatingSectionData.setLayer(j, nibblearray2);
                  this.changedSections.add(j);
               }
            }
         }

         this.updatingSectionData.clearCache();
         if (!p_215522_3_) {
            for(long l : this.queuedSections.keySet()) {
               this.checkEdgesForSection(p_215522_1_, l);
            }
         } else {
            for(long i1 : this.untrustedSections) {
               this.checkEdgesForSection(p_215522_1_, i1);
            }
         }

         this.untrustedSections.clear();
         ObjectIterator<Entry<NibbleArray>> objectiterator = this.queuedSections.long2ObjectEntrySet().iterator();

         while(objectiterator.hasNext()) {
            Entry<NibbleArray> entry1 = objectiterator.next();
            long j1 = entry1.getLongKey();
            if (this.storingLightForSection(j1)) {
               objectiterator.remove();
            }
         }

      }
   }

   private void checkEdgesForSection(LightEngine<M, ?> p_241538_1_, long p_241538_2_) {
      if (this.storingLightForSection(p_241538_2_)) {
         int i = SectionPos.sectionToBlockCoord(SectionPos.x(p_241538_2_));
         int j = SectionPos.sectionToBlockCoord(SectionPos.y(p_241538_2_));
         int k = SectionPos.sectionToBlockCoord(SectionPos.z(p_241538_2_));

         for(Direction direction : DIRECTIONS) {
            long l = SectionPos.offset(p_241538_2_, direction);
            if (!this.queuedSections.containsKey(l) && this.storingLightForSection(l)) {
               for(int i1 = 0; i1 < 16; ++i1) {
                  for(int j1 = 0; j1 < 16; ++j1) {
                     long k1;
                     long l1;
                     switch(direction) {
                     case DOWN:
                        k1 = BlockPos.asLong(i + j1, j, k + i1);
                        l1 = BlockPos.asLong(i + j1, j - 1, k + i1);
                        break;
                     case UP:
                        k1 = BlockPos.asLong(i + j1, j + 16 - 1, k + i1);
                        l1 = BlockPos.asLong(i + j1, j + 16, k + i1);
                        break;
                     case NORTH:
                        k1 = BlockPos.asLong(i + i1, j + j1, k);
                        l1 = BlockPos.asLong(i + i1, j + j1, k - 1);
                        break;
                     case SOUTH:
                        k1 = BlockPos.asLong(i + i1, j + j1, k + 16 - 1);
                        l1 = BlockPos.asLong(i + i1, j + j1, k + 16);
                        break;
                     case WEST:
                        k1 = BlockPos.asLong(i, j + i1, k + j1);
                        l1 = BlockPos.asLong(i - 1, j + i1, k + j1);
                        break;
                     default:
                        k1 = BlockPos.asLong(i + 16 - 1, j + i1, k + j1);
                        l1 = BlockPos.asLong(i + 16, j + i1, k + j1);
                     }

                     p_241538_1_.checkEdge(k1, l1, p_241538_1_.computeLevelFromNeighbor(k1, l1, p_241538_1_.getLevel(k1)), false);
                     p_241538_1_.checkEdge(l1, k1, p_241538_1_.computeLevelFromNeighbor(l1, k1, p_241538_1_.getLevel(l1)), false);
                  }
               }
            }
         }

      }
   }

   protected void onNodeAdded(long p_215524_1_) {
   }

   protected void onNodeRemoved(long p_215523_1_) {
   }

   protected void enableLightSources(long p_215526_1_, boolean p_215526_3_) {
   }

   public void retainData(long p_223113_1_, boolean p_223113_3_) {
      if (p_223113_3_) {
         this.columnsToRetainQueuedDataFor.add(p_223113_1_);
      } else {
         this.columnsToRetainQueuedDataFor.remove(p_223113_1_);
      }

   }

   protected void queueSectionData(long p_215529_1_, @Nullable NibbleArray p_215529_3_, boolean p_215529_4_) {
      if (p_215529_3_ != null) {
         this.queuedSections.put(p_215529_1_, p_215529_3_);
         if (!p_215529_4_) {
            this.untrustedSections.add(p_215529_1_);
         }
      } else {
         this.queuedSections.remove(p_215529_1_);
      }

   }

   protected void updateSectionStatus(long p_215519_1_, boolean p_215519_3_) {
      boolean flag = this.dataSectionSet.contains(p_215519_1_);
      if (!flag && !p_215519_3_) {
         this.toMarkData.add(p_215519_1_);
         this.checkEdge(Long.MAX_VALUE, p_215519_1_, 0, true);
      }

      if (flag && p_215519_3_) {
         this.toMarkNoData.add(p_215519_1_);
         this.checkEdge(Long.MAX_VALUE, p_215519_1_, 2, false);
      }

   }

   protected void runAllUpdates() {
      if (this.hasWork()) {
         this.runUpdates(Integer.MAX_VALUE);
      }

   }

   protected void swapSectionMap() {
      if (!this.changedSections.isEmpty()) {
         M m = this.updatingSectionData.copy();
         m.disableCache();
         this.visibleSectionData = m;
         this.changedSections.clear();
      }

      if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
         LongIterator longiterator = this.sectionsAffectedByLightUpdates.iterator();

         while(longiterator.hasNext()) {
            long i = longiterator.nextLong();
            this.chunkSource.onLightUpdate(this.layer, SectionPos.of(i));
         }

         this.sectionsAffectedByLightUpdates.clear();
      }

   }
}
