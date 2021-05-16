package net.minecraft.world.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.command.TimerCallbackSerializers;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorldInfo implements IServerWorldInfo, IServerConfiguration {
   private static final Logger LOGGER = LogManager.getLogger();
   private WorldSettings settings;
   private final DimensionGeneratorSettings worldGenSettings;
   private final Lifecycle worldGenSettingsLifecycle;
   private int xSpawn;
   private int ySpawn;
   private int zSpawn;
   private float spawnAngle;
   private long gameTime;
   private long dayTime;
   @Nullable
   private final DataFixer fixerUpper;
   private final int playerDataVersion;
   private boolean upgradedPlayerTag;
   @Nullable
   private CompoundNBT loadedPlayerTag;
   private final int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private boolean initialized;
   private boolean difficultyLocked;
   private WorldBorder.Serializer worldBorder;
   private CompoundNBT endDragonFightData;
   @Nullable
   private CompoundNBT customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   @Nullable
   private UUID wanderingTraderId;
   private final Set<String> knownServerBrands;
   private boolean wasModded;
   private final TimerCallbackManager<MinecraftServer> scheduledEvents;

   private ServerWorldInfo(@Nullable DataFixer p_i242043_1_, int p_i242043_2_, @Nullable CompoundNBT p_i242043_3_, boolean p_i242043_4_, int p_i242043_5_, int p_i242043_6_, int p_i242043_7_, float p_i242043_8_, long p_i242043_9_, long p_i242043_11_, int p_i242043_13_, int p_i242043_14_, int p_i242043_15_, boolean p_i242043_16_, int p_i242043_17_, boolean p_i242043_18_, boolean p_i242043_19_, boolean p_i242043_20_, WorldBorder.Serializer p_i242043_21_, int p_i242043_22_, int p_i242043_23_, @Nullable UUID p_i242043_24_, LinkedHashSet<String> p_i242043_25_, TimerCallbackManager<MinecraftServer> p_i242043_26_, @Nullable CompoundNBT p_i242043_27_, CompoundNBT p_i242043_28_, WorldSettings p_i242043_29_, DimensionGeneratorSettings p_i242043_30_, Lifecycle p_i242043_31_) {
      this.fixerUpper = p_i242043_1_;
      this.wasModded = p_i242043_4_;
      this.xSpawn = p_i242043_5_;
      this.ySpawn = p_i242043_6_;
      this.zSpawn = p_i242043_7_;
      this.spawnAngle = p_i242043_8_;
      this.gameTime = p_i242043_9_;
      this.dayTime = p_i242043_11_;
      this.version = p_i242043_13_;
      this.clearWeatherTime = p_i242043_14_;
      this.rainTime = p_i242043_15_;
      this.raining = p_i242043_16_;
      this.thunderTime = p_i242043_17_;
      this.thundering = p_i242043_18_;
      this.initialized = p_i242043_19_;
      this.difficultyLocked = p_i242043_20_;
      this.worldBorder = p_i242043_21_;
      this.wanderingTraderSpawnDelay = p_i242043_22_;
      this.wanderingTraderSpawnChance = p_i242043_23_;
      this.wanderingTraderId = p_i242043_24_;
      this.knownServerBrands = p_i242043_25_;
      this.loadedPlayerTag = p_i242043_3_;
      this.playerDataVersion = p_i242043_2_;
      this.scheduledEvents = p_i242043_26_;
      this.customBossEvents = p_i242043_27_;
      this.endDragonFightData = p_i242043_28_;
      this.settings = p_i242043_29_;
      this.worldGenSettings = p_i242043_30_;
      this.worldGenSettingsLifecycle = p_i242043_31_;
   }

   public ServerWorldInfo(WorldSettings p_i232158_1_, DimensionGeneratorSettings p_i232158_2_, Lifecycle p_i232158_3_) {
      this((DataFixer)null, SharedConstants.getCurrentVersion().getWorldVersion(), (CompoundNBT)null, false, 0, 0, 0, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, (UUID)null, Sets.newLinkedHashSet(), new TimerCallbackManager<>(TimerCallbackSerializers.SERVER_CALLBACKS), (CompoundNBT)null, new CompoundNBT(), p_i232158_1_.copy(), p_i232158_2_, p_i232158_3_);
   }

   public static ServerWorldInfo parse(Dynamic<INBT> p_237369_0_, DataFixer p_237369_1_, int p_237369_2_, @Nullable CompoundNBT p_237369_3_, WorldSettings p_237369_4_, VersionData p_237369_5_, DimensionGeneratorSettings p_237369_6_, Lifecycle p_237369_7_) {
      long i = p_237369_0_.get("Time").asLong(0L);
      CompoundNBT compoundnbt = (CompoundNBT)p_237369_0_.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> {
         return p_237369_0_.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue();
      });
      return new ServerWorldInfo(p_237369_1_, p_237369_2_, p_237369_3_, p_237369_0_.get("WasModded").asBoolean(false), p_237369_0_.get("SpawnX").asInt(0), p_237369_0_.get("SpawnY").asInt(0), p_237369_0_.get("SpawnZ").asInt(0), p_237369_0_.get("SpawnAngle").asFloat(0.0F), i, p_237369_0_.get("DayTime").asLong(i), p_237369_5_.levelDataVersion(), p_237369_0_.get("clearWeatherTime").asInt(0), p_237369_0_.get("rainTime").asInt(0), p_237369_0_.get("raining").asBoolean(false), p_237369_0_.get("thunderTime").asInt(0), p_237369_0_.get("thundering").asBoolean(false), p_237369_0_.get("initialized").asBoolean(true), p_237369_0_.get("DifficultyLocked").asBoolean(false), WorldBorder.Serializer.read(p_237369_0_, WorldBorder.DEFAULT_SETTINGS), p_237369_0_.get("WanderingTraderSpawnDelay").asInt(0), p_237369_0_.get("WanderingTraderSpawnChance").asInt(0), p_237369_0_.get("WanderingTraderId").read(UUIDCodec.CODEC).result().orElse((UUID)null), p_237369_0_.get("ServerBrands").asStream().flatMap((p_237368_0_) -> {
         return Util.toStream(p_237368_0_.asString().result());
      }).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new TimerCallbackManager<>(TimerCallbackSerializers.SERVER_CALLBACKS, p_237369_0_.get("ScheduledEvents").asStream()), (CompoundNBT)p_237369_0_.get("CustomBossEvents").orElseEmptyMap().getValue(), compoundnbt, p_237369_4_, p_237369_6_, p_237369_7_);
   }

   public CompoundNBT createTag(DynamicRegistries p_230411_1_, @Nullable CompoundNBT p_230411_2_) {
      this.updatePlayerTag();
      if (p_230411_2_ == null) {
         p_230411_2_ = this.loadedPlayerTag;
      }

      CompoundNBT compoundnbt = new CompoundNBT();
      this.setTagData(p_230411_1_, compoundnbt, p_230411_2_);
      return compoundnbt;
   }

   private void setTagData(DynamicRegistries p_237370_1_, CompoundNBT p_237370_2_, @Nullable CompoundNBT p_237370_3_) {
      ListNBT listnbt = new ListNBT();
      this.knownServerBrands.stream().map(StringNBT::valueOf).forEach(listnbt::add);
      p_237370_2_.put("ServerBrands", listnbt);
      p_237370_2_.putBoolean("WasModded", this.wasModded);
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", SharedConstants.getCurrentVersion().getName());
      compoundnbt.putInt("Id", SharedConstants.getCurrentVersion().getWorldVersion());
      compoundnbt.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
      p_237370_2_.put("Version", compoundnbt);
      p_237370_2_.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      WorldGenSettingsExport<INBT> worldgensettingsexport = WorldGenSettingsExport.create(NBTDynamicOps.INSTANCE, p_237370_1_);
      DimensionGeneratorSettings.CODEC.encodeStart(worldgensettingsexport, this.worldGenSettings).resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error)).ifPresent((p_237373_1_) -> {
         p_237370_2_.put("WorldGenSettings", p_237373_1_);
      });
      p_237370_2_.putInt("GameType", this.settings.gameType().getId());
      p_237370_2_.putInt("SpawnX", this.xSpawn);
      p_237370_2_.putInt("SpawnY", this.ySpawn);
      p_237370_2_.putInt("SpawnZ", this.zSpawn);
      p_237370_2_.putFloat("SpawnAngle", this.spawnAngle);
      p_237370_2_.putLong("Time", this.gameTime);
      p_237370_2_.putLong("DayTime", this.dayTime);
      p_237370_2_.putLong("LastPlayed", Util.getEpochMillis());
      p_237370_2_.putString("LevelName", this.settings.levelName());
      p_237370_2_.putInt("version", 19133);
      p_237370_2_.putInt("clearWeatherTime", this.clearWeatherTime);
      p_237370_2_.putInt("rainTime", this.rainTime);
      p_237370_2_.putBoolean("raining", this.raining);
      p_237370_2_.putInt("thunderTime", this.thunderTime);
      p_237370_2_.putBoolean("thundering", this.thundering);
      p_237370_2_.putBoolean("hardcore", this.settings.hardcore());
      p_237370_2_.putBoolean("allowCommands", this.settings.allowCommands());
      p_237370_2_.putBoolean("initialized", this.initialized);
      this.worldBorder.write(p_237370_2_);
      p_237370_2_.putByte("Difficulty", (byte)this.settings.difficulty().getId());
      p_237370_2_.putBoolean("DifficultyLocked", this.difficultyLocked);
      p_237370_2_.put("GameRules", this.settings.gameRules().createTag());
      p_237370_2_.put("DragonFight", this.endDragonFightData);
      if (p_237370_3_ != null) {
         p_237370_2_.put("Player", p_237370_3_);
      }

      DatapackCodec.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.settings.getDataPackConfig()).result().ifPresent((p_237371_1_) -> {
         p_237370_2_.put("DataPacks", p_237371_1_);
      });
      if (this.customBossEvents != null) {
         p_237370_2_.put("CustomBossEvents", this.customBossEvents);
      }

      p_237370_2_.put("ScheduledEvents", this.scheduledEvents.store());
      p_237370_2_.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      p_237370_2_.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if (this.wanderingTraderId != null) {
         p_237370_2_.putUUID("WanderingTraderId", this.wanderingTraderId);
      }

   }

   public int getXSpawn() {
      return this.xSpawn;
   }

   public int getYSpawn() {
      return this.ySpawn;
   }

   public int getZSpawn() {
      return this.zSpawn;
   }

   public float getSpawnAngle() {
      return this.spawnAngle;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   private void updatePlayerTag() {
      if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
         if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if (this.fixerUpper == null) {
               throw (NullPointerException)Util.pauseInIde(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }

            this.loadedPlayerTag = NBTUtil.update(this.fixerUpper, DefaultTypeReferences.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
         }

         this.upgradedPlayerTag = true;
      }
   }

   public CompoundNBT getLoadedPlayerTag() {
      this.updatePlayerTag();
      return this.loadedPlayerTag;
   }

   public void setXSpawn(int p_76058_1_) {
      this.xSpawn = p_76058_1_;
   }

   public void setYSpawn(int p_76056_1_) {
      this.ySpawn = p_76056_1_;
   }

   public void setZSpawn(int p_76087_1_) {
      this.zSpawn = p_76087_1_;
   }

   public void setSpawnAngle(float p_241859_1_) {
      this.spawnAngle = p_241859_1_;
   }

   public void setGameTime(long p_82572_1_) {
      this.gameTime = p_82572_1_;
   }

   public void setDayTime(long p_76068_1_) {
      this.dayTime = p_76068_1_;
   }

   public void setSpawn(BlockPos p_176143_1_, float p_176143_2_) {
      this.xSpawn = p_176143_1_.getX();
      this.ySpawn = p_176143_1_.getY();
      this.zSpawn = p_176143_1_.getZ();
      this.spawnAngle = p_176143_2_;
   }

   public String getLevelName() {
      return this.settings.levelName();
   }

   public int getVersion() {
      return this.version;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int p_230391_1_) {
      this.clearWeatherTime = p_230391_1_;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean p_76069_1_) {
      this.thundering = p_76069_1_;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int p_76090_1_) {
      this.thunderTime = p_76090_1_;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean p_76084_1_) {
      this.raining = p_76084_1_;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int p_76080_1_) {
      this.rainTime = p_76080_1_;
   }

   public GameType getGameType() {
      return this.settings.gameType();
   }

   public void setGameType(GameType p_230392_1_) {
      this.settings = this.settings.withGameType(p_230392_1_);
   }

   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   public boolean getAllowCommands() {
      return this.settings.allowCommands();
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean p_76091_1_) {
      this.initialized = p_76091_1_;
   }

   public GameRules getGameRules() {
      return this.settings.gameRules();
   }

   public WorldBorder.Serializer getWorldBorder() {
      return this.worldBorder;
   }

   public void setWorldBorder(WorldBorder.Serializer p_230393_1_) {
      this.worldBorder = p_230393_1_;
   }

   public Difficulty getDifficulty() {
      return this.settings.difficulty();
   }

   public void setDifficulty(Difficulty p_230409_1_) {
      this.settings = this.settings.withDifficulty(p_230409_1_);
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean p_230415_1_) {
      this.difficultyLocked = p_230415_1_;
   }

   public TimerCallbackManager<MinecraftServer> getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void fillCrashReportCategory(CrashReportCategory p_85118_1_) {
      IServerWorldInfo.super.fillCrashReportCategory(p_85118_1_);
      IServerConfiguration.super.fillCrashReportCategory(p_85118_1_);
   }

   public DimensionGeneratorSettings worldGenSettings() {
      return this.worldGenSettings;
   }

   @OnlyIn(Dist.CLIENT)
   public Lifecycle worldGenSettingsLifecycle() {
      return this.worldGenSettingsLifecycle;
   }

   public CompoundNBT endDragonFightData() {
      return this.endDragonFightData;
   }

   public void setEndDragonFightData(CompoundNBT p_230413_1_) {
      this.endDragonFightData = p_230413_1_;
   }

   public DatapackCodec getDataPackConfig() {
      return this.settings.getDataPackConfig();
   }

   public void setDataPackConfig(DatapackCodec p_230410_1_) {
      this.settings = this.settings.withDataPackConfig(p_230410_1_);
   }

   @Nullable
   public CompoundNBT getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable CompoundNBT p_230414_1_) {
      this.customBossEvents = p_230414_1_;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int p_230396_1_) {
      this.wanderingTraderSpawnDelay = p_230396_1_;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int p_230397_1_) {
      this.wanderingTraderSpawnChance = p_230397_1_;
   }

   public void setWanderingTraderId(UUID p_230394_1_) {
      this.wanderingTraderId = p_230394_1_;
   }

   public void setModdedInfo(String p_230412_1_, boolean p_230412_2_) {
      this.knownServerBrands.add(p_230412_1_);
      this.wasModded |= p_230412_2_;
   }

   public boolean wasModded() {
      return this.wasModded;
   }

   public Set<String> getKnownServerBrands() {
      return ImmutableSet.copyOf(this.knownServerBrands);
   }

   public IServerWorldInfo overworldData() {
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldSettings getLevelSettings() {
      return this.settings.copy();
   }
}
