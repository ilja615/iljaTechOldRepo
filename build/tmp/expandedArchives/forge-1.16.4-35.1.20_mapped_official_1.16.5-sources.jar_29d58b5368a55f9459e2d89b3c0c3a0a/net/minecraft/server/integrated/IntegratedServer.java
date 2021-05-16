package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.LanServerPingThread;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.GameType;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private boolean paused;
   private int publishedPort = -1;
   private LanServerPingThread lanPinger;
   private UUID uuid;

   public IntegratedServer(Thread p_i232494_1_, Minecraft p_i232494_2_, DynamicRegistries.Impl p_i232494_3_, SaveFormat.LevelSave p_i232494_4_, ResourcePackList p_i232494_5_, DataPackRegistries p_i232494_6_, IServerConfiguration p_i232494_7_, MinecraftSessionService p_i232494_8_, GameProfileRepository p_i232494_9_, PlayerProfileCache p_i232494_10_, IChunkStatusListenerFactory p_i232494_11_) {
      super(p_i232494_1_, p_i232494_3_, p_i232494_4_, p_i232494_7_, p_i232494_5_, p_i232494_2_.getProxy(), p_i232494_2_.getFixerUpper(), p_i232494_6_, p_i232494_8_, p_i232494_9_, p_i232494_10_, p_i232494_11_);
      this.setSingleplayerName(p_i232494_2_.getUser().getName());
      this.setDemo(p_i232494_2_.isDemo());
      this.setMaxBuildHeight(256);
      this.setPlayerList(new IntegratedPlayerList(this, this.registryHolder, this.playerDataStorage));
      this.minecraft = p_i232494_2_;
   }

   public boolean initServer() {
      LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      this.initializeKeyPair();
      if (!net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerAboutToStart(this)) return false;
      this.loadLevel();
      this.setMotd(this.getSingleplayerName() + " - " + this.getWorldData().getLevelName());
      return net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStarting(this);
   }

   public void tickServer(BooleanSupplier p_71217_1_) {
      boolean flag = this.paused;
      this.paused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused();
      IProfiler iprofiler = this.getProfiler();
      if (!flag && this.paused) {
         iprofiler.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAll();
         this.saveAllChunks(false, false, false);
         iprofiler.pop();
      }

      if (!this.paused) {
         super.tickServer(p_71217_1_);
         int i = Math.max(2, this.minecraft.options.renderDistance + -1);
         if (i != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(i);
         }

      }
   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public int getRateLimitPacketsPerSecond() {
      return 0;
   }

   public boolean isEpollEnabled() {
      return false;
   }

   public void onServerCrash(CrashReport p_71228_1_) {
      this.minecraft.delayCrash(p_71228_1_);
   }

   public CrashReport fillReport(CrashReport p_71230_1_) {
      p_71230_1_ = super.fillReport(p_71230_1_);
      p_71230_1_.getSystemDetails().setDetail("Type", "Integrated Server (map_client.txt)");
      p_71230_1_.getSystemDetails().setDetail("Is Modded", () -> {
         return this.getModdedStatus().orElse("Probably not. Jar signature remains and both client + server brands are untouched.");
      });
      return p_71230_1_;
   }

   public Optional<String> getModdedStatus() {
      String s = ClientBrandRetriever.getClientModName();
      if (!s.equals("vanilla")) {
         return Optional.of("Definitely; Client brand changed to '" + s + "'");
      } else {
         s = this.getServerModName();
         if (!"vanilla".equals(s)) {
            return Optional.of("Definitely; Server brand changed to '" + s + "'");
         } else {
            return Minecraft.class.getSigners() == null ? Optional.of("Very likely; Jar signature invalidated") : Optional.empty();
         }
      }
   }

   public void populateSnooper(Snooper p_70000_1_) {
      super.populateSnooper(p_70000_1_);
      p_70000_1_.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
   }

   public boolean publishServer(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_) {
      try {
         this.getConnection().startTcpServerListener((InetAddress)null, p_195565_3_);
         LOGGER.info("Started serving on {}", (int)p_195565_3_);
         this.publishedPort = p_195565_3_;
         this.lanPinger = new LanServerPingThread(this.getMotd(), p_195565_3_ + "");
         this.lanPinger.start();
         this.getPlayerList().setOverrideGameMode(p_195565_1_);
         this.getPlayerList().setAllowCheatsForAllPlayers(p_195565_2_);
         int i = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(i);

         for(ServerPlayerEntity serverplayerentity : this.getPlayerList().getPlayers()) {
            this.getCommands().sendCommands(serverplayerentity);
         }

         return true;
      } catch (IOException ioexception) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public void halt(boolean p_71263_1_) {
      if (isRunning())
      this.executeBlocking(() -> {
         for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!serverplayerentity.getUUID().equals(this.uuid)) {
               this.getPlayerList().remove(serverplayerentity);
            }
         }

      });
      super.halt(p_71263_1_);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   public int getPort() {
      return this.publishedPort;
   }

   public void setDefaultGameType(GameType p_71235_1_) {
      super.setDefaultGameType(p_71235_1_);
      this.getPlayerList().setOverrideGameMode(p_71235_1_);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID p_211527_1_) {
      this.uuid = p_211527_1_;
   }

   public boolean isSingleplayerOwner(GameProfile p_213199_1_) {
      return p_213199_1_.getName().equalsIgnoreCase(this.getSingleplayerName());
   }

   public int getScaledTrackingDistance(int p_230512_1_) {
      return (int)(this.minecraft.options.entityDistanceScaling * (float)p_230512_1_);
   }

   public boolean forceSynchronousWrites() {
      return this.minecraft.options.syncWrites;
   }
}
