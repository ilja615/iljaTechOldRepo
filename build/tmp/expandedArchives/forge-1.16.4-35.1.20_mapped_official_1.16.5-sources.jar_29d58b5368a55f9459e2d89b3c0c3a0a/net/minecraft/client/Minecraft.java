package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import com.google.gson.JsonElement;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.OfflineSocialInteractions;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.PlatformDescriptors;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmBackupScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DatapackFailureScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.EditWorldScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MemoryErrorScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.social.FilterManager;
import net.minecraft.client.gui.social.SocialInteractionsScreen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GPUWarning;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.PaintingSpriteUploader;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.LegacyResourcePackWrapperV4;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.IMutableSearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.client.util.SearchTreeReloadable;
import net.minecraft.client.util.Splashes;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.profiler.DataPoint;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.LongTickDetector;
import net.minecraft.profiler.Snooper;
import net.minecraft.profiler.TimeTracker;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Timer;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.listener.ChainedChunkStatusListener;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Minecraft extends RecursiveEventLoop<Runnable> implements ISnooperInfo, IWindowEventListener {
   private static Minecraft instance;
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean ON_OSX = Util.getPlatform() == Util.OS.OSX;
   public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("default");
   public static final ResourceLocation UNIFORM_FONT = new ResourceLocation("uniform");
   public static final ResourceLocation ALT_FONT = new ResourceLocation("alt");
   private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private static final ITextComponent SOCIAL_INTERACTIONS_NOT_AVAILABLE = new TranslationTextComponent("multiplayer.socialInteractions.not_available");
   private final File resourcePackDirectory;
   private final PropertyMap profileProperties;
   public final TextureManager textureManager;
   private final DataFixer fixerUpper;
   private final VirtualScreen virtualScreen;
   private final MainWindow window;
   private final Timer timer = new Timer(20.0F, 0L);
   private final Snooper snooper = new Snooper("client", this, Util.getMillis());
   private final RenderTypeBuffers renderBuffers;
   public final WorldRenderer levelRenderer;
   private final EntityRendererManager entityRenderDispatcher;
   private final ItemRenderer itemRenderer;
   private final FirstPersonRenderer itemInHandRenderer;
   public final ParticleManager particleEngine;
   private final SearchTreeManager searchRegistry = new SearchTreeManager();
   private final Session user;
   public final FontRenderer font;
   public final GameRenderer gameRenderer;
   public final DebugRenderer debugRenderer;
   private final AtomicReference<TrackingChunkStatusListener> progressListener = new AtomicReference<>();
   public final IngameGui gui;
   public final GameSettings options;
   private final CreativeSettings hotbarManager;
   public final MouseHelper mouseHandler;
   public final KeyboardListener keyboardHandler;
   public final File gameDirectory;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private final SaveFormat levelSource;
   public final FrameTimer frameTimer = new FrameTimer();
   private final boolean is64bit;
   private final boolean demo;
   private final boolean allowsMultiplayer;
   private final boolean allowsChat;
   private final IReloadableResourceManager resourceManager;
   private final DownloadingPackFinder clientPackSource;
   private final ResourcePackList resourcePackRepository;
   private final LanguageManager languageManager;
   private final BlockColors blockColors;
   private final ItemColors itemColors;
   private final Framebuffer mainRenderTarget;
   private final SoundHandler soundManager;
   private final MusicTicker musicManager;
   private final FontResourceManager fontManager;
   private final Splashes splashManager;
   private final GPUWarning gpuWarnlistManager;
   private final MinecraftSessionService minecraftSessionService;
   private final SocialInteractionsService socialInteractionsService;
   private final SkinManager skinManager;
   private final ModelManager modelManager;
   private final BlockRendererDispatcher blockRenderer;
   private final PaintingSpriteUploader paintingTextures;
   private final PotionSpriteUploader mobEffectTextures;
   private final ToastGui toast;
   private final MinecraftGame game = new MinecraftGame(this);
   private final Tutorial tutorial;
   private final FilterManager playerSocialManager;
   public static byte[] reserve = new byte[10485760];
   @Nullable
   public PlayerController gameMode;
   @Nullable
   public ClientWorld level;
   @Nullable
   public ClientPlayerEntity player;
   @Nullable
   private IntegratedServer singleplayerServer;
   @Nullable
   private ServerData currentServer;
   @Nullable
   private NetworkManager pendingConnection;
   private boolean isLocalServer;
   @Nullable
   public Entity cameraEntity;
   @Nullable
   public Entity crosshairPickEntity;
   @Nullable
   public RayTraceResult hitResult;
   private int rightClickDelay;
   protected int missTime;
   private boolean pause;
   private float pausePartialTick;
   private long lastNanoTime = Util.getNanos();
   private long lastTime;
   private int frames;
   public boolean noRender;
   @Nullable
   public Screen screen;
   @Nullable
   public LoadingGui overlay;
   private boolean connectedToRealms;
   private Thread gameThread;
   private volatile boolean running = true;
   @Nullable
   private CrashReport delayedCrash;
   private static int fps;
   public String fpsString = "";
   public boolean chunkPath;
   public boolean chunkVisibility;
   public boolean smartCull = true;
   private boolean windowActive;
   private final Queue<Runnable> progressTasks = Queues.newConcurrentLinkedQueue();
   @Nullable
   private CompletableFuture<Void> pendingReload;
   @Nullable
   private TutorialToast socialInteractionsToast;
   private IProfiler profiler = EmptyProfiler.INSTANCE;
   private int fpsPieRenderTicks;
   private final TimeTracker fpsPieProfiler = new TimeTracker(Util.timeSource, () -> {
      return this.fpsPieRenderTicks;
   });
   @Nullable
   private IProfileResult fpsPieResults;
   private String debugPath = "root";

   public Minecraft(GameConfiguration p_i45547_1_) {
      super("Client");
      instance = this;
      net.minecraftforge.client.ForgeHooksClient.invalidateLog4jThreadCache();
      this.gameDirectory = p_i45547_1_.location.gameDirectory;
      File file1 = p_i45547_1_.location.assetDirectory;
      this.resourcePackDirectory = p_i45547_1_.location.resourcePackDirectory;
      this.launchedVersion = p_i45547_1_.game.launchVersion;
      this.versionType = p_i45547_1_.game.versionType;
      this.profileProperties = p_i45547_1_.user.profileProperties;
      this.clientPackSource = new DownloadingPackFinder(new File(this.gameDirectory, "server-resource-packs"), p_i45547_1_.location.getAssetIndex());
      this.resourcePackRepository = new ResourcePackList(Minecraft::createClientPackAdapter, this.clientPackSource, new FolderPackFinder(this.resourcePackDirectory, IPackNameDecorator.DEFAULT));
      this.proxy = p_i45547_1_.user.proxy;
      YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy);
      this.minecraftSessionService = yggdrasilauthenticationservice.createMinecraftSessionService();
      this.socialInteractionsService = this.createSocialInteractions(yggdrasilauthenticationservice, p_i45547_1_);
      this.user = p_i45547_1_.user.user;
      LOGGER.info("Setting user: {}", (Object)this.user.getName());
      this.demo = p_i45547_1_.game.demo;
      this.allowsMultiplayer = !p_i45547_1_.game.disableMultiplayer;
      this.allowsChat = !p_i45547_1_.game.disableChat;
      this.is64bit = checkIs64Bit();
      this.singleplayerServer = null;
      String s;
      int i;
      if (this.allowsMultiplayer() && p_i45547_1_.server.hostname != null) {
         s = p_i45547_1_.server.hostname;
         i = p_i45547_1_.server.port;
      } else {
         s = null;
         i = 0;
      }

      KeybindTextComponent.setKeyResolver(KeyBinding::createNameSupplier);
      this.fixerUpper = DataFixesManager.getDataFixer();
      this.toast = new ToastGui(this);
      this.tutorial = new Tutorial(this);
      this.gameThread = Thread.currentThread();
      this.options = new GameSettings(this, this.gameDirectory);
      this.hotbarManager = new CreativeSettings(this.gameDirectory, this.fixerUpper);
      LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
      ScreenSize screensize;
      if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
         screensize = new ScreenSize(this.options.overrideWidth, this.options.overrideHeight, p_i45547_1_.display.fullscreenWidth, p_i45547_1_.display.fullscreenHeight, p_i45547_1_.display.isFullscreen);
      } else {
         screensize = p_i45547_1_.display;
      }

      Util.timeSource = RenderSystem.initBackendSystem();
      this.virtualScreen = new VirtualScreen(this);
      this.window = this.virtualScreen.newWindow(screensize, this.options.fullscreenVideoModeString, this.createTitle());
      this.setWindowActive(true);

      try {
         InputStream inputstream = this.getClientPackSource().getVanillaPack().getResource(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
         InputStream inputstream1 = this.getClientPackSource().getVanillaPack().getResource(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
         this.window.setIcon(inputstream, inputstream1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't set icon", (Throwable)ioexception);
      }

      this.window.setFramerateLimit(this.options.framerateLimit);
      this.mouseHandler = new MouseHelper(this);
      this.keyboardHandler = new KeyboardListener(this);
      this.keyboardHandler.setup(this.window.getWindow());
      RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
      this.mainRenderTarget = new Framebuffer(this.window.getWidth(), this.window.getHeight(), true, ON_OSX);
      this.mainRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.resourceManager = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES);
      net.minecraftforge.fml.client.ClientModLoader.begin(this, this.resourcePackRepository, this.resourceManager, this.clientPackSource);
      this.resourcePackRepository.reload();
      this.options.loadSelectedResourcePacks(this.resourcePackRepository);
      this.languageManager = new LanguageManager(this.options.languageCode);
      this.resourceManager.registerReloadListener(this.languageManager);
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.registerReloadListener(this.textureManager);
      this.skinManager = new SkinManager(this.textureManager, new File(file1, "skins"), this.minecraftSessionService);
      this.levelSource = new SaveFormat(this.gameDirectory.toPath().resolve("saves"), this.gameDirectory.toPath().resolve("backups"), this.fixerUpper);
      this.soundManager = new SoundHandler(this.resourceManager, this.options);
      this.resourceManager.registerReloadListener(this.soundManager);
      this.splashManager = new Splashes(this.user);
      this.resourceManager.registerReloadListener(this.splashManager);
      this.musicManager = new MusicTicker(this);
      this.fontManager = new FontResourceManager(this.textureManager);
      this.font = this.fontManager.createFont();
      this.resourceManager.registerReloadListener(this.fontManager.getReloadListener());
      this.selectMainFont(this.isEnforceUnicode());
      this.resourceManager.registerReloadListener(new GrassColorReloadListener());
      this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
      this.window.setErrorSection("Startup");
      RenderSystem.setupDefaultState(0, 0, this.window.getWidth(), this.window.getHeight());
      this.window.setErrorSection("Post startup");
      this.blockColors = BlockColors.createDefault();
      this.itemColors = ItemColors.createDefault(this.blockColors);
      this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.options.mipmapLevels);
      this.resourceManager.registerReloadListener(this.modelManager);
      this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
      this.entityRenderDispatcher = new EntityRendererManager(this.textureManager, this.itemRenderer, this.resourceManager, this.font, this.options);
      this.itemInHandRenderer = new FirstPersonRenderer(this);
      this.resourceManager.registerReloadListener(this.itemRenderer);
      this.renderBuffers = new RenderTypeBuffers();
      this.gameRenderer = new GameRenderer(this, this.resourceManager, this.renderBuffers);
      this.resourceManager.registerReloadListener(this.gameRenderer);
      this.playerSocialManager = new FilterManager(this, this.socialInteractionsService);
      this.blockRenderer = new BlockRendererDispatcher(this.modelManager.getBlockModelShaper(), this.blockColors);
      this.resourceManager.registerReloadListener(this.blockRenderer);
      this.levelRenderer = new WorldRenderer(this, this.renderBuffers);
      this.resourceManager.registerReloadListener(this.levelRenderer);
      this.createSearchTrees();
      this.resourceManager.registerReloadListener(this.searchRegistry);
      this.particleEngine = new ParticleManager(this.level, this.textureManager);
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.ParticleFactoryRegisterEvent());
      this.resourceManager.registerReloadListener(this.particleEngine);
      this.paintingTextures = new PaintingSpriteUploader(this.textureManager);
      this.resourceManager.registerReloadListener(this.paintingTextures);
      this.mobEffectTextures = new PotionSpriteUploader(this.textureManager);
      this.resourceManager.registerReloadListener(this.mobEffectTextures);
      this.gpuWarnlistManager = new GPUWarning();
      this.resourceManager.registerReloadListener(this.gpuWarnlistManager);
      this.gui = new net.minecraftforge.client.gui.ForgeIngameGui(this);
      this.mouseHandler.setup(this.window.getWindow()); //Forge: Moved below ingameGUI setting to prevent NPEs in handeler.
      this.debugRenderer = new DebugRenderer(this);
      RenderSystem.setErrorCallback(this::onFullscreenError);
      if (this.options.fullscreen && !this.window.isFullscreen()) {
         this.window.toggleFullScreen();
         this.options.fullscreen = this.window.isFullscreen();
      }

      this.window.updateVsync(this.options.enableVsync);
      this.window.updateRawMouseInput(this.options.rawMouseInput);
      this.window.setDefaultErrorCallback();
      this.resizeDisplay();

      ResourceLoadProgressGui.registerTextures(this);
      List<IResourcePack> list = this.resourcePackRepository.openAllSelected();
      this.setOverlay(new ResourceLoadProgressGui(this, this.resourceManager.createFullReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, list), (p_238197_1_) -> {
         Util.ifElse(p_238197_1_, this::rollbackResourcePacks, () -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               this.selfTest();
            }
            if (net.minecraftforge.fml.client.ClientModLoader.completeModLoading()) return; // Do not overwrite the error screen
            // FORGE: Move opening initial screen to after startup and events are enabled.
            // Also Fixes MC-145102
            if (s != null) {
               this.setScreen(new ConnectingScreen(new MainMenuScreen(), this, s, i));
            } else {
               this.setScreen(new MainMenuScreen(true));
            }
         });
      }, false));
   }

   public void updateTitle() {
      this.window.setTitle(this.createTitle());
   }

   private String createTitle() {
      StringBuilder stringbuilder = new StringBuilder("Minecraft");
      if (this.isProbablyModded()) {
         stringbuilder.append("*");
      }

      stringbuilder.append(" ");
      stringbuilder.append(SharedConstants.getCurrentVersion().getName());
      ClientPlayNetHandler clientplaynethandler = this.getConnection();
      if (clientplaynethandler != null && clientplaynethandler.getConnection().isConnected()) {
         stringbuilder.append(" - ");
         if (this.singleplayerServer != null && !this.singleplayerServer.isPublished()) {
            stringbuilder.append(I18n.get("title.singleplayer"));
         } else if (this.isConnectedToRealms()) {
            stringbuilder.append(I18n.get("title.multiplayer.realms"));
         } else if (this.singleplayerServer == null && (this.currentServer == null || !this.currentServer.isLan())) {
            stringbuilder.append(I18n.get("title.multiplayer.other"));
         } else {
            stringbuilder.append(I18n.get("title.multiplayer.lan"));
         }
      }

      return stringbuilder.toString();
   }

   private SocialInteractionsService createSocialInteractions(YggdrasilAuthenticationService p_244735_1_, GameConfiguration p_244735_2_) {
      try {
         return p_244735_1_.createSocialInteractionsService(p_244735_2_.user.user.getAccessToken());
      } catch (AuthenticationException authenticationexception) {
         LOGGER.error("Failed to verify authentication", (Throwable)authenticationexception);
         return new OfflineSocialInteractions();
      }
   }

   public boolean isProbablyModded() {
      return !"vanilla".equals(ClientBrandRetriever.getClientModName()) || Minecraft.class.getSigners() == null;
   }

   private void rollbackResourcePacks(Throwable p_229988_1_) {
      if (this.resourcePackRepository.getSelectedPacks().stream().anyMatch(e -> !e.isRequired())) { //Forge: This caused infinite loop if any resource packs are forced. Such as mod resources. So check if we can disable any.
         ITextComponent itextcomponent;
         if (p_229988_1_ instanceof SimpleReloadableResourceManager.FailedPackException) {
            itextcomponent = new StringTextComponent(((SimpleReloadableResourceManager.FailedPackException)p_229988_1_).getPack().getName());
         } else {
            itextcomponent = null;
         }

         this.clearResourcePacksOnError(p_229988_1_, itextcomponent);
      } else {
         Util.throwAsRuntime(p_229988_1_);
      }

   }

   public void clearResourcePacksOnError(Throwable p_243208_1_, @Nullable ITextComponent p_243208_2_) {
      LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", p_243208_1_);
      this.resourcePackRepository.setSelected(Collections.emptyList());
      this.options.resourcePacks.clear();
      this.options.incompatibleResourcePacks.clear();
      this.options.save();
      this.reloadResourcePacks().thenRun(() -> {
         ToastGui toastgui = this.getToasts();
         SystemToast.addOrUpdate(toastgui, SystemToast.Type.PACK_LOAD_FAILURE, new TranslationTextComponent("resourcePack.load_fail"), p_243208_2_);
      });
   }

   public void run() {
      this.gameThread = Thread.currentThread();

      try {
         boolean flag = false;

         while(this.running) {
            if (this.delayedCrash != null) {
               crash(this.delayedCrash);
               return;
            }

            try {
               LongTickDetector longtickdetector = LongTickDetector.createTickProfiler("Renderer");
               boolean flag1 = this.shouldRenderFpsPie();
               this.startProfilers(flag1, longtickdetector);
               this.profiler.startTick();
               this.runTick(!flag);
               this.profiler.endTick();
               this.finishProfilers(flag1, longtickdetector);
            } catch (OutOfMemoryError outofmemoryerror) {
               if (flag) {
                  throw outofmemoryerror;
               }

               this.emergencySave();
               this.setScreen(new MemoryErrorScreen());
               System.gc();
               LOGGER.fatal("Out of memory", (Throwable)outofmemoryerror);
               flag = true;
            }
         }
      } catch (ReportedException reportedexception) {
         this.fillReport(reportedexception.getReport());
         this.emergencySave();
         LOGGER.fatal("Reported exception thrown!", (Throwable)reportedexception);
         crash(reportedexception.getReport());
      } catch (Throwable throwable) {
         CrashReport crashreport = this.fillReport(new CrashReport("Unexpected error", throwable));
         LOGGER.fatal("Unreported exception thrown!", throwable);
         this.emergencySave();
         crash(crashreport);
      }

   }

   void selectMainFont(boolean p_238209_1_) {
      this.fontManager.setRenames(p_238209_1_ ? ImmutableMap.of(DEFAULT_FONT, UNIFORM_FONT) : ImmutableMap.of());
   }

   public void createSearchTrees() {
      SearchTree<ItemStack> searchtree = new SearchTree<>((p_213242_0_) -> {
         return p_213242_0_.getTooltipLines((PlayerEntity)null, ITooltipFlag.TooltipFlags.NORMAL).stream().map((p_213230_0_) -> {
            return TextFormatting.stripFormatting(p_213230_0_.getString()).trim();
         }).filter((p_213267_0_) -> {
            return !p_213267_0_.isEmpty();
         });
      }, (p_213251_0_) -> {
         return Stream.of(Registry.ITEM.getKey(p_213251_0_.getItem()));
      });
      SearchTreeReloadable<ItemStack> searchtreereloadable = new SearchTreeReloadable<>((p_213235_0_) -> {
         return p_213235_0_.getItem().getTags().stream();
      });
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(Item item : Registry.ITEM) {
         item.fillItemCategory(ItemGroup.TAB_SEARCH, nonnulllist);
      }

      nonnulllist.forEach((p_213232_2_) -> {
         searchtree.add(p_213232_2_);
         searchtreereloadable.add(p_213232_2_);
      });
      SearchTree<RecipeList> searchtree1 = new SearchTree<>((p_213252_0_) -> {
         return p_213252_0_.getRecipes().stream().flatMap((p_213234_0_) -> {
            return p_213234_0_.getResultItem().getTooltipLines((PlayerEntity)null, ITooltipFlag.TooltipFlags.NORMAL).stream();
         }).map((p_213264_0_) -> {
            return TextFormatting.stripFormatting(p_213264_0_.getString()).trim();
         }).filter((p_213238_0_) -> {
            return !p_213238_0_.isEmpty();
         });
      }, (p_213258_0_) -> {
         return p_213258_0_.getRecipes().stream().map((p_213244_0_) -> {
            return Registry.ITEM.getKey(p_213244_0_.getResultItem().getItem());
         });
      });
      this.searchRegistry.register(SearchTreeManager.CREATIVE_NAMES, searchtree);
      this.searchRegistry.register(SearchTreeManager.CREATIVE_TAGS, searchtreereloadable);
      this.searchRegistry.register(SearchTreeManager.RECIPE_COLLECTIONS, searchtree1);
   }

   private void onFullscreenError(int p_195545_1_, long p_195545_2_) {
      this.options.enableVsync = false;
      this.options.save();
   }

   private static boolean checkIs64Bit() {
      String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for(String s : astring) {
         String s1 = System.getProperty(s);
         if (s1 != null && s1.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public Framebuffer getMainRenderTarget() {
      return this.mainRenderTarget;
   }

   public String getLaunchedVersion() {
      return this.launchedVersion;
   }

   public String getVersionType() {
      return this.versionType;
   }

   public void delayCrash(CrashReport p_71404_1_) {
      this.delayedCrash = p_71404_1_;
   }

   public static void crash(CrashReport p_71377_0_) {
      File file1 = new File(getInstance().gameDirectory, "crash-reports");
      File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.realStdoutPrintln(p_71377_0_.getFriendlyReport());
      if (p_71377_0_.getSaveFile() != null) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + p_71377_0_.getSaveFile());
         net.minecraftforge.fml.server.ServerLifecycleHooks.handleExit(-1);
      } else if (p_71377_0_.saveToFile(file2)) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         net.minecraftforge.fml.server.ServerLifecycleHooks.handleExit(-1);
      } else {
         Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         net.minecraftforge.fml.server.ServerLifecycleHooks.handleExit(-2);
      }

   }

   public boolean isEnforceUnicode() {
      return this.options.forceUnicodeFont;
   }

   @Deprecated // Forge: Use selective refreshResources method in FMLClientHandler
   public CompletableFuture<Void> reloadResourcePacks() {
      if (this.pendingReload != null) {
         return this.pendingReload;
      } else {
         CompletableFuture<Void> completablefuture = new CompletableFuture<>();
         if (this.overlay instanceof ResourceLoadProgressGui) {
            this.pendingReload = completablefuture;
            return completablefuture;
         } else {
            this.resourcePackRepository.reload();
            List<IResourcePack> list = this.resourcePackRepository.openAllSelected();
            this.setOverlay(new ResourceLoadProgressGui(this, this.resourceManager.createFullReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, list), (p_238200_2_) -> {
               Util.ifElse(p_238200_2_, this::rollbackResourcePacks, () -> {
                  this.levelRenderer.allChanged();
                  completablefuture.complete((Void)null);
               });
            }, true));
            return completablefuture;
         }
      }
   }

   private void selfTest() {
      boolean flag = false;
      BlockModelShapes blockmodelshapes = this.getBlockRenderer().getBlockModelShaper();
      IBakedModel ibakedmodel = blockmodelshapes.getModelManager().getMissingModel();

      for(Block block : Registry.BLOCK) {
         for(BlockState blockstate : block.getStateDefinition().getPossibleStates()) {
            if (blockstate.getRenderShape() == BlockRenderType.MODEL) {
               IBakedModel ibakedmodel1 = blockmodelshapes.getBlockModel(blockstate);
               if (ibakedmodel1 == ibakedmodel) {
                  LOGGER.debug("Missing model for: {}", (Object)blockstate);
                  flag = true;
               }
            }
         }
      }

      TextureAtlasSprite textureatlassprite1 = ibakedmodel.getParticleIcon();

      for(Block block1 : Registry.BLOCK) {
         for(BlockState blockstate1 : block1.getStateDefinition().getPossibleStates()) {
            TextureAtlasSprite textureatlassprite = blockmodelshapes.getParticleIcon(blockstate1);
            if (!blockstate1.isAir() && textureatlassprite == textureatlassprite1) {
               LOGGER.debug("Missing particle icon for: {}", (Object)blockstate1);
               flag = true;
            }
         }
      }

      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(Item item : Registry.ITEM) {
         nonnulllist.clear();
         item.fillItemCategory(ItemGroup.TAB_SEARCH, nonnulllist);

         for(ItemStack itemstack : nonnulllist) {
            String s = itemstack.getDescriptionId();
            String s1 = (new TranslationTextComponent(s)).getString();
            if (s1.toLowerCase(Locale.ROOT).equals(item.getDescriptionId())) {
               LOGGER.debug("Missing translation for: {} {} {}", itemstack, s, itemstack.getItem());
            }
         }
      }

      flag = flag | ScreenManager.selfTest();
      if (flag) {
         throw new IllegalStateException("Your game data is foobar, fix the errors above!");
      }
   }

   public SaveFormat getLevelSource() {
      return this.levelSource;
   }

   private void openChatScreen(String p_238207_1_) {
      if (!this.isLocalServer() && !this.allowsChat()) {
         if (this.player != null) {
            this.player.sendMessage((new TranslationTextComponent("chat.cannotSend")).withStyle(TextFormatting.RED), Util.NIL_UUID);
         }
      } else {
         this.setScreen(new ChatScreen(p_238207_1_));
      }

   }

   public void setScreen(@Nullable Screen p_147108_1_) {
      if (p_147108_1_ == null && this.level == null) {
         p_147108_1_ = new MainMenuScreen();
      } else if (p_147108_1_ == null && this.player.isDeadOrDying()) {
         if (this.player.shouldShowDeathScreen()) {
            p_147108_1_ = new DeathScreen((ITextComponent)null, this.level.getLevelData().isHardcore());
         } else {
            this.player.respawn();
         }
      }

      Screen old = this.screen;
      net.minecraftforge.client.event.GuiOpenEvent event = new net.minecraftforge.client.event.GuiOpenEvent(p_147108_1_);
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;

      p_147108_1_ = event.getGui();
      if (old != null && p_147108_1_ != old)
         old.removed();

      if (p_147108_1_ instanceof MainMenuScreen || p_147108_1_ instanceof MultiplayerScreen) {
         this.options.renderDebug = false;
         this.gui.getChat().clearMessages(true);
      }

      this.screen = p_147108_1_;
      if (p_147108_1_ != null) {
         this.mouseHandler.releaseMouse();
         KeyBinding.releaseAll();
         p_147108_1_.init(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
         this.noRender = false;
         NarratorChatListener.INSTANCE.sayNow(p_147108_1_.getNarrationMessage());
      } else {
         this.soundManager.resume();
         this.mouseHandler.grabMouse();
      }

      this.updateTitle();
   }

   public void setOverlay(@Nullable LoadingGui p_213268_1_) {
      this.overlay = p_213268_1_;
   }

   public void destroy() {
      try {
         LOGGER.info("Stopping!");

         try {
            NarratorChatListener.INSTANCE.destroy();
         } catch (Throwable throwable1) {
         }

         try {
            if (this.level != null) {
               this.level.disconnect();
            }

            this.clearLevel();
         } catch (Throwable throwable) {
         }

         if (this.screen != null) {
            this.screen.removed();
         }

         this.close();
      } finally {
         Util.timeSource = System::nanoTime;
         if (this.delayedCrash == null) {
            System.exit(0);
         }

      }

   }

   public void close() {
      try {
         this.modelManager.close();
         this.fontManager.close();
         this.gameRenderer.close();
         this.levelRenderer.close();
         this.soundManager.destroy();
         this.resourcePackRepository.close();
         this.particleEngine.close();
         this.mobEffectTextures.close();
         this.paintingTextures.close();
         this.textureManager.close();
         this.resourceManager.close();
         Util.shutdownExecutors();
      } catch (Throwable throwable) {
         LOGGER.error("Shutdown failure!", throwable);
         throw throwable;
      } finally {
         this.virtualScreen.close();
         this.window.close();
      }

   }

   private void runTick(boolean p_195542_1_) {
      this.window.setErrorSection("Pre render");
      long i = Util.getNanos();
      if (this.window.shouldClose()) {
         this.stop();
      }

      if (this.pendingReload != null && !(this.overlay instanceof ResourceLoadProgressGui)) {
         CompletableFuture<Void> completablefuture = this.pendingReload;
         this.pendingReload = null;
         this.reloadResourcePacks().thenRun(() -> {
            completablefuture.complete((Void)null);
         });
      }

      Runnable runnable;
      while((runnable = this.progressTasks.poll()) != null) {
         runnable.run();
      }

      if (p_195542_1_) {
         int j = this.timer.advanceTime(Util.getMillis());
         this.profiler.push("scheduledExecutables");
         this.runAllTasks();
         this.profiler.pop();
         this.profiler.push("tick");

         for(int k = 0; k < Math.min(10, j); ++k) {
            this.profiler.incrementCounter("clientTick");
            this.tick();
         }

         this.profiler.pop();
      }

      this.mouseHandler.turnPlayer();
      this.window.setErrorSection("Render");
      this.profiler.push("sound");
      this.soundManager.updateSource(this.gameRenderer.getMainCamera());
      this.profiler.pop();
      this.profiler.push("render");
      RenderSystem.pushMatrix();
      RenderSystem.clear(16640, ON_OSX);
      this.mainRenderTarget.bindWrite(true);
      FogRenderer.setupNoFog();
      this.profiler.push("display");
      RenderSystem.enableTexture();
      RenderSystem.enableCull();
      this.profiler.pop();
      if (!this.noRender) {
         net.minecraftforge.fml.hooks.BasicEventHooks.onRenderTickStart(this.pause ? this.pausePartialTick : this.timer.partialTick);
         this.profiler.popPush("gameRenderer");
         this.gameRenderer.render(this.pause ? this.pausePartialTick : this.timer.partialTick, i, p_195542_1_);
         this.profiler.popPush("toasts");
         this.toast.render(new MatrixStack());
         this.profiler.pop();
         net.minecraftforge.fml.hooks.BasicEventHooks.onRenderTickEnd(this.pause ? this.pausePartialTick : this.timer.partialTick);
      }

      if (this.fpsPieResults != null) {
         this.profiler.push("fpsPie");
         this.renderFpsMeter(new MatrixStack(), this.fpsPieResults);
         this.profiler.pop();
      }

      this.profiler.push("blit");
      this.mainRenderTarget.unbindWrite();
      RenderSystem.popMatrix();
      RenderSystem.pushMatrix();
      this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
      RenderSystem.popMatrix();
      this.profiler.popPush("updateDisplay");
      this.window.updateDisplay();
      int i1 = this.getFramerateLimit();
      if ((double)i1 < AbstractOption.FRAMERATE_LIMIT.getMaxValue()) {
         RenderSystem.limitDisplayFPS(i1);
      }

      this.profiler.popPush("yield");
      Thread.yield();
      this.profiler.pop();
      this.window.setErrorSection("Post render");
      ++this.frames;
      boolean flag = this.hasSingleplayerServer() && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen()) && !this.singleplayerServer.isPublished();
      if (this.pause != flag) {
         if (this.pause) {
            this.pausePartialTick = this.timer.partialTick;
         } else {
            this.timer.partialTick = this.pausePartialTick;
         }

         this.pause = flag;
      }

      long l = Util.getNanos();
      this.frameTimer.logFrameDuration(l - this.lastNanoTime);
      this.lastNanoTime = l;
      this.profiler.push("fpsUpdate");

      while(Util.getMillis() >= this.lastTime + 1000L) {
         fps = this.frames;
         this.fpsString = String.format("%d fps T: %s%s%s%s B: %d", fps, (double)this.options.framerateLimit == AbstractOption.FRAMERATE_LIMIT.getMaxValue() ? "inf" : this.options.framerateLimit, this.options.enableVsync ? " vsync" : "", this.options.graphicsMode.toString(), this.options.renderClouds == CloudOption.OFF ? "" : (this.options.renderClouds == CloudOption.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius);
         this.lastTime += 1000L;
         this.frames = 0;
         this.snooper.prepare();
         if (!this.snooper.isStarted()) {
            this.snooper.start();
         }
      }

      this.profiler.pop();
   }

   private boolean shouldRenderFpsPie() {
      return this.options.renderDebug && this.options.renderDebugCharts && !this.options.hideGui;
   }

   private void startProfilers(boolean p_238201_1_, @Nullable LongTickDetector p_238201_2_) {
      if (p_238201_1_) {
         if (!this.fpsPieProfiler.isEnabled()) {
            this.fpsPieRenderTicks = 0;
            this.fpsPieProfiler.enable();
         }

         ++this.fpsPieRenderTicks;
      } else {
         this.fpsPieProfiler.disable();
      }

      this.profiler = LongTickDetector.decorateFiller(this.fpsPieProfiler.getFiller(), p_238201_2_);
   }

   private void finishProfilers(boolean p_238210_1_, @Nullable LongTickDetector p_238210_2_) {
      if (p_238210_2_ != null) {
         p_238210_2_.endTick();
      }

      if (p_238210_1_) {
         this.fpsPieResults = this.fpsPieProfiler.getResults();
      } else {
         this.fpsPieResults = null;
      }

      this.profiler = this.fpsPieProfiler.getFiller();
   }

   public void resizeDisplay() {
      int i = this.window.calculateScale(this.options.guiScale, this.isEnforceUnicode());
      this.window.setGuiScale((double)i);
      if (this.screen != null) {
         this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
      }

      Framebuffer framebuffer = this.getMainRenderTarget();
      framebuffer.resize(this.window.getWidth(), this.window.getHeight(), ON_OSX);
      if (this.gameRenderer != null)
      this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
      this.mouseHandler.setIgnoreFirstMove();
   }

   public void cursorEntered() {
      this.mouseHandler.cursorEntered();
   }

   private int getFramerateLimit() {
      return this.level != null || this.screen == null && this.overlay == null ? this.window.getFramerateLimit() : 60;
   }

   public void emergencySave() {
      try {
         reserve = new byte[0];
         this.levelRenderer.clear();
      } catch (Throwable throwable1) {
      }

      try {
         System.gc();
         if (this.isLocalServer && this.singleplayerServer != null) {
            this.singleplayerServer.halt(true);
         }

         this.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
      } catch (Throwable throwable) {
      }

      System.gc();
   }

   void debugFpsMeterKeyPress(int p_71383_1_) {
      if (this.fpsPieResults != null) {
         List<DataPoint> list = this.fpsPieResults.getTimes(this.debugPath);
         if (!list.isEmpty()) {
            DataPoint datapoint = list.remove(0);
            if (p_71383_1_ == 0) {
               if (!datapoint.name.isEmpty()) {
                  int i = this.debugPath.lastIndexOf(30);
                  if (i >= 0) {
                     this.debugPath = this.debugPath.substring(0, i);
                  }
               }
            } else {
               --p_71383_1_;
               if (p_71383_1_ < list.size() && !"unspecified".equals((list.get(p_71383_1_)).name)) {
                  if (!this.debugPath.isEmpty()) {
                     this.debugPath = this.debugPath + '\u001e';
                  }

                  this.debugPath = this.debugPath + (list.get(p_71383_1_)).name;
               }
            }

         }
      }
   }

   private void renderFpsMeter(MatrixStack p_238183_1_, IProfileResult p_238183_2_) {
      List<DataPoint> list = p_238183_2_.getTimes(this.debugPath);
      DataPoint datapoint = list.remove(0);
      RenderSystem.clear(256, ON_OSX);
      RenderSystem.matrixMode(5889);
      RenderSystem.loadIdentity();
      RenderSystem.ortho(0.0D, (double)this.window.getWidth(), (double)this.window.getHeight(), 0.0D, 1000.0D, 3000.0D);
      RenderSystem.matrixMode(5888);
      RenderSystem.loadIdentity();
      RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
      RenderSystem.lineWidth(1.0F);
      RenderSystem.disableTexture();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      int i = 160;
      int j = this.window.getWidth() - 160 - 10;
      int k = this.window.getHeight() - 320;
      RenderSystem.enableBlend();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.vertex((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
      bufferbuilder.vertex((double)((float)j - 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
      bufferbuilder.vertex((double)((float)j + 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
      bufferbuilder.vertex((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
      tessellator.end();
      RenderSystem.disableBlend();
      double d0 = 0.0D;

      for(DataPoint datapoint1 : list) {
         int l = MathHelper.floor(datapoint1.percentage / 4.0D) + 1;
         bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
         int i1 = datapoint1.getColor();
         int j1 = i1 >> 16 & 255;
         int k1 = i1 >> 8 & 255;
         int l1 = i1 & 255;
         bufferbuilder.vertex((double)j, (double)k, 0.0D).color(j1, k1, l1, 255).endVertex();

         for(int i2 = l; i2 >= 0; --i2) {
            float f = (float)((d0 + datapoint1.percentage * (double)i2 / (double)l) * (double)((float)Math.PI * 2F) / 100.0D);
            float f1 = MathHelper.sin(f) * 160.0F;
            float f2 = MathHelper.cos(f) * 160.0F * 0.5F;
            bufferbuilder.vertex((double)((float)j + f1), (double)((float)k - f2), 0.0D).color(j1, k1, l1, 255).endVertex();
         }

         tessellator.end();
         bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

         for(int l2 = l; l2 >= 0; --l2) {
            float f3 = (float)((d0 + datapoint1.percentage * (double)l2 / (double)l) * (double)((float)Math.PI * 2F) / 100.0D);
            float f4 = MathHelper.sin(f3) * 160.0F;
            float f5 = MathHelper.cos(f3) * 160.0F * 0.5F;
            if (!(f5 > 0.0F)) {
               bufferbuilder.vertex((double)((float)j + f4), (double)((float)k - f5), 0.0D).color(j1 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
               bufferbuilder.vertex((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).color(j1 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
            }
         }

         tessellator.end();
         d0 += datapoint1.percentage;
      }

      DecimalFormat decimalformat = new DecimalFormat("##0.00");
      decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      RenderSystem.enableTexture();
      String s = IProfileResult.demanglePath(datapoint.name);
      String s1 = "";
      if (!"unspecified".equals(s)) {
         s1 = s1 + "[0] ";
      }

      if (s.isEmpty()) {
         s1 = s1 + "ROOT ";
      } else {
         s1 = s1 + s + ' ';
      }

      int k2 = 16777215;
      this.font.drawShadow(p_238183_1_, s1, (float)(j - 160), (float)(k - 80 - 16), 16777215);
      s1 = decimalformat.format(datapoint.globalPercentage) + "%";
      this.font.drawShadow(p_238183_1_, s1, (float)(j + 160 - this.font.width(s1)), (float)(k - 80 - 16), 16777215);

      for(int j2 = 0; j2 < list.size(); ++j2) {
         DataPoint datapoint2 = list.get(j2);
         StringBuilder stringbuilder = new StringBuilder();
         if ("unspecified".equals(datapoint2.name)) {
            stringbuilder.append("[?] ");
         } else {
            stringbuilder.append("[").append(j2 + 1).append("] ");
         }

         String s2 = stringbuilder.append(datapoint2.name).toString();
         this.font.drawShadow(p_238183_1_, s2, (float)(j - 160), (float)(k + 80 + j2 * 8 + 20), datapoint2.getColor());
         s2 = decimalformat.format(datapoint2.percentage) + "%";
         this.font.drawShadow(p_238183_1_, s2, (float)(j + 160 - 50 - this.font.width(s2)), (float)(k + 80 + j2 * 8 + 20), datapoint2.getColor());
         s2 = decimalformat.format(datapoint2.globalPercentage) + "%";
         this.font.drawShadow(p_238183_1_, s2, (float)(j + 160 - this.font.width(s2)), (float)(k + 80 + j2 * 8 + 20), datapoint2.getColor());
      }

   }

   public void stop() {
      this.running = false;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void pauseGame(boolean p_71385_1_) {
      if (this.screen == null) {
         boolean flag = this.hasSingleplayerServer() && !this.singleplayerServer.isPublished();
         if (flag) {
            this.setScreen(new IngameMenuScreen(!p_71385_1_));
            this.soundManager.pause();
         } else {
            this.setScreen(new IngameMenuScreen(true));
         }

      }
   }

   private void continueAttack(boolean p_147115_1_) {
      if (!p_147115_1_) {
         this.missTime = 0;
      }

      if (this.missTime <= 0 && !this.player.isUsingItem()) {
         if (p_147115_1_ && this.hitResult != null && this.hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.hitResult;
            BlockPos blockpos = blockraytraceresult.getBlockPos();
            if (!this.level.isEmptyBlock(blockpos)) {
               net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(0, this.options.keyAttack, Hand.MAIN_HAND);
               if (inputEvent.isCanceled()) {
                  if (inputEvent.shouldSwingHand()) {
                     this.particleEngine.addBlockHitEffects(blockpos, blockraytraceresult);
                     this.player.swing(Hand.MAIN_HAND);
                  }
                  return;
               }
               Direction direction = blockraytraceresult.getDirection();
               if (this.gameMode.continueDestroyBlock(blockpos, direction)) {
                  if (inputEvent.shouldSwingHand()) {
                  this.particleEngine.addBlockHitEffects(blockpos, blockraytraceresult);
                  this.player.swing(Hand.MAIN_HAND);
                  }
               }
            }

         } else {
            this.gameMode.stopDestroyBlock();
         }
      }
   }

   private void startAttack() {
      if (this.missTime <= 0) {
         if (this.hitResult == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.gameMode.hasMissTime()) {
               this.missTime = 10;
            }

         } else if (!this.player.isHandsBusy()) {
            net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(0, this.options.keyAttack, Hand.MAIN_HAND);
            if (!inputEvent.isCanceled())
            switch(this.hitResult.getType()) {
            case ENTITY:
               this.gameMode.attack(this.player, ((EntityRayTraceResult)this.hitResult).getEntity());
               break;
            case BLOCK:
               BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.hitResult;
               BlockPos blockpos = blockraytraceresult.getBlockPos();
               if (!this.level.isEmptyBlock(blockpos)) {
                  this.gameMode.startDestroyBlock(blockpos, blockraytraceresult.getDirection());
                  break;
               }
            case MISS:
               if (this.gameMode.hasMissTime()) {
                  this.missTime = 10;
               }

               this.player.resetAttackStrengthTicker();
               net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(this.player);
            }

            if (inputEvent.shouldSwingHand())
            this.player.swing(Hand.MAIN_HAND);
         }
      }
   }

   private void startUseItem() {
      if (!this.gameMode.isDestroying()) {
         this.rightClickDelay = 4;
         if (!this.player.isHandsBusy()) {
            if (this.hitResult == null) {
               LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            for(Hand hand : Hand.values()) {
               net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(1, this.options.keyUse, hand);
               if (inputEvent.isCanceled()) {
                  if (inputEvent.shouldSwingHand()) this.player.swing(hand);
                  return;
               }
               ItemStack itemstack = this.player.getItemInHand(hand);
               if (this.hitResult != null) {
                  switch(this.hitResult.getType()) {
                  case ENTITY:
                     EntityRayTraceResult entityraytraceresult = (EntityRayTraceResult)this.hitResult;
                     Entity entity = entityraytraceresult.getEntity();
                     ActionResultType actionresulttype = this.gameMode.interactAt(this.player, entity, entityraytraceresult, hand);
                     if (!actionresulttype.consumesAction()) {
                        actionresulttype = this.gameMode.interact(this.player, entity, hand);
                     }

                     if (actionresulttype.consumesAction()) {
                        if (actionresulttype.shouldSwing()) {
                           if (inputEvent.shouldSwingHand())
                           this.player.swing(hand);
                        }

                        return;
                     }
                     break;
                  case BLOCK:
                     BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.hitResult;
                     int i = itemstack.getCount();
                     ActionResultType actionresulttype1 = this.gameMode.useItemOn(this.player, this.level, hand, blockraytraceresult);
                     if (actionresulttype1.consumesAction()) {
                        if (actionresulttype1.shouldSwing()) {
                           if (inputEvent.shouldSwingHand())
                           this.player.swing(hand);
                           if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.gameMode.hasInfiniteItems())) {
                              this.gameRenderer.itemInHandRenderer.itemUsed(hand);
                           }
                        }

                        return;
                     }

                     if (actionresulttype1 == ActionResultType.FAIL) {
                        return;
                     }
                  }
               }

               if (itemstack.isEmpty() && (this.hitResult == null || this.hitResult.getType() == RayTraceResult.Type.MISS))
                  net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, hand);

               if (!itemstack.isEmpty()) {
                  ActionResultType actionresulttype2 = this.gameMode.useItem(this.player, this.level, hand);
                  if (actionresulttype2.consumesAction()) {
                     if (actionresulttype2.shouldSwing()) {
                        this.player.swing(hand);
                     }

                     this.gameRenderer.itemInHandRenderer.itemUsed(hand);
                     return;
                  }
               }
            }

         }
      }
   }

   public MusicTicker getMusicManager() {
      return this.musicManager;
   }

   public void tick() {
      if (this.rightClickDelay > 0) {
         --this.rightClickDelay;
      }

      net.minecraftforge.fml.hooks.BasicEventHooks.onPreClientTick();

      this.profiler.push("gui");
      if (!this.pause) {
         this.gui.tick();
      }

      this.profiler.pop();
      this.gameRenderer.pick(1.0F);
      this.tutorial.onLookAt(this.level, this.hitResult);
      this.profiler.push("gameMode");
      if (!this.pause && this.level != null) {
         this.gameMode.tick();
      }

      this.profiler.popPush("textures");
      if (this.level != null) {
         this.textureManager.tick();
      }

      if (this.screen == null && this.player != null) {
         if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
            this.setScreen((Screen)null);
         } else if (this.player.isSleeping() && this.level != null) {
            this.setScreen(new SleepInMultiplayerScreen());
         }
      } else if (this.screen != null && this.screen instanceof SleepInMultiplayerScreen && !this.player.isSleeping()) {
         this.setScreen((Screen)null);
      }

      if (this.screen != null) {
         this.missTime = 10000;
      }

      if (this.screen != null) {
         Screen.wrapScreenError(() -> {
            this.screen.tick();
         }, "Ticking screen", this.screen.getClass().getCanonicalName());
      }

      if (!this.options.renderDebug) {
         this.gui.clearCache();
      }

      if (this.overlay == null && (this.screen == null || this.screen.passEvents)) {
         this.profiler.popPush("Keybindings");
         this.handleKeybinds();
         if (this.missTime > 0) {
            --this.missTime;
         }
      }

      if (this.level != null) {
         this.profiler.popPush("gameRenderer");
         if (!this.pause) {
            this.gameRenderer.tick();
         }

         this.profiler.popPush("levelRenderer");
         if (!this.pause) {
            this.levelRenderer.tick();
         }

         this.profiler.popPush("level");
         if (!this.pause) {
            if (this.level.getSkyFlashTime() > 0) {
               this.level.setSkyFlashTime(this.level.getSkyFlashTime() - 1);
            }

            this.level.tickEntities();
         }
      } else if (this.gameRenderer.currentEffect() != null) {
         this.gameRenderer.shutdownEffect();
      }

      if (!this.pause) {
         this.musicManager.tick();
      }

      this.soundManager.tick(this.pause);
      if (this.level != null) {
         if (!this.pause) {
            if (!this.options.joinedFirstServer && this.isMultiplayerServer()) {
               ITextComponent itextcomponent = new TranslationTextComponent("tutorial.socialInteractions.title");
               ITextComponent itextcomponent1 = new TranslationTextComponent("tutorial.socialInteractions.description", Tutorial.key("socialInteractions"));
               this.socialInteractionsToast = new TutorialToast(TutorialToast.Icons.SOCIAL_INTERACTIONS, itextcomponent, itextcomponent1, true);
               this.tutorial.addTimedToast(this.socialInteractionsToast, 160);
               this.options.joinedFirstServer = true;
               this.options.save();
            }

            this.tutorial.tick();

            try {
               this.level.tick(() -> {
                  return true;
               });
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception in world tick");
               if (this.level == null) {
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Affected level");
                  crashreportcategory.setDetail("Problem", "Level is null!");
               } else {
                  this.level.fillReportDetails(crashreport);
               }

               throw new ReportedException(crashreport);
            }
         }

         this.profiler.popPush("animateTick");
         if (!this.pause && this.level != null) {
            this.level.animateTick(MathHelper.floor(this.player.getX()), MathHelper.floor(this.player.getY()), MathHelper.floor(this.player.getZ()));
         }

         this.profiler.popPush("particles");
         if (!this.pause) {
            this.particleEngine.tick();
         }
      } else if (this.pendingConnection != null) {
         this.profiler.popPush("pendingConnection");
         this.pendingConnection.tick();
      }

      this.profiler.popPush("keyboard");
      this.keyboardHandler.tick();
      this.profiler.pop();

      net.minecraftforge.fml.hooks.BasicEventHooks.onPostClientTick();
   }

   private boolean isMultiplayerServer() {
      return !this.isLocalServer || this.singleplayerServer != null && this.singleplayerServer.isPublished();
   }

   private void handleKeybinds() {
      for(; this.options.keyTogglePerspective.consumeClick(); this.levelRenderer.needsUpdate()) {
         PointOfView pointofview = this.options.getCameraType();
         this.options.setCameraType(this.options.getCameraType().cycle());
         if (pointofview.isFirstPerson() != this.options.getCameraType().isFirstPerson()) {
            this.gameRenderer.checkEntityPostEffect(this.options.getCameraType().isFirstPerson() ? this.getCameraEntity() : null);
         }
      }

      while(this.options.keySmoothCamera.consumeClick()) {
         this.options.smoothCamera = !this.options.smoothCamera;
      }

      for(int i = 0; i < 9; ++i) {
         boolean flag = this.options.keySaveHotbarActivator.isDown();
         boolean flag1 = this.options.keyLoadHotbarActivator.isDown();
         if (this.options.keyHotbarSlots[i].consumeClick()) {
            if (this.player.isSpectator()) {
               this.gui.getSpectatorGui().onHotbarSelected(i);
            } else if (!this.player.isCreative() || this.screen != null || !flag1 && !flag) {
               this.player.inventory.selected = i;
            } else {
               CreativeScreen.handleHotbarLoadOrSave(this, i, flag1, flag);
            }
         }
      }

      while(this.options.keySocialInteractions.consumeClick()) {
         if (!this.isMultiplayerServer()) {
            this.player.displayClientMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
            NarratorChatListener.INSTANCE.sayNow(SOCIAL_INTERACTIONS_NOT_AVAILABLE.getString());
         } else {
            if (this.socialInteractionsToast != null) {
               this.tutorial.removeTimedToast(this.socialInteractionsToast);
               this.socialInteractionsToast = null;
            }

            this.setScreen(new SocialInteractionsScreen());
         }
      }

      while(this.options.keyInventory.consumeClick()) {
         if (this.gameMode.isServerControlledInventory()) {
            this.player.sendOpenInventory();
         } else {
            this.tutorial.onOpenInventory();
            this.setScreen(new InventoryScreen(this.player));
         }
      }

      while(this.options.keyAdvancements.consumeClick()) {
         this.setScreen(new AdvancementsScreen(this.player.connection.getAdvancements()));
      }

      while(this.options.keySwapOffhand.consumeClick()) {
         if (!this.player.isSpectator()) {
            this.getConnection().send(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
         }
      }

      while(this.options.keyDrop.consumeClick()) {
         if (!this.player.isSpectator() && this.player.drop(Screen.hasControlDown())) {
            this.player.swing(Hand.MAIN_HAND);
         }
      }

      boolean flag2 = this.options.chatVisibility != ChatVisibility.HIDDEN;
      if (flag2) {
         while(this.options.keyChat.consumeClick()) {
            this.openChatScreen("");
         }

         if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
            this.openChatScreen("/");
         }
      }

      if (this.player.isUsingItem()) {
         if (!this.options.keyUse.isDown()) {
            this.gameMode.releaseUsingItem(this.player);
         }

         while(this.options.keyAttack.consumeClick()) {
         }

         while(this.options.keyUse.consumeClick()) {
         }

         while(this.options.keyPickItem.consumeClick()) {
         }
      } else {
         while(this.options.keyAttack.consumeClick()) {
            this.startAttack();
         }

         while(this.options.keyUse.consumeClick()) {
            this.startUseItem();
         }

         while(this.options.keyPickItem.consumeClick()) {
            this.pickBlock();
         }
      }

      if (this.options.keyUse.isDown() && this.rightClickDelay == 0 && !this.player.isUsingItem()) {
         this.startUseItem();
      }

      this.continueAttack(this.screen == null && this.options.keyAttack.isDown() && this.mouseHandler.isMouseGrabbed());
   }

   public static DatapackCodec loadDataPacks(SaveFormat.LevelSave p_238180_0_) {
      MinecraftServer.convertFromRegionFormatIfNeeded(p_238180_0_);
      DatapackCodec datapackcodec = p_238180_0_.getDataPacks();
      if (datapackcodec == null) {
         throw new IllegalStateException("Failed to load data pack config");
      } else {
         return datapackcodec;
      }
   }

   public static IServerConfiguration loadWorldData(SaveFormat.LevelSave p_238181_0_, DynamicRegistries.Impl p_238181_1_, IResourceManager p_238181_2_, DatapackCodec p_238181_3_) {
      WorldSettingsImport<INBT> worldsettingsimport = WorldSettingsImport.create(NBTDynamicOps.INSTANCE, p_238181_2_, p_238181_1_);
      IServerConfiguration iserverconfiguration = p_238181_0_.getDataTag(worldsettingsimport, p_238181_3_);
      if (iserverconfiguration == null) {
         throw new IllegalStateException("Failed to load world");
      } else {
         return iserverconfiguration;
      }
   }

   public void loadLevel(String p_238191_1_) {
      this.doLoadLevel(p_238191_1_, DynamicRegistries.builtin(), Minecraft::loadDataPacks, Minecraft::loadWorldData, false, Minecraft.WorldSelectionType.BACKUP);
   }

   public void createLevel(String p_238192_1_, WorldSettings p_238192_2_, DynamicRegistries.Impl p_238192_3_, DimensionGeneratorSettings p_238192_4_) {
      this.doLoadLevel(p_238192_1_, p_238192_3_, (p_238179_1_) -> {
         return p_238192_2_.getDataPackConfig();
      }, (p_238187_3_, p_238187_4_, p_238187_5_, p_238187_6_) -> {
         WorldGenSettingsExport<JsonElement> worldgensettingsexport = WorldGenSettingsExport.create(JsonOps.INSTANCE, p_238192_3_);
         WorldSettingsImport<JsonElement> worldsettingsimport = WorldSettingsImport.create(JsonOps.INSTANCE, p_238187_5_, p_238192_3_);
         DataResult<DimensionGeneratorSettings> dataresult = DimensionGeneratorSettings.CODEC.encodeStart(worldgensettingsexport, p_238192_4_).setLifecycle(Lifecycle.stable()).flatMap((p_243209_1_) -> {
            return DimensionGeneratorSettings.CODEC.parse(worldsettingsimport, p_243209_1_);
         });
         DimensionGeneratorSettings dimensiongeneratorsettings = dataresult.resultOrPartial(Util.prefix("Error reading worldgen settings after loading data packs: ", LOGGER::error)).orElse(p_238192_4_);
         return new ServerWorldInfo(p_238192_2_, dimensiongeneratorsettings, dataresult.lifecycle());
      }, false, Minecraft.WorldSelectionType.CREATE);
   }

   private void doLoadLevel(String p_238195_1_, DynamicRegistries.Impl p_238195_2_, Function<SaveFormat.LevelSave, DatapackCodec> p_238195_3_, Function4<SaveFormat.LevelSave, DynamicRegistries.Impl, IResourceManager, DatapackCodec, IServerConfiguration> p_238195_4_, boolean p_238195_5_, Minecraft.WorldSelectionType p_238195_6_) {
      SaveFormat.LevelSave saveformat$levelsave;
      try {
         saveformat$levelsave = this.levelSource.createAccess(p_238195_1_);
      } catch (IOException ioexception2) {
         LOGGER.warn("Failed to read level {} data", p_238195_1_, ioexception2);
         SystemToast.onWorldAccessFailure(this, p_238195_1_);
         this.setScreen((Screen)null);
         return;
      }

      Minecraft.PackManager minecraft$packmanager;
      try {
         minecraft$packmanager = this.makeServerStem(p_238195_2_, p_238195_3_, p_238195_4_, p_238195_5_, saveformat$levelsave);
      } catch (Exception exception) {
         LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)exception);
         this.setScreen(new DatapackFailureScreen(() -> {
            this.doLoadLevel(p_238195_1_, p_238195_2_, p_238195_3_, p_238195_4_, true, p_238195_6_);
         }));

         try {
            saveformat$levelsave.close();
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to unlock access to level {}", p_238195_1_, ioexception);
         }

         return;
      }

      IServerConfiguration iserverconfiguration = minecraft$packmanager.worldData();
      boolean flag = iserverconfiguration.worldGenSettings().isOldCustomizedWorld();
      boolean flag1 = iserverconfiguration.worldGenSettingsLifecycle() != Lifecycle.stable();
      if (p_238195_6_ == Minecraft.WorldSelectionType.NONE || !flag && !flag1) {
         this.clearLevel();
         this.progressListener.set((TrackingChunkStatusListener)null);

         try {
            saveformat$levelsave.saveDataTag(p_238195_2_, iserverconfiguration);
            minecraft$packmanager.serverResources().updateGlobals();
            YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy);
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
            SkullTileEntity.setProfileCache(playerprofilecache);
            SkullTileEntity.setSessionService(minecraftsessionservice);
            PlayerProfileCache.setUsesAuthentication(false);
            this.singleplayerServer = MinecraftServer.spin((p_238188_8_) -> {
               return new IntegratedServer(p_238188_8_, this, p_238195_2_, saveformat$levelsave, minecraft$packmanager.packRepository(), minecraft$packmanager.serverResources(), iserverconfiguration, minecraftsessionservice, gameprofilerepository, playerprofilecache, (p_238211_1_) -> {
                  TrackingChunkStatusListener trackingchunkstatuslistener = new TrackingChunkStatusListener(p_238211_1_ + 0);
                  trackingchunkstatuslistener.start();
                  this.progressListener.set(trackingchunkstatuslistener);
                  return new ChainedChunkStatusListener(trackingchunkstatuslistener, this.progressTasks::add);
               });
            });
            this.isLocalServer = true;
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Starting integrated server");
            crashreportcategory.setDetail("Level ID", p_238195_1_);
            crashreportcategory.setDetail("Level Name", iserverconfiguration.getLevelName());
            throw new ReportedException(crashreport);
         }

         while(this.progressListener.get() == null) {
            Thread.yield();
         }

         WorldLoadProgressScreen worldloadprogressscreen = new WorldLoadProgressScreen(this.progressListener.get());
         this.setScreen(worldloadprogressscreen);
         this.profiler.push("waitForServer");

         while(!this.singleplayerServer.isReady()) {
            worldloadprogressscreen.tick();
            this.runTick(false);

            try {
               Thread.sleep(16L);
            } catch (InterruptedException interruptedexception) {
            }

            if (this.delayedCrash != null) {
               crash(this.delayedCrash);
               return;
            }
         }

         this.profiler.pop();
         SocketAddress socketaddress = this.singleplayerServer.getConnection().startMemoryChannel();
         NetworkManager networkmanager = NetworkManager.connectToLocalServer(socketaddress);
         networkmanager.setListener(new ClientLoginNetHandler(networkmanager, this, (Screen)null, (p_229998_0_) -> {
         }));
         networkmanager.send(new CHandshakePacket(socketaddress.toString(), 0, ProtocolType.LOGIN));
         com.mojang.authlib.GameProfile gameProfile = this.getUser().getGameProfile();
         if (!this.getUser().hasCachedProperties()) {
            gameProfile = minecraftSessionService.fillProfileProperties(gameProfile, true); //Forge: Fill profile properties upon game load. Fixes MC-52974.
            this.getUser().setProperties(gameProfile.getProperties());
         }
         networkmanager.send(new CLoginStartPacket(gameProfile));
         this.pendingConnection = networkmanager;
      } else {
         this.displayExperimentalConfirmationDialog(p_238195_6_, p_238195_1_, flag, () -> {
            this.doLoadLevel(p_238195_1_, p_238195_2_, p_238195_3_, p_238195_4_, p_238195_5_, Minecraft.WorldSelectionType.NONE);
         });
         minecraft$packmanager.close();

         try {
            saveformat$levelsave.close();
         } catch (IOException ioexception1) {
            LOGGER.warn("Failed to unlock access to level {}", p_238195_1_, ioexception1);
         }

      }
   }

   private void displayExperimentalConfirmationDialog(Minecraft.WorldSelectionType p_241559_1_, String p_241559_2_, boolean p_241559_3_, Runnable p_241559_4_) {
      if (p_241559_1_ == Minecraft.WorldSelectionType.BACKUP) {
         ITextComponent itextcomponent;
         ITextComponent itextcomponent1;
         if (p_241559_3_) {
            itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion.customized");
            itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning.customized");
         } else {
            itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion.experimental");
            itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning.experimental");
         }

         this.setScreen(new ConfirmBackupScreen((Screen)null, (p_241561_3_, p_241561_4_) -> {
            if (p_241561_3_) {
               EditWorldScreen.makeBackupAndShowToast(this.levelSource, p_241559_2_);
            }

            p_241559_4_.run();
         }, itextcomponent, itextcomponent1, false));
      } else {
         this.setScreen(new ConfirmScreen((p_241560_3_) -> {
            if (p_241560_3_) {
               p_241559_4_.run();
            } else {
               this.setScreen((Screen)null);

               try (SaveFormat.LevelSave saveformat$levelsave = this.levelSource.createAccess(p_241559_2_)) {
                  saveformat$levelsave.deleteLevel();
               } catch (IOException ioexception) {
                  SystemToast.onWorldDeleteFailure(this, p_241559_2_);
                  LOGGER.error("Failed to delete world {}", p_241559_2_, ioexception);
               }
            }

         }, new TranslationTextComponent("selectWorld.backupQuestion.experimental"), new TranslationTextComponent("selectWorld.backupWarning.experimental"), DialogTexts.GUI_PROCEED, DialogTexts.GUI_CANCEL));
      }

   }

   public Minecraft.PackManager makeServerStem(DynamicRegistries.Impl p_238189_1_, Function<SaveFormat.LevelSave, DatapackCodec> p_238189_2_, Function4<SaveFormat.LevelSave, DynamicRegistries.Impl, IResourceManager, DatapackCodec, IServerConfiguration> p_238189_3_, boolean p_238189_4_, SaveFormat.LevelSave p_238189_5_) throws InterruptedException, ExecutionException {
      DatapackCodec datapackcodec = p_238189_2_.apply(p_238189_5_);
      ResourcePackList resourcepacklist = new ResourcePackList(new ServerPackFinder(), new FolderPackFinder(p_238189_5_.getLevelPath(FolderName.DATAPACK_DIR).toFile(), IPackNameDecorator.WORLD));

      try {
         DatapackCodec datapackcodec1 = MinecraftServer.configurePackRepository(resourcepacklist, datapackcodec, p_238189_4_);
         CompletableFuture<DataPackRegistries> completablefuture = DataPackRegistries.loadResources(resourcepacklist.openAllSelected(), Commands.EnvironmentType.INTEGRATED, 2, Util.backgroundExecutor(), this);
         this.managedBlock(completablefuture::isDone);
         DataPackRegistries datapackregistries = completablefuture.get();
         IServerConfiguration iserverconfiguration = p_238189_3_.apply(p_238189_5_, p_238189_1_, datapackregistries.getResourceManager(), datapackcodec1);
         return new Minecraft.PackManager(resourcepacklist, datapackregistries, iserverconfiguration);
      } catch (ExecutionException | InterruptedException interruptedexception) {
         resourcepacklist.close();
         throw interruptedexception;
      }
   }

   public void setLevel(ClientWorld p_71403_1_) {
      if (level != null) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(level));
      WorkingScreen workingscreen = new WorkingScreen();
      workingscreen.progressStartNoAbort(new TranslationTextComponent("connect.joining"));
      this.updateScreenAndTick(workingscreen);
      this.level = p_71403_1_;
      this.updateLevelInEngines(p_71403_1_);
      if (!this.isLocalServer) {
         AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy);
         MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
         SkullTileEntity.setProfileCache(playerprofilecache);
         SkullTileEntity.setSessionService(minecraftsessionservice);
         PlayerProfileCache.setUsesAuthentication(false);
      }

   }

   public void clearLevel() {
      this.clearLevel(new WorkingScreen());
   }

   public void clearLevel(Screen p_213231_1_) {
      ClientPlayNetHandler clientplaynethandler = this.getConnection();
      if (clientplaynethandler != null) {
         this.dropAllTasks();
         clientplaynethandler.cleanup();
      }

      IntegratedServer integratedserver = this.singleplayerServer;
      this.singleplayerServer = null;
      this.gameRenderer.resetData();
      net.minecraftforge.fml.client.ClientHooks.firePlayerLogout(this.gameMode, this.player);
      this.gameMode = null;
      NarratorChatListener.INSTANCE.clear();
      this.updateScreenAndTick(p_213231_1_);
      if (this.level != null) {
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(level));
         if (integratedserver != null) {
            this.profiler.push("waitForServer");

            while(!integratedserver.isShutdown()) {
               this.runTick(false);
            }

            this.profiler.pop();
         }

         this.clientPackSource.clearServerPack();
         this.gui.onDisconnected();
         this.currentServer = null;
         this.isLocalServer = false;
         net.minecraftforge.fml.client.ClientHooks.handleClientWorldClosing(level);
         this.game.onLeaveGameSession();
      }

      this.level = null;
      this.updateLevelInEngines((ClientWorld)null);
      this.player = null;
   }

   private void updateScreenAndTick(Screen p_213241_1_) {
      this.profiler.push("forcedTick");
      this.soundManager.stop();
      this.cameraEntity = null;
      this.pendingConnection = null;
      this.setScreen(p_213241_1_);
      this.runTick(false);
      this.profiler.pop();
   }

   public void forceSetScreen(Screen p_241562_1_) {
      this.profiler.push("forcedTick");
      this.setScreen(p_241562_1_);
      this.runTick(false);
      this.profiler.pop();
   }

   private void updateLevelInEngines(@Nullable ClientWorld p_213257_1_) {
      this.levelRenderer.setLevel(p_213257_1_);
      this.particleEngine.setLevel(p_213257_1_);
      TileEntityRendererDispatcher.instance.setLevel(p_213257_1_);
      this.updateTitle();
      net.minecraftforge.client.MinecraftForgeClient.clearRenderCache();
   }

   public boolean allowsMultiplayer() {
      return this.allowsMultiplayer && this.socialInteractionsService.serversAllowed();
   }

   public boolean isBlocked(UUID p_238198_1_) {
      if (this.allowsChat()) {
         return this.playerSocialManager.shouldHideMessageFrom(p_238198_1_);
      } else {
         return (this.player == null || !p_238198_1_.equals(this.player.getUUID())) && !p_238198_1_.equals(Util.NIL_UUID);
      }
   }

   public boolean allowsChat() {
      return this.allowsChat && this.socialInteractionsService.chatAllowed();
   }

   public final boolean isDemo() {
      return this.demo;
   }

   @Nullable
   public ClientPlayNetHandler getConnection() {
      return this.player == null ? null : this.player.connection;
   }

   public static boolean renderNames() {
      return !instance.options.hideGui;
   }

   public static boolean useFancyGraphics() {
      return instance.options.graphicsMode.getId() >= GraphicsFanciness.FANCY.getId();
   }

   public static boolean useShaderTransparency() {
      return instance.options.graphicsMode.getId() >= GraphicsFanciness.FABULOUS.getId();
   }

   public static boolean useAmbientOcclusion() {
      return instance.options.ambientOcclusion != AmbientOcclusionStatus.OFF;
   }

   private void pickBlock() {
      if (this.hitResult != null && this.hitResult.getType() != RayTraceResult.Type.MISS) {
         if (!net.minecraftforge.client.ForgeHooksClient.onClickInput(2, this.options.keyPickItem, Hand.MAIN_HAND).isCanceled())
         net.minecraftforge.common.ForgeHooks.onPickBlock(this.hitResult, this.player, this.level);
         // We delete this code wholly instead of commenting it out, to make sure we detect changes in it between MC versions
      }
   }

   public ItemStack addCustomNbtData(ItemStack p_184119_1_, TileEntity p_184119_2_) {
      CompoundNBT compoundnbt = p_184119_2_.save(new CompoundNBT());
      if (p_184119_1_.getItem() instanceof SkullItem && compoundnbt.contains("SkullOwner")) {
         CompoundNBT compoundnbt2 = compoundnbt.getCompound("SkullOwner");
         p_184119_1_.getOrCreateTag().put("SkullOwner", compoundnbt2);
         return p_184119_1_;
      } else {
         p_184119_1_.addTagElement("BlockEntityTag", compoundnbt);
         CompoundNBT compoundnbt1 = new CompoundNBT();
         ListNBT listnbt = new ListNBT();
         listnbt.add(StringNBT.valueOf("\"(+NBT)\""));
         compoundnbt1.put("Lore", listnbt);
         p_184119_1_.addTagElement("display", compoundnbt1);
         return p_184119_1_;
      }
   }

   public CrashReport fillReport(CrashReport p_71396_1_) {
      fillReport(this.languageManager, this.launchedVersion, this.options, p_71396_1_);
      if (this.level != null) {
         this.level.fillReportDetails(p_71396_1_);
      }

      return p_71396_1_;
   }

   public static void fillReport(@Nullable LanguageManager p_228009_0_, String p_228009_1_, @Nullable GameSettings p_228009_2_, CrashReport p_228009_3_) {
      CrashReportCategory crashreportcategory = p_228009_3_.getSystemDetails();
      crashreportcategory.setDetail("Launched Version", () -> {
         return p_228009_1_;
      });
      crashreportcategory.setDetail("Backend library", RenderSystem::getBackendDescription);
      crashreportcategory.setDetail("Backend API", RenderSystem::getApiDescription);
      crashreportcategory.setDetail("GL Caps", RenderSystem::getCapsString);
      crashreportcategory.setDetail("Using VBOs", () -> {
         return "Yes";
      });
      crashreportcategory.setDetail("Is Modded", () -> {
         String s1 = ClientBrandRetriever.getClientModName();
         if (!"vanilla".equals(s1)) {
            return "Definitely; Client brand changed to '" + s1 + "'";
         } else {
            return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
         }
      });
      crashreportcategory.setDetail("Type", "Client (map_client.txt)");
      if (p_228009_2_ != null) {
         if (instance != null) {
            String s = instance.getGpuWarnlistManager().getAllWarnings();
            if (s != null) {
               crashreportcategory.setDetail("GPU Warnings", s);
            }
         }

         crashreportcategory.setDetail("Graphics mode", p_228009_2_.graphicsMode);
         crashreportcategory.setDetail("Resource Packs", () -> {
            StringBuilder stringbuilder = new StringBuilder();

            for(String s1 : p_228009_2_.resourcePacks) {
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(s1);
               if (p_228009_2_.incompatibleResourcePacks.contains(s1)) {
                  stringbuilder.append(" (incompatible)");
               }
            }

            return stringbuilder.toString();
         });
      }

      if (p_228009_0_ != null) {
         crashreportcategory.setDetail("Current Language", () -> {
            return p_228009_0_.getSelected().toString();
         });
      }

      crashreportcategory.setDetail("CPU", PlatformDescriptors::getCpuInfo);
   }

   public static Minecraft getInstance() {
      return instance;
   }

   @Deprecated // Forge: Use selective scheduleResourceRefresh method in FMLClientHandler
   public CompletableFuture<Void> delayTextureReload() {
      return this.submit(this::reloadResourcePacks).thenCompose((p_229993_0_) -> {
         return p_229993_0_;
      });
   }

   public void populateSnooper(Snooper p_70000_1_) {
      p_70000_1_.setDynamicData("fps", fps);
      p_70000_1_.setDynamicData("vsync_enabled", this.options.enableVsync);
      p_70000_1_.setDynamicData("display_frequency", this.window.getRefreshRate());
      p_70000_1_.setDynamicData("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
      p_70000_1_.setDynamicData("run_time", (Util.getMillis() - p_70000_1_.getStartupTime()) / 60L * 1000L);
      p_70000_1_.setDynamicData("current_action", this.getCurrentSnooperAction());
      p_70000_1_.setDynamicData("language", this.options.languageCode == null ? "en_us" : this.options.languageCode);
      String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      p_70000_1_.setDynamicData("endianness", s);
      p_70000_1_.setDynamicData("subtitles", this.options.showSubtitles);
      p_70000_1_.setDynamicData("touch", this.options.touchscreen ? "touch" : "mouse");
      int i = 0;

      for(ResourcePackInfo resourcepackinfo : this.resourcePackRepository.getSelectedPacks()) {
         if (!resourcepackinfo.isRequired() && !resourcepackinfo.isFixedPosition()) {
            p_70000_1_.setDynamicData("resource_pack[" + i++ + "]", resourcepackinfo.getId());
         }
      }

      p_70000_1_.setDynamicData("resource_packs", i);
      if (this.singleplayerServer != null) {
         p_70000_1_.setDynamicData("snooper_partner", this.singleplayerServer.getSnooper().getToken());
      }

   }

   private String getCurrentSnooperAction() {
      if (this.singleplayerServer != null) {
         return this.singleplayerServer.isPublished() ? "hosting_lan" : "singleplayer";
      } else if (this.currentServer != null) {
         return this.currentServer.isLan() ? "playing_lan" : "multiplayer";
      } else {
         return "out_of_game";
      }
   }

   public void setCurrentServer(@Nullable ServerData p_71351_1_) {
      this.currentServer = p_71351_1_;
   }

   @Nullable
   public ServerData getCurrentServer() {
      return this.currentServer;
   }

   public boolean isLocalServer() {
      return this.isLocalServer;
   }

   public boolean hasSingleplayerServer() {
      return this.isLocalServer && this.singleplayerServer != null;
   }

   @Nullable
   public IntegratedServer getSingleplayerServer() {
      return this.singleplayerServer;
   }

   public Snooper getSnooper() {
      return this.snooper;
   }

   public Session getUser() {
      return this.user;
   }

   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile gameprofile = this.getMinecraftSessionService().fillProfileProperties(this.user.getGameProfile(), false);
         this.profileProperties.putAll(gameprofile.getProperties());
      }

      return this.profileProperties;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public IResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList getResourcePackRepository() {
      return this.resourcePackRepository;
   }

   public DownloadingPackFinder getClientPackSource() {
      return this.clientPackSource;
   }

   public File getResourcePackDirectory() {
      return this.resourcePackDirectory;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public Function<ResourceLocation, TextureAtlasSprite> getTextureAtlas(ResourceLocation p_228015_1_) {
      return this.modelManager.getAtlas(p_228015_1_)::getSprite;
   }

   public boolean is64Bit() {
      return this.is64bit;
   }

   public boolean isPaused() {
      return this.pause;
   }

   public GPUWarning getGpuWarnlistManager() {
      return this.gpuWarnlistManager;
   }

   public SoundHandler getSoundManager() {
      return this.soundManager;
   }

   public BackgroundMusicSelector getSituationalMusic() {
      if (this.screen instanceof WinGameScreen) {
         return BackgroundMusicTracks.CREDITS;
      } else if (this.player != null) {
         if (this.player.level.dimension() == World.END) {
            return this.gui.getBossOverlay().shouldPlayMusic() ? BackgroundMusicTracks.END_BOSS : BackgroundMusicTracks.END;
         } else {
            Biome.Category biome$category = this.player.level.getBiome(this.player.blockPosition()).getBiomeCategory();
            if (!this.musicManager.isPlayingMusic(BackgroundMusicTracks.UNDER_WATER) && (!this.player.isUnderWater() || biome$category != Biome.Category.OCEAN && biome$category != Biome.Category.RIVER)) {
               return this.player.level.dimension() != World.NETHER && this.player.abilities.instabuild && this.player.abilities.mayfly ? BackgroundMusicTracks.CREATIVE : this.level.getBiomeManager().getNoiseBiomeAtPosition(this.player.blockPosition()).getBackgroundMusic().orElse(BackgroundMusicTracks.GAME);
            } else {
               return BackgroundMusicTracks.UNDER_WATER;
            }
         }
      } else {
         return BackgroundMusicTracks.MENU;
      }
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.minecraftSessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   @Nullable
   public Entity getCameraEntity() {
      return this.cameraEntity;
   }

   public void setCameraEntity(Entity p_175607_1_) {
      this.cameraEntity = p_175607_1_;
      this.gameRenderer.checkEntityPostEffect(p_175607_1_);
   }

   public boolean shouldEntityAppearGlowing(Entity p_238206_1_) {
      return p_238206_1_.isGlowing() || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isDown() && p_238206_1_.getType() == EntityType.PLAYER;
   }

   protected Thread getRunningThread() {
      return this.gameThread;
   }

   protected Runnable wrapRunnable(Runnable p_212875_1_) {
      return p_212875_1_;
   }

   protected boolean shouldRun(Runnable p_212874_1_) {
      return true;
   }

   public BlockRendererDispatcher getBlockRenderer() {
      return this.blockRenderer;
   }

   public EntityRendererManager getEntityRenderDispatcher() {
      return this.entityRenderDispatcher;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public FirstPersonRenderer getItemInHandRenderer() {
      return this.itemInHandRenderer;
   }

   public <T> IMutableSearchTree<T> getSearchTree(SearchTreeManager.Key<T> p_213253_1_) {
      return this.searchRegistry.getTree(p_213253_1_);
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public boolean isConnectedToRealms() {
      return this.connectedToRealms;
   }

   public void setConnectedToRealms(boolean p_181537_1_) {
      this.connectedToRealms = p_181537_1_;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public float getFrameTime() {
      return this.timer.partialTick;
   }

   public float getDeltaFrameTime() {
      return this.timer.tickDelta;
   }

   public BlockColors getBlockColors() {
      return this.blockColors;
   }

   public boolean showOnlyReducedInfo() {
      return this.player != null && this.player.isReducedDebugInfo() || this.options.reducedDebugInfo;
   }

   public ToastGui getToasts() {
      return this.toast;
   }

   public Tutorial getTutorial() {
      return this.tutorial;
   }

   public boolean isWindowActive() {
      return this.windowActive;
   }

   public CreativeSettings getHotbarManager() {
      return this.hotbarManager;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public PaintingSpriteUploader getPaintingTextures() {
      return this.paintingTextures;
   }

   public PotionSpriteUploader getMobEffectTextures() {
      return this.mobEffectTextures;
   }

   public void setWindowActive(boolean p_213228_1_) {
      this.windowActive = p_213228_1_;
   }

   public IProfiler getProfiler() {
      return this.profiler;
   }

   public MinecraftGame getGame() {
      return this.game;
   }

   public Splashes getSplashManager() {
      return this.splashManager;
   }

   @Nullable
   public LoadingGui getOverlay() {
      return this.overlay;
   }

   public FilterManager getPlayerSocialManager() {
      return this.playerSocialManager;
   }

   public boolean renderOnThread() {
      return false;
   }

   public MainWindow getWindow() {
      return this.window;
   }

   public RenderTypeBuffers renderBuffers() {
      return this.renderBuffers;
   }

   private static ResourcePackInfo createClientPackAdapter(String p_228011_0_, boolean p_228011_1_, Supplier<IResourcePack> p_228011_2_, IResourcePack p_228011_3_, PackMetadataSection p_228011_4_, ResourcePackInfo.Priority p_228011_5_, IPackNameDecorator p_228011_6_) {
      int i = p_228011_4_.getPackFormat();
      Supplier<IResourcePack> supplier = p_228011_2_;
      if (i <= 3) {
         supplier = adaptV3(p_228011_2_);
      }

      if (i <= 4) {
         supplier = adaptV4(supplier);
      }

      return new ResourcePackInfo(p_228011_0_, p_228011_1_, supplier, p_228011_3_, p_228011_4_, p_228011_5_, p_228011_6_, p_228011_3_.isHidden());
   }

   private static Supplier<IResourcePack> adaptV3(Supplier<IResourcePack> p_228021_0_) {
      return () -> {
         return new LegacyResourcePackWrapper(p_228021_0_.get(), LegacyResourcePackWrapper.V3);
      };
   }

   private static Supplier<IResourcePack> adaptV4(Supplier<IResourcePack> p_228022_0_) {
      return () -> {
         return new LegacyResourcePackWrapperV4(p_228022_0_.get());
      };
   }

   public void updateMaxMipLevel(int p_228020_1_) {
      this.modelManager.updateMaxMipLevel(p_228020_1_);
   }

   public ItemColors getItemColors() {
      return this.itemColors;
   }

   public SearchTreeManager getSearchTreeManager() {
      return this.searchRegistry;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class PackManager implements AutoCloseable {
      private final ResourcePackList packRepository;
      private final DataPackRegistries serverResources;
      private final IServerConfiguration worldData;

      private PackManager(ResourcePackList p_i232241_1_, DataPackRegistries p_i232241_2_, IServerConfiguration p_i232241_3_) {
         this.packRepository = p_i232241_1_;
         this.serverResources = p_i232241_2_;
         this.worldData = p_i232241_3_;
      }

      public ResourcePackList packRepository() {
         return this.packRepository;
      }

      public DataPackRegistries serverResources() {
         return this.serverResources;
      }

      public IServerConfiguration worldData() {
         return this.worldData;
      }

      public void close() {
         this.packRepository.close();
         this.serverResources.close();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum WorldSelectionType {
      NONE,
      CREATE,
      BACKUP;
   }
}
