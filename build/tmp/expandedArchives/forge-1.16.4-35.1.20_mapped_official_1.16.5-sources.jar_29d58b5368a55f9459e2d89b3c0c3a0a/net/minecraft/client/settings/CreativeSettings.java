package net.minecraft.client.settings;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreativeSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File optionsFile;
   private final DataFixer fixerUpper;
   private final HotbarSnapshot[] hotbars = new HotbarSnapshot[9];
   private boolean loaded;

   public CreativeSettings(File p_i49702_1_, DataFixer p_i49702_2_) {
      this.optionsFile = new File(p_i49702_1_, "hotbar.nbt");
      this.fixerUpper = p_i49702_2_;

      for(int i = 0; i < 9; ++i) {
         this.hotbars[i] = new HotbarSnapshot();
      }

   }

   private void load() {
      try {
         CompoundNBT compoundnbt = CompressedStreamTools.read(this.optionsFile);
         if (compoundnbt == null) {
            return;
         }

         if (!compoundnbt.contains("DataVersion", 99)) {
            compoundnbt.putInt("DataVersion", 1343);
         }

         compoundnbt = NBTUtil.update(this.fixerUpper, DefaultTypeReferences.HOTBAR, compoundnbt, compoundnbt.getInt("DataVersion"));

         for(int i = 0; i < 9; ++i) {
            this.hotbars[i].fromTag(compoundnbt.getList(String.valueOf(i), 10));
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to load creative mode options", (Throwable)exception);
      }

   }

   public void save() {
      try {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

         for(int i = 0; i < 9; ++i) {
            compoundnbt.put(String.valueOf(i), this.get(i).createTag());
         }

         CompressedStreamTools.write(compoundnbt, this.optionsFile);
      } catch (Exception exception) {
         LOGGER.error("Failed to save creative mode options", (Throwable)exception);
      }

   }

   public HotbarSnapshot get(int p_192563_1_) {
      if (!this.loaded) {
         this.load();
         this.loaded = true;
      }

      return this.hotbars[p_192563_1_];
   }
}
