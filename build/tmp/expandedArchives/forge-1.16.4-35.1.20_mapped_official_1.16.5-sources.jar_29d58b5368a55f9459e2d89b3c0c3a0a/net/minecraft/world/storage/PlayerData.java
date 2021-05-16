package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerData {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File playerDir;
   protected final DataFixer fixerUpper;

   public PlayerData(SaveFormat.LevelSave p_i232157_1_, DataFixer p_i232157_2_) {
      this.fixerUpper = p_i232157_2_;
      this.playerDir = p_i232157_1_.getLevelPath(FolderName.PLAYER_DATA_DIR).toFile();
      this.playerDir.mkdirs();
   }

   public void save(PlayerEntity p_237335_1_) {
      try {
         CompoundNBT compoundnbt = p_237335_1_.saveWithoutId(new CompoundNBT());
         File file1 = File.createTempFile(p_237335_1_.getStringUUID() + "-", ".dat", this.playerDir);
         CompressedStreamTools.writeCompressed(compoundnbt, file1);
         File file2 = new File(this.playerDir, p_237335_1_.getStringUUID() + ".dat");
         File file3 = new File(this.playerDir, p_237335_1_.getStringUUID() + ".dat_old");
         Util.safeReplaceFile(file2, file1, file3);
         net.minecraftforge.event.ForgeEventFactory.firePlayerSavingEvent(p_237335_1_, playerDir, p_237335_1_.getStringUUID());
      } catch (Exception exception) {
         LOGGER.warn("Failed to save player data for {}", (Object)p_237335_1_.getName().getString());
      }

   }

   @Nullable
   public CompoundNBT load(PlayerEntity p_237336_1_) {
      CompoundNBT compoundnbt = null;

      try {
         File file1 = new File(this.playerDir, p_237336_1_.getStringUUID() + ".dat");
         if (file1.exists() && file1.isFile()) {
            compoundnbt = CompressedStreamTools.readCompressed(file1);
         }
      } catch (Exception exception) {
         LOGGER.warn("Failed to load player data for {}", (Object)p_237336_1_.getName().getString());
      }

      if (compoundnbt != null) {
         int i = compoundnbt.contains("DataVersion", 3) ? compoundnbt.getInt("DataVersion") : -1;
         p_237336_1_.load(NBTUtil.update(this.fixerUpper, DefaultTypeReferences.PLAYER, compoundnbt, i));
      }
      net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(p_237336_1_, playerDir, p_237336_1_.getStringUUID());

      return compoundnbt;
   }

   public String[] getSeenPlayers() {
      String[] astring = this.playerDir.list();
      if (astring == null) {
         astring = new String[0];
      }

      for(int i = 0; i < astring.length; ++i) {
         if (astring[i].endsWith(".dat")) {
            astring[i] = astring[i].substring(0, astring[i].length() - 4);
         }
      }

      return astring;
   }

   public File getPlayerDataFolder() {
      return playerDir;
   }
}
