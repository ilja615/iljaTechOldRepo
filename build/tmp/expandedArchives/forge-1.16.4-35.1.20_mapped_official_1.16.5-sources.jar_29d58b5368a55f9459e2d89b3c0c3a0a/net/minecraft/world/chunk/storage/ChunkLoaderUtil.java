package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoaderUtil {
   public static ChunkLoaderUtil.AnvilConverterData load(CompoundNBT p_76691_0_) {
      int i = p_76691_0_.getInt("xPos");
      int j = p_76691_0_.getInt("zPos");
      ChunkLoaderUtil.AnvilConverterData chunkloaderutil$anvilconverterdata = new ChunkLoaderUtil.AnvilConverterData(i, j);
      chunkloaderutil$anvilconverterdata.blocks = p_76691_0_.getByteArray("Blocks");
      chunkloaderutil$anvilconverterdata.data = new NibbleArrayReader(p_76691_0_.getByteArray("Data"), 7);
      chunkloaderutil$anvilconverterdata.skyLight = new NibbleArrayReader(p_76691_0_.getByteArray("SkyLight"), 7);
      chunkloaderutil$anvilconverterdata.blockLight = new NibbleArrayReader(p_76691_0_.getByteArray("BlockLight"), 7);
      chunkloaderutil$anvilconverterdata.heightmap = p_76691_0_.getByteArray("HeightMap");
      chunkloaderutil$anvilconverterdata.terrainPopulated = p_76691_0_.getBoolean("TerrainPopulated");
      chunkloaderutil$anvilconverterdata.entities = p_76691_0_.getList("Entities", 10);
      chunkloaderutil$anvilconverterdata.blockEntities = p_76691_0_.getList("TileEntities", 10);
      chunkloaderutil$anvilconverterdata.blockTicks = p_76691_0_.getList("TileTicks", 10);

      try {
         chunkloaderutil$anvilconverterdata.lastUpdated = p_76691_0_.getLong("LastUpdate");
      } catch (ClassCastException classcastexception) {
         chunkloaderutil$anvilconverterdata.lastUpdated = (long)p_76691_0_.getInt("LastUpdate");
      }

      return chunkloaderutil$anvilconverterdata;
   }

   public static void convertToAnvilFormat(DynamicRegistries.Impl p_242708_0_, ChunkLoaderUtil.AnvilConverterData p_242708_1_, CompoundNBT p_242708_2_, BiomeProvider p_242708_3_) {
      p_242708_2_.putInt("xPos", p_242708_1_.x);
      p_242708_2_.putInt("zPos", p_242708_1_.z);
      p_242708_2_.putLong("LastUpdate", p_242708_1_.lastUpdated);
      int[] aint = new int[p_242708_1_.heightmap.length];

      for(int i = 0; i < p_242708_1_.heightmap.length; ++i) {
         aint[i] = p_242708_1_.heightmap[i];
      }

      p_242708_2_.putIntArray("HeightMap", aint);
      p_242708_2_.putBoolean("TerrainPopulated", p_242708_1_.terrainPopulated);
      ListNBT listnbt = new ListNBT();

      for(int j = 0; j < 8; ++j) {
         boolean flag = true;

         for(int k = 0; k < 16 && flag; ++k) {
            for(int l = 0; l < 16 && flag; ++l) {
               for(int i1 = 0; i1 < 16; ++i1) {
                  int j1 = k << 11 | i1 << 7 | l + (j << 4);
                  int k1 = p_242708_1_.blocks[j1];
                  if (k1 != 0) {
                     flag = false;
                     break;
                  }
               }
            }
         }

         if (!flag) {
            byte[] abyte = new byte[4096];
            NibbleArray nibblearray = new NibbleArray();
            NibbleArray nibblearray1 = new NibbleArray();
            NibbleArray nibblearray2 = new NibbleArray();

            for(int l2 = 0; l2 < 16; ++l2) {
               for(int l1 = 0; l1 < 16; ++l1) {
                  for(int i2 = 0; i2 < 16; ++i2) {
                     int j2 = l2 << 11 | i2 << 7 | l1 + (j << 4);
                     int k2 = p_242708_1_.blocks[j2];
                     abyte[l1 << 8 | i2 << 4 | l2] = (byte)(k2 & 255);
                     nibblearray.set(l2, l1, i2, p_242708_1_.data.get(l2, l1 + (j << 4), i2));
                     nibblearray1.set(l2, l1, i2, p_242708_1_.skyLight.get(l2, l1 + (j << 4), i2));
                     nibblearray2.set(l2, l1, i2, p_242708_1_.blockLight.get(l2, l1 + (j << 4), i2));
                  }
               }
            }

            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Y", (byte)(j & 255));
            compoundnbt.putByteArray("Blocks", abyte);
            compoundnbt.putByteArray("Data", nibblearray.getData());
            compoundnbt.putByteArray("SkyLight", nibblearray1.getData());
            compoundnbt.putByteArray("BlockLight", nibblearray2.getData());
            listnbt.add(compoundnbt);
         }
      }

      p_242708_2_.put("Sections", listnbt);
      p_242708_2_.putIntArray("Biomes", (new BiomeContainer(p_242708_0_.registryOrThrow(Registry.BIOME_REGISTRY), new ChunkPos(p_242708_1_.x, p_242708_1_.z), p_242708_3_)).writeBiomes());
      p_242708_2_.put("Entities", p_242708_1_.entities);
      p_242708_2_.put("TileEntities", p_242708_1_.blockEntities);
      if (p_242708_1_.blockTicks != null) {
         p_242708_2_.put("TileTicks", p_242708_1_.blockTicks);
      }

      p_242708_2_.putBoolean("convertedFromAlphaFormat", true);
   }

   public static class AnvilConverterData {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public NibbleArrayReader blockLight;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader data;
      public byte[] blocks;
      public ListNBT entities;
      public ListNBT blockEntities;
      public ListNBT blockTicks;
      public final int x;
      public final int z;

      public AnvilConverterData(int p_i1999_1_, int p_i1999_2_) {
         this.x = p_i1999_1_;
         this.z = p_i1999_2_;
      }
   }
}
