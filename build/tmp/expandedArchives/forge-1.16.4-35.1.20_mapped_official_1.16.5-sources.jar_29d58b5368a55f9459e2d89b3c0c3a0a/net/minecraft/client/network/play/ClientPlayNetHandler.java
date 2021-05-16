package net.minecraft.client.network.play;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BeeAngrySound;
import net.minecraft.client.audio.BeeFlightSound;
import net.minecraft.client.audio.BeeSound;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MinecartTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.EntityAIDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateDebugRenderer;
import net.minecraft.client.renderer.debug.PointOfInterestDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptsDebugRenderer;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.IMutableSearchTree;
import net.minecraft.client.util.NBTQueryManager;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientPlayNetHandler implements IClientPlayNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ITextComponent GENERIC_DISCONNECT_MESSAGE = new TranslationTextComponent("disconnect.lost");
   private final NetworkManager connection;
   private final GameProfile localGameProfile;
   private final Screen callbackScreen;
   private Minecraft minecraft;
   private ClientWorld level;
   private ClientWorld.ClientWorldInfo levelData;
   private boolean started;
   private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   private final ClientAdvancementManager advancements;
   private final ClientSuggestionProvider suggestionsProvider;
   private ITagCollectionSupplier tags = ITagCollectionSupplier.EMPTY;
   private final NBTQueryManager debugQueryHandler = new NBTQueryManager(this);
   private int serverChunkRadius = 3;
   private final Random random = new Random();
   private CommandDispatcher<ISuggestionProvider> commands = new CommandDispatcher<>();
   private final RecipeManager recipeManager = new RecipeManager();
   private final UUID id = UUID.randomUUID();
   private Set<RegistryKey<World>> levels;
   private DynamicRegistries registryAccess = DynamicRegistries.builtin();

   public ClientPlayNetHandler(Minecraft p_i46300_1_, Screen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
      this.minecraft = p_i46300_1_;
      this.callbackScreen = p_i46300_2_;
      this.connection = p_i46300_3_;
      this.localGameProfile = p_i46300_4_;
      this.advancements = new ClientAdvancementManager(p_i46300_1_);
      this.suggestionsProvider = new ClientSuggestionProvider(this, p_i46300_1_);
   }

   public ClientSuggestionProvider getSuggestionsProvider() {
      return this.suggestionsProvider;
   }

   public void cleanup() {
      this.level = null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public void handleLogin(SJoinGamePacket p_147282_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147282_1_, this, this.minecraft);
      this.minecraft.gameMode = new PlayerController(this.minecraft, this);
      if (!this.connection.isMemoryConnection()) {
         TagRegistryManager.resetAllToEmpty();
      }

      ArrayList<RegistryKey<World>> arraylist = Lists.newArrayList(p_147282_1_.levels());
      Collections.shuffle(arraylist);
      this.levels = Sets.newLinkedHashSet(arraylist);
      this.registryAccess = p_147282_1_.registryAccess();
      RegistryKey<World> registrykey = p_147282_1_.getDimension();
      DimensionType dimensiontype = p_147282_1_.getDimensionType();
      this.serverChunkRadius = p_147282_1_.getChunkRadius();
      boolean flag = p_147282_1_.isDebug();
      boolean flag1 = p_147282_1_.isFlat();
      ClientWorld.ClientWorldInfo clientworld$clientworldinfo = new ClientWorld.ClientWorldInfo(Difficulty.NORMAL, p_147282_1_.isHardcore(), flag1);
      this.levelData = clientworld$clientworldinfo;
      this.level = new ClientWorld(this, clientworld$clientworldinfo, registrykey, dimensiontype, this.serverChunkRadius, this.minecraft::getProfiler, this.minecraft.levelRenderer, flag, p_147282_1_.getSeed());
      this.minecraft.setLevel(this.level);
      if (this.minecraft.player == null) {
         this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatisticsManager(), new ClientRecipeBook());
         this.minecraft.player.yRot = -180.0F;
         if (this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
         }
      }

      this.minecraft.debugRenderer.clear();
      this.minecraft.player.resetPos();
      net.minecraftforge.fml.client.ClientHooks.firePlayerLogin(this.minecraft.gameMode, this.minecraft.player, this.minecraft.getConnection().getConnection());
      int i = p_147282_1_.getPlayerId();
      this.level.addPlayer(i, this.minecraft.player);
      this.minecraft.player.input = new MovementInputFromOptions(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
      this.minecraft.cameraEntity = this.minecraft.player;
      this.minecraft.setScreen(new DownloadTerrainScreen());
      this.minecraft.player.setId(i);
      this.minecraft.player.setReducedDebugInfo(p_147282_1_.isReducedDebugInfo());
      this.minecraft.player.setShowDeathScreen(p_147282_1_.shouldShowDeathScreen());
      this.minecraft.gameMode.setLocalMode(p_147282_1_.getGameType());
      this.minecraft.gameMode.setPreviousLocalMode(p_147282_1_.getPreviousGameType());
      net.minecraftforge.fml.network.NetworkHooks.sendMCRegistryPackets(connection, "PLAY_TO_SERVER");
      this.minecraft.options.broadcastOptions();
      this.connection.send(new CCustomPayloadPacket(CCustomPayloadPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeUtf(ClientBrandRetriever.getClientModName())));
      this.minecraft.getGame().onStartGameSession();
   }

   public void handleAddEntity(SSpawnObjectPacket p_147235_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147235_1_, this, this.minecraft);
      double d0 = p_147235_1_.getX();
      double d1 = p_147235_1_.getY();
      double d2 = p_147235_1_.getZ();
      EntityType<?> entitytype = p_147235_1_.getType();
      Entity entity;
      if (entitytype == EntityType.CHEST_MINECART) {
         entity = new ChestMinecartEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.FURNACE_MINECART) {
         entity = new FurnaceMinecartEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.TNT_MINECART) {
         entity = new TNTMinecartEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.SPAWNER_MINECART) {
         entity = new SpawnerMinecartEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.HOPPER_MINECART) {
         entity = new HopperMinecartEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.COMMAND_BLOCK_MINECART) {
         entity = new CommandBlockMinecartEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.MINECART) {
         entity = new MinecartEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.FISHING_BOBBER) {
         Entity entity1 = this.level.getEntity(p_147235_1_.getData());
         if (entity1 instanceof PlayerEntity) {
            entity = new FishingBobberEntity(this.level, (PlayerEntity)entity1, d0, d1, d2);
         } else {
            entity = null;
         }
      } else if (entitytype == EntityType.ARROW) {
         entity = new ArrowEntity(this.level, d0, d1, d2);
         Entity entity2 = this.level.getEntity(p_147235_1_.getData());
         if (entity2 != null) {
            ((AbstractArrowEntity)entity).setOwner(entity2);
         }
      } else if (entitytype == EntityType.SPECTRAL_ARROW) {
         entity = new SpectralArrowEntity(this.level, d0, d1, d2);
         Entity entity3 = this.level.getEntity(p_147235_1_.getData());
         if (entity3 != null) {
            ((AbstractArrowEntity)entity).setOwner(entity3);
         }
      } else if (entitytype == EntityType.TRIDENT) {
         entity = new TridentEntity(this.level, d0, d1, d2);
         Entity entity4 = this.level.getEntity(p_147235_1_.getData());
         if (entity4 != null) {
            ((AbstractArrowEntity)entity).setOwner(entity4);
         }
      } else if (entitytype == EntityType.SNOWBALL) {
         entity = new SnowballEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.LLAMA_SPIT) {
         entity = new LlamaSpitEntity(this.level, d0, d1, d2, p_147235_1_.getXa(), p_147235_1_.getYa(), p_147235_1_.getZa());
      } else if (entitytype == EntityType.ITEM_FRAME) {
         entity = new ItemFrameEntity(this.level, new BlockPos(d0, d1, d2), Direction.from3DDataValue(p_147235_1_.getData()));
      } else if (entitytype == EntityType.LEASH_KNOT) {
         entity = new LeashKnotEntity(this.level, new BlockPos(d0, d1, d2));
      } else if (entitytype == EntityType.ENDER_PEARL) {
         entity = new EnderPearlEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.EYE_OF_ENDER) {
         entity = new EyeOfEnderEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.FIREWORK_ROCKET) {
         entity = new FireworkRocketEntity(this.level, d0, d1, d2, ItemStack.EMPTY);
      } else if (entitytype == EntityType.FIREBALL) {
         entity = new FireballEntity(this.level, d0, d1, d2, p_147235_1_.getXa(), p_147235_1_.getYa(), p_147235_1_.getZa());
      } else if (entitytype == EntityType.DRAGON_FIREBALL) {
         entity = new DragonFireballEntity(this.level, d0, d1, d2, p_147235_1_.getXa(), p_147235_1_.getYa(), p_147235_1_.getZa());
      } else if (entitytype == EntityType.SMALL_FIREBALL) {
         entity = new SmallFireballEntity(this.level, d0, d1, d2, p_147235_1_.getXa(), p_147235_1_.getYa(), p_147235_1_.getZa());
      } else if (entitytype == EntityType.WITHER_SKULL) {
         entity = new WitherSkullEntity(this.level, d0, d1, d2, p_147235_1_.getXa(), p_147235_1_.getYa(), p_147235_1_.getZa());
      } else if (entitytype == EntityType.SHULKER_BULLET) {
         entity = new ShulkerBulletEntity(this.level, d0, d1, d2, p_147235_1_.getXa(), p_147235_1_.getYa(), p_147235_1_.getZa());
      } else if (entitytype == EntityType.EGG) {
         entity = new EggEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.EVOKER_FANGS) {
         entity = new EvokerFangsEntity(this.level, d0, d1, d2, 0.0F, 0, (LivingEntity)null);
      } else if (entitytype == EntityType.POTION) {
         entity = new PotionEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.EXPERIENCE_BOTTLE) {
         entity = new ExperienceBottleEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.BOAT) {
         entity = new BoatEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.TNT) {
         entity = new TNTEntity(this.level, d0, d1, d2, (LivingEntity)null);
      } else if (entitytype == EntityType.ARMOR_STAND) {
         entity = new ArmorStandEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.END_CRYSTAL) {
         entity = new EnderCrystalEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.ITEM) {
         entity = new ItemEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.FALLING_BLOCK) {
         entity = new FallingBlockEntity(this.level, d0, d1, d2, Block.stateById(p_147235_1_.getData()));
      } else if (entitytype == EntityType.AREA_EFFECT_CLOUD) {
         entity = new AreaEffectCloudEntity(this.level, d0, d1, d2);
      } else if (entitytype == EntityType.LIGHTNING_BOLT) {
         entity = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, this.level);
      } else {
         entity = null;
      }

      if (entity != null) {
         int i = p_147235_1_.getId();
         entity.setPacketCoordinates(d0, d1, d2);
         entity.moveTo(d0, d1, d2);
         entity.xRot = (float)(p_147235_1_.getxRot() * 360) / 256.0F;
         entity.yRot = (float)(p_147235_1_.getyRot() * 360) / 256.0F;
         entity.setId(i);
         entity.setUUID(p_147235_1_.getUUID());
         this.level.putNonPlayerEntity(i, entity);
         if (entity instanceof AbstractMinecartEntity) {
            this.minecraft.getSoundManager().play(new MinecartTickableSound((AbstractMinecartEntity)entity));
         }
      }

   }

   public void handleAddExperienceOrb(SSpawnExperienceOrbPacket p_147286_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147286_1_, this, this.minecraft);
      double d0 = p_147286_1_.getX();
      double d1 = p_147286_1_.getY();
      double d2 = p_147286_1_.getZ();
      Entity entity = new ExperienceOrbEntity(this.level, d0, d1, d2, p_147286_1_.getValue());
      entity.setPacketCoordinates(d0, d1, d2);
      entity.yRot = 0.0F;
      entity.xRot = 0.0F;
      entity.setId(p_147286_1_.getId());
      this.level.putNonPlayerEntity(p_147286_1_.getId(), entity);
   }

   public void handleAddPainting(SSpawnPaintingPacket p_147288_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147288_1_, this, this.minecraft);
      PaintingEntity paintingentity = new PaintingEntity(this.level, p_147288_1_.getPos(), p_147288_1_.getDirection(), p_147288_1_.getMotive());
      paintingentity.setId(p_147288_1_.getId());
      paintingentity.setUUID(p_147288_1_.getUUID());
      this.level.putNonPlayerEntity(p_147288_1_.getId(), paintingentity);
   }

   public void handleSetEntityMotion(SEntityVelocityPacket p_147244_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147244_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147244_1_.getId());
      if (entity != null) {
         entity.lerpMotion((double)p_147244_1_.getXa() / 8000.0D, (double)p_147244_1_.getYa() / 8000.0D, (double)p_147244_1_.getZa() / 8000.0D);
      }
   }

   public void handleSetEntityData(SEntityMetadataPacket p_147284_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147284_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147284_1_.getId());
      if (entity != null && p_147284_1_.getUnpackedData() != null) {
         entity.getEntityData().assignValues(p_147284_1_.getUnpackedData());
      }

   }

   public void handleAddPlayer(SSpawnPlayerPacket p_147237_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147237_1_, this, this.minecraft);
      double d0 = p_147237_1_.getX();
      double d1 = p_147237_1_.getY();
      double d2 = p_147237_1_.getZ();
      float f = (float)(p_147237_1_.getyRot() * 360) / 256.0F;
      float f1 = (float)(p_147237_1_.getxRot() * 360) / 256.0F;
      int i = p_147237_1_.getEntityId();
      RemoteClientPlayerEntity remoteclientplayerentity = new RemoteClientPlayerEntity(this.minecraft.level, this.getPlayerInfo(p_147237_1_.getPlayerId()).getProfile());
      remoteclientplayerentity.setId(i);
      remoteclientplayerentity.setPosAndOldPos(d0, d1, d2);
      remoteclientplayerentity.setPacketCoordinates(d0, d1, d2);
      remoteclientplayerentity.absMoveTo(d0, d1, d2, f, f1);
      this.level.addPlayer(i, remoteclientplayerentity);
   }

   public void handleTeleportEntity(SEntityTeleportPacket p_147275_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147275_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147275_1_.getId());
      if (entity != null) {
         double d0 = p_147275_1_.getX();
         double d1 = p_147275_1_.getY();
         double d2 = p_147275_1_.getZ();
         entity.setPacketCoordinates(d0, d1, d2);
         if (!entity.isControlledByLocalInstance()) {
            float f = (float)(p_147275_1_.getyRot() * 360) / 256.0F;
            float f1 = (float)(p_147275_1_.getxRot() * 360) / 256.0F;
            entity.lerpTo(d0, d1, d2, f, f1, 3, true);
            entity.setOnGround(p_147275_1_.isOnGround());
         }

      }
   }

   public void handleSetCarriedItem(SHeldItemChangePacket p_147257_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147257_1_, this, this.minecraft);
      if (PlayerInventory.isHotbarSlot(p_147257_1_.getSlot())) {
         this.minecraft.player.inventory.selected = p_147257_1_.getSlot();
      }

   }

   public void handleMoveEntity(SEntityPacket p_147259_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147259_1_, this, this.minecraft);
      Entity entity = p_147259_1_.getEntity(this.level);
      if (entity != null) {
         if (!entity.isControlledByLocalInstance()) {
            if (p_147259_1_.hasPosition()) {
               Vector3d vector3d = p_147259_1_.updateEntityPosition(entity.getPacketCoordinates());
               entity.setPacketCoordinates(vector3d);
               float f = p_147259_1_.hasRotation() ? (float)(p_147259_1_.getyRot() * 360) / 256.0F : entity.yRot;
               float f1 = p_147259_1_.hasRotation() ? (float)(p_147259_1_.getxRot() * 360) / 256.0F : entity.xRot;
               entity.lerpTo(vector3d.x(), vector3d.y(), vector3d.z(), f, f1, 3, false);
            } else if (p_147259_1_.hasRotation()) {
               float f2 = (float)(p_147259_1_.getyRot() * 360) / 256.0F;
               float f3 = (float)(p_147259_1_.getxRot() * 360) / 256.0F;
               entity.lerpTo(entity.getX(), entity.getY(), entity.getZ(), f2, f3, 3, false);
            }

            entity.setOnGround(p_147259_1_.isOnGround());
         }

      }
   }

   public void handleRotateMob(SEntityHeadLookPacket p_147267_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147267_1_, this, this.minecraft);
      Entity entity = p_147267_1_.getEntity(this.level);
      if (entity != null) {
         float f = (float)(p_147267_1_.getYHeadRot() * 360) / 256.0F;
         entity.lerpHeadTo(f, 3);
      }
   }

   public void handleRemoveEntity(SDestroyEntitiesPacket p_147238_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147238_1_, this, this.minecraft);

      for(int i = 0; i < p_147238_1_.getEntityIds().length; ++i) {
         int j = p_147238_1_.getEntityIds()[i];
         this.level.removeEntity(j);
      }

   }

   public void handleMovePlayer(SPlayerPositionLookPacket p_184330_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184330_1_, this, this.minecraft);
      PlayerEntity playerentity = this.minecraft.player;
      Vector3d vector3d = playerentity.getDeltaMovement();
      boolean flag = p_184330_1_.getRelativeArguments().contains(SPlayerPositionLookPacket.Flags.X);
      boolean flag1 = p_184330_1_.getRelativeArguments().contains(SPlayerPositionLookPacket.Flags.Y);
      boolean flag2 = p_184330_1_.getRelativeArguments().contains(SPlayerPositionLookPacket.Flags.Z);
      double d0;
      double d1;
      if (flag) {
         d0 = vector3d.x();
         d1 = playerentity.getX() + p_184330_1_.getX();
         playerentity.xOld += p_184330_1_.getX();
      } else {
         d0 = 0.0D;
         d1 = p_184330_1_.getX();
         playerentity.xOld = d1;
      }

      double d2;
      double d3;
      if (flag1) {
         d2 = vector3d.y();
         d3 = playerentity.getY() + p_184330_1_.getY();
         playerentity.yOld += p_184330_1_.getY();
      } else {
         d2 = 0.0D;
         d3 = p_184330_1_.getY();
         playerentity.yOld = d3;
      }

      double d4;
      double d5;
      if (flag2) {
         d4 = vector3d.z();
         d5 = playerentity.getZ() + p_184330_1_.getZ();
         playerentity.zOld += p_184330_1_.getZ();
      } else {
         d4 = 0.0D;
         d5 = p_184330_1_.getZ();
         playerentity.zOld = d5;
      }

      if (playerentity.tickCount > 0 && playerentity.getVehicle() != null) {
         playerentity.removeVehicle();
      }

      playerentity.setPosRaw(d1, d3, d5);
      playerentity.xo = d1;
      playerentity.yo = d3;
      playerentity.zo = d5;
      playerentity.setDeltaMovement(d0, d2, d4);
      float f = p_184330_1_.getYRot();
      float f1 = p_184330_1_.getXRot();
      if (p_184330_1_.getRelativeArguments().contains(SPlayerPositionLookPacket.Flags.X_ROT)) {
         f1 += playerentity.xRot;
      }

      if (p_184330_1_.getRelativeArguments().contains(SPlayerPositionLookPacket.Flags.Y_ROT)) {
         f += playerentity.yRot;
      }

      playerentity.absMoveTo(d1, d3, d5, f, f1);
      this.connection.send(new CConfirmTeleportPacket(p_184330_1_.getId()));
      this.connection.send(new CPlayerPacket.PositionRotationPacket(playerentity.getX(), playerentity.getY(), playerentity.getZ(), playerentity.yRot, playerentity.xRot, false));
      if (!this.started) {
         this.started = true;
         this.minecraft.setScreen((Screen)null);
      }

   }

   public void handleChunkBlocksUpdate(SMultiBlockChangePacket p_147287_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147287_1_, this, this.minecraft);
      int i = 19 | (p_147287_1_.shouldSuppressLightUpdates() ? 128 : 0);
      p_147287_1_.runUpdates((p_243492_2_, p_243492_3_) -> {
         this.level.setBlock(p_243492_2_, p_243492_3_, i);
      });
   }

   public void handleLevelChunk(SChunkDataPacket p_147263_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147263_1_, this, this.minecraft);
      int i = p_147263_1_.getX();
      int j = p_147263_1_.getZ();
      BiomeContainer biomecontainer = p_147263_1_.getBiomes() == null ? null : new BiomeContainer(this.registryAccess.registryOrThrow(Registry.BIOME_REGISTRY), p_147263_1_.getBiomes());
      Chunk chunk = this.level.getChunkSource().replaceWithPacketData(i, j, biomecontainer, p_147263_1_.getReadBuffer(), p_147263_1_.getHeightmaps(), p_147263_1_.getAvailableSections(), p_147263_1_.isFullChunk());
      if (chunk != null && p_147263_1_.isFullChunk()) {
         this.level.reAddEntitiesToChunk(chunk);
      }

      for(int k = 0; k < 16; ++k) {
         this.level.setSectionDirtyWithNeighbors(i, k, j);
      }

      for(CompoundNBT compoundnbt : p_147263_1_.getBlockEntitiesTags()) {
         BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
         TileEntity tileentity = this.level.getBlockEntity(blockpos);
         if (tileentity != null) {
            tileentity.handleUpdateTag(this.level.getBlockState(blockpos), compoundnbt);
         }
      }

   }

   public void handleForgetLevelChunk(SUnloadChunkPacket p_184326_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184326_1_, this, this.minecraft);
      int i = p_184326_1_.getX();
      int j = p_184326_1_.getZ();
      ClientChunkProvider clientchunkprovider = this.level.getChunkSource();
      clientchunkprovider.drop(i, j);
      WorldLightManager worldlightmanager = clientchunkprovider.getLightEngine();

      for(int k = 0; k < 16; ++k) {
         this.level.setSectionDirtyWithNeighbors(i, k, j);
         worldlightmanager.updateSectionStatus(SectionPos.of(i, k, j), true);
      }

      worldlightmanager.enableLightSources(new ChunkPos(i, j), false);
   }

   public void handleBlockUpdate(SChangeBlockPacket p_147234_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147234_1_, this, this.minecraft);
      this.level.setKnownState(p_147234_1_.getPos(), p_147234_1_.getBlockState());
   }

   public void handleDisconnect(SDisconnectPacket p_147253_1_) {
      this.connection.disconnect(p_147253_1_.getReason());
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      this.minecraft.clearLevel();
      if (this.callbackScreen != null) {
         if (this.callbackScreen instanceof RealmsScreen) {
            this.minecraft.setScreen(new DisconnectedRealmsScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, p_147231_1_));
         } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, p_147231_1_));
         }
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(new MultiplayerScreen(new MainMenuScreen()), GENERIC_DISCONNECT_MESSAGE, p_147231_1_));
      }

   }

   public void send(IPacket<?> p_147297_1_) {
      this.connection.send(p_147297_1_);
   }

   public void handleTakeItemEntity(SCollectItemPacket p_147246_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147246_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147246_1_.getItemId());
      LivingEntity livingentity = (LivingEntity)this.level.getEntity(p_147246_1_.getPlayerId());
      if (livingentity == null) {
         livingentity = this.minecraft.player;
      }

      if (entity != null) {
         if (entity instanceof ExperienceOrbEntity) {
            this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F, false);
         }

         this.minecraft.particleEngine.add(new ItemPickupParticle(this.minecraft.getEntityRenderDispatcher(), this.minecraft.renderBuffers(), this.level, entity, livingentity));
         if (entity instanceof ItemEntity) {
            ItemEntity itementity = (ItemEntity)entity;
            ItemStack itemstack = itementity.getItem();
            itemstack.shrink(p_147246_1_.getAmount());
            if (itemstack.isEmpty()) {
               this.level.removeEntity(p_147246_1_.getItemId());
            }
         } else {
            this.level.removeEntity(p_147246_1_.getItemId());
         }
      }

   }

   public void handleChat(SChatPacket p_147251_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147251_1_, this, this.minecraft);
      net.minecraft.util.text.ITextComponent message = net.minecraftforge.event.ForgeEventFactory.onClientChat(p_147251_1_.getType(), p_147251_1_.getMessage(), p_147251_1_.getSender());
      if (message == null) return;
      this.minecraft.gui.handleChat(p_147251_1_.getType(), message, p_147251_1_.getSender());
   }

   public void handleAnimate(SAnimateHandPacket p_147279_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147279_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147279_1_.getId());
      if (entity != null) {
         if (p_147279_1_.getAction() == 0) {
            LivingEntity livingentity = (LivingEntity)entity;
            livingentity.swing(Hand.MAIN_HAND);
         } else if (p_147279_1_.getAction() == 3) {
            LivingEntity livingentity1 = (LivingEntity)entity;
            livingentity1.swing(Hand.OFF_HAND);
         } else if (p_147279_1_.getAction() == 1) {
            entity.animateHurt();
         } else if (p_147279_1_.getAction() == 2) {
            PlayerEntity playerentity = (PlayerEntity)entity;
            playerentity.stopSleepInBed(false, false);
         } else if (p_147279_1_.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.CRIT);
         } else if (p_147279_1_.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.ENCHANTED_HIT);
         }

      }
   }

   public void handleAddMob(SSpawnMobPacket p_147281_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147281_1_, this, this.minecraft);
      double d0 = p_147281_1_.getX();
      double d1 = p_147281_1_.getY();
      double d2 = p_147281_1_.getZ();
      float f = (float)(p_147281_1_.getyRot() * 360) / 256.0F;
      float f1 = (float)(p_147281_1_.getxRot() * 360) / 256.0F;
      LivingEntity livingentity = (LivingEntity)EntityType.create(p_147281_1_.getType(), this.minecraft.level);
      if (livingentity != null) {
         livingentity.setPacketCoordinates(d0, d1, d2);
         livingentity.yBodyRot = (float)(p_147281_1_.getyHeadRot() * 360) / 256.0F;
         livingentity.yHeadRot = (float)(p_147281_1_.getyHeadRot() * 360) / 256.0F;
         if (livingentity instanceof EnderDragonEntity) {
            EnderDragonPartEntity[] aenderdragonpartentity = ((EnderDragonEntity)livingentity).getSubEntities();

            for(int i = 0; i < aenderdragonpartentity.length; ++i) {
               aenderdragonpartentity[i].setId(i + p_147281_1_.getId());
            }
         }

         livingentity.setId(p_147281_1_.getId());
         livingentity.setUUID(p_147281_1_.getUUID());
         livingentity.absMoveTo(d0, d1, d2, f, f1);
         livingentity.setDeltaMovement((double)((float)p_147281_1_.getXd() / 8000.0F), (double)((float)p_147281_1_.getYd() / 8000.0F), (double)((float)p_147281_1_.getZd() / 8000.0F));
         this.level.putNonPlayerEntity(p_147281_1_.getId(), livingentity);
         if (livingentity instanceof BeeEntity) {
            boolean flag = ((BeeEntity)livingentity).isAngry();
            BeeSound beesound;
            if (flag) {
               beesound = new BeeAngrySound((BeeEntity)livingentity);
            } else {
               beesound = new BeeFlightSound((BeeEntity)livingentity);
            }

            this.minecraft.getSoundManager().queueTickingSound(beesound);
         }
      } else {
         LOGGER.warn("Skipping Entity with id {}", (int)p_147281_1_.getType());
      }

   }

   public void handleSetTime(SUpdateTimePacket p_147285_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147285_1_, this, this.minecraft);
      this.minecraft.level.setGameTime(p_147285_1_.getGameTime());
      this.minecraft.level.setDayTime(p_147285_1_.getDayTime());
   }

   public void handleSetSpawn(SWorldSpawnChangedPacket p_230488_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_230488_1_, this, this.minecraft);
      this.minecraft.level.setDefaultSpawnPos(p_230488_1_.getPos(), p_230488_1_.getAngle());
   }

   public void handleSetEntityPassengersPacket(SSetPassengersPacket p_184328_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184328_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_184328_1_.getVehicle());
      if (entity == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean flag = entity.hasIndirectPassenger(this.minecraft.player);
         entity.ejectPassengers();

         for(int i : p_184328_1_.getPassengers()) {
            Entity entity1 = this.level.getEntity(i);
            if (entity1 != null) {
               entity1.startRiding(entity, true);
               if (entity1 == this.minecraft.player && !flag) {
                  this.minecraft.gui.setOverlayMessage(new TranslationTextComponent("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage()), false);
               }
            }
         }

      }
   }

   public void handleEntityLinkPacket(SMountEntityPacket p_147243_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147243_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147243_1_.getSourceId());
      if (entity instanceof MobEntity) {
         ((MobEntity)entity).setDelayedLeashHolderId(p_147243_1_.getDestId());
      }

   }

   private static ItemStack findTotem(PlayerEntity p_217282_0_) {
      for(Hand hand : Hand.values()) {
         ItemStack itemstack = p_217282_0_.getItemInHand(hand);
         if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
            return itemstack;
         }
      }

      return new ItemStack(Items.TOTEM_OF_UNDYING);
   }

   public void handleEntityEvent(SEntityStatusPacket p_147236_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147236_1_, this, this.minecraft);
      Entity entity = p_147236_1_.getEntity(this.level);
      if (entity != null) {
         if (p_147236_1_.getEventId() == 21) {
            this.minecraft.getSoundManager().play(new GuardianSound((GuardianEntity)entity));
         } else if (p_147236_1_.getEventId() == 35) {
            int i = 40;
            this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
            this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
            if (entity == this.minecraft.player) {
               this.minecraft.gameRenderer.displayItemActivation(findTotem(this.minecraft.player));
            }
         } else {
            entity.handleEntityEvent(p_147236_1_.getEventId());
         }
      }

   }

   public void handleSetHealth(SUpdateHealthPacket p_147249_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147249_1_, this, this.minecraft);
      this.minecraft.player.hurtTo(p_147249_1_.getHealth());
      this.minecraft.player.getFoodData().setFoodLevel(p_147249_1_.getFood());
      this.minecraft.player.getFoodData().setSaturation(p_147249_1_.getSaturation());
   }

   public void handleSetExperience(SSetExperiencePacket p_147295_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147295_1_, this, this.minecraft);
      this.minecraft.player.setExperienceValues(p_147295_1_.getExperienceProgress(), p_147295_1_.getTotalExperience(), p_147295_1_.getExperienceLevel());
   }

   public void handleRespawn(SRespawnPacket p_147280_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147280_1_, this, this.minecraft);
      RegistryKey<World> registrykey = p_147280_1_.getDimension();
      DimensionType dimensiontype = p_147280_1_.getDimensionType();
      ClientPlayerEntity clientplayerentity = this.minecraft.player;
      int i = clientplayerentity.getId();
      this.started = false;
      if (registrykey != clientplayerentity.level.dimension()) {
         Scoreboard scoreboard = this.level.getScoreboard();
         boolean flag = p_147280_1_.isDebug();
         boolean flag1 = p_147280_1_.isFlat();
         ClientWorld.ClientWorldInfo clientworld$clientworldinfo = new ClientWorld.ClientWorldInfo(this.levelData.getDifficulty(), this.levelData.isHardcore(), flag1);
         this.levelData = clientworld$clientworldinfo;
         this.level = new ClientWorld(this, clientworld$clientworldinfo, registrykey, dimensiontype, this.serverChunkRadius, this.minecraft::getProfiler, this.minecraft.levelRenderer, flag, p_147280_1_.getSeed());
         this.level.setScoreboard(scoreboard);
         this.minecraft.setLevel(this.level);
         this.minecraft.setScreen(new DownloadTerrainScreen());
      }

      this.level.removeAllPendingEntityRemovals();
      String s = clientplayerentity.getServerBrand();
      this.minecraft.cameraEntity = null;
      ClientPlayerEntity clientplayerentity1 = this.minecraft.gameMode.createPlayer(this.level, clientplayerentity.getStats(), clientplayerentity.getRecipeBook(), clientplayerentity.isShiftKeyDown(), clientplayerentity.isSprinting());
      clientplayerentity1.setId(i);
      this.minecraft.player = clientplayerentity1;
      if (registrykey != clientplayerentity.level.dimension()) {
         this.minecraft.getMusicManager().stopPlaying();
      }

      this.minecraft.cameraEntity = clientplayerentity1;
      clientplayerentity1.getEntityData().assignValues(clientplayerentity.getEntityData().getAll());
      if (p_147280_1_.shouldKeepAllPlayerData()) {
         clientplayerentity1.getAttributes().assignValues(clientplayerentity.getAttributes());
      }

      clientplayerentity1.updateSyncFields(clientplayerentity); // Forge: fix MC-10657
      clientplayerentity1.resetPos();
      clientplayerentity1.setServerBrand(s);
      net.minecraftforge.fml.client.ClientHooks.firePlayerRespawn(this.minecraft.gameMode, clientplayerentity, clientplayerentity1, clientplayerentity1.connection.getConnection());
      this.level.addPlayer(i, clientplayerentity1);
      clientplayerentity1.yRot = -180.0F;
      clientplayerentity1.input = new MovementInputFromOptions(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(clientplayerentity1);
      clientplayerentity1.setReducedDebugInfo(clientplayerentity.isReducedDebugInfo());
      clientplayerentity1.setShowDeathScreen(clientplayerentity.shouldShowDeathScreen());
      if (this.minecraft.screen instanceof DeathScreen) {
         this.minecraft.setScreen((Screen)null);
      }

      this.minecraft.gameMode.setLocalMode(p_147280_1_.getPlayerGameType());
      this.minecraft.gameMode.setPreviousLocalMode(p_147280_1_.getPreviousPlayerGameType());
   }

   public void handleExplosion(SExplosionPacket p_147283_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147283_1_, this, this.minecraft);
      Explosion explosion = new Explosion(this.minecraft.level, (Entity)null, p_147283_1_.getX(), p_147283_1_.getY(), p_147283_1_.getZ(), p_147283_1_.getPower(), p_147283_1_.getToBlow());
      explosion.finalizeExplosion(true);
      this.minecraft.player.setDeltaMovement(this.minecraft.player.getDeltaMovement().add((double)p_147283_1_.getKnockbackX(), (double)p_147283_1_.getKnockbackY(), (double)p_147283_1_.getKnockbackZ()));
   }

   public void handleHorseScreenOpen(SOpenHorseWindowPacket p_217271_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217271_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_217271_1_.getEntityId());
      if (entity instanceof AbstractHorseEntity) {
         ClientPlayerEntity clientplayerentity = this.minecraft.player;
         AbstractHorseEntity abstracthorseentity = (AbstractHorseEntity)entity;
         Inventory inventory = new Inventory(p_217271_1_.getSize());
         HorseInventoryContainer horseinventorycontainer = new HorseInventoryContainer(p_217271_1_.getContainerId(), clientplayerentity.inventory, inventory, abstracthorseentity);
         clientplayerentity.containerMenu = horseinventorycontainer;
         this.minecraft.setScreen(new HorseInventoryScreen(horseinventorycontainer, clientplayerentity.inventory, abstracthorseentity));
      }

   }

   public void handleOpenScreen(SOpenWindowPacket p_217272_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217272_1_, this, this.minecraft);
      ScreenManager.create(p_217272_1_.getType(), this.minecraft, p_217272_1_.getContainerId(), p_217272_1_.getTitle());
   }

   public void handleContainerSetSlot(SSetSlotPacket p_147266_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147266_1_, this, this.minecraft);
      PlayerEntity playerentity = this.minecraft.player;
      ItemStack itemstack = p_147266_1_.getItem();
      int i = p_147266_1_.getSlot();
      this.minecraft.getTutorial().onGetItem(itemstack);
      if (p_147266_1_.getContainerId() == -1) {
         if (!(this.minecraft.screen instanceof CreativeScreen)) {
            playerentity.inventory.setCarried(itemstack);
         }
      } else if (p_147266_1_.getContainerId() == -2) {
         playerentity.inventory.setItem(i, itemstack);
      } else {
         boolean flag = false;
         if (this.minecraft.screen instanceof CreativeScreen) {
            CreativeScreen creativescreen = (CreativeScreen)this.minecraft.screen;
            flag = creativescreen.getSelectedTab() != ItemGroup.TAB_INVENTORY.getId();
         }

         if (p_147266_1_.getContainerId() == 0 && p_147266_1_.getSlot() >= 36 && i < 45) {
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = playerentity.inventoryMenu.getSlot(i).getItem();
               if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount()) {
                  itemstack.setPopTime(5);
               }
            }

            playerentity.inventoryMenu.setItem(i, itemstack);
         } else if (p_147266_1_.getContainerId() == playerentity.containerMenu.containerId && (p_147266_1_.getContainerId() != 0 || !flag)) {
            playerentity.containerMenu.setItem(i, itemstack);
         }
      }

   }

   public void handleContainerAck(SConfirmTransactionPacket p_147239_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147239_1_, this, this.minecraft);
      Container container = null;
      PlayerEntity playerentity = this.minecraft.player;
      if (p_147239_1_.getContainerId() == 0) {
         container = playerentity.inventoryMenu;
      } else if (p_147239_1_.getContainerId() == playerentity.containerMenu.containerId) {
         container = playerentity.containerMenu;
      }

      if (container != null && !p_147239_1_.isAccepted()) {
         this.send(new CConfirmTransactionPacket(p_147239_1_.getContainerId(), p_147239_1_.getUid(), true));
      }

   }

   public void handleContainerContent(SWindowItemsPacket p_147241_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147241_1_, this, this.minecraft);
      PlayerEntity playerentity = this.minecraft.player;
      if (p_147241_1_.getContainerId() == 0) {
         playerentity.inventoryMenu.setAll(p_147241_1_.getItems());
      } else if (p_147241_1_.getContainerId() == playerentity.containerMenu.containerId) {
         playerentity.containerMenu.setAll(p_147241_1_.getItems());
      }

   }

   public void handleOpenSignEditor(SOpenSignMenuPacket p_147268_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147268_1_, this, this.minecraft);
      TileEntity tileentity = this.level.getBlockEntity(p_147268_1_.getPos());
      if (!(tileentity instanceof SignTileEntity)) {
         tileentity = new SignTileEntity();
         tileentity.setLevelAndPosition(this.level, p_147268_1_.getPos());
      }

      this.minecraft.player.openTextEdit((SignTileEntity)tileentity);
   }

   public void handleBlockEntityData(SUpdateTileEntityPacket p_147273_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147273_1_, this, this.minecraft);
      BlockPos blockpos = p_147273_1_.getPos();
      TileEntity tileentity = this.minecraft.level.getBlockEntity(blockpos);
      int i = p_147273_1_.getType();
      boolean flag = i == 2 && tileentity instanceof CommandBlockTileEntity;
      if (i == 1 && tileentity instanceof MobSpawnerTileEntity || flag || i == 3 && tileentity instanceof BeaconTileEntity || i == 4 && tileentity instanceof SkullTileEntity || i == 6 && tileentity instanceof BannerTileEntity || i == 7 && tileentity instanceof StructureBlockTileEntity || i == 8 && tileentity instanceof EndGatewayTileEntity || i == 9 && tileentity instanceof SignTileEntity || i == 11 && tileentity instanceof BedTileEntity || i == 5 && tileentity instanceof ConduitTileEntity || i == 12 && tileentity instanceof JigsawTileEntity || i == 13 && tileentity instanceof CampfireTileEntity || i == 14 && tileentity instanceof BeehiveTileEntity) {
         tileentity.load(this.minecraft.level.getBlockState(blockpos), p_147273_1_.getTag());
      }

      if (flag && this.minecraft.screen instanceof CommandBlockScreen) {
         ((CommandBlockScreen)this.minecraft.screen).updateGui();
      } else {
         if(tileentity == null) {
            LOGGER.error("Received invalid update packet for null tile entity at {} with data: {}", p_147273_1_.getPos(), p_147273_1_.getTag());
            return;
         }
         tileentity.onDataPacket(connection, p_147273_1_);
      }

   }

   public void handleContainerSetData(SWindowPropertyPacket p_147245_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147245_1_, this, this.minecraft);
      PlayerEntity playerentity = this.minecraft.player;
      if (playerentity.containerMenu != null && playerentity.containerMenu.containerId == p_147245_1_.getContainerId()) {
         playerentity.containerMenu.setData(p_147245_1_.getId(), p_147245_1_.getValue());
      }

   }

   public void handleSetEquipment(SEntityEquipmentPacket p_147242_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147242_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147242_1_.getEntity());
      if (entity != null) {
         p_147242_1_.getSlots().forEach((p_241664_1_) -> {
            entity.setItemSlot(p_241664_1_.getFirst(), p_241664_1_.getSecond());
         });
      }

   }

   public void handleContainerClose(SCloseWindowPacket p_147276_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147276_1_, this, this.minecraft);
      this.minecraft.player.clientSideCloseContainer();
   }

   public void handleBlockEvent(SBlockActionPacket p_147261_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147261_1_, this, this.minecraft);
      this.minecraft.level.blockEvent(p_147261_1_.getPos(), p_147261_1_.getBlock(), p_147261_1_.getB0(), p_147261_1_.getB1());
   }

   public void handleBlockDestruction(SAnimateBlockBreakPacket p_147294_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147294_1_, this, this.minecraft);
      this.minecraft.level.destroyBlockProgress(p_147294_1_.getId(), p_147294_1_.getPos(), p_147294_1_.getProgress());
   }

   public void handleGameEvent(SChangeGameStatePacket p_147252_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147252_1_, this, this.minecraft);
      PlayerEntity playerentity = this.minecraft.player;
      SChangeGameStatePacket.State schangegamestatepacket$state = p_147252_1_.getEvent();
      float f = p_147252_1_.getParam();
      int i = MathHelper.floor(f + 0.5F);
      if (schangegamestatepacket$state == SChangeGameStatePacket.NO_RESPAWN_BLOCK_AVAILABLE) {
         playerentity.displayClientMessage(new TranslationTextComponent("block.minecraft.spawn.not_valid"), false);
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.START_RAINING) {
         this.level.getLevelData().setRaining(true);
         this.level.setRainLevel(0.0F);
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.STOP_RAINING) {
         this.level.getLevelData().setRaining(false);
         this.level.setRainLevel(1.0F);
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.CHANGE_GAME_MODE) {
         this.minecraft.gameMode.setLocalMode(GameType.byId(i));
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.WIN_GAME) {
         if (i == 0) {
            this.minecraft.player.connection.send(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            this.minecraft.setScreen(new DownloadTerrainScreen());
         } else if (i == 1) {
            this.minecraft.setScreen(new WinGameScreen(true, () -> {
               this.minecraft.player.connection.send(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            }));
         }
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.DEMO_EVENT) {
         GameSettings gamesettings = this.minecraft.options;
         if (f == 0.0F) {
            this.minecraft.setScreen(new DemoScreen());
         } else if (f == 101.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslationTextComponent("demo.help.movement", gamesettings.keyUp.getTranslatedKeyMessage(), gamesettings.keyLeft.getTranslatedKeyMessage(), gamesettings.keyDown.getTranslatedKeyMessage(), gamesettings.keyRight.getTranslatedKeyMessage()));
         } else if (f == 102.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslationTextComponent("demo.help.jump", gamesettings.keyJump.getTranslatedKeyMessage()));
         } else if (f == 103.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslationTextComponent("demo.help.inventory", gamesettings.keyInventory.getTranslatedKeyMessage()));
         } else if (f == 104.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslationTextComponent("demo.day.6", gamesettings.keyScreenshot.getTranslatedKeyMessage()));
         }
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.ARROW_HIT_PLAYER) {
         this.level.playSound(playerentity, playerentity.getX(), playerentity.getEyeY(), playerentity.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F);
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.RAIN_LEVEL_CHANGE) {
         this.level.setRainLevel(f);
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.THUNDER_LEVEL_CHANGE) {
         this.level.setThunderLevel(f);
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.PUFFER_FISH_STING) {
         this.level.playSound(playerentity, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0F, 1.0F);
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.GUARDIAN_ELDER_EFFECT) {
         this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, playerentity.getX(), playerentity.getY(), playerentity.getZ(), 0.0D, 0.0D, 0.0D);
         if (i == 1) {
            this.level.playSound(playerentity, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
         }
      } else if (schangegamestatepacket$state == SChangeGameStatePacket.IMMEDIATE_RESPAWN) {
         this.minecraft.player.setShowDeathScreen(f == 0.0F);
      }

   }

   public void handleMapItemData(SMapDataPacket p_147264_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147264_1_, this, this.minecraft);
      MapItemRenderer mapitemrenderer = this.minecraft.gameRenderer.getMapRenderer();
      String s = FilledMapItem.makeKey(p_147264_1_.getMapId());
      MapData mapdata = this.minecraft.level.getMapData(s);
      if (mapdata == null) {
         mapdata = new MapData(s);
         if (mapitemrenderer.getMapInstanceIfExists(s) != null) {
            MapData mapdata1 = mapitemrenderer.getData(mapitemrenderer.getMapInstanceIfExists(s));
            if (mapdata1 != null) {
               mapdata = mapdata1;
            }
         }

         this.minecraft.level.setMapData(mapdata);
      }

      p_147264_1_.applyToMap(mapdata);
      mapitemrenderer.update(mapdata);
   }

   public void handleLevelEvent(SPlaySoundEventPacket p_147277_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147277_1_, this, this.minecraft);
      if (p_147277_1_.isGlobalEvent()) {
         this.minecraft.level.globalLevelEvent(p_147277_1_.getType(), p_147277_1_.getPos(), p_147277_1_.getData());
      } else {
         this.minecraft.level.levelEvent(p_147277_1_.getType(), p_147277_1_.getPos(), p_147277_1_.getData());
      }

   }

   public void handleUpdateAdvancementsPacket(SAdvancementInfoPacket p_191981_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_191981_1_, this, this.minecraft);
      this.advancements.update(p_191981_1_);
   }

   public void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket p_194022_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_194022_1_, this, this.minecraft);
      ResourceLocation resourcelocation = p_194022_1_.getTab();
      if (resourcelocation == null) {
         this.advancements.setSelectedTab((Advancement)null, false);
      } else {
         Advancement advancement = this.advancements.getAdvancements().get(resourcelocation);
         this.advancements.setSelectedTab(advancement, false);
      }

   }

   public void handleCommands(SCommandListPacket p_195511_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_195511_1_, this, this.minecraft);
      this.commands = new CommandDispatcher<>(p_195511_1_.getRoot());
   }

   public void handleStopSoundEvent(SStopSoundPacket p_195512_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_195512_1_, this, this.minecraft);
      this.minecraft.getSoundManager().stop(p_195512_1_.getName(), p_195512_1_.getSource());
   }

   public void handleCommandSuggestions(STabCompletePacket p_195510_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_195510_1_, this, this.minecraft);
      this.suggestionsProvider.completeCustomSuggestions(p_195510_1_.getId(), p_195510_1_.getSuggestions());
   }

   public void handleUpdateRecipes(SUpdateRecipesPacket p_199525_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_199525_1_, this, this.minecraft);
      this.recipeManager.replaceRecipes(p_199525_1_.getRecipes());
      IMutableSearchTree<RecipeList> imutablesearchtree = this.minecraft.getSearchTree(SearchTreeManager.RECIPE_COLLECTIONS);
      imutablesearchtree.clear();
      ClientRecipeBook clientrecipebook = this.minecraft.player.getRecipeBook();
      clientrecipebook.setupCollections(this.recipeManager.getRecipes());
      clientrecipebook.getCollections().forEach(imutablesearchtree::add);
      imutablesearchtree.refresh();
      net.minecraftforge.client.ForgeHooksClient.onRecipesUpdated(this.recipeManager);
   }

   public void handleLookAt(SPlayerLookPacket p_200232_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_200232_1_, this, this.minecraft);
      Vector3d vector3d = p_200232_1_.getPosition(this.level);
      if (vector3d != null) {
         this.minecraft.player.lookAt(p_200232_1_.getFromAnchor(), vector3d);
      }

   }

   public void handleTagQueryPacket(SQueryNBTResponsePacket p_211522_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_211522_1_, this, this.minecraft);
      if (!this.debugQueryHandler.handleResponse(p_211522_1_.getTransactionId(), p_211522_1_.getTag())) {
         LOGGER.debug("Got unhandled response to tag query {}", (int)p_211522_1_.getTransactionId());
      }

   }

   public void handleAwardStats(SStatisticsPacket p_147293_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147293_1_, this, this.minecraft);

      for(Entry<Stat<?>, Integer> entry : p_147293_1_.getStats().entrySet()) {
         Stat<?> stat = entry.getKey();
         int i = entry.getValue();
         this.minecraft.player.getStats().setValue(this.minecraft.player, stat, i);
      }

      if (this.minecraft.screen instanceof IProgressMeter) {
         ((IProgressMeter)this.minecraft.screen).onStatsUpdated();
      }

   }

   public void handleAddOrRemoveRecipes(SRecipeBookPacket p_191980_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_191980_1_, this, this.minecraft);
      ClientRecipeBook clientrecipebook = this.minecraft.player.getRecipeBook();
      clientrecipebook.setBookSettings(p_191980_1_.getBookSettings());
      SRecipeBookPacket.State srecipebookpacket$state = p_191980_1_.getState();
      switch(srecipebookpacket$state) {
      case REMOVE:
         for(ResourceLocation resourcelocation3 : p_191980_1_.getRecipes()) {
            this.recipeManager.byKey(resourcelocation3).ifPresent(clientrecipebook::remove);
         }
         break;
      case INIT:
         for(ResourceLocation resourcelocation1 : p_191980_1_.getRecipes()) {
            this.recipeManager.byKey(resourcelocation1).ifPresent(clientrecipebook::add);
         }

         for(ResourceLocation resourcelocation2 : p_191980_1_.getHighlights()) {
            this.recipeManager.byKey(resourcelocation2).ifPresent(clientrecipebook::addHighlight);
         }
         break;
      case ADD:
         for(ResourceLocation resourcelocation : p_191980_1_.getRecipes()) {
            this.recipeManager.byKey(resourcelocation).ifPresent((p_217278_2_) -> {
               clientrecipebook.add(p_217278_2_);
               clientrecipebook.addHighlight(p_217278_2_);
               RecipeToast.addOrUpdate(this.minecraft.getToasts(), p_217278_2_);
            });
         }
      }

      clientrecipebook.getCollections().forEach((p_199527_1_) -> {
         p_199527_1_.updateKnownRecipes(clientrecipebook);
      });
      if (this.minecraft.screen instanceof IRecipeShownListener) {
         ((IRecipeShownListener)this.minecraft.screen).recipesUpdated();
      }

   }

   public void handleUpdateMobEffect(SPlayEntityEffectPacket p_147260_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147260_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147260_1_.getEntityId());
      if (entity instanceof LivingEntity) {
         Effect effect = Effect.byId(p_147260_1_.getEffectId() & 0xFF);
         if (effect != null) {
            EffectInstance effectinstance = new EffectInstance(effect, p_147260_1_.getEffectDurationTicks(), p_147260_1_.getEffectAmplifier(), p_147260_1_.isEffectAmbient(), p_147260_1_.isEffectVisible(), p_147260_1_.effectShowsIcon());
            effectinstance.setNoCounter(p_147260_1_.isSuperLongDuration());
            ((LivingEntity)entity).forceAddEffect(effectinstance);
         }
      }
   }

   public void handleUpdateTags(STagsListPacket p_199723_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_199723_1_, this, this.minecraft);
      ITagCollectionSupplier itagcollectionsupplier = p_199723_1_.getTags();
      boolean vanillaConnection = net.minecraftforge.fml.network.NetworkHooks.isVanillaConnection(connection);
      Multimap<ResourceLocation, ResourceLocation> multimap = vanillaConnection ? TagRegistryManager.getAllMissingTags(net.minecraftforge.common.ForgeTagHandler.withNoCustom(itagcollectionsupplier)) : TagRegistryManager.validateVanillaTags(itagcollectionsupplier);//Forge: If we are connecting to vanilla validate all tags to properly validate custom tags the client may "require", and if we are connecting to forge only validate the vanilla tag types as the custom tag types get synced in a separate packet so may still arrive
      if (!multimap.isEmpty()) {
         LOGGER.warn("Incomplete server tags, disconnecting. Missing: {}", (Object)multimap);
         this.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.missing_tags"));
      } else {
         net.minecraftforge.common.ForgeTagHandler.resetCachedTagCollections(true, vanillaConnection);
         itagcollectionsupplier = ITagCollectionSupplier.reinjectOptionalTags(itagcollectionsupplier);
         this.tags = itagcollectionsupplier;
         if (!this.connection.isMemoryConnection()) {
            itagcollectionsupplier.bindToGlobal();
         }

         this.minecraft.getSearchTree(SearchTreeManager.CREATIVE_TAGS).refresh();
      }
   }

   public void handlePlayerCombat(SCombatPacket p_175098_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_175098_1_, this, this.minecraft);
      if (p_175098_1_.event == SCombatPacket.Event.ENTITY_DIED) {
         Entity entity = this.level.getEntity(p_175098_1_.playerId);
         if (entity == this.minecraft.player) {
            if (this.minecraft.player.shouldShowDeathScreen()) {
               this.minecraft.setScreen(new DeathScreen(p_175098_1_.message, this.level.getLevelData().isHardcore()));
            } else {
               this.minecraft.player.respawn();
            }
         }
      }

   }

   public void handleChangeDifficulty(SServerDifficultyPacket p_175101_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_175101_1_, this, this.minecraft);
      this.levelData.setDifficulty(p_175101_1_.getDifficulty());
      this.levelData.setDifficultyLocked(p_175101_1_.isLocked());
   }

   public void handleSetCamera(SCameraPacket p_175094_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_175094_1_, this, this.minecraft);
      Entity entity = p_175094_1_.getEntity(this.level);
      if (entity != null) {
         this.minecraft.setCameraEntity(entity);
      }

   }

   public void handleSetBorder(SWorldBorderPacket p_175093_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_175093_1_, this, this.minecraft);
      p_175093_1_.applyChanges(this.level.getWorldBorder());
   }

   public void handleSetTitles(STitlePacket p_175099_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_175099_1_, this, this.minecraft);
      STitlePacket.Type stitlepacket$type = p_175099_1_.getType();
      ITextComponent itextcomponent = null;
      ITextComponent itextcomponent1 = null;
      ITextComponent itextcomponent2 = p_175099_1_.getText() != null ? p_175099_1_.getText() : StringTextComponent.EMPTY;
      switch(stitlepacket$type) {
      case TITLE:
         itextcomponent = itextcomponent2;
         break;
      case SUBTITLE:
         itextcomponent1 = itextcomponent2;
         break;
      case ACTIONBAR:
         this.minecraft.gui.setOverlayMessage(itextcomponent2, false);
         return;
      case RESET:
         this.minecraft.gui.setTitles((ITextComponent)null, (ITextComponent)null, -1, -1, -1);
         this.minecraft.gui.resetTitleTimes();
         return;
      }

      this.minecraft.gui.setTitles(itextcomponent, itextcomponent1, p_175099_1_.getFadeInTime(), p_175099_1_.getStayTime(), p_175099_1_.getFadeOutTime());
   }

   public void handleTabListCustomisation(SPlayerListHeaderFooterPacket p_175096_1_) {
      this.minecraft.gui.getTabList().setHeader(p_175096_1_.getHeader().getString().isEmpty() ? null : p_175096_1_.getHeader());
      this.minecraft.gui.getTabList().setFooter(p_175096_1_.getFooter().getString().isEmpty() ? null : p_175096_1_.getFooter());
   }

   public void handleRemoveMobEffect(SRemoveEntityEffectPacket p_147262_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147262_1_, this, this.minecraft);
      Entity entity = p_147262_1_.getEntity(this.level);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).removeEffectNoUpdate(p_147262_1_.getEffect());
      }

   }

   public void handlePlayerInfo(SPlayerListItemPacket p_147256_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147256_1_, this, this.minecraft);

      for(SPlayerListItemPacket.AddPlayerData splayerlistitempacket$addplayerdata : p_147256_1_.getEntries()) {
         if (p_147256_1_.getAction() == SPlayerListItemPacket.Action.REMOVE_PLAYER) {
            this.minecraft.getPlayerSocialManager().removePlayer(splayerlistitempacket$addplayerdata.getProfile().getId());
            this.playerInfoMap.remove(splayerlistitempacket$addplayerdata.getProfile().getId());
         } else {
            NetworkPlayerInfo networkplayerinfo = this.playerInfoMap.get(splayerlistitempacket$addplayerdata.getProfile().getId());
            if (p_147256_1_.getAction() == SPlayerListItemPacket.Action.ADD_PLAYER) {
               networkplayerinfo = new NetworkPlayerInfo(splayerlistitempacket$addplayerdata);
               this.playerInfoMap.put(networkplayerinfo.getProfile().getId(), networkplayerinfo);
               this.minecraft.getPlayerSocialManager().addPlayer(networkplayerinfo);
            }

            if (networkplayerinfo != null) {
               switch(p_147256_1_.getAction()) {
               case ADD_PLAYER:
                  networkplayerinfo.setGameMode(splayerlistitempacket$addplayerdata.getGameMode());
                  networkplayerinfo.setLatency(splayerlistitempacket$addplayerdata.getLatency());
                  networkplayerinfo.setTabListDisplayName(splayerlistitempacket$addplayerdata.getDisplayName());
                  break;
               case UPDATE_GAME_MODE:
                  networkplayerinfo.setGameMode(splayerlistitempacket$addplayerdata.getGameMode());
                  break;
               case UPDATE_LATENCY:
                  networkplayerinfo.setLatency(splayerlistitempacket$addplayerdata.getLatency());
                  break;
               case UPDATE_DISPLAY_NAME:
                  networkplayerinfo.setTabListDisplayName(splayerlistitempacket$addplayerdata.getDisplayName());
               }
            }
         }
      }

   }

   public void handleKeepAlive(SKeepAlivePacket p_147272_1_) {
      this.send(new CKeepAlivePacket(p_147272_1_.getId()));
   }

   public void handlePlayerAbilities(SPlayerAbilitiesPacket p_147270_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147270_1_, this, this.minecraft);
      PlayerEntity playerentity = this.minecraft.player;
      playerentity.abilities.flying = p_147270_1_.isFlying();
      playerentity.abilities.instabuild = p_147270_1_.canInstabuild();
      playerentity.abilities.invulnerable = p_147270_1_.isInvulnerable();
      playerentity.abilities.mayfly = p_147270_1_.canFly();
      playerentity.abilities.setFlyingSpeed(p_147270_1_.getFlyingSpeed());
      playerentity.abilities.setWalkingSpeed(p_147270_1_.getWalkingSpeed());
   }

   public void handleSoundEvent(SPlaySoundEffectPacket p_184327_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184327_1_, this, this.minecraft);
      this.minecraft.level.playSound(this.minecraft.player, p_184327_1_.getX(), p_184327_1_.getY(), p_184327_1_.getZ(), p_184327_1_.getSound(), p_184327_1_.getSource(), p_184327_1_.getVolume(), p_184327_1_.getPitch());
   }

   public void handleSoundEntityEvent(SSpawnMovingSoundEffectPacket p_217266_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217266_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_217266_1_.getId());
      if (entity != null) {
         this.minecraft.level.playSound(this.minecraft.player, entity, p_217266_1_.getSound(), p_217266_1_.getSource(), p_217266_1_.getVolume(), p_217266_1_.getPitch());
      }
   }

   public void handleCustomSoundEvent(SPlaySoundPacket p_184329_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184329_1_, this, this.minecraft);
      this.minecraft.getSoundManager().play(new SimpleSound(p_184329_1_.getName(), p_184329_1_.getSource(), p_184329_1_.getVolume(), p_184329_1_.getPitch(), false, 0, ISound.AttenuationType.LINEAR, p_184329_1_.getX(), p_184329_1_.getY(), p_184329_1_.getZ(), false));
   }

   public void handleResourcePack(SSendResourcePackPacket p_175095_1_) {
      String s = p_175095_1_.getUrl();
      String s1 = p_175095_1_.getHash();
      if (this.validateResourcePackUrl(s)) {
         if (s.startsWith("level://")) {
            try {
               String s2 = URLDecoder.decode(s.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File file1 = new File(this.minecraft.gameDirectory, "saves");
               File file2 = new File(file1, s2);
               if (file2.isFile()) {
                  this.send(CResourcePackStatusPacket.Action.ACCEPTED);
                  CompletableFuture<?> completablefuture = this.minecraft.getClientPackSource().setServerPack(file2, IPackNameDecorator.WORLD);
                  this.downloadCallback(completablefuture);
                  return;
               }
            } catch (UnsupportedEncodingException unsupportedencodingexception) {
            }

            this.send(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         } else {
            ServerData serverdata = this.minecraft.getCurrentServer();
            if (serverdata != null && serverdata.getResourcePackStatus() == ServerData.ServerResourceMode.ENABLED) {
               this.send(CResourcePackStatusPacket.Action.ACCEPTED);
               this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(s, s1));
            } else if (serverdata != null && serverdata.getResourcePackStatus() != ServerData.ServerResourceMode.PROMPT) {
               this.send(CResourcePackStatusPacket.Action.DECLINED);
            } else {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(new ConfirmScreen((p_217274_3_) -> {
                     this.minecraft = Minecraft.getInstance();
                     ServerData serverdata1 = this.minecraft.getCurrentServer();
                     if (p_217274_3_) {
                        if (serverdata1 != null) {
                           serverdata1.setResourcePackStatus(ServerData.ServerResourceMode.ENABLED);
                        }

                        this.send(CResourcePackStatusPacket.Action.ACCEPTED);
                        this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(s, s1));
                     } else {
                        if (serverdata1 != null) {
                           serverdata1.setResourcePackStatus(ServerData.ServerResourceMode.DISABLED);
                        }

                        this.send(CResourcePackStatusPacket.Action.DECLINED);
                     }

                     ServerList.saveSingleServer(serverdata1);
                     this.minecraft.setScreen((Screen)null);
                  }, new TranslationTextComponent("multiplayer.texturePrompt.line1"), new TranslationTextComponent("multiplayer.texturePrompt.line2")));
               });
            }

         }
      }
   }

   private boolean validateResourcePackUrl(String p_189688_1_) {
      try {
         URI uri = new URI(p_189688_1_);
         String s = uri.getScheme();
         boolean flag = "level".equals(s);
         if (!"http".equals(s) && !"https".equals(s) && !flag) {
            throw new URISyntaxException(p_189688_1_, "Wrong protocol");
         } else if (!flag || !p_189688_1_.contains("..") && p_189688_1_.endsWith("/resources.zip")) {
            return true;
         } else {
            throw new URISyntaxException(p_189688_1_, "Invalid levelstorage resourcepack path");
         }
      } catch (URISyntaxException urisyntaxexception) {
         this.send(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         return false;
      }
   }

   private void downloadCallback(CompletableFuture<?> p_217279_1_) {
      p_217279_1_.thenRun(() -> {
         this.send(CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED);
      }).exceptionally((p_217276_1_) -> {
         this.send(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         return null;
      });
   }

   private void send(CResourcePackStatusPacket.Action p_217283_1_) {
      this.connection.send(new CResourcePackStatusPacket(p_217283_1_));
   }

   public void handleBossUpdate(SUpdateBossInfoPacket p_184325_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184325_1_, this, this.minecraft);
      this.minecraft.gui.getBossOverlay().update(p_184325_1_);
   }

   public void handleItemCooldown(SCooldownPacket p_184324_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184324_1_, this, this.minecraft);
      if (p_184324_1_.getDuration() == 0) {
         this.minecraft.player.getCooldowns().removeCooldown(p_184324_1_.getItem());
      } else {
         this.minecraft.player.getCooldowns().addCooldown(p_184324_1_.getItem(), p_184324_1_.getDuration());
      }

   }

   public void handleMoveVehicle(SMoveVehiclePacket p_184323_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_184323_1_, this, this.minecraft);
      Entity entity = this.minecraft.player.getRootVehicle();
      if (entity != this.minecraft.player && entity.isControlledByLocalInstance()) {
         entity.absMoveTo(p_184323_1_.getX(), p_184323_1_.getY(), p_184323_1_.getZ(), p_184323_1_.getYRot(), p_184323_1_.getXRot());
         this.connection.send(new CMoveVehiclePacket(entity));
      }

   }

   public void handleOpenBook(SOpenBookWindowPacket p_217268_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217268_1_, this, this.minecraft);
      ItemStack itemstack = this.minecraft.player.getItemInHand(p_217268_1_.getHand());
      if (itemstack.getItem() == Items.WRITTEN_BOOK) {
         this.minecraft.setScreen(new ReadBookScreen(new ReadBookScreen.WrittenBookInfo(itemstack)));
      }

   }

   public void handleCustomPayload(SCustomPayloadPlayPacket p_147240_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147240_1_, this, this.minecraft);
      ResourceLocation resourcelocation = p_147240_1_.getIdentifier();
      PacketBuffer packetbuffer = null;

      try {
         packetbuffer = p_147240_1_.getData();
         if (SCustomPayloadPlayPacket.BRAND.equals(resourcelocation)) {
            this.minecraft.player.setServerBrand(packetbuffer.readUtf(32767));
         } else if (SCustomPayloadPlayPacket.DEBUG_PATHFINDING_PACKET.equals(resourcelocation)) {
            int i = packetbuffer.readInt();
            float f = packetbuffer.readFloat();
            Path path = Path.createFromStream(packetbuffer);
            this.minecraft.debugRenderer.pathfindingRenderer.addPath(i, path, f);
         } else if (SCustomPayloadPlayPacket.DEBUG_NEIGHBORSUPDATE_PACKET.equals(resourcelocation)) {
            long l1 = packetbuffer.readVarLong();
            BlockPos blockpos9 = packetbuffer.readBlockPos();
            ((NeighborsUpdateDebugRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(l1, blockpos9);
         } else if (SCustomPayloadPlayPacket.DEBUG_CAVES_PACKET.equals(resourcelocation)) {
            BlockPos blockpos2 = packetbuffer.readBlockPos();
            int k2 = packetbuffer.readInt();
            List<BlockPos> list1 = Lists.newArrayList();
            List<Float> list = Lists.newArrayList();

            for(int j = 0; j < k2; ++j) {
               list1.add(packetbuffer.readBlockPos());
               list.add(packetbuffer.readFloat());
            }

            this.minecraft.debugRenderer.caveRenderer.addTunnel(blockpos2, list1, list);
         } else if (SCustomPayloadPlayPacket.DEBUG_STRUCTURES_PACKET.equals(resourcelocation)) {
            DimensionType dimensiontype = this.registryAccess.dimensionTypes().get(packetbuffer.readResourceLocation());
            MutableBoundingBox mutableboundingbox = new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt());
            int i4 = packetbuffer.readInt();
            List<MutableBoundingBox> list2 = Lists.newArrayList();
            List<Boolean> list4 = Lists.newArrayList();

            for(int k = 0; k < i4; ++k) {
               list2.add(new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt()));
               list4.add(packetbuffer.readBoolean());
            }

            this.minecraft.debugRenderer.structureRenderer.addBoundingBox(mutableboundingbox, list2, list4, dimensiontype);
         } else if (SCustomPayloadPlayPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals(resourcelocation)) {
            ((WorldGenAttemptsDebugRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos(packetbuffer.readBlockPos(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat());
         } else if (SCustomPayloadPlayPacket.DEBUG_VILLAGE_SECTIONS.equals(resourcelocation)) {
            int i2 = packetbuffer.readInt();

            for(int l2 = 0; l2 < i2; ++l2) {
               this.minecraft.debugRenderer.villageSectionsDebugRenderer.setVillageSection(packetbuffer.readSectionPos());
            }

            int i3 = packetbuffer.readInt();

            for(int j4 = 0; j4 < i3; ++j4) {
               this.minecraft.debugRenderer.villageSectionsDebugRenderer.setNotVillageSection(packetbuffer.readSectionPos());
            }
         } else if (SCustomPayloadPlayPacket.DEBUG_POI_ADDED_PACKET.equals(resourcelocation)) {
            BlockPos blockpos3 = packetbuffer.readBlockPos();
            String s8 = packetbuffer.readUtf();
            int k4 = packetbuffer.readInt();
            PointOfInterestDebugRenderer.POIInfo pointofinterestdebugrenderer$poiinfo = new PointOfInterestDebugRenderer.POIInfo(blockpos3, s8, k4);
            this.minecraft.debugRenderer.brainDebugRenderer.addPoi(pointofinterestdebugrenderer$poiinfo);
         } else if (SCustomPayloadPlayPacket.DEBUG_POI_REMOVED_PACKET.equals(resourcelocation)) {
            BlockPos blockpos4 = packetbuffer.readBlockPos();
            this.minecraft.debugRenderer.brainDebugRenderer.removePoi(blockpos4);
         } else if (SCustomPayloadPlayPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals(resourcelocation)) {
            BlockPos blockpos5 = packetbuffer.readBlockPos();
            int j3 = packetbuffer.readInt();
            this.minecraft.debugRenderer.brainDebugRenderer.setFreeTicketCount(blockpos5, j3);
         } else if (SCustomPayloadPlayPacket.DEBUG_GOAL_SELECTOR.equals(resourcelocation)) {
            BlockPos blockpos6 = packetbuffer.readBlockPos();
            int k3 = packetbuffer.readInt();
            int l4 = packetbuffer.readInt();
            List<EntityAIDebugRenderer.Entry> list3 = Lists.newArrayList();

            for(int i6 = 0; i6 < l4; ++i6) {
               int j6 = packetbuffer.readInt();
               boolean flag = packetbuffer.readBoolean();
               String s = packetbuffer.readUtf(255);
               list3.add(new EntityAIDebugRenderer.Entry(blockpos6, j6, s, flag));
            }

            this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(k3, list3);
         } else if (SCustomPayloadPlayPacket.DEBUG_RAIDS.equals(resourcelocation)) {
            int j2 = packetbuffer.readInt();
            Collection<BlockPos> collection = Lists.newArrayList();

            for(int i5 = 0; i5 < j2; ++i5) {
               collection.add(packetbuffer.readBlockPos());
            }

            this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(collection);
         } else if (SCustomPayloadPlayPacket.DEBUG_BRAIN.equals(resourcelocation)) {
            double d0 = packetbuffer.readDouble();
            double d2 = packetbuffer.readDouble();
            double d4 = packetbuffer.readDouble();
            IPosition iposition = new Position(d0, d2, d4);
            UUID uuid = packetbuffer.readUUID();
            int l = packetbuffer.readInt();
            String s1 = packetbuffer.readUtf();
            String s2 = packetbuffer.readUtf();
            int i1 = packetbuffer.readInt();
            float f1 = packetbuffer.readFloat();
            float f2 = packetbuffer.readFloat();
            String s3 = packetbuffer.readUtf();
            boolean flag1 = packetbuffer.readBoolean();
            Path path1;
            if (flag1) {
               path1 = Path.createFromStream(packetbuffer);
            } else {
               path1 = null;
            }

            boolean flag2 = packetbuffer.readBoolean();
            PointOfInterestDebugRenderer.BrainInfo pointofinterestdebugrenderer$braininfo = new PointOfInterestDebugRenderer.BrainInfo(uuid, l, s1, s2, i1, f1, f2, iposition, s3, path1, flag2);
            int j1 = packetbuffer.readInt();

            for(int k1 = 0; k1 < j1; ++k1) {
               String s4 = packetbuffer.readUtf();
               pointofinterestdebugrenderer$braininfo.activities.add(s4);
            }

            int i8 = packetbuffer.readInt();

            for(int j8 = 0; j8 < i8; ++j8) {
               String s5 = packetbuffer.readUtf();
               pointofinterestdebugrenderer$braininfo.behaviors.add(s5);
            }

            int k8 = packetbuffer.readInt();

            for(int l8 = 0; l8 < k8; ++l8) {
               String s6 = packetbuffer.readUtf();
               pointofinterestdebugrenderer$braininfo.memories.add(s6);
            }

            int i9 = packetbuffer.readInt();

            for(int j9 = 0; j9 < i9; ++j9) {
               BlockPos blockpos = packetbuffer.readBlockPos();
               pointofinterestdebugrenderer$braininfo.pois.add(blockpos);
            }

            int k9 = packetbuffer.readInt();

            for(int l9 = 0; l9 < k9; ++l9) {
               BlockPos blockpos1 = packetbuffer.readBlockPos();
               pointofinterestdebugrenderer$braininfo.potentialPois.add(blockpos1);
            }

            int i10 = packetbuffer.readInt();

            for(int j10 = 0; j10 < i10; ++j10) {
               String s7 = packetbuffer.readUtf();
               pointofinterestdebugrenderer$braininfo.gossips.add(s7);
            }

            this.minecraft.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump(pointofinterestdebugrenderer$braininfo);
         } else if (SCustomPayloadPlayPacket.DEBUG_BEE.equals(resourcelocation)) {
            double d1 = packetbuffer.readDouble();
            double d3 = packetbuffer.readDouble();
            double d5 = packetbuffer.readDouble();
            IPosition iposition1 = new Position(d1, d3, d5);
            UUID uuid1 = packetbuffer.readUUID();
            int k6 = packetbuffer.readInt();
            boolean flag4 = packetbuffer.readBoolean();
            BlockPos blockpos10 = null;
            if (flag4) {
               blockpos10 = packetbuffer.readBlockPos();
            }

            boolean flag5 = packetbuffer.readBoolean();
            BlockPos blockpos11 = null;
            if (flag5) {
               blockpos11 = packetbuffer.readBlockPos();
            }

            int l6 = packetbuffer.readInt();
            boolean flag6 = packetbuffer.readBoolean();
            Path path2 = null;
            if (flag6) {
               path2 = Path.createFromStream(packetbuffer);
            }

            BeeDebugRenderer.Bee beedebugrenderer$bee = new BeeDebugRenderer.Bee(uuid1, k6, iposition1, path2, blockpos10, blockpos11, l6);
            int i7 = packetbuffer.readInt();

            for(int j7 = 0; j7 < i7; ++j7) {
               String s11 = packetbuffer.readUtf();
               beedebugrenderer$bee.goals.add(s11);
            }

            int k7 = packetbuffer.readInt();

            for(int l7 = 0; l7 < k7; ++l7) {
               BlockPos blockpos12 = packetbuffer.readBlockPos();
               beedebugrenderer$bee.blacklistedHives.add(blockpos12);
            }

            this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo(beedebugrenderer$bee);
         } else if (SCustomPayloadPlayPacket.DEBUG_HIVE.equals(resourcelocation)) {
            BlockPos blockpos7 = packetbuffer.readBlockPos();
            String s9 = packetbuffer.readUtf();
            int j5 = packetbuffer.readInt();
            int k5 = packetbuffer.readInt();
            boolean flag3 = packetbuffer.readBoolean();
            BeeDebugRenderer.Hive beedebugrenderer$hive = new BeeDebugRenderer.Hive(blockpos7, s9, j5, k5, flag3, this.level.getGameTime());
            this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo(beedebugrenderer$hive);
         } else if (SCustomPayloadPlayPacket.DEBUG_GAME_TEST_CLEAR.equals(resourcelocation)) {
            this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
         } else if (SCustomPayloadPlayPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(resourcelocation)) {
            BlockPos blockpos8 = packetbuffer.readBlockPos();
            int l3 = packetbuffer.readInt();
            String s10 = packetbuffer.readUtf();
            int l5 = packetbuffer.readInt();
            this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker(blockpos8, l3, s10, l5);
         } else {
            if (!net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(p_147240_1_, this.connection))
            LOGGER.warn("Unknown custom packet identifier: {}", (Object)resourcelocation);
         }
      } finally {
         if (packetbuffer != null) {
            if (false) // Forge: let packet handle releasing buffer
            packetbuffer.release();
         }

      }

   }

   public void handleAddObjective(SScoreboardObjectivePacket p_147291_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147291_1_, this, this.minecraft);
      Scoreboard scoreboard = this.level.getScoreboard();
      String s = p_147291_1_.getObjectiveName();
      if (p_147291_1_.getMethod() == 0) {
         scoreboard.addObjective(s, ScoreCriteria.DUMMY, p_147291_1_.getDisplayName(), p_147291_1_.getRenderType());
      } else if (scoreboard.hasObjective(s)) {
         ScoreObjective scoreobjective = scoreboard.getObjective(s);
         if (p_147291_1_.getMethod() == 1) {
            scoreboard.removeObjective(scoreobjective);
         } else if (p_147291_1_.getMethod() == 2) {
            scoreobjective.setRenderType(p_147291_1_.getRenderType());
            scoreobjective.setDisplayName(p_147291_1_.getDisplayName());
         }
      }

   }

   public void handleSetScore(SUpdateScorePacket p_147250_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147250_1_, this, this.minecraft);
      Scoreboard scoreboard = this.level.getScoreboard();
      String s = p_147250_1_.getObjectiveName();
      switch(p_147250_1_.getMethod()) {
      case CHANGE:
         ScoreObjective scoreobjective = scoreboard.getOrCreateObjective(s);
         Score score = scoreboard.getOrCreatePlayerScore(p_147250_1_.getOwner(), scoreobjective);
         score.setScore(p_147250_1_.getScore());
         break;
      case REMOVE:
         scoreboard.resetPlayerScore(p_147250_1_.getOwner(), scoreboard.getObjective(s));
      }

   }

   public void handleSetDisplayObjective(SDisplayObjectivePacket p_147254_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147254_1_, this, this.minecraft);
      Scoreboard scoreboard = this.level.getScoreboard();
      String s = p_147254_1_.getObjectiveName();
      ScoreObjective scoreobjective = s == null ? null : scoreboard.getOrCreateObjective(s);
      scoreboard.setDisplayObjective(p_147254_1_.getSlot(), scoreobjective);
   }

   public void handleSetPlayerTeamPacket(STeamsPacket p_147247_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147247_1_, this, this.minecraft);
      Scoreboard scoreboard = this.level.getScoreboard();
      ScorePlayerTeam scoreplayerteam;
      if (p_147247_1_.getMethod() == 0) {
         scoreplayerteam = scoreboard.addPlayerTeam(p_147247_1_.getName());
      } else {
         scoreplayerteam = scoreboard.getPlayerTeam(p_147247_1_.getName());
      }

      if (p_147247_1_.getMethod() == 0 || p_147247_1_.getMethod() == 2) {
         scoreplayerteam.setDisplayName(p_147247_1_.getDisplayName());
         scoreplayerteam.setColor(p_147247_1_.getColor());
         scoreplayerteam.unpackOptions(p_147247_1_.getOptions());
         Team.Visible team$visible = Team.Visible.byName(p_147247_1_.getNametagVisibility());
         if (team$visible != null) {
            scoreplayerteam.setNameTagVisibility(team$visible);
         }

         Team.CollisionRule team$collisionrule = Team.CollisionRule.byName(p_147247_1_.getCollisionRule());
         if (team$collisionrule != null) {
            scoreplayerteam.setCollisionRule(team$collisionrule);
         }

         scoreplayerteam.setPlayerPrefix(p_147247_1_.getPlayerPrefix());
         scoreplayerteam.setPlayerSuffix(p_147247_1_.getPlayerSuffix());
      }

      if (p_147247_1_.getMethod() == 0 || p_147247_1_.getMethod() == 3) {
         for(String s : p_147247_1_.getPlayers()) {
            scoreboard.addPlayerToTeam(s, scoreplayerteam);
         }
      }

      if (p_147247_1_.getMethod() == 4) {
         for(String s1 : p_147247_1_.getPlayers()) {
            scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
         }
      }

      if (p_147247_1_.getMethod() == 1) {
         scoreboard.removePlayerTeam(scoreplayerteam);
      }

   }

   public void handleParticleEvent(SSpawnParticlePacket p_147289_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147289_1_, this, this.minecraft);
      if (p_147289_1_.getCount() == 0) {
         double d0 = (double)(p_147289_1_.getMaxSpeed() * p_147289_1_.getXDist());
         double d2 = (double)(p_147289_1_.getMaxSpeed() * p_147289_1_.getYDist());
         double d4 = (double)(p_147289_1_.getMaxSpeed() * p_147289_1_.getZDist());

         try {
            this.level.addParticle(p_147289_1_.getParticle(), p_147289_1_.isOverrideLimiter(), p_147289_1_.getX(), p_147289_1_.getY(), p_147289_1_.getZ(), d0, d2, d4);
         } catch (Throwable throwable1) {
            LOGGER.warn("Could not spawn particle effect {}", (Object)p_147289_1_.getParticle());
         }
      } else {
         for(int i = 0; i < p_147289_1_.getCount(); ++i) {
            double d1 = this.random.nextGaussian() * (double)p_147289_1_.getXDist();
            double d3 = this.random.nextGaussian() * (double)p_147289_1_.getYDist();
            double d5 = this.random.nextGaussian() * (double)p_147289_1_.getZDist();
            double d6 = this.random.nextGaussian() * (double)p_147289_1_.getMaxSpeed();
            double d7 = this.random.nextGaussian() * (double)p_147289_1_.getMaxSpeed();
            double d8 = this.random.nextGaussian() * (double)p_147289_1_.getMaxSpeed();

            try {
               this.level.addParticle(p_147289_1_.getParticle(), p_147289_1_.isOverrideLimiter(), p_147289_1_.getX() + d1, p_147289_1_.getY() + d3, p_147289_1_.getZ() + d5, d6, d7, d8);
            } catch (Throwable throwable) {
               LOGGER.warn("Could not spawn particle effect {}", (Object)p_147289_1_.getParticle());
               return;
            }
         }
      }

   }

   public void handleUpdateAttributes(SEntityPropertiesPacket p_147290_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_147290_1_, this, this.minecraft);
      Entity entity = this.level.getEntity(p_147290_1_.getEntityId());
      if (entity != null) {
         if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
         } else {
            AttributeModifierManager attributemodifiermanager = ((LivingEntity)entity).getAttributes();

            for(SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot : p_147290_1_.getValues()) {
               ModifiableAttributeInstance modifiableattributeinstance = attributemodifiermanager.getInstance(sentitypropertiespacket$snapshot.getAttribute());
               if (modifiableattributeinstance == null) {
                  LOGGER.warn("Entity {} does not have attribute {}", entity, Registry.ATTRIBUTE.getKey(sentitypropertiespacket$snapshot.getAttribute()));
               } else {
                  modifiableattributeinstance.setBaseValue(sentitypropertiespacket$snapshot.getBase());
                  modifiableattributeinstance.removeModifiers();

                  for(AttributeModifier attributemodifier : sentitypropertiespacket$snapshot.getModifiers()) {
                     modifiableattributeinstance.addTransientModifier(attributemodifier);
                  }
               }
            }

         }
      }
   }

   public void handlePlaceRecipe(SPlaceGhostRecipePacket p_194307_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_194307_1_, this, this.minecraft);
      Container container = this.minecraft.player.containerMenu;
      if (container.containerId == p_194307_1_.getContainerId() && container.isSynched(this.minecraft.player)) {
         this.recipeManager.byKey(p_194307_1_.getRecipe()).ifPresent((p_241665_2_) -> {
            if (this.minecraft.screen instanceof IRecipeShownListener) {
               RecipeBookGui recipebookgui = ((IRecipeShownListener)this.minecraft.screen).getRecipeBookComponent();
               recipebookgui.setupGhostRecipe(p_241665_2_, container.slots);
            }

         });
      }
   }

   public void handleLightUpdatePacked(SUpdateLightPacket p_217269_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217269_1_, this, this.minecraft);
      int i = p_217269_1_.getX();
      int j = p_217269_1_.getZ();
      WorldLightManager worldlightmanager = this.level.getChunkSource().getLightEngine();
      int k = p_217269_1_.getSkyYMask();
      int l = p_217269_1_.getEmptySkyYMask();
      Iterator<byte[]> iterator = p_217269_1_.getSkyUpdates().iterator();
      this.readSectionList(i, j, worldlightmanager, LightType.SKY, k, l, iterator, p_217269_1_.getTrustEdges());
      int i1 = p_217269_1_.getBlockYMask();
      int j1 = p_217269_1_.getEmptyBlockYMask();
      Iterator<byte[]> iterator1 = p_217269_1_.getBlockUpdates().iterator();
      this.readSectionList(i, j, worldlightmanager, LightType.BLOCK, i1, j1, iterator1, p_217269_1_.getTrustEdges());
   }

   public void handleMerchantOffers(SMerchantOffersPacket p_217273_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217273_1_, this, this.minecraft);
      Container container = this.minecraft.player.containerMenu;
      if (p_217273_1_.getContainerId() == container.containerId && container instanceof MerchantContainer) {
         ((MerchantContainer)container).setOffers(new MerchantOffers(p_217273_1_.getOffers().createTag()));
         ((MerchantContainer)container).setXp(p_217273_1_.getVillagerXp());
         ((MerchantContainer)container).setMerchantLevel(p_217273_1_.getVillagerLevel());
         ((MerchantContainer)container).setShowProgressBar(p_217273_1_.showProgress());
         ((MerchantContainer)container).setCanRestock(p_217273_1_.canRestock());
      }

   }

   public void handleSetChunkCacheRadius(SUpdateViewDistancePacket p_217270_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217270_1_, this, this.minecraft);
      this.serverChunkRadius = p_217270_1_.getRadius();
      this.level.getChunkSource().updateViewRadius(p_217270_1_.getRadius());
   }

   public void handleSetChunkCacheCenter(SUpdateChunkPositionPacket p_217267_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_217267_1_, this, this.minecraft);
      this.level.getChunkSource().updateViewCenter(p_217267_1_.getX(), p_217267_1_.getZ());
   }

   public void handleBlockBreakAck(SPlayerDiggingPacket p_225312_1_) {
      PacketThreadUtil.ensureRunningOnSameThread(p_225312_1_, this, this.minecraft);
      this.minecraft.gameMode.handleBlockBreakAck(this.level, p_225312_1_.getPos(), p_225312_1_.getState(), p_225312_1_.action(), p_225312_1_.allGood());
   }

   private void readSectionList(int p_217284_1_, int p_217284_2_, WorldLightManager p_217284_3_, LightType p_217284_4_, int p_217284_5_, int p_217284_6_, Iterator<byte[]> p_217284_7_, boolean p_217284_8_) {
      for(int i = 0; i < 18; ++i) {
         int j = -1 + i;
         boolean flag = (p_217284_5_ & 1 << i) != 0;
         boolean flag1 = (p_217284_6_ & 1 << i) != 0;
         if (flag || flag1) {
            p_217284_3_.queueSectionData(p_217284_4_, SectionPos.of(p_217284_1_, j, p_217284_2_), flag ? new NibbleArray((byte[])p_217284_7_.next().clone()) : new NibbleArray(), p_217284_8_);
            this.level.setSectionDirtyWithNeighbors(p_217284_1_, j, p_217284_2_);
         }
      }

   }

   public NetworkManager getConnection() {
      return this.connection;
   }

   public Collection<NetworkPlayerInfo> getOnlinePlayers() {
      return this.playerInfoMap.values();
   }

   public Collection<UUID> getOnlinePlayerIds() {
      return this.playerInfoMap.keySet();
   }

   @Nullable
   public NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_) {
      return this.playerInfoMap.get(p_175102_1_);
   }

   @Nullable
   public NetworkPlayerInfo getPlayerInfo(String p_175104_1_) {
      for(NetworkPlayerInfo networkplayerinfo : this.playerInfoMap.values()) {
         if (networkplayerinfo.getProfile().getName().equals(p_175104_1_)) {
            return networkplayerinfo;
         }
      }

      return null;
   }

   public GameProfile getLocalGameProfile() {
      return this.localGameProfile;
   }

   public ClientAdvancementManager getAdvancements() {
      return this.advancements;
   }

   public CommandDispatcher<ISuggestionProvider> getCommands() {
      return this.commands;
   }

   public ClientWorld getLevel() {
      return this.level;
   }

   public ITagCollectionSupplier getTags() {
      return this.tags;
   }

   public NBTQueryManager getDebugQueryHandler() {
      return this.debugQueryHandler;
   }

   public UUID getId() {
      return this.id;
   }

   public Set<RegistryKey<World>> levels() {
      return this.levels;
   }

   public DynamicRegistries registryAccess() {
      return this.registryAccess;
   }
}
