package net.minecraft.world.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IServerConfiguration {
   DatapackCodec getDataPackConfig();

   void setDataPackConfig(DatapackCodec p_230410_1_);

   boolean wasModded();

   Set<String> getKnownServerBrands();

   void setModdedInfo(String p_230412_1_, boolean p_230412_2_);

   default void fillCrashReportCategory(CrashReportCategory p_85118_1_) {
      p_85118_1_.setDetail("Known server brands", () -> {
         return String.join(", ", this.getKnownServerBrands());
      });
      p_85118_1_.setDetail("Level was modded", () -> {
         return Boolean.toString(this.wasModded());
      });
      p_85118_1_.setDetail("Level storage version", () -> {
         int i = this.getVersion();
         return String.format("0x%05X - %s", i, this.getStorageVersionName(i));
      });
   }

   default String getStorageVersionName(int p_237379_1_) {
      switch(p_237379_1_) {
      case 19132:
         return "McRegion";
      case 19133:
         return "Anvil";
      default:
         return "Unknown?";
      }
   }

   @Nullable
   CompoundNBT getCustomBossEvents();

   void setCustomBossEvents(@Nullable CompoundNBT p_230414_1_);

   IServerWorldInfo overworldData();

   @OnlyIn(Dist.CLIENT)
   WorldSettings getLevelSettings();

   CompoundNBT createTag(DynamicRegistries p_230411_1_, @Nullable CompoundNBT p_230411_2_);

   boolean isHardcore();

   int getVersion();

   String getLevelName();

   GameType getGameType();

   void setGameType(GameType p_230392_1_);

   boolean getAllowCommands();

   Difficulty getDifficulty();

   void setDifficulty(Difficulty p_230409_1_);

   boolean isDifficultyLocked();

   void setDifficultyLocked(boolean p_230415_1_);

   GameRules getGameRules();

   CompoundNBT getLoadedPlayerTag();

   CompoundNBT endDragonFightData();

   void setEndDragonFightData(CompoundNBT p_230413_1_);

   DimensionGeneratorSettings worldGenSettings();

   @OnlyIn(Dist.CLIENT)
   Lifecycle worldGenSettingsLifecycle();
}
