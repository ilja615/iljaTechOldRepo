package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.settings.ToggleableKeyBinding;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final TypeToken<List<String>> RESOURCE_PACK_TYPE = new TypeToken<List<String>>() {
   };
   private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
   public double sensitivity = 0.5D;
   public int renderDistance = -1;
   public float entityDistanceScaling = 1.0F;
   public int framerateLimit = 120;
   public CloudOption renderClouds = CloudOption.FANCY;
   public GraphicsFanciness graphicsMode = GraphicsFanciness.FANCY;
   public AmbientOcclusionStatus ambientOcclusion = AmbientOcclusionStatus.MAX;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public ChatVisibility chatVisibility = ChatVisibility.FULL;
   public double chatOpacity = 1.0D;
   public double chatLineSpacing = 0.0D;
   public double textBackgroundOpacity = 0.5D;
   @Nullable
   public String fullscreenVideoModeString;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<PlayerModelPart> modelParts = Sets.newHashSet(PlayerModelPart.values());
   public HandSide mainHand = HandSide.RIGHT;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public double chatScale = 1.0D;
   public double chatWidth = 1.0D;
   public double chatHeightUnfocused = (double)0.44366196F;
   public double chatHeightFocused = 1.0D;
   public double chatDelay = 0.0D;
   public int mipmapLevels = 4;
   private final Map<SoundCategory, Float> sourceVolumes = Maps.newEnumMap(SoundCategory.class);
   public boolean useNativeTransport = true;
   public AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.CROSSHAIR;
   public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
   public boolean joinedFirstServer = false;
   public int biomeBlendRadius = 2;
   public double mouseWheelSensitivity = 1.0D;
   public boolean rawMouseInput = true;
   public int glDebugVerbosity = 1;
   public boolean autoJump = true;
   public boolean autoSuggestions = true;
   public boolean chatColors = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public boolean enableVsync = true;
   public boolean entityShadows = true;
   public boolean forceUnicodeFont;
   public boolean invertYMouse;
   public boolean discreteMouseScroll;
   public boolean realmsNotifications = true;
   public boolean reducedDebugInfo;
   public boolean snooperEnabled = true;
   public boolean showSubtitles;
   public boolean backgroundForChatOnly = true;
   public boolean touchscreen;
   public boolean fullscreen;
   public boolean bobView = true;
   public boolean toggleCrouch;
   public boolean toggleSprint;
   public boolean skipMultiplayerWarning;
   public boolean hideMatchedNames = true;
   public final KeyBinding keyUp = new KeyBinding("key.forward", 87, "key.categories.movement");
   public final KeyBinding keyLeft = new KeyBinding("key.left", 65, "key.categories.movement");
   public final KeyBinding keyDown = new KeyBinding("key.back", 83, "key.categories.movement");
   public final KeyBinding keyRight = new KeyBinding("key.right", 68, "key.categories.movement");
   public final KeyBinding keyJump = new KeyBinding("key.jump", 32, "key.categories.movement");
   public final KeyBinding keyShift = new ToggleableKeyBinding("key.sneak", 340, "key.categories.movement", () -> {
      return this.toggleCrouch;
   });
   public final KeyBinding keySprint = new ToggleableKeyBinding("key.sprint", 341, "key.categories.movement", () -> {
      return this.toggleSprint;
   });
   public final KeyBinding keyInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
   public final KeyBinding keySwapOffhand = new KeyBinding("key.swapOffhand", 70, "key.categories.inventory");
   public final KeyBinding keyDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
   public final KeyBinding keyUse = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
   public final KeyBinding keyAttack = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
   public final KeyBinding keyPickItem = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
   public final KeyBinding keyChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
   public final KeyBinding keyPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
   public final KeyBinding keyCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
   public final KeyBinding keySocialInteractions = new KeyBinding("key.socialInteractions", 80, "key.categories.multiplayer");
   public final KeyBinding keyScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
   public final KeyBinding keyTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
   public final KeyBinding keySmoothCamera = new KeyBinding("key.smoothCamera", InputMappings.UNKNOWN.getValue(), "key.categories.misc");
   public final KeyBinding keyFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
   public final KeyBinding keySpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputMappings.UNKNOWN.getValue(), "key.categories.misc");
   public final KeyBinding keyAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
   public final KeyBinding[] keyHotbarSlots = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
   public final KeyBinding keySaveHotbarActivator = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
   public final KeyBinding keyLoadHotbarActivator = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
   public KeyBinding[] keyMappings = ArrayUtils.addAll(new KeyBinding[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements}, this.keyHotbarSlots);
   protected Minecraft minecraft;
   private final File optionsFile;
   public Difficulty difficulty = Difficulty.NORMAL;
   public boolean hideGui;
   private PointOfView cameraType = PointOfView.FIRST_PERSON;
   public boolean renderDebug;
   public boolean renderDebugCharts;
   public boolean renderFpsChart;
   public String lastMpIp = "";
   public boolean smoothCamera;
   public double fov = 70.0D;
   public float screenEffectScale = 1.0F;
   public float fovEffectScale = 1.0F;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles = ParticleStatus.ALL;
   public NarratorStatus narratorStatus = NarratorStatus.OFF;
   public String languageCode = "en_us";
   public boolean syncWrites;

   public GameSettings(Minecraft p_i46326_1_, File p_i46326_2_) {
      setForgeKeybindProperties();
      this.minecraft = p_i46326_1_;
      this.optionsFile = new File(p_i46326_2_, "options.txt");
      if (p_i46326_1_.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         AbstractOption.RENDER_DISTANCE.setMaxValue(32.0F);
      } else {
         AbstractOption.RENDER_DISTANCE.setMaxValue(16.0F);
      }

      this.renderDistance = p_i46326_1_.is64Bit() ? 12 : 8;
      this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
      this.load();
   }

   public float getBackgroundOpacity(float p_216840_1_) {
      return this.backgroundForChatOnly ? p_216840_1_ : (float)this.textBackgroundOpacity;
   }

   public int getBackgroundColor(float p_216841_1_) {
      return (int)(this.getBackgroundOpacity(p_216841_1_) * 255.0F) << 24 & -16777216;
   }

   public int getBackgroundColor(int p_216839_1_) {
      return this.backgroundForChatOnly ? p_216839_1_ : (int)(this.textBackgroundOpacity * 255.0D) << 24 & -16777216;
   }

   public void setKey(KeyBinding p_198014_1_, InputMappings.Input p_198014_2_) {
      p_198014_1_.setKey(p_198014_2_);
      this.save();
   }

   public void load() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.sourceVolumes.clear();
         CompoundNBT compoundnbt = new CompoundNBT();

         try (BufferedReader bufferedreader = Files.newReader(this.optionsFile, Charsets.UTF_8)) {
            bufferedreader.lines().forEach((p_230004_1_) -> {
               try {
                  Iterator<String> iterator = OPTION_SPLITTER.split(p_230004_1_).iterator();
                  compoundnbt.putString(iterator.next(), iterator.next());
               } catch (Exception exception2) {
                  LOGGER.warn("Skipping bad option: {}", (Object)p_230004_1_);
               }

            });
         }

         CompoundNBT compoundnbt1 = this.dataFix(compoundnbt);
         if (!compoundnbt1.contains("graphicsMode") && compoundnbt1.contains("fancyGraphics")) {
            if ("true".equals(compoundnbt1.getString("fancyGraphics"))) {
               this.graphicsMode = GraphicsFanciness.FANCY;
            } else {
               this.graphicsMode = GraphicsFanciness.FAST;
            }
         }

         for(String s : compoundnbt1.getAllKeys()) {
            String s1 = compoundnbt1.getString(s);

            try {
               if ("autoJump".equals(s)) {
                  AbstractOption.AUTO_JUMP.set(this, s1);
               }

               if ("autoSuggestions".equals(s)) {
                  AbstractOption.AUTO_SUGGESTIONS.set(this, s1);
               }

               if ("chatColors".equals(s)) {
                  AbstractOption.CHAT_COLOR.set(this, s1);
               }

               if ("chatLinks".equals(s)) {
                  AbstractOption.CHAT_LINKS.set(this, s1);
               }

               if ("chatLinksPrompt".equals(s)) {
                  AbstractOption.CHAT_LINKS_PROMPT.set(this, s1);
               }

               if ("enableVsync".equals(s)) {
                  AbstractOption.ENABLE_VSYNC.set(this, s1);
               }

               if ("entityShadows".equals(s)) {
                  AbstractOption.ENTITY_SHADOWS.set(this, s1);
               }

               if ("forceUnicodeFont".equals(s)) {
                  AbstractOption.FORCE_UNICODE_FONT.set(this, s1);
               }

               if ("discrete_mouse_scroll".equals(s)) {
                  AbstractOption.DISCRETE_MOUSE_SCROLL.set(this, s1);
               }

               if ("invertYMouse".equals(s)) {
                  AbstractOption.INVERT_MOUSE.set(this, s1);
               }

               if ("realmsNotifications".equals(s)) {
                  AbstractOption.REALMS_NOTIFICATIONS.set(this, s1);
               }

               if ("reducedDebugInfo".equals(s)) {
                  AbstractOption.REDUCED_DEBUG_INFO.set(this, s1);
               }

               if ("showSubtitles".equals(s)) {
                  AbstractOption.SHOW_SUBTITLES.set(this, s1);
               }

               if ("snooperEnabled".equals(s)) {
                  AbstractOption.SNOOPER_ENABLED.set(this, s1);
               }

               if ("touchscreen".equals(s)) {
                  AbstractOption.TOUCHSCREEN.set(this, s1);
               }

               if ("fullscreen".equals(s)) {
                  AbstractOption.USE_FULLSCREEN.set(this, s1);
               }

               if ("bobView".equals(s)) {
                  AbstractOption.VIEW_BOBBING.set(this, s1);
               }

               if ("toggleCrouch".equals(s)) {
                  this.toggleCrouch = "true".equals(s1);
               }

               if ("toggleSprint".equals(s)) {
                  this.toggleSprint = "true".equals(s1);
               }

               if ("mouseSensitivity".equals(s)) {
                  this.sensitivity = (double)readFloat(s1);
               }

               if ("fov".equals(s)) {
                  this.fov = (double)(readFloat(s1) * 40.0F + 70.0F);
               }

               if ("screenEffectScale".equals(s)) {
                  this.screenEffectScale = readFloat(s1);
               }

               if ("fovEffectScale".equals(s)) {
                  this.fovEffectScale = readFloat(s1);
               }

               if ("gamma".equals(s)) {
                  this.gamma = (double)readFloat(s1);
               }

               if ("renderDistance".equals(s)) {
                  this.renderDistance = Integer.parseInt(s1);
               }

               if ("entityDistanceScaling".equals(s)) {
                  this.entityDistanceScaling = Float.parseFloat(s1);
               }

               if ("guiScale".equals(s)) {
                  this.guiScale = Integer.parseInt(s1);
               }

               if ("particles".equals(s)) {
                  this.particles = ParticleStatus.byId(Integer.parseInt(s1));
               }

               if ("maxFps".equals(s)) {
                  this.framerateLimit = Integer.parseInt(s1);
                  if (this.minecraft.getWindow() != null) {
                     this.minecraft.getWindow().setFramerateLimit(this.framerateLimit);
                  }
               }

               if ("difficulty".equals(s)) {
                  this.difficulty = Difficulty.byId(Integer.parseInt(s1));
               }

               if ("graphicsMode".equals(s)) {
                  this.graphicsMode = GraphicsFanciness.byId(Integer.parseInt(s1));
               }

               if ("tutorialStep".equals(s)) {
                  this.tutorialStep = TutorialSteps.getByName(s1);
               }

               if ("ao".equals(s)) {
                  if ("true".equals(s1)) {
                     this.ambientOcclusion = AmbientOcclusionStatus.MAX;
                  } else if ("false".equals(s1)) {
                     this.ambientOcclusion = AmbientOcclusionStatus.OFF;
                  } else {
                     this.ambientOcclusion = AmbientOcclusionStatus.byId(Integer.parseInt(s1));
                  }
               }

               if ("renderClouds".equals(s)) {
                  if ("true".equals(s1)) {
                     this.renderClouds = CloudOption.FANCY;
                  } else if ("false".equals(s1)) {
                     this.renderClouds = CloudOption.OFF;
                  } else if ("fast".equals(s1)) {
                     this.renderClouds = CloudOption.FAST;
                  }
               }

               if ("attackIndicator".equals(s)) {
                  this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(s1));
               }

               if ("resourcePacks".equals(s)) {
                  this.resourcePacks = JSONUtils.fromJson(GSON, s1, RESOURCE_PACK_TYPE);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(s)) {
                  this.incompatibleResourcePacks = JSONUtils.fromJson(GSON, s1, RESOURCE_PACK_TYPE);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(s)) {
                  this.lastMpIp = s1;
               }

               if ("lang".equals(s)) {
                  this.languageCode = s1;
               }

               if ("chatVisibility".equals(s)) {
                  this.chatVisibility = ChatVisibility.byId(Integer.parseInt(s1));
               }

               if ("chatOpacity".equals(s)) {
                  this.chatOpacity = (double)readFloat(s1);
               }

               if ("chatLineSpacing".equals(s)) {
                  this.chatLineSpacing = (double)readFloat(s1);
               }

               if ("textBackgroundOpacity".equals(s)) {
                  this.textBackgroundOpacity = (double)readFloat(s1);
               }

               if ("backgroundForChatOnly".equals(s)) {
                  this.backgroundForChatOnly = "true".equals(s1);
               }

               if ("fullscreenResolution".equals(s)) {
                  this.fullscreenVideoModeString = s1;
               }

               if ("hideServerAddress".equals(s)) {
                  this.hideServerAddress = "true".equals(s1);
               }

               if ("advancedItemTooltips".equals(s)) {
                  this.advancedItemTooltips = "true".equals(s1);
               }

               if ("pauseOnLostFocus".equals(s)) {
                  this.pauseOnLostFocus = "true".equals(s1);
               }

               if ("overrideHeight".equals(s)) {
                  this.overrideHeight = Integer.parseInt(s1);
               }

               if ("overrideWidth".equals(s)) {
                  this.overrideWidth = Integer.parseInt(s1);
               }

               if ("heldItemTooltips".equals(s)) {
                  this.heldItemTooltips = "true".equals(s1);
               }

               if ("chatHeightFocused".equals(s)) {
                  this.chatHeightFocused = (double)readFloat(s1);
               }

               if ("chatDelay".equals(s)) {
                  this.chatDelay = (double)readFloat(s1);
               }

               if ("chatHeightUnfocused".equals(s)) {
                  this.chatHeightUnfocused = (double)readFloat(s1);
               }

               if ("chatScale".equals(s)) {
                  this.chatScale = (double)readFloat(s1);
               }

               if ("chatWidth".equals(s)) {
                  this.chatWidth = (double)readFloat(s1);
               }

               if ("mipmapLevels".equals(s)) {
                  this.mipmapLevels = Integer.parseInt(s1);
               }

               if ("useNativeTransport".equals(s)) {
                  this.useNativeTransport = "true".equals(s1);
               }

               if ("mainHand".equals(s)) {
                  this.mainHand = "left".equals(s1) ? HandSide.LEFT : HandSide.RIGHT;
               }

               if ("narrator".equals(s)) {
                  this.narratorStatus = NarratorStatus.byId(Integer.parseInt(s1));
               }

               if ("biomeBlendRadius".equals(s)) {
                  this.biomeBlendRadius = Integer.parseInt(s1);
               }

               if ("mouseWheelSensitivity".equals(s)) {
                  this.mouseWheelSensitivity = (double)readFloat(s1);
               }

               if ("rawMouseInput".equals(s)) {
                  this.rawMouseInput = "true".equals(s1);
               }

               if ("glDebugVerbosity".equals(s)) {
                  this.glDebugVerbosity = Integer.parseInt(s1);
               }

               if ("skipMultiplayerWarning".equals(s)) {
                  this.skipMultiplayerWarning = "true".equals(s1);
               }

               if ("hideMatchedNames".equals(s)) {
                  this.hideMatchedNames = "true".equals(s1);
               }

               if ("joinedFirstServer".equals(s)) {
                  this.joinedFirstServer = "true".equals(s1);
               }

               if ("syncChunkWrites".equals(s)) {
                  this.syncWrites = "true".equals(s1);
               }

               for(KeyBinding keybinding : this.keyMappings) {
                  if (s.equals("key_" + keybinding.getName())) {
                     if (s1.indexOf(':') != -1) {
                        String[] pts = s1.split(":");
                        keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.valueFromString(pts[1]), InputMappings.getKey(pts[0]));
                     } else
                        keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, InputMappings.getKey(s1));
                  }
               }

               for(SoundCategory soundcategory : SoundCategory.values()) {
                  if (s.equals("soundCategory_" + soundcategory.getName())) {
                     this.sourceVolumes.put(soundcategory, readFloat(s1));
                  }
               }

               for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
                  if (s.equals("modelPart_" + playermodelpart.getId())) {
                     this.setModelPart(playermodelpart, "true".equals(s1));
                  }
               }
            } catch (Exception exception) {
               LOGGER.warn("Skipping bad option: {}:{}", s, s1);
            }
         }

         KeyBinding.resetMapping();
      } catch (Exception exception1) {
         LOGGER.error("Failed to load options", (Throwable)exception1);
      }

   }

   private CompoundNBT dataFix(CompoundNBT p_189988_1_) {
      int i = 0;

      try {
         i = Integer.parseInt(p_189988_1_.getString("version"));
      } catch (RuntimeException runtimeexception) {
      }

      return NBTUtil.update(this.minecraft.getFixerUpper(), DefaultTypeReferences.OPTIONS, p_189988_1_, i);
   }

   private static float readFloat(String p_74305_0_) {
      if ("true".equals(p_74305_0_)) {
         return 1.0F;
      } else {
         return "false".equals(p_74305_0_) ? 0.0F : Float.parseFloat(p_74305_0_);
      }
   }

   public void save() {
      if (net.minecraftforge.fml.client.ClientModLoader.isLoading()) return; //Don't save settings before mods add keybindigns and the like to prevent them from being deleted.
      try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
         printwriter.println("version:" + SharedConstants.getCurrentVersion().getWorldVersion());
         printwriter.println("autoJump:" + AbstractOption.AUTO_JUMP.get(this));
         printwriter.println("autoSuggestions:" + AbstractOption.AUTO_SUGGESTIONS.get(this));
         printwriter.println("chatColors:" + AbstractOption.CHAT_COLOR.get(this));
         printwriter.println("chatLinks:" + AbstractOption.CHAT_LINKS.get(this));
         printwriter.println("chatLinksPrompt:" + AbstractOption.CHAT_LINKS_PROMPT.get(this));
         printwriter.println("enableVsync:" + AbstractOption.ENABLE_VSYNC.get(this));
         printwriter.println("entityShadows:" + AbstractOption.ENTITY_SHADOWS.get(this));
         printwriter.println("forceUnicodeFont:" + AbstractOption.FORCE_UNICODE_FONT.get(this));
         printwriter.println("discrete_mouse_scroll:" + AbstractOption.DISCRETE_MOUSE_SCROLL.get(this));
         printwriter.println("invertYMouse:" + AbstractOption.INVERT_MOUSE.get(this));
         printwriter.println("realmsNotifications:" + AbstractOption.REALMS_NOTIFICATIONS.get(this));
         printwriter.println("reducedDebugInfo:" + AbstractOption.REDUCED_DEBUG_INFO.get(this));
         printwriter.println("snooperEnabled:" + AbstractOption.SNOOPER_ENABLED.get(this));
         printwriter.println("showSubtitles:" + AbstractOption.SHOW_SUBTITLES.get(this));
         printwriter.println("touchscreen:" + AbstractOption.TOUCHSCREEN.get(this));
         printwriter.println("fullscreen:" + AbstractOption.USE_FULLSCREEN.get(this));
         printwriter.println("bobView:" + AbstractOption.VIEW_BOBBING.get(this));
         printwriter.println("toggleCrouch:" + this.toggleCrouch);
         printwriter.println("toggleSprint:" + this.toggleSprint);
         printwriter.println("mouseSensitivity:" + this.sensitivity);
         printwriter.println("fov:" + (this.fov - 70.0D) / 40.0D);
         printwriter.println("screenEffectScale:" + this.screenEffectScale);
         printwriter.println("fovEffectScale:" + this.fovEffectScale);
         printwriter.println("gamma:" + this.gamma);
         printwriter.println("renderDistance:" + this.renderDistance);
         printwriter.println("entityDistanceScaling:" + this.entityDistanceScaling);
         printwriter.println("guiScale:" + this.guiScale);
         printwriter.println("particles:" + this.particles.getId());
         printwriter.println("maxFps:" + this.framerateLimit);
         printwriter.println("difficulty:" + this.difficulty.getId());
         printwriter.println("graphicsMode:" + this.graphicsMode.getId());
         printwriter.println("ao:" + this.ambientOcclusion.getId());
         printwriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
         switch(this.renderClouds) {
         case FANCY:
            printwriter.println("renderClouds:true");
            break;
         case FAST:
            printwriter.println("renderClouds:fast");
            break;
         case OFF:
            printwriter.println("renderClouds:false");
         }

         printwriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
         printwriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
         printwriter.println("lastServer:" + this.lastMpIp);
         printwriter.println("lang:" + this.languageCode);
         printwriter.println("chatVisibility:" + this.chatVisibility.getId());
         printwriter.println("chatOpacity:" + this.chatOpacity);
         printwriter.println("chatLineSpacing:" + this.chatLineSpacing);
         printwriter.println("textBackgroundOpacity:" + this.textBackgroundOpacity);
         printwriter.println("backgroundForChatOnly:" + this.backgroundForChatOnly);
         if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
            printwriter.println("fullscreenResolution:" + this.minecraft.getWindow().getPreferredFullscreenVideoMode().get().write());
         }

         printwriter.println("hideServerAddress:" + this.hideServerAddress);
         printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printwriter.println("overrideWidth:" + this.overrideWidth);
         printwriter.println("overrideHeight:" + this.overrideHeight);
         printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printwriter.println("chatDelay: " + this.chatDelay);
         printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printwriter.println("chatScale:" + this.chatScale);
         printwriter.println("chatWidth:" + this.chatWidth);
         printwriter.println("mipmapLevels:" + this.mipmapLevels);
         printwriter.println("useNativeTransport:" + this.useNativeTransport);
         printwriter.println("mainHand:" + (this.mainHand == HandSide.LEFT ? "left" : "right"));
         printwriter.println("attackIndicator:" + this.attackIndicator.getId());
         printwriter.println("narrator:" + this.narratorStatus.getId());
         printwriter.println("tutorialStep:" + this.tutorialStep.getName());
         printwriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
         printwriter.println("rawMouseInput:" + AbstractOption.RAW_MOUSE_INPUT.get(this));
         printwriter.println("glDebugVerbosity:" + this.glDebugVerbosity);
         printwriter.println("skipMultiplayerWarning:" + this.skipMultiplayerWarning);
         printwriter.println("hideMatchedNames:" + this.hideMatchedNames);
         printwriter.println("joinedFirstServer:" + this.joinedFirstServer);
         printwriter.println("syncChunkWrites:" + this.syncWrites);

         for(KeyBinding keybinding : this.keyMappings) {
            printwriter.println("key_" + keybinding.getName() + ":" + keybinding.saveString() + (keybinding.getKeyModifier() != net.minecraftforge.client.settings.KeyModifier.NONE ? ":" + keybinding.getKeyModifier() : ""));
         }

         for(SoundCategory soundcategory : SoundCategory.values()) {
            printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundSourceVolume(soundcategory));
         }

         for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
            printwriter.println("modelPart_" + playermodelpart.getId() + ":" + this.modelParts.contains(playermodelpart));
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to save options", (Throwable)exception);
      }

      this.broadcastOptions();
   }

   public float getSoundSourceVolume(SoundCategory p_186711_1_) {
      return this.sourceVolumes.containsKey(p_186711_1_) ? this.sourceVolumes.get(p_186711_1_) : 1.0F;
   }

   public void setSoundCategoryVolume(SoundCategory p_186712_1_, float p_186712_2_) {
      this.sourceVolumes.put(p_186712_1_, p_186712_2_);
      this.minecraft.getSoundManager().updateSourceVolume(p_186712_1_, p_186712_2_);
   }

   public void broadcastOptions() {
      if (this.minecraft.player != null) {
         int i = 0;

         for(PlayerModelPart playermodelpart : this.modelParts) {
            i |= playermodelpart.getMask();
         }

         this.minecraft.player.connection.send(new CClientSettingsPacket(this.languageCode, this.renderDistance, this.chatVisibility, this.chatColors, i, this.mainHand));
      }

   }

   public Set<PlayerModelPart> getModelParts() {
      return ImmutableSet.copyOf(this.modelParts);
   }

   public void setModelPart(PlayerModelPart p_178878_1_, boolean p_178878_2_) {
      if (p_178878_2_) {
         this.modelParts.add(p_178878_1_);
      } else {
         this.modelParts.remove(p_178878_1_);
      }

      this.broadcastOptions();
   }

   public void toggleModelPart(PlayerModelPart p_178877_1_) {
      if (this.getModelParts().contains(p_178877_1_)) {
         this.modelParts.remove(p_178877_1_);
      } else {
         this.modelParts.add(p_178877_1_);
      }

      this.broadcastOptions();
   }

   public CloudOption getCloudsType() {
      return this.renderDistance >= 4 ? this.renderClouds : CloudOption.OFF;
   }

   public boolean useNativeTransport() {
      return this.useNativeTransport;
   }

   public void loadSelectedResourcePacks(ResourcePackList p_198017_1_) {
      Set<String> set = Sets.newLinkedHashSet();
      Iterator<String> iterator = this.resourcePacks.iterator();

      while(iterator.hasNext()) {
         String s = iterator.next();
         ResourcePackInfo resourcepackinfo = p_198017_1_.getPack(s);
         if (resourcepackinfo == null && !s.startsWith("file/")) {
            resourcepackinfo = p_198017_1_.getPack("file/" + s);
         }

         if (resourcepackinfo == null) {
            LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)s);
            iterator.remove();
         } else if (!resourcepackinfo.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(s)) {
            LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)s);
            iterator.remove();
         } else if (resourcepackinfo.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(s)) {
            LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)s);
            this.incompatibleResourcePacks.remove(s);
         } else {
            set.add(resourcepackinfo.getId());
         }
      }

      p_198017_1_.setSelected(set);
   }

   private void setForgeKeybindProperties() {
      net.minecraftforge.client.settings.KeyConflictContext inGame = net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
      keyUp.setKeyConflictContext(inGame);
      keyLeft.setKeyConflictContext(inGame);
      keyDown.setKeyConflictContext(inGame);
      keyRight.setKeyConflictContext(inGame);
      keyJump.setKeyConflictContext(inGame);
      keyShift.setKeyConflictContext(inGame);
      keySprint.setKeyConflictContext(inGame);
      keyAttack.setKeyConflictContext(inGame);
      keyChat.setKeyConflictContext(inGame);
      keyPlayerList.setKeyConflictContext(inGame);
      keyCommand.setKeyConflictContext(inGame);
      keyTogglePerspective.setKeyConflictContext(inGame);
      keySmoothCamera.setKeyConflictContext(inGame);
   }

   public PointOfView getCameraType() {
      return this.cameraType;
   }

   public void setCameraType(PointOfView p_243229_1_) {
      this.cameraType = p_243229_1_;
   }
}
