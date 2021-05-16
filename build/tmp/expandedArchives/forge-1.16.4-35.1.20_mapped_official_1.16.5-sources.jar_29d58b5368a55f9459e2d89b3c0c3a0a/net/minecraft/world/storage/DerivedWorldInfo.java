package net.minecraft.world.storage;

import java.util.UUID;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;

public class DerivedWorldInfo implements IServerWorldInfo {
   private final IServerConfiguration worldData;
   private final IServerWorldInfo wrapped;

   public DerivedWorldInfo(IServerConfiguration p_i232150_1_, IServerWorldInfo p_i232150_2_) {
      this.worldData = p_i232150_1_;
      this.wrapped = p_i232150_2_;
   }

   public int getXSpawn() {
      return this.wrapped.getXSpawn();
   }

   public int getYSpawn() {
      return this.wrapped.getYSpawn();
   }

   public int getZSpawn() {
      return this.wrapped.getZSpawn();
   }

   public float getSpawnAngle() {
      return this.wrapped.getSpawnAngle();
   }

   public long getGameTime() {
      return this.wrapped.getGameTime();
   }

   public long getDayTime() {
      return this.wrapped.getDayTime();
   }

   public String getLevelName() {
      return this.worldData.getLevelName();
   }

   public int getClearWeatherTime() {
      return this.wrapped.getClearWeatherTime();
   }

   public void setClearWeatherTime(int p_230391_1_) {
   }

   public boolean isThundering() {
      return this.wrapped.isThundering();
   }

   public int getThunderTime() {
      return this.wrapped.getThunderTime();
   }

   public boolean isRaining() {
      return this.wrapped.isRaining();
   }

   public int getRainTime() {
      return this.wrapped.getRainTime();
   }

   public GameType getGameType() {
      return this.worldData.getGameType();
   }

   public void setXSpawn(int p_76058_1_) {
   }

   public void setYSpawn(int p_76056_1_) {
   }

   public void setZSpawn(int p_76087_1_) {
   }

   public void setSpawnAngle(float p_241859_1_) {
   }

   public void setGameTime(long p_82572_1_) {
   }

   public void setDayTime(long p_76068_1_) {
   }

   public void setSpawn(BlockPos p_176143_1_, float p_176143_2_) {
   }

   public void setThundering(boolean p_76069_1_) {
   }

   public void setThunderTime(int p_76090_1_) {
   }

   public void setRaining(boolean p_76084_1_) {
   }

   public void setRainTime(int p_76080_1_) {
   }

   public void setGameType(GameType p_230392_1_) {
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public boolean getAllowCommands() {
      return this.worldData.getAllowCommands();
   }

   public boolean isInitialized() {
      return this.wrapped.isInitialized();
   }

   public void setInitialized(boolean p_76091_1_) {
   }

   public GameRules getGameRules() {
      return this.worldData.getGameRules();
   }

   public WorldBorder.Serializer getWorldBorder() {
      return this.wrapped.getWorldBorder();
   }

   public void setWorldBorder(WorldBorder.Serializer p_230393_1_) {
   }

   public Difficulty getDifficulty() {
      return this.worldData.getDifficulty();
   }

   public boolean isDifficultyLocked() {
      return this.worldData.isDifficultyLocked();
   }

   public TimerCallbackManager<MinecraftServer> getScheduledEvents() {
      return this.wrapped.getScheduledEvents();
   }

   public int getWanderingTraderSpawnDelay() {
      return 0;
   }

   public void setWanderingTraderSpawnDelay(int p_230396_1_) {
   }

   public int getWanderingTraderSpawnChance() {
      return 0;
   }

   public void setWanderingTraderSpawnChance(int p_230397_1_) {
   }

   public void setWanderingTraderId(UUID p_230394_1_) {
   }

   public void fillCrashReportCategory(CrashReportCategory p_85118_1_) {
      p_85118_1_.setDetail("Derived", true);
      this.wrapped.fillCrashReportCategory(p_85118_1_);
   }
}
