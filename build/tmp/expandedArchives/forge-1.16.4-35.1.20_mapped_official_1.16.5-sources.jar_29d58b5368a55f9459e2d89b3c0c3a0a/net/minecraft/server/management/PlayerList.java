package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.PlayerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
   public static final File USERBANLIST_FILE = new File("banned-players.json");
   public static final File IPBANLIST_FILE = new File("banned-ips.json");
   public static final File OPLIST_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List<ServerPlayerEntity> players = Lists.newArrayList();
   private final Map<UUID, ServerPlayerEntity> playersByUUID = Maps.newHashMap();
   private final BanList bans = new BanList(USERBANLIST_FILE);
   private final IPBanList ipBans = new IPBanList(IPBANLIST_FILE);
   private final OpList ops = new OpList(OPLIST_FILE);
   private final WhiteList whitelist = new WhiteList(WHITELIST_FILE);
   private final Map<UUID, ServerStatisticsManager> stats = Maps.newHashMap();
   private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
   private final PlayerData playerIo;
   private boolean doWhiteList;
   private final DynamicRegistries.Impl registryHolder;
   protected final int maxPlayers;
   private int viewDistance;
   private GameType overrideGameMode;
   private boolean allowCheatsForAllPlayers;
   private int sendAllPlayerInfoIn;
   private final List<ServerPlayerEntity> playersView = java.util.Collections.unmodifiableList(players);

   public PlayerList(MinecraftServer p_i231425_1_, DynamicRegistries.Impl p_i231425_2_, PlayerData p_i231425_3_, int p_i231425_4_) {
      this.server = p_i231425_1_;
      this.registryHolder = p_i231425_2_;
      this.maxPlayers = p_i231425_4_;
      this.playerIo = p_i231425_3_;
   }

   public void placeNewPlayer(NetworkManager p_72355_1_, ServerPlayerEntity p_72355_2_) {
      GameProfile gameprofile = p_72355_2_.getGameProfile();
      PlayerProfileCache playerprofilecache = this.server.getProfileCache();
      GameProfile gameprofile1 = playerprofilecache.get(gameprofile.getId());
      String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
      playerprofilecache.add(gameprofile);
      CompoundNBT compoundnbt = this.load(p_72355_2_);
      RegistryKey<World> registrykey = compoundnbt != null ? DimensionType.parseLegacy(new Dynamic<>(NBTDynamicOps.INSTANCE, compoundnbt.get("Dimension"))).resultOrPartial(LOGGER::error).orElse(World.OVERWORLD) : World.OVERWORLD;
      ServerWorld serverworld = this.server.getLevel(registrykey);
      ServerWorld serverworld1;
      if (serverworld == null) {
         LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", (Object)registrykey);
         serverworld1 = this.server.overworld();
      } else {
         serverworld1 = serverworld;
      }

      p_72355_2_.setLevel(serverworld1);
      p_72355_2_.gameMode.setLevel((ServerWorld)p_72355_2_.level);
      String s1 = "local";
      if (p_72355_1_.getRemoteAddress() != null) {
         s1 = p_72355_1_.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", p_72355_2_.getName().getString(), s1, p_72355_2_.getId(), p_72355_2_.getX(), p_72355_2_.getY(), p_72355_2_.getZ());
      IWorldInfo iworldinfo = serverworld1.getLevelData();
      this.updatePlayerGameMode(p_72355_2_, (ServerPlayerEntity)null, serverworld1);
      ServerPlayNetHandler serverplaynethandler = new ServerPlayNetHandler(this.server, p_72355_1_, p_72355_2_);
      net.minecraftforge.fml.network.NetworkHooks.sendMCRegistryPackets(p_72355_1_, "PLAY_TO_CLIENT");
      GameRules gamerules = serverworld1.getGameRules();
      boolean flag = gamerules.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
      boolean flag1 = gamerules.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
      serverplaynethandler.send(new SJoinGamePacket(p_72355_2_.getId(), p_72355_2_.gameMode.getGameModeForPlayer(), p_72355_2_.gameMode.getPreviousGameModeForPlayer(), BiomeManager.obfuscateSeed(serverworld1.getSeed()), iworldinfo.isHardcore(), this.server.levelKeys(), this.registryHolder, serverworld1.dimensionType(), serverworld1.dimension(), this.getMaxPlayers(), this.viewDistance, flag1, !flag, serverworld1.isDebug(), serverworld1.isFlat()));
      serverplaynethandler.send(new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeUtf(this.getServer().getServerModName())));
      serverplaynethandler.send(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
      serverplaynethandler.send(new SPlayerAbilitiesPacket(p_72355_2_.abilities));
      serverplaynethandler.send(new SHeldItemChangePacket(p_72355_2_.inventory.selected));
      serverplaynethandler.send(new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      serverplaynethandler.send(new STagsListPacket(this.server.getTags()));
      net.minecraftforge.fml.network.NetworkHooks.syncCustomTagTypes(p_72355_2_, this.server.getTags());
      this.sendPlayerPermissionLevel(p_72355_2_);
      p_72355_2_.getStats().markAllDirty();
      p_72355_2_.getRecipeBook().sendInitialRecipeBook(p_72355_2_);
      this.updateEntireScoreboard(serverworld1.getScoreboard(), p_72355_2_);
      this.server.invalidateStatus();
      IFormattableTextComponent iformattabletextcomponent;
      if (p_72355_2_.getGameProfile().getName().equalsIgnoreCase(s)) {
         iformattabletextcomponent = new TranslationTextComponent("multiplayer.player.joined", p_72355_2_.getDisplayName());
      } else {
         iformattabletextcomponent = new TranslationTextComponent("multiplayer.player.joined.renamed", p_72355_2_.getDisplayName(), s);
      }

      this.broadcastMessage(iformattabletextcomponent.withStyle(TextFormatting.YELLOW), ChatType.SYSTEM, Util.NIL_UUID);
      serverplaynethandler.teleport(p_72355_2_.getX(), p_72355_2_.getY(), p_72355_2_.getZ(), p_72355_2_.yRot, p_72355_2_.xRot);
      this.addPlayer(p_72355_2_);
      this.playersByUUID.put(p_72355_2_.getUUID(), p_72355_2_);
      this.broadcastAll(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, p_72355_2_));

      for(int i = 0; i < this.players.size(); ++i) {
         p_72355_2_.connection.send(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, this.players.get(i)));
      }

      serverworld1.addNewPlayer(p_72355_2_);
      this.server.getCustomBossEvents().onPlayerConnect(p_72355_2_);
      this.sendLevelInfo(p_72355_2_, serverworld1);
      if (!this.server.getResourcePack().isEmpty()) {
         p_72355_2_.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash());
      }

      for(EffectInstance effectinstance : p_72355_2_.getActiveEffects()) {
         serverplaynethandler.send(new SPlayEntityEffectPacket(p_72355_2_.getId(), effectinstance));
      }

      if (compoundnbt != null && compoundnbt.contains("RootVehicle", 10)) {
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("RootVehicle");
         Entity entity1 = EntityType.loadEntityRecursive(compoundnbt1.getCompound("Entity"), serverworld1, (p_217885_1_) -> {
            return !serverworld1.addWithUUID(p_217885_1_) ? null : p_217885_1_;
         });
         if (entity1 != null) {
            UUID uuid;
            if (compoundnbt1.hasUUID("Attach")) {
               uuid = compoundnbt1.getUUID("Attach");
            } else {
               uuid = null;
            }

            if (entity1.getUUID().equals(uuid)) {
               p_72355_2_.startRiding(entity1, true);
            } else {
               for(Entity entity : entity1.getIndirectPassengers()) {
                  if (entity.getUUID().equals(uuid)) {
                     p_72355_2_.startRiding(entity, true);
                     break;
                  }
               }
            }

            if (!p_72355_2_.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               serverworld1.despawn(entity1);

               for(Entity entity2 : entity1.getIndirectPassengers()) {
                  serverworld1.despawn(entity2);
               }
            }
         }
      }

      p_72355_2_.initMenu();
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedIn( p_72355_2_ );
   }

   protected void updateEntireScoreboard(ServerScoreboard p_96456_1_, ServerPlayerEntity p_96456_2_) {
      Set<ScoreObjective> set = Sets.newHashSet();

      for(ScorePlayerTeam scoreplayerteam : p_96456_1_.getPlayerTeams()) {
         p_96456_2_.connection.send(new STeamsPacket(scoreplayerteam, 0));
      }

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = p_96456_1_.getDisplayObjective(i);
         if (scoreobjective != null && !set.contains(scoreobjective)) {
            for(IPacket<?> ipacket : p_96456_1_.getStartTrackingPackets(scoreobjective)) {
               p_96456_2_.connection.send(ipacket);
            }

            set.add(scoreobjective);
         }
      }

   }

   public void setLevel(ServerWorld p_212504_1_) {
      p_212504_1_.getWorldBorder().addListener(new IBorderListener() {
         public void onBorderSizeSet(WorldBorder p_177694_1_, double p_177694_2_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177694_1_, SWorldBorderPacket.Action.SET_SIZE));
         }

         public void onBorderSizeLerping(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177692_1_, SWorldBorderPacket.Action.LERP_SIZE));
         }

         public void onBorderCenterSet(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177693_1_, SWorldBorderPacket.Action.SET_CENTER));
         }

         public void onBorderSetWarningTime(WorldBorder p_177691_1_, int p_177691_2_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177691_1_, SWorldBorderPacket.Action.SET_WARNING_TIME));
         }

         public void onBorderSetWarningBlocks(WorldBorder p_177690_1_, int p_177690_2_) {
            PlayerList.this.broadcastAll(new SWorldBorderPacket(p_177690_1_, SWorldBorderPacket.Action.SET_WARNING_BLOCKS));
         }

         public void onBorderSetDamagePerBlock(WorldBorder p_177696_1_, double p_177696_2_) {
         }

         public void onBorderSetDamageSafeZOne(WorldBorder p_177695_1_, double p_177695_2_) {
         }
      });
   }

   @Nullable
   public CompoundNBT load(ServerPlayerEntity p_72380_1_) {
      CompoundNBT compoundnbt = this.server.getWorldData().getLoadedPlayerTag();
      CompoundNBT compoundnbt1;
      if (p_72380_1_.getName().getString().equals(this.server.getSingleplayerName()) && compoundnbt != null) {
         compoundnbt1 = compoundnbt;
         p_72380_1_.load(compoundnbt);
         LOGGER.debug("loading single player");
         net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(p_72380_1_, this.playerIo, p_72380_1_.getUUID().toString());
      } else {
         compoundnbt1 = this.playerIo.load(p_72380_1_);
      }

      return compoundnbt1;
   }

   protected void save(ServerPlayerEntity p_72391_1_) {
      if (p_72391_1_.connection == null) return;
      this.playerIo.save(p_72391_1_);
      ServerStatisticsManager serverstatisticsmanager = this.stats.get(p_72391_1_.getUUID());
      if (serverstatisticsmanager != null) {
         serverstatisticsmanager.save();
      }

      PlayerAdvancements playeradvancements = this.advancements.get(p_72391_1_.getUUID());
      if (playeradvancements != null) {
         playeradvancements.save();
      }

   }

   public void remove(ServerPlayerEntity p_72367_1_) {
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedOut(p_72367_1_);
      ServerWorld serverworld = p_72367_1_.getLevel();
      p_72367_1_.awardStat(Stats.LEAVE_GAME);
      this.save(p_72367_1_);
      if (p_72367_1_.isPassenger()) {
         Entity entity = p_72367_1_.getRootVehicle();
         if (entity.hasOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            p_72367_1_.stopRiding();
            serverworld.despawn(entity);
            entity.removed = true;

            for(Entity entity1 : entity.getIndirectPassengers()) {
               serverworld.despawn(entity1);
               entity1.removed = true;
            }

            serverworld.getChunk(p_72367_1_.xChunk, p_72367_1_.zChunk).markUnsaved();
         }
      }

      p_72367_1_.unRide();
      serverworld.removePlayerImmediately(p_72367_1_);
      p_72367_1_.getAdvancements().stopListening();
      this.removePlayer(p_72367_1_);
      this.server.getCustomBossEvents().onPlayerDisconnect(p_72367_1_);
      UUID uuid = p_72367_1_.getUUID();
      ServerPlayerEntity serverplayerentity = this.playersByUUID.get(uuid);
      if (serverplayerentity == p_72367_1_) {
         this.playersByUUID.remove(uuid);
         this.stats.remove(uuid);
         this.advancements.remove(uuid);
      }

      this.broadcastAll(new SPlayerListItemPacket(SPlayerListItemPacket.Action.REMOVE_PLAYER, p_72367_1_));
   }

   @Nullable
   public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      if (this.bans.isBanned(p_206258_2_)) {
         ProfileBanEntry profilebanentry = this.bans.get(p_206258_2_);
         IFormattableTextComponent iformattabletextcomponent1 = new TranslationTextComponent("multiplayer.disconnect.banned.reason", profilebanentry.getReason());
         if (profilebanentry.getExpires() != null) {
            iformattabletextcomponent1.append(new TranslationTextComponent("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format(profilebanentry.getExpires())));
         }

         return iformattabletextcomponent1;
      } else if (!this.isWhiteListed(p_206258_2_)) {
         return new TranslationTextComponent("multiplayer.disconnect.not_whitelisted");
      } else if (this.ipBans.isBanned(p_206258_1_)) {
         IPBanEntry ipbanentry = this.ipBans.get(p_206258_1_);
         IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent("multiplayer.disconnect.banned_ip.reason", ipbanentry.getReason());
         if (ipbanentry.getExpires() != null) {
            iformattabletextcomponent.append(new TranslationTextComponent("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format(ipbanentry.getExpires())));
         }

         return iformattabletextcomponent;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(p_206258_2_) ? new TranslationTextComponent("multiplayer.disconnect.server_full") : null;
      }
   }

   public ServerPlayerEntity getPlayerForLogin(GameProfile p_148545_1_) {
      UUID uuid = PlayerEntity.createPlayerUUID(p_148545_1_);
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity.getUUID().equals(uuid)) {
            list.add(serverplayerentity);
         }
      }

      ServerPlayerEntity serverplayerentity2 = this.playersByUUID.get(p_148545_1_.getId());
      if (serverplayerentity2 != null && !list.contains(serverplayerentity2)) {
         list.add(serverplayerentity2);
      }

      for(ServerPlayerEntity serverplayerentity1 : list) {
         serverplayerentity1.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.duplicate_login"));
      }

      ServerWorld serverworld = this.server.overworld();
      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(serverworld);
      } else {
         playerinteractionmanager = new PlayerInteractionManager(serverworld);
      }

      return new ServerPlayerEntity(this.server, serverworld, p_148545_1_, playerinteractionmanager);
   }

   public ServerPlayerEntity respawn(ServerPlayerEntity p_232644_1_, boolean p_232644_2_) {
      this.removePlayer(p_232644_1_);
      p_232644_1_.getLevel().removePlayer(p_232644_1_, true); // Forge: keep data until copyFrom called
      BlockPos blockpos = p_232644_1_.getRespawnPosition();
      float f = p_232644_1_.getRespawnAngle();
      boolean flag = p_232644_1_.isRespawnForced();
      ServerWorld serverworld = this.server.getLevel(p_232644_1_.getRespawnDimension());
      Optional<Vector3d> optional;
      if (serverworld != null && blockpos != null) {
         optional = PlayerEntity.findRespawnPositionAndUseSpawnBlock(serverworld, blockpos, f, flag, p_232644_2_);
      } else {
         optional = Optional.empty();
      }

      ServerWorld serverworld1 = serverworld != null && optional.isPresent() ? serverworld : this.server.overworld();
      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(serverworld1);
      } else {
         playerinteractionmanager = new PlayerInteractionManager(serverworld1);
      }

      ServerPlayerEntity serverplayerentity = new ServerPlayerEntity(this.server, serverworld1, p_232644_1_.getGameProfile(), playerinteractionmanager);
      serverplayerentity.connection = p_232644_1_.connection;
      serverplayerentity.restoreFrom(p_232644_1_, p_232644_2_);
      p_232644_1_.remove(false); // Forge: clone event had a chance to see old data, now discard it
      serverplayerentity.setId(p_232644_1_.getId());
      serverplayerentity.setMainArm(p_232644_1_.getMainArm());

      for(String s : p_232644_1_.getTags()) {
         serverplayerentity.addTag(s);
      }

      this.updatePlayerGameMode(serverplayerentity, p_232644_1_, serverworld1);
      boolean flag2 = false;
      if (optional.isPresent()) {
         BlockState blockstate = serverworld1.getBlockState(blockpos);
         boolean flag1 = blockstate.is(Blocks.RESPAWN_ANCHOR);
         Vector3d vector3d = optional.get();
         float f1;
         if (!blockstate.is(BlockTags.BEDS) && !flag1) {
            f1 = f;
         } else {
            Vector3d vector3d1 = Vector3d.atBottomCenterOf(blockpos).subtract(vector3d).normalize();
            f1 = (float)MathHelper.wrapDegrees(MathHelper.atan2(vector3d1.z, vector3d1.x) * (double)(180F / (float)Math.PI) - 90.0D);
         }

         serverplayerentity.moveTo(vector3d.x, vector3d.y, vector3d.z, f1, 0.0F);
         serverplayerentity.setRespawnPosition(serverworld1.dimension(), blockpos, f, flag, false);
         flag2 = !p_232644_2_ && flag1;
      } else if (blockpos != null) {
         serverplayerentity.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
      }

      while(!serverworld1.noCollision(serverplayerentity) && serverplayerentity.getY() < 256.0D) {
         serverplayerentity.setPos(serverplayerentity.getX(), serverplayerentity.getY() + 1.0D, serverplayerentity.getZ());
      }

      IWorldInfo iworldinfo = serverplayerentity.level.getLevelData();
      serverplayerentity.connection.send(new SRespawnPacket(serverplayerentity.level.dimensionType(), serverplayerentity.level.dimension(), BiomeManager.obfuscateSeed(serverplayerentity.getLevel().getSeed()), serverplayerentity.gameMode.getGameModeForPlayer(), serverplayerentity.gameMode.getPreviousGameModeForPlayer(), serverplayerentity.getLevel().isDebug(), serverplayerentity.getLevel().isFlat(), p_232644_2_));
      serverplayerentity.connection.teleport(serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), serverplayerentity.yRot, serverplayerentity.xRot);
      serverplayerentity.connection.send(new SWorldSpawnChangedPacket(serverworld1.getSharedSpawnPos(), serverworld1.getSharedSpawnAngle()));
      serverplayerentity.connection.send(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
      serverplayerentity.connection.send(new SSetExperiencePacket(serverplayerentity.experienceProgress, serverplayerentity.totalExperience, serverplayerentity.experienceLevel));
      this.sendLevelInfo(serverplayerentity, serverworld1);
      this.sendPlayerPermissionLevel(serverplayerentity);
      serverworld1.addRespawnedPlayer(serverplayerentity);
      this.addPlayer(serverplayerentity);
      this.playersByUUID.put(serverplayerentity.getUUID(), serverplayerentity);
      serverplayerentity.initMenu();
      serverplayerentity.setHealth(serverplayerentity.getHealth());
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerRespawnEvent(serverplayerentity, p_232644_2_);
      if (flag2) {
         serverplayerentity.connection.send(new SPlaySoundEffectPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0F, 1.0F));
      }

      return serverplayerentity;
   }

   public void sendPlayerPermissionLevel(ServerPlayerEntity p_187243_1_) {
      GameProfile gameprofile = p_187243_1_.getGameProfile();
      int i = this.server.getProfilePermissions(gameprofile);
      this.sendPlayerPermissionLevel(p_187243_1_, i);
   }

   public void tick() {
      if (++this.sendAllPlayerInfoIn > 600) {
         this.broadcastAll(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_LATENCY, this.players));
         this.sendAllPlayerInfoIn = 0;
      }

   }

   public void broadcastAll(IPacket<?> p_148540_1_) {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.send(p_148540_1_);
      }

   }

   public void broadcastAll(IPacket<?> p_232642_1_, RegistryKey<World> p_232642_2_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity.level.dimension() == p_232642_2_) {
            serverplayerentity.connection.send(p_232642_1_);
         }
      }

   }

   public void broadcastToTeam(PlayerEntity p_177453_1_, ITextComponent p_177453_2_) {
      Team team = p_177453_1_.getTeam();
      if (team != null) {
         for(String s : team.getPlayers()) {
            ServerPlayerEntity serverplayerentity = this.getPlayerByName(s);
            if (serverplayerentity != null && serverplayerentity != p_177453_1_) {
               serverplayerentity.sendMessage(p_177453_2_, p_177453_1_.getUUID());
            }
         }

      }
   }

   public void broadcastToAllExceptTeam(PlayerEntity p_177452_1_, ITextComponent p_177452_2_) {
      Team team = p_177452_1_.getTeam();
      if (team == null) {
         this.broadcastMessage(p_177452_2_, ChatType.SYSTEM, p_177452_1_.getUUID());
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverplayerentity = this.players.get(i);
            if (serverplayerentity.getTeam() != team) {
               serverplayerentity.sendMessage(p_177452_2_, p_177452_1_.getUUID());
            }
         }

      }
   }

   public String[] getPlayerNamesArray() {
      String[] astring = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         astring[i] = this.players.get(i).getGameProfile().getName();
      }

      return astring;
   }

   public BanList getBans() {
      return this.bans;
   }

   public IPBanList getIpBans() {
      return this.ipBans;
   }

   public void op(GameProfile p_152605_1_) {
      this.ops.add(new OpEntry(p_152605_1_, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(p_152605_1_)));
      ServerPlayerEntity serverplayerentity = this.getPlayer(p_152605_1_.getId());
      if (serverplayerentity != null) {
         this.sendPlayerPermissionLevel(serverplayerentity);
      }

   }

   public void deop(GameProfile p_152610_1_) {
      this.ops.remove(p_152610_1_);
      ServerPlayerEntity serverplayerentity = this.getPlayer(p_152610_1_.getId());
      if (serverplayerentity != null) {
         this.sendPlayerPermissionLevel(serverplayerentity);
      }

   }

   private void sendPlayerPermissionLevel(ServerPlayerEntity p_187245_1_, int p_187245_2_) {
      if (p_187245_1_.connection != null) {
         byte b0;
         if (p_187245_2_ <= 0) {
            b0 = 24;
         } else if (p_187245_2_ >= 4) {
            b0 = 28;
         } else {
            b0 = (byte)(24 + p_187245_2_);
         }

         p_187245_1_.connection.send(new SEntityStatusPacket(p_187245_1_, b0));
      }

      this.server.getCommands().sendCommands(p_187245_1_);
   }

   public boolean isWhiteListed(GameProfile p_152607_1_) {
      return !this.doWhiteList || this.ops.contains(p_152607_1_) || this.whitelist.contains(p_152607_1_);
   }

   public boolean isOp(GameProfile p_152596_1_) {
      return this.ops.contains(p_152596_1_) || this.server.isSingleplayerOwner(p_152596_1_) && this.server.getWorldData().getAllowCommands() || this.allowCheatsForAllPlayers;
   }

   @Nullable
   public ServerPlayerEntity getPlayerByName(String p_152612_1_) {
      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.getGameProfile().getName().equalsIgnoreCase(p_152612_1_)) {
            return serverplayerentity;
         }
      }

      return null;
   }

   public void broadcast(@Nullable PlayerEntity p_148543_1_, double p_148543_2_, double p_148543_4_, double p_148543_6_, double p_148543_8_, RegistryKey<World> p_148543_10_, IPacket<?> p_148543_11_) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity != p_148543_1_ && serverplayerentity.level.dimension() == p_148543_10_) {
            double d0 = p_148543_2_ - serverplayerentity.getX();
            double d1 = p_148543_4_ - serverplayerentity.getY();
            double d2 = p_148543_6_ - serverplayerentity.getZ();
            if (d0 * d0 + d1 * d1 + d2 * d2 < p_148543_8_ * p_148543_8_) {
               serverplayerentity.connection.send(p_148543_11_);
            }
         }
      }

   }

   public void saveAll() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.save(this.players.get(i));
      }

   }

   public WhiteList getWhiteList() {
      return this.whitelist;
   }

   public String[] getWhiteListNames() {
      return this.whitelist.getUserList();
   }

   public OpList getOps() {
      return this.ops;
   }

   public String[] getOpNames() {
      return this.ops.getUserList();
   }

   public void reloadWhiteList() {
   }

   public void sendLevelInfo(ServerPlayerEntity p_72354_1_, ServerWorld p_72354_2_) {
      WorldBorder worldborder = this.server.overworld().getWorldBorder();
      p_72354_1_.connection.send(new SWorldBorderPacket(worldborder, SWorldBorderPacket.Action.INITIALIZE));
      p_72354_1_.connection.send(new SUpdateTimePacket(p_72354_2_.getGameTime(), p_72354_2_.getDayTime(), p_72354_2_.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      p_72354_1_.connection.send(new SWorldSpawnChangedPacket(p_72354_2_.getSharedSpawnPos(), p_72354_2_.getSharedSpawnAngle()));
      if (p_72354_2_.isRaining()) {
         p_72354_1_.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.START_RAINING, 0.0F));
         p_72354_1_.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.RAIN_LEVEL_CHANGE, p_72354_2_.getRainLevel(1.0F)));
         p_72354_1_.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.THUNDER_LEVEL_CHANGE, p_72354_2_.getThunderLevel(1.0F)));
      }

   }

   public void sendAllPlayerInfo(ServerPlayerEntity p_72385_1_) {
      p_72385_1_.refreshContainer(p_72385_1_.inventoryMenu);
      p_72385_1_.resetSentInfo();
      p_72385_1_.connection.send(new SHeldItemChangePacket(p_72385_1_.inventory.selected));
   }

   public int getPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isUsingWhitelist() {
      return this.doWhiteList;
   }

   public void setUsingWhiteList(boolean p_72371_1_) {
      this.doWhiteList = p_72371_1_;
   }

   public List<ServerPlayerEntity> getPlayersWithAddress(String p_72382_1_) {
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.getIpAddress().equals(p_72382_1_)) {
            list.add(serverplayerentity);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public CompoundNBT getSingleplayerData() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setOverrideGameMode(GameType p_152604_1_) {
      this.overrideGameMode = p_152604_1_;
   }

   private void updatePlayerGameMode(ServerPlayerEntity p_72381_1_, @Nullable ServerPlayerEntity p_72381_2_, ServerWorld p_72381_3_) {
      if (p_72381_2_ != null) {
         p_72381_1_.gameMode.setGameModeForPlayer(p_72381_2_.gameMode.getGameModeForPlayer(), p_72381_2_.gameMode.getPreviousGameModeForPlayer());
      } else if (this.overrideGameMode != null) {
         p_72381_1_.gameMode.setGameModeForPlayer(this.overrideGameMode, GameType.NOT_SET);
      }

      p_72381_1_.gameMode.updateGameMode(p_72381_3_.getServer().getWorldData().getGameType());
   }

   @OnlyIn(Dist.CLIENT)
   public void setAllowCheatsForAllPlayers(boolean p_72387_1_) {
      this.allowCheatsForAllPlayers = p_72387_1_;
   }

   public void removeAll() {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void broadcastMessage(ITextComponent p_232641_1_, ChatType p_232641_2_, UUID p_232641_3_) {
      this.server.sendMessage(p_232641_1_, p_232641_3_);
      this.broadcastAll(new SChatPacket(p_232641_1_, p_232641_2_, p_232641_3_));
   }

   public ServerStatisticsManager getPlayerStats(PlayerEntity p_152602_1_) {
      UUID uuid = p_152602_1_.getUUID();
      ServerStatisticsManager serverstatisticsmanager = uuid == null ? null : this.stats.get(uuid);
      if (serverstatisticsmanager == null) {
         File file1 = this.server.getWorldPath(FolderName.PLAYER_STATS_DIR).toFile();
         File file2 = new File(file1, uuid + ".json");
         if (!file2.exists()) {
            File file3 = new File(file1, p_152602_1_.getName().getString() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         serverstatisticsmanager = new ServerStatisticsManager(this.server, file2);
         this.stats.put(uuid, serverstatisticsmanager);
      }

      return serverstatisticsmanager;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayerEntity p_192054_1_) {
      UUID uuid = p_192054_1_.getUUID();
      PlayerAdvancements playeradvancements = this.advancements.get(uuid);
      if (playeradvancements == null) {
         File file1 = this.server.getWorldPath(FolderName.PLAYER_ADVANCEMENTS_DIR).toFile();
         File file2 = new File(file1, uuid + ".json");
         playeradvancements = new PlayerAdvancements(this.server.getFixerUpper(), this, this.server.getAdvancements(), file2, p_192054_1_);
         this.advancements.put(uuid, playeradvancements);
      }

      // Forge: don't overwrite active player with a fake one.
      if (!(p_192054_1_ instanceof net.minecraftforge.common.util.FakePlayer))
      playeradvancements.setPlayer(p_192054_1_);
      return playeradvancements;
   }

   public void setViewDistance(int p_217884_1_) {
      this.viewDistance = p_217884_1_;
      this.broadcastAll(new SUpdateViewDistancePacket(p_217884_1_));

      for(ServerWorld serverworld : this.server.getAllLevels()) {
         if (serverworld != null) {
            serverworld.getChunkSource().setViewDistance(p_217884_1_);
         }
      }

   }

   public List<ServerPlayerEntity> getPlayers() {
      return this.playersView; //Unmodifiable view, we don't want people removing things without us knowing.
   }

   @Nullable
   public ServerPlayerEntity getPlayer(UUID p_177451_1_) {
      return this.playersByUUID.get(p_177451_1_);
   }

   public boolean canBypassPlayerLimit(GameProfile p_183023_1_) {
      return false;
   }

   public void reloadResources() {
      for(PlayerAdvancements playeradvancements : this.advancements.values()) {
         playeradvancements.reload(this.server.getAdvancements());
      }

      this.broadcastAll(new STagsListPacket(this.server.getTags()));
      net.minecraftforge.fml.network.NetworkHooks.syncCustomTagTypes(this.server.getTags());
      SUpdateRecipesPacket supdaterecipespacket = new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());

      for(ServerPlayerEntity serverplayerentity : this.players) {
         serverplayerentity.connection.send(supdaterecipespacket);
         serverplayerentity.getRecipeBook().sendInitialRecipeBook(serverplayerentity);
      }

   }

   public boolean isAllowCheatsForAllPlayers() {
      return this.allowCheatsForAllPlayers;
   }

   public boolean addPlayer(ServerPlayerEntity player) {
      return players.add(player);
   }

   public boolean removePlayer(ServerPlayerEntity player) {
       return this.players.remove(player);
   }
}
