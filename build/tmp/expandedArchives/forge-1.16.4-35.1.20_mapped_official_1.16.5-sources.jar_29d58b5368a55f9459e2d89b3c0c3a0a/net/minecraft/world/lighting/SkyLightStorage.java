package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public class SkyLightStorage extends SectionLightStorage<SkyLightStorage.StorageMap> {
   private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private final LongSet sectionsWithSources = new LongOpenHashSet();
   private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
   private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
   private final LongSet columnsWithSkySources = new LongOpenHashSet();
   private volatile boolean hasSourceInconsistencies;

   protected SkyLightStorage(IChunkLightProvider p_i51288_1_) {
      super(LightType.SKY, p_i51288_1_, new SkyLightStorage.StorageMap(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
   }

   protected int getLightValue(long p_215525_1_) {
      long i = SectionPos.blockToSection(p_215525_1_);
      int j = SectionPos.y(i);
      SkyLightStorage.StorageMap skylightstorage$storagemap = this.visibleSectionData;
      int k = skylightstorage$storagemap.topSections.get(SectionPos.getZeroNode(i));
      if (k != skylightstorage$storagemap.currentLowestY && j < k) {
         NibbleArray nibblearray = this.getDataLayer(skylightstorage$storagemap, i);
         if (nibblearray == null) {
            for(p_215525_1_ = BlockPos.getFlatIndex(p_215525_1_); nibblearray == null; nibblearray = this.getDataLayer(skylightstorage$storagemap, i)) {
               i = SectionPos.offset(i, Direction.UP);
               ++j;
               if (j >= k) {
                  return 15;
               }

               p_215525_1_ = BlockPos.offset(p_215525_1_, 0, 16, 0);
            }
         }

         return nibblearray.get(SectionPos.sectionRelative(BlockPos.getX(p_215525_1_)), SectionPos.sectionRelative(BlockPos.getY(p_215525_1_)), SectionPos.sectionRelative(BlockPos.getZ(p_215525_1_)));
      } else {
         return 15;
      }
   }

   protected void onNodeAdded(long p_215524_1_) {
      int i = SectionPos.y(p_215524_1_);
      if ((this.updatingSectionData).currentLowestY > i) {
         (this.updatingSectionData).currentLowestY = i;
         (this.updatingSectionData).topSections.defaultReturnValue((this.updatingSectionData).currentLowestY);
      }

      long j = SectionPos.getZeroNode(p_215524_1_);
      int k = (this.updatingSectionData).topSections.get(j);
      if (k < i + 1) {
         (this.updatingSectionData).topSections.put(j, i + 1);
         if (this.columnsWithSkySources.contains(j)) {
            this.queueAddSource(p_215524_1_);
            if (k > (this.updatingSectionData).currentLowestY) {
               long l = SectionPos.asLong(SectionPos.x(p_215524_1_), k - 1, SectionPos.z(p_215524_1_));
               this.queueRemoveSource(l);
            }

            this.recheckInconsistencyFlag();
         }
      }

   }

   private void queueRemoveSource(long p_223403_1_) {
      this.sectionsToRemoveSourcesFrom.add(p_223403_1_);
      this.sectionsToAddSourcesTo.remove(p_223403_1_);
   }

   private void queueAddSource(long p_223404_1_) {
      this.sectionsToAddSourcesTo.add(p_223404_1_);
      this.sectionsToRemoveSourcesFrom.remove(p_223404_1_);
   }

   private void recheckInconsistencyFlag() {
      this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
   }

   protected void onNodeRemoved(long p_215523_1_) {
      long i = SectionPos.getZeroNode(p_215523_1_);
      boolean flag = this.columnsWithSkySources.contains(i);
      if (flag) {
         this.queueRemoveSource(p_215523_1_);
      }

      int j = SectionPos.y(p_215523_1_);
      if ((this.updatingSectionData).topSections.get(i) == j + 1) {
         long k;
         for(k = p_215523_1_; !this.storingLightForSection(k) && this.hasSectionsBelow(j); k = SectionPos.offset(k, Direction.DOWN)) {
            --j;
         }

         if (this.storingLightForSection(k)) {
            (this.updatingSectionData).topSections.put(i, j + 1);
            if (flag) {
               this.queueAddSource(k);
            }
         } else {
            (this.updatingSectionData).topSections.remove(i);
         }
      }

      if (flag) {
         this.recheckInconsistencyFlag();
      }

   }

   protected void enableLightSources(long p_215526_1_, boolean p_215526_3_) {
      this.runAllUpdates();
      if (p_215526_3_ && this.columnsWithSkySources.add(p_215526_1_)) {
         int i = (this.updatingSectionData).topSections.get(p_215526_1_);
         if (i != (this.updatingSectionData).currentLowestY) {
            long j = SectionPos.asLong(SectionPos.x(p_215526_1_), i - 1, SectionPos.z(p_215526_1_));
            this.queueAddSource(j);
            this.recheckInconsistencyFlag();
         }
      } else if (!p_215526_3_) {
         this.columnsWithSkySources.remove(p_215526_1_);
      }

   }

   protected boolean hasInconsistencies() {
      return super.hasInconsistencies() || this.hasSourceInconsistencies;
   }

   protected NibbleArray createDataLayer(long p_215530_1_) {
      NibbleArray nibblearray = this.queuedSections.get(p_215530_1_);
      if (nibblearray != null) {
         return nibblearray;
      } else {
         long i = SectionPos.offset(p_215530_1_, Direction.UP);
         int j = (this.updatingSectionData).topSections.get(SectionPos.getZeroNode(p_215530_1_));
         if (j != (this.updatingSectionData).currentLowestY && SectionPos.y(i) < j) {
            NibbleArray nibblearray1;
            while((nibblearray1 = this.getDataLayer(i, true)) == null) {
               i = SectionPos.offset(i, Direction.UP);
            }

            return new NibbleArray((new NibbleArrayRepeater(nibblearray1, 0)).getData());
         } else {
            return new NibbleArray();
         }
      }
   }

   protected void markNewInconsistencies(LightEngine<SkyLightStorage.StorageMap, ?> p_215522_1_, boolean p_215522_2_, boolean p_215522_3_) {
      super.markNewInconsistencies(p_215522_1_, p_215522_2_, p_215522_3_);
      if (p_215522_2_) {
         if (!this.sectionsToAddSourcesTo.isEmpty()) {
            for(long i : this.sectionsToAddSourcesTo) {
               int j = this.getLevel(i);
               if (j != 2 && !this.sectionsToRemoveSourcesFrom.contains(i) && this.sectionsWithSources.add(i)) {
                  if (j == 1) {
                     this.clearQueuedSectionBlocks(p_215522_1_, i);
                     if (this.changedSections.add(i)) {
                        this.updatingSectionData.copyDataLayer(i);
                     }

                     Arrays.fill(this.getDataLayer(i, true).getData(), (byte)-1);
                     int i3 = SectionPos.sectionToBlockCoord(SectionPos.x(i));
                     int k3 = SectionPos.sectionToBlockCoord(SectionPos.y(i));
                     int i4 = SectionPos.sectionToBlockCoord(SectionPos.z(i));

                     for(Direction direction : HORIZONTALS) {
                        long j1 = SectionPos.offset(i, direction);
                        if ((this.sectionsToRemoveSourcesFrom.contains(j1) || !this.sectionsWithSources.contains(j1) && !this.sectionsToAddSourcesTo.contains(j1)) && this.storingLightForSection(j1)) {
                           for(int k1 = 0; k1 < 16; ++k1) {
                              for(int l1 = 0; l1 < 16; ++l1) {
                                 long i2;
                                 long j2;
                                 switch(direction) {
                                 case NORTH:
                                    i2 = BlockPos.asLong(i3 + k1, k3 + l1, i4);
                                    j2 = BlockPos.asLong(i3 + k1, k3 + l1, i4 - 1);
                                    break;
                                 case SOUTH:
                                    i2 = BlockPos.asLong(i3 + k1, k3 + l1, i4 + 16 - 1);
                                    j2 = BlockPos.asLong(i3 + k1, k3 + l1, i4 + 16);
                                    break;
                                 case WEST:
                                    i2 = BlockPos.asLong(i3, k3 + k1, i4 + l1);
                                    j2 = BlockPos.asLong(i3 - 1, k3 + k1, i4 + l1);
                                    break;
                                 default:
                                    i2 = BlockPos.asLong(i3 + 16 - 1, k3 + k1, i4 + l1);
                                    j2 = BlockPos.asLong(i3 + 16, k3 + k1, i4 + l1);
                                 }

                                 p_215522_1_.checkEdge(i2, j2, p_215522_1_.computeLevelFromNeighbor(i2, j2, 0), true);
                              }
                           }
                        }
                     }

                     for(int j4 = 0; j4 < 16; ++j4) {
                        for(int k4 = 0; k4 < 16; ++k4) {
                           long l4 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(i)) + j4, SectionPos.sectionToBlockCoord(SectionPos.y(i)), SectionPos.sectionToBlockCoord(SectionPos.z(i)) + k4);
                           long i5 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(i)) + j4, SectionPos.sectionToBlockCoord(SectionPos.y(i)) - 1, SectionPos.sectionToBlockCoord(SectionPos.z(i)) + k4);
                           p_215522_1_.checkEdge(l4, i5, p_215522_1_.computeLevelFromNeighbor(l4, i5, 0), true);
                        }
                     }
                  } else {
                     for(int k = 0; k < 16; ++k) {
                        for(int l = 0; l < 16; ++l) {
                           long i1 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(i)) + k, SectionPos.sectionToBlockCoord(SectionPos.y(i)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(i)) + l);
                           p_215522_1_.checkEdge(Long.MAX_VALUE, i1, 0, true);
                        }
                     }
                  }
               }
            }
         }

         this.sectionsToAddSourcesTo.clear();
         if (!this.sectionsToRemoveSourcesFrom.isEmpty()) {
            for(long k2 : this.sectionsToRemoveSourcesFrom) {
               if (this.sectionsWithSources.remove(k2) && this.storingLightForSection(k2)) {
                  for(int l2 = 0; l2 < 16; ++l2) {
                     for(int j3 = 0; j3 < 16; ++j3) {
                        long l3 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(k2)) + l2, SectionPos.sectionToBlockCoord(SectionPos.y(k2)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(k2)) + j3);
                        p_215522_1_.checkEdge(Long.MAX_VALUE, l3, 15, false);
                     }
                  }
               }
            }
         }

         this.sectionsToRemoveSourcesFrom.clear();
         this.hasSourceInconsistencies = false;
      }
   }

   protected boolean hasSectionsBelow(int p_215550_1_) {
      return p_215550_1_ >= (this.updatingSectionData).currentLowestY;
   }

   protected boolean hasLightSource(long p_215551_1_) {
      int i = BlockPos.getY(p_215551_1_);
      if ((i & 15) != 15) {
         return false;
      } else {
         long j = SectionPos.blockToSection(p_215551_1_);
         long k = SectionPos.getZeroNode(j);
         if (!this.columnsWithSkySources.contains(k)) {
            return false;
         } else {
            int l = (this.updatingSectionData).topSections.get(k);
            return SectionPos.sectionToBlockCoord(l) == i + 16;
         }
      }
   }

   protected boolean isAboveData(long p_215549_1_) {
      long i = SectionPos.getZeroNode(p_215549_1_);
      int j = (this.updatingSectionData).topSections.get(i);
      return j == (this.updatingSectionData).currentLowestY || SectionPos.y(p_215549_1_) >= j;
   }

   protected boolean lightOnInSection(long p_215548_1_) {
      long i = SectionPos.getZeroNode(p_215548_1_);
      return this.columnsWithSkySources.contains(i);
   }

   public static final class StorageMap extends LightDataMap<SkyLightStorage.StorageMap> {
      private int currentLowestY;
      private final Long2IntOpenHashMap topSections;

      public StorageMap(Long2ObjectOpenHashMap<NibbleArray> p_i50496_1_, Long2IntOpenHashMap p_i50496_2_, int p_i50496_3_) {
         super(p_i50496_1_);
         this.topSections = p_i50496_2_;
         p_i50496_2_.defaultReturnValue(p_i50496_3_);
         this.currentLowestY = p_i50496_3_;
      }

      public SkyLightStorage.StorageMap copy() {
         return new SkyLightStorage.StorageMap(this.map.clone(), this.topSections.clone(), this.currentLowestY);
      }
   }
}
