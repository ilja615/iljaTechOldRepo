package net.minecraft.world.storage;

import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class WorldSavedData implements net.minecraftforge.common.util.INBTSerializable<CompoundNBT> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String id;
   private boolean dirty;

   public WorldSavedData(String p_i2141_1_) {
      this.id = p_i2141_1_;
   }

   public abstract void load(CompoundNBT p_76184_1_);

   public abstract CompoundNBT save(CompoundNBT p_189551_1_);

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean p_76186_1_) {
      this.dirty = p_76186_1_;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public String getId() {
      return this.id;
   }

   public void save(File p_215158_1_) {
      if (this.isDirty()) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.put("data", this.save(new CompoundNBT()));
         compoundnbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

         try {
            CompressedStreamTools.writeCompressed(compoundnbt, p_215158_1_);
         } catch (IOException ioexception) {
            LOGGER.error("Could not save data {}", this, ioexception);
         }

         this.setDirty(false);
      }
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      load(nbt);
   }

   @Override
   public CompoundNBT serializeNBT() {
      return save(new CompoundNBT());
   }
}
