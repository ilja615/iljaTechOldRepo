package net.minecraft.world.storage;

import java.util.UUID;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;

public interface IServerWorldInfo extends ISpawnWorldInfo {
   String getLevelName();

   void setThundering(boolean p_76069_1_);

   int getRainTime();

   void setRainTime(int p_76080_1_);

   void setThunderTime(int p_76090_1_);

   int getThunderTime();

   default void fillCrashReportCategory(CrashReportCategory p_85118_1_) {
      ISpawnWorldInfo.super.fillCrashReportCategory(p_85118_1_);
      p_85118_1_.setDetail("Level name", this::getLevelName);
      p_85118_1_.setDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameType().getName(), this.getGameType().getId(), this.isHardcore(), this.getAllowCommands());
      });
      p_85118_1_.setDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering());
      });
   }

   int getClearWeatherTime();

   void setClearWeatherTime(int p_230391_1_);

   int getWanderingTraderSpawnDelay();

   void setWanderingTraderSpawnDelay(int p_230396_1_);

   int getWanderingTraderSpawnChance();

   void setWanderingTraderSpawnChance(int p_230397_1_);

   void setWanderingTraderId(UUID p_230394_1_);

   GameType getGameType();

   void setWorldBorder(WorldBorder.Serializer p_230393_1_);

   WorldBorder.Serializer getWorldBorder();

   boolean isInitialized();

   void setInitialized(boolean p_76091_1_);

   boolean getAllowCommands();

   void setGameType(GameType p_230392_1_);

   TimerCallbackManager<MinecraftServer> getScheduledEvents();

   void setGameTime(long p_82572_1_);

   void setDayTime(long p_76068_1_);
}
