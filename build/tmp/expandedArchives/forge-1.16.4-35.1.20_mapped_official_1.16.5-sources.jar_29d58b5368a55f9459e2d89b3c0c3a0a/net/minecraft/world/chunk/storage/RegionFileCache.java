package net.minecraft.world.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.ChunkPos;

public final class RegionFileCache implements AutoCloseable {
   private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap<>();
   private final File folder;
   private final boolean sync;

   RegionFileCache(File p_i231895_1_, boolean p_i231895_2_) {
      this.folder = p_i231895_1_;
      this.sync = p_i231895_2_;
   }

   private RegionFile getRegionFile(ChunkPos p_219098_1_) throws IOException {
      long i = ChunkPos.asLong(p_219098_1_.getRegionX(), p_219098_1_.getRegionZ());
      RegionFile regionfile = this.regionCache.getAndMoveToFirst(i);
      if (regionfile != null) {
         return regionfile;
      } else {
         if (this.regionCache.size() >= 256) {
            this.regionCache.removeLast().close();
         }

         if (!this.folder.exists()) {
            this.folder.mkdirs();
         }

         File file1 = new File(this.folder, "r." + p_219098_1_.getRegionX() + "." + p_219098_1_.getRegionZ() + ".mca");
         RegionFile regionfile1 = new RegionFile(file1, this.folder, this.sync);
         this.regionCache.putAndMoveToFirst(i, regionfile1);
         return regionfile1;
      }
   }

   @Nullable
   public CompoundNBT read(ChunkPos p_219099_1_) throws IOException {
      RegionFile regionfile = this.getRegionFile(p_219099_1_);

      Object object;
      try (DataInputStream datainputstream = regionfile.getChunkDataInputStream(p_219099_1_)) {
         if (datainputstream != null) {
            return CompressedStreamTools.read(datainputstream);
         }

         object = null;
      }

      return (CompoundNBT)object;
   }

   protected void write(ChunkPos p_219100_1_, CompoundNBT p_219100_2_) throws IOException {
      RegionFile regionfile = this.getRegionFile(p_219100_1_);

      try (DataOutputStream dataoutputstream = regionfile.getChunkDataOutputStream(p_219100_1_)) {
         CompressedStreamTools.write(p_219100_2_, dataoutputstream);
      }

   }

   public void close() throws IOException {
      SuppressedExceptions<IOException> suppressedexceptions = new SuppressedExceptions<>();

      for(RegionFile regionfile : this.regionCache.values()) {
         try {
            regionfile.close();
         } catch (IOException ioexception) {
            suppressedexceptions.add(ioexception);
         }
      }

      suppressedexceptions.throwIfPresent();
   }

   public void flush() throws IOException {
      for(RegionFile regionfile : this.regionCache.values()) {
         regionfile.flush();
      }

   }
}
