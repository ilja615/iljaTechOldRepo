package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class LegacyStructureDataUtil {
   private static final Map<String, String> CURRENT_TO_LEGACY_MAP = Util.make(Maps.newHashMap(), (p_208213_0_) -> {
      p_208213_0_.put("Village", "Village");
      p_208213_0_.put("Mineshaft", "Mineshaft");
      p_208213_0_.put("Mansion", "Mansion");
      p_208213_0_.put("Igloo", "Temple");
      p_208213_0_.put("Desert_Pyramid", "Temple");
      p_208213_0_.put("Jungle_Pyramid", "Temple");
      p_208213_0_.put("Swamp_Hut", "Temple");
      p_208213_0_.put("Stronghold", "Stronghold");
      p_208213_0_.put("Monument", "Monument");
      p_208213_0_.put("Fortress", "Fortress");
      p_208213_0_.put("EndCity", "EndCity");
   });
   private static final Map<String, String> LEGACY_TO_CURRENT_MAP = Util.make(Maps.newHashMap(), (p_208215_0_) -> {
      p_208215_0_.put("Iglu", "Igloo");
      p_208215_0_.put("TeDP", "Desert_Pyramid");
      p_208215_0_.put("TeJP", "Jungle_Pyramid");
      p_208215_0_.put("TeSH", "Swamp_Hut");
   });
   private final boolean hasLegacyData;
   private final Map<String, Long2ObjectMap<CompoundNBT>> dataMap = Maps.newHashMap();
   private final Map<String, StructureIndexesSavedData> indexMap = Maps.newHashMap();
   private final List<String> legacyKeys;
   private final List<String> currentKeys;

   public LegacyStructureDataUtil(@Nullable DimensionSavedDataManager p_i51349_1_, List<String> p_i51349_2_, List<String> p_i51349_3_) {
      this.legacyKeys = p_i51349_2_;
      this.currentKeys = p_i51349_3_;
      this.populateCaches(p_i51349_1_);
      boolean flag = false;

      for(String s : this.currentKeys) {
         flag |= this.dataMap.get(s) != null;
      }

      this.hasLegacyData = flag;
   }

   public void removeIndex(long p_208216_1_) {
      for(String s : this.legacyKeys) {
         StructureIndexesSavedData structureindexessaveddata = this.indexMap.get(s);
         if (structureindexessaveddata != null && structureindexessaveddata.hasUnhandledIndex(p_208216_1_)) {
            structureindexessaveddata.removeIndex(p_208216_1_);
            structureindexessaveddata.setDirty();
         }
      }

   }

   public CompoundNBT updateFromLegacy(CompoundNBT p_212181_1_) {
      CompoundNBT compoundnbt = p_212181_1_.getCompound("Level");
      ChunkPos chunkpos = new ChunkPos(compoundnbt.getInt("xPos"), compoundnbt.getInt("zPos"));
      if (this.isUnhandledStructureStart(chunkpos.x, chunkpos.z)) {
         p_212181_1_ = this.updateStructureStart(p_212181_1_, chunkpos);
      }

      CompoundNBT compoundnbt1 = compoundnbt.getCompound("Structures");
      CompoundNBT compoundnbt2 = compoundnbt1.getCompound("References");

      for(String s : this.currentKeys) {
         Structure<?> structure = Structure.STRUCTURES_REGISTRY.get(s.toLowerCase(Locale.ROOT));
         if (!compoundnbt2.contains(s, 12) && structure != null) {
            int i = 8;
            LongList longlist = new LongArrayList();

            for(int j = chunkpos.x - 8; j <= chunkpos.x + 8; ++j) {
               for(int k = chunkpos.z - 8; k <= chunkpos.z + 8; ++k) {
                  if (this.hasLegacyStart(j, k, s)) {
                     longlist.add(ChunkPos.asLong(j, k));
                  }
               }
            }

            compoundnbt2.putLongArray(s, longlist);
         }
      }

      compoundnbt1.put("References", compoundnbt2);
      compoundnbt.put("Structures", compoundnbt1);
      p_212181_1_.put("Level", compoundnbt);
      return p_212181_1_;
   }

   private boolean hasLegacyStart(int p_208211_1_, int p_208211_2_, String p_208211_3_) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         return this.dataMap.get(p_208211_3_) != null && this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(p_208211_3_)).hasStartIndex(ChunkPos.asLong(p_208211_1_, p_208211_2_));
      }
   }

   private boolean isUnhandledStructureStart(int p_208209_1_, int p_208209_2_) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         for(String s : this.currentKeys) {
            if (this.dataMap.get(s) != null && this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(s)).hasUnhandledIndex(ChunkPos.asLong(p_208209_1_, p_208209_2_))) {
               return true;
            }
         }

         return false;
      }
   }

   private CompoundNBT updateStructureStart(CompoundNBT p_212182_1_, ChunkPos p_212182_2_) {
      CompoundNBT compoundnbt = p_212182_1_.getCompound("Level");
      CompoundNBT compoundnbt1 = compoundnbt.getCompound("Structures");
      CompoundNBT compoundnbt2 = compoundnbt1.getCompound("Starts");

      for(String s : this.currentKeys) {
         Long2ObjectMap<CompoundNBT> long2objectmap = this.dataMap.get(s);
         if (long2objectmap != null) {
            long i = p_212182_2_.toLong();
            if (this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(s)).hasUnhandledIndex(i)) {
               CompoundNBT compoundnbt3 = long2objectmap.get(i);
               if (compoundnbt3 != null) {
                  compoundnbt2.put(s, compoundnbt3);
               }
            }
         }
      }

      compoundnbt1.put("Starts", compoundnbt2);
      compoundnbt.put("Structures", compoundnbt1);
      p_212182_1_.put("Level", compoundnbt);
      return p_212182_1_;
   }

   private void populateCaches(@Nullable DimensionSavedDataManager p_212184_1_) {
      if (p_212184_1_ != null) {
         for(String s : this.legacyKeys) {
            CompoundNBT compoundnbt = new CompoundNBT();

            try {
               compoundnbt = p_212184_1_.readTagFromDisk(s, 1493).getCompound("data").getCompound("Features");
               if (compoundnbt.isEmpty()) {
                  continue;
               }
            } catch (IOException ioexception) {
            }

            for(String s1 : compoundnbt.getAllKeys()) {
               CompoundNBT compoundnbt1 = compoundnbt.getCompound(s1);
               long i = ChunkPos.asLong(compoundnbt1.getInt("ChunkX"), compoundnbt1.getInt("ChunkZ"));
               ListNBT listnbt = compoundnbt1.getList("Children", 10);
               if (!listnbt.isEmpty()) {
                  String s3 = listnbt.getCompound(0).getString("id");
                  String s4 = LEGACY_TO_CURRENT_MAP.get(s3);
                  if (s4 != null) {
                     compoundnbt1.putString("id", s4);
                  }
               }

               String s6 = compoundnbt1.getString("id");
               this.dataMap.computeIfAbsent(s6, (p_208208_0_) -> {
                  return new Long2ObjectOpenHashMap();
               }).put(i, compoundnbt1);
            }

            String s5 = s + "_index";
            StructureIndexesSavedData structureindexessaveddata = p_212184_1_.computeIfAbsent(() -> {
               return new StructureIndexesSavedData(s5);
            }, s5);
            if (!structureindexessaveddata.getAll().isEmpty()) {
               this.indexMap.put(s, structureindexessaveddata);
            } else {
               StructureIndexesSavedData structureindexessaveddata1 = new StructureIndexesSavedData(s5);
               this.indexMap.put(s, structureindexessaveddata1);

               for(String s2 : compoundnbt.getAllKeys()) {
                  CompoundNBT compoundnbt2 = compoundnbt.getCompound(s2);
                  structureindexessaveddata1.addIndex(ChunkPos.asLong(compoundnbt2.getInt("ChunkX"), compoundnbt2.getInt("ChunkZ")));
               }

               structureindexessaveddata1.setDirty();
            }
         }

      }
   }

   public static LegacyStructureDataUtil getLegacyStructureHandler(RegistryKey<World> p_236992_0_, @Nullable DimensionSavedDataManager p_236992_1_) {
      if (p_236992_0_ == World.OVERWORLD) {
         return new LegacyStructureDataUtil(p_236992_1_, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
      } else if (p_236992_0_ == World.NETHER) {
         List<String> list1 = ImmutableList.of("Fortress");
         return new LegacyStructureDataUtil(p_236992_1_, list1, list1);
      } else if (p_236992_0_ == World.END) {
         List<String> list = ImmutableList.of("EndCity");
         return new LegacyStructureDataUtil(p_236992_1_, list, list);
      } else {
         throw new RuntimeException(String.format("Unknown dimension type : %s", p_236992_0_));
      }
   }
}
