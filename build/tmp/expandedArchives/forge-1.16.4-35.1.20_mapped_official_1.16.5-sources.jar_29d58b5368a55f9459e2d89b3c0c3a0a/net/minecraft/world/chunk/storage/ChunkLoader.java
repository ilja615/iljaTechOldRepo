package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class ChunkLoader implements AutoCloseable {
   private final IOWorker worker;
   protected final DataFixer fixerUpper;
   @Nullable
   private LegacyStructureDataUtil legacyStructureHandler;

   public ChunkLoader(File p_i231889_1_, DataFixer p_i231889_2_, boolean p_i231889_3_) {
      this.fixerUpper = p_i231889_2_;
      this.worker = new IOWorker(p_i231889_1_, p_i231889_3_, "chunk");
   }

   public CompoundNBT upgradeChunkTag(RegistryKey<World> p_235968_1_, Supplier<DimensionSavedDataManager> p_235968_2_, CompoundNBT p_235968_3_) {
      int i = getVersion(p_235968_3_);
      int j = 1493;
      if (i < 1493) {
         p_235968_3_ = NBTUtil.update(this.fixerUpper, DefaultTypeReferences.CHUNK, p_235968_3_, i, 1493);
         if (p_235968_3_.getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.legacyStructureHandler == null) {
               this.legacyStructureHandler = LegacyStructureDataUtil.getLegacyStructureHandler(p_235968_1_, p_235968_2_.get());
            }

            p_235968_3_ = this.legacyStructureHandler.updateFromLegacy(p_235968_3_);
         }
      }

      p_235968_3_ = NBTUtil.update(this.fixerUpper, DefaultTypeReferences.CHUNK, p_235968_3_, Math.max(1493, i));
      if (i < SharedConstants.getCurrentVersion().getWorldVersion()) {
         p_235968_3_.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      }

      return p_235968_3_;
   }

   public static int getVersion(CompoundNBT p_219165_0_) {
      return p_219165_0_.contains("DataVersion", 99) ? p_219165_0_.getInt("DataVersion") : -1;
   }

   @Nullable
   public CompoundNBT read(ChunkPos p_227078_1_) throws IOException {
      return this.worker.load(p_227078_1_);
   }

   public void write(ChunkPos p_219100_1_, CompoundNBT p_219100_2_) {
      this.worker.store(p_219100_1_, p_219100_2_);
      if (this.legacyStructureHandler != null) {
         this.legacyStructureHandler.removeIndex(p_219100_1_.toLong());
      }

   }

   public void flushWorker() {
      this.worker.synchronize().join();
   }

   public void close() throws IOException {
      this.worker.close();
   }
}
