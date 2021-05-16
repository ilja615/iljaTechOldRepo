package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

public class ServerProperties extends PropertyManager<ServerProperties> {
   public final boolean onlineMode = this.get("online-mode", true);
   public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
   public final String serverIp = this.get("server-ip", "");
   public final boolean spawnAnimals = this.get("spawn-animals", true);
   public final boolean spawnNpcs = this.get("spawn-npcs", true);
   public final boolean pvp = this.get("pvp", true);
   public final boolean allowFlight = this.get("allow-flight", false);
   public final String resourcePack = this.get("resource-pack", "");
   public final String motd = this.get("motd", "A Minecraft Server");
   public final boolean forceGameMode = this.get("force-gamemode", false);
   public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
   public final Difficulty difficulty = this.get("difficulty", dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY);
   public final GameType gamemode = this.get("gamemode", dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
   public final String levelName = this.get("level-name", "world");
   public final int serverPort = this.get("server-port", 25565);
   public final int maxBuildHeight = this.get("max-build-height", (p_218987_0_) -> {
      return MathHelper.clamp((p_218987_0_ + 8) / 16 * 16, 64, 256);
   }, 256);
   public final Boolean announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
   public final boolean enableQuery = this.get("enable-query", false);
   public final int queryPort = this.get("query.port", 25565);
   public final boolean enableRcon = this.get("enable-rcon", false);
   public final int rconPort = this.get("rcon.port", 25575);
   public final String rconPassword = this.get("rcon.password", "");
   public final String resourcePackHash = this.getLegacyString("resource-pack-hash");
   public final String resourcePackSha1 = this.get("resource-pack-sha1", "");
   public final boolean hardcore = this.get("hardcore", false);
   public final boolean allowNether = this.get("allow-nether", true);
   public final boolean spawnMonsters = this.get("spawn-monsters", true);
   public final boolean snooperEnabled;
   public final boolean useNativeTransport;
   public final boolean enableCommandBlock;
   public final int spawnProtection;
   public final int opPermissionLevel;
   public final int functionPermissionLevel;
   public final long maxTickTime;
   public final int rateLimitPacketsPerSecond;
   public final int viewDistance;
   public final int maxPlayers;
   public final int networkCompressionThreshold;
   public final boolean broadcastRconToOps;
   public final boolean broadcastConsoleToOps;
   public final int maxWorldSize;
   public final boolean syncChunkWrites;
   public final boolean enableJmxMonitoring;
   public final boolean enableStatus;
   public final int entityBroadcastRangePercentage;
   public final String textFilteringConfig;
   public final PropertyManager<ServerProperties>.Property<Integer> playerIdleTimeout;
   public final PropertyManager<ServerProperties>.Property<Boolean> whiteList;
   public final DimensionGeneratorSettings worldGenSettings;

   public ServerProperties(Properties p_i242099_1_, DynamicRegistries p_i242099_2_) {
      super(p_i242099_1_);
      if (this.get("snooper-enabled", true)) {
      }

      this.snooperEnabled = false;
      this.useNativeTransport = this.get("use-native-transport", true);
      this.enableCommandBlock = this.get("enable-command-block", false);
      this.spawnProtection = this.get("spawn-protection", 16);
      this.opPermissionLevel = this.get("op-permission-level", 4);
      this.functionPermissionLevel = this.get("function-permission-level", 2);
      this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
      this.rateLimitPacketsPerSecond = this.get("rate-limit", 0);
      this.viewDistance = this.get("view-distance", 10);
      this.maxPlayers = this.get("max-players", 20);
      this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
      this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
      this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
      this.maxWorldSize = this.get("max-world-size", (p_218986_0_) -> {
         return MathHelper.clamp(p_218986_0_, 1, 29999984);
      }, 29999984);
      this.syncChunkWrites = this.get("sync-chunk-writes", true);
      this.enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
      this.enableStatus = this.get("enable-status", true);
      this.entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", (p_241083_0_) -> {
         return MathHelper.clamp(p_241083_0_, 10, 1000);
      }, 100);
      this.textFilteringConfig = this.get("text-filtering-config", "");
      this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
      this.whiteList = this.getMutable("white-list", false);
      this.worldGenSettings = DimensionGeneratorSettings.create(p_i242099_2_, p_i242099_1_);
   }

   public static ServerProperties fromFile(DynamicRegistries p_244380_0_, Path p_244380_1_) {
      return new ServerProperties(loadFromFile(p_244380_1_), p_244380_0_);
   }

   protected ServerProperties reload(DynamicRegistries p_241881_1_, Properties p_241881_2_) {
      return new ServerProperties(p_241881_2_, p_241881_1_);
   }
}
