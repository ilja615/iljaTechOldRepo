package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionSavedDataManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, WorldSavedData> cache = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private final File dataFolder;

   public DimensionSavedDataManager(File p_i51279_1_, DataFixer p_i51279_2_) {
      this.fixerUpper = p_i51279_2_;
      this.dataFolder = p_i51279_1_;
   }

   private File getDataFile(String p_215754_1_) {
      return new File(this.dataFolder, p_215754_1_ + ".dat");
   }

   public <T extends WorldSavedData> T computeIfAbsent(Supplier<T> p_215752_1_, String p_215752_2_) {
      T t = this.get(p_215752_1_, p_215752_2_);
      if (t != null) {
         return t;
      } else {
         T t1 = p_215752_1_.get();
         this.set(t1);
         return t1;
      }
   }

   @Nullable
   public <T extends WorldSavedData> T get(Supplier<T> p_215753_1_, String p_215753_2_) {
      WorldSavedData worldsaveddata = this.cache.get(p_215753_2_);
      if (worldsaveddata == net.minecraftforge.common.util.DummyWorldSaveData.DUMMY) return null;
      if (worldsaveddata == null && !this.cache.containsKey(p_215753_2_)) {
         worldsaveddata = this.readSavedData(p_215753_1_, p_215753_2_);
         this.cache.put(p_215753_2_, worldsaveddata);
      } else if (worldsaveddata == null) {
         this.cache.put(p_215753_2_, net.minecraftforge.common.util.DummyWorldSaveData.DUMMY);
         return null;
      }

      return (T)worldsaveddata;
   }

   @Nullable
   private <T extends WorldSavedData> T readSavedData(Supplier<T> p_223409_1_, String p_223409_2_) {
      try {
         File file1 = this.getDataFile(p_223409_2_);
         if (file1.exists()) {
            T t = p_223409_1_.get();
            CompoundNBT compoundnbt = this.readTagFromDisk(p_223409_2_, SharedConstants.getCurrentVersion().getWorldVersion());
            t.load(compoundnbt.getCompound("data"));
            return t;
         }
      } catch (Exception exception) {
         LOGGER.error("Error loading saved data: {}", p_223409_2_, exception);
      }

      return (T)null;
   }

   public void set(WorldSavedData p_215757_1_) {
      this.cache.put(p_215757_1_.getId(), p_215757_1_);
   }

   public CompoundNBT readTagFromDisk(String p_215755_1_, int p_215755_2_) throws IOException {
      File file1 = this.getDataFile(p_215755_1_);

      CompoundNBT compoundnbt1;
      try (
         FileInputStream fileinputstream = new FileInputStream(file1);
         PushbackInputStream pushbackinputstream = new PushbackInputStream(fileinputstream, 2);
      ) {
         CompoundNBT compoundnbt;
         if (this.isGzip(pushbackinputstream)) {
            compoundnbt = CompressedStreamTools.readCompressed(pushbackinputstream);
         } else {
            try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) {
               compoundnbt = CompressedStreamTools.read(datainputstream);
            }
         }

         int i = compoundnbt.contains("DataVersion", 99) ? compoundnbt.getInt("DataVersion") : 1343;
         compoundnbt1 = NBTUtil.update(this.fixerUpper, DefaultTypeReferences.SAVED_DATA, compoundnbt, i, p_215755_2_);
      }

      return compoundnbt1;
   }

   private boolean isGzip(PushbackInputStream p_215756_1_) throws IOException {
      byte[] abyte = new byte[2];
      boolean flag = false;
      int i = p_215756_1_.read(abyte, 0, 2);
      if (i == 2) {
         int j = (abyte[1] & 255) << 8 | abyte[0] & 255;
         if (j == 35615) {
            flag = true;
         }
      }

      if (i != 0) {
         p_215756_1_.unread(abyte, 0, i);
      }

      return flag;
   }

   public void save() {
      for(WorldSavedData worldsaveddata : this.cache.values()) {
         if (worldsaveddata != null) {
            worldsaveddata.save(this.getDataFile(worldsaveddata.getId()));
         }
      }

   }
}
