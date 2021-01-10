package net.minecraft.client;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.GPUWarning;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.settings.SliderMultiplierOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractOption {
   public static final SliderPercentageOption BIOME_BLEND_RADIUS = new SliderPercentageOption("options.biomeBlendRadius", 0.0D, 7.0D, 1.0F, (settings) -> {
      return (double)settings.biomeBlendRadius;
   }, (settings, optionValues) -> {
      settings.biomeBlendRadius = MathHelper.clamp((int)optionValues.doubleValue(), 0, 7);
      Minecraft.getInstance().worldRenderer.loadRenderers();
   }, (settings, optionValues) -> {
      double d0 = optionValues.get(settings);
      int i = (int)d0 * 2 + 1;
      return optionValues.getGenericValueComponent(new TranslationTextComponent("options.biomeBlendRadius." + i));
   });
   public static final SliderPercentageOption CHAT_HEIGHT_FOCUSED = new SliderPercentageOption("options.chat.height.focused", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.chatHeightFocused;
   }, (settings, optionValues) -> {
      settings.chatHeightFocused = optionValues;
      Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      return optionValues.getPixelValueComponent(NewChatGui.calculateChatboxHeight(d0));
   });
   public static final SliderPercentageOption CHAT_HEIGHT_UNFOCUSED = new SliderPercentageOption("options.chat.height.unfocused", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.chatHeightUnfocused;
   }, (settings, optionValues) -> {
      settings.chatHeightUnfocused = optionValues;
      Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      return optionValues.getPixelValueComponent(NewChatGui.calculateChatboxHeight(d0));
   });
   public static final SliderPercentageOption CHAT_OPACITY = new SliderPercentageOption("options.chat.opacity", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.chatOpacity;
   }, (settings, optionValues) -> {
      settings.chatOpacity = optionValues;
      Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      return optionValues.getPercentValueComponent(d0 * 0.9D + 0.1D);
   });
   public static final SliderPercentageOption CHAT_SCALE = new SliderPercentageOption("options.chat.scale", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.chatScale;
   }, (settings, optionValues) -> {
      settings.chatScale = optionValues;
      Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      return (ITextComponent)(d0 == 0.0D ? DialogTexts.getComposedOptionMessage(optionValues.getBaseMessageTranslation(), false) : optionValues.getPercentValueComponent(d0));
   });
   public static final SliderPercentageOption CHAT_WIDTH = new SliderPercentageOption("options.chat.width", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.chatWidth;
   }, (settings, optionValues) -> {
      settings.chatWidth = optionValues;
      Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      return optionValues.getPixelValueComponent(NewChatGui.calculateChatboxWidth(d0));
   });
   public static final SliderPercentageOption LINE_SPACING = new SliderPercentageOption("options.chat.line_spacing", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.chatLineSpacing;
   }, (settings, optionValues) -> {
      settings.chatLineSpacing = optionValues;
   }, (settings, optionValues) -> {
      return optionValues.getPercentValueComponent(optionValues.normalizeValue(optionValues.get(settings)));
   });
   public static final SliderPercentageOption DELAY_INSTANT = new SliderPercentageOption("options.chat.delay_instant", 0.0D, 6.0D, 0.1F, (settings) -> {
      return settings.chatDelay;
   }, (settings, optionValues) -> {
      settings.chatDelay = optionValues;
   }, (settings, optionValues) -> {
      double d0 = optionValues.get(settings);
      return d0 <= 0.0D ? new TranslationTextComponent("options.chat.delay_none") : new TranslationTextComponent("options.chat.delay", String.format("%.1f", d0));
   });
   public static final SliderPercentageOption FOV = new SliderPercentageOption("options.fov", 30.0D, 110.0D, 1.0F, (settings) -> {
      return settings.fov;
   }, (settings, optionValues) -> {
      settings.fov = optionValues;
   }, (settings, optionValues) -> {
      double d0 = optionValues.get(settings);
      if (d0 == 70.0D) {
         return optionValues.getGenericValueComponent(new TranslationTextComponent("options.fov.min"));
      } else {
         return d0 == optionValues.getMaxValue() ? optionValues.getGenericValueComponent(new TranslationTextComponent("options.fov.max")) : optionValues.getMessageWithValue((int)d0);
      }
   });
   private static final ITextComponent FOV_EFFECT_SCALE_TOOLTIP = new TranslationTextComponent("options.fovEffectScale.tooltip");
   public static final SliderPercentageOption FOV_EFFECT_SCALE_SLIDER = new SliderPercentageOption("options.fovEffectScale", 0.0D, 1.0D, 0.0F, (settings) -> {
      return Math.pow((double)settings.fovScaleEffect, 2.0D);
   }, (settings, optionValues) -> {
      settings.fovScaleEffect = MathHelper.sqrt(optionValues);
   }, (settings, optionValues) -> {
      optionValues.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FOV_EFFECT_SCALE_TOOLTIP, 200));
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      return d0 == 0.0D ? optionValues.getGenericValueComponent(new TranslationTextComponent("options.fovEffectScale.off")) : optionValues.getPercentValueComponent(d0);
   });
   private static final ITextComponent SCREEN_EFFECT_SCALE_TOOLTIP = new TranslationTextComponent("options.screenEffectScale.tooltip");
   public static final SliderPercentageOption SCREEN_EFFECT_SCALE_SLIDER = new SliderPercentageOption("options.screenEffectScale", 0.0D, 1.0D, 0.0F, (settings) -> {
      return (double)settings.screenEffectScale;
   }, (settings, percentage) -> {
      settings.screenEffectScale = percentage.floatValue();
   }, (percentage, percentage2) -> {
      percentage2.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(SCREEN_EFFECT_SCALE_TOOLTIP, 200));
      double d0 = percentage2.normalizeValue(percentage2.get(percentage));
      return d0 == 0.0D ? percentage2.getGenericValueComponent(new TranslationTextComponent("options.screenEffectScale.off")) : percentage2.getPercentValueComponent(d0);
   });
   public static final SliderPercentageOption FRAMERATE_LIMIT = new SliderPercentageOption("options.framerateLimit", 10.0D, 260.0D, 10.0F, (settings) -> {
      return (double)settings.framerateLimit;
   }, (settings, percentage) -> {
      settings.framerateLimit = (int)percentage.doubleValue();
      Minecraft.getInstance().getMainWindow().setFramerateLimit(settings.framerateLimit);
   }, (settings, percentage) -> {
      double d0 = percentage.get(settings);
      return d0 == percentage.getMaxValue() ? percentage.getGenericValueComponent(new TranslationTextComponent("options.framerateLimit.max")) : percentage.getGenericValueComponent(new TranslationTextComponent("options.framerate", (int)d0));
   });
   public static final SliderPercentageOption GAMMA = new SliderPercentageOption("options.gamma", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.gamma;
   }, (settings, optionValues) -> {
      settings.gamma = optionValues;
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      if (d0 == 0.0D) {
         return optionValues.getGenericValueComponent(new TranslationTextComponent("options.gamma.min"));
      } else {
         return d0 == 1.0D ? optionValues.getGenericValueComponent(new TranslationTextComponent("options.gamma.max")) : optionValues.getPercentageAddMessage((int)(d0 * 100.0D));
      }
   });
   public static final SliderPercentageOption MIPMAP_LEVELS = new SliderPercentageOption("options.mipmapLevels", 0.0D, 4.0D, 1.0F, (settings) -> {
      return (double)settings.mipmapLevels;
   }, (settings, optionValues) -> {
      settings.mipmapLevels = (int)optionValues.doubleValue();
   }, (settings, optionValues) -> {
      double d0 = optionValues.get(settings);
      return (ITextComponent)(d0 == 0.0D ? DialogTexts.getComposedOptionMessage(optionValues.getBaseMessageTranslation(), false) : optionValues.getMessageWithValue((int)d0));
   });
   public static final SliderPercentageOption MOUSE_WHEEL_SENSITIVITY = new SliderMultiplierOption("options.mouseWheelSensitivity", 0.01D, 10.0D, 0.01F, (settings) -> {
      return settings.mouseWheelSensitivity;
   }, (settings, optionValues) -> {
      settings.mouseWheelSensitivity = optionValues;
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      return optionValues.getGenericValueComponent(new StringTextComponent(String.format("%.2f", optionValues.denormalizeValue(d0))));
   });
   public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", (settings) -> {
      return settings.rawMouseInput;
   }, (settings, optionValues) -> {
      settings.rawMouseInput = optionValues;
      MainWindow mainwindow = Minecraft.getInstance().getMainWindow();
      if (mainwindow != null) {
         mainwindow.setRawMouseInput(optionValues);
      }

   });
   public static final SliderPercentageOption RENDER_DISTANCE = new SliderPercentageOption("options.renderDistance", 2.0D, 16.0D, 1.0F, (settings) -> {
      return (double)settings.renderDistanceChunks;
   }, (settings, optionValues) -> {
      settings.renderDistanceChunks = (int)optionValues.doubleValue();
      Minecraft.getInstance().worldRenderer.setDisplayListEntitiesDirty();
   }, (settings, optionValues) -> {
      double d0 = optionValues.get(settings);
      return optionValues.getGenericValueComponent(new TranslationTextComponent("options.chunks", (int)d0));
   });
   public static final SliderPercentageOption ENTITY_DISTANCE_SCALING = new SliderPercentageOption("options.entityDistanceScaling", 0.5D, 5.0D, 0.25F, (settings) -> {
      return (double)settings.entityDistanceScaling;
   }, (settings, optionValues) -> {
      settings.entityDistanceScaling = (float)optionValues.doubleValue();
   }, (settings, optionValues) -> {
      double d0 = optionValues.get(settings);
      return optionValues.getPercentValueComponent(d0);
   });
   public static final SliderPercentageOption SENSITIVITY = new SliderPercentageOption("options.sensitivity", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.mouseSensitivity;
   }, (settings, optionValues) -> {
      settings.mouseSensitivity = optionValues;
   }, (settings, optionValues) -> {
      double d0 = optionValues.normalizeValue(optionValues.get(settings));
      if (d0 == 0.0D) {
         return optionValues.getGenericValueComponent(new TranslationTextComponent("options.sensitivity.min"));
      } else {
         return d0 == 1.0D ? optionValues.getGenericValueComponent(new TranslationTextComponent("options.sensitivity.max")) : optionValues.getPercentValueComponent(2.0D * d0);
      }
   });
   public static final SliderPercentageOption ACCESSIBILITY_TEXT_BACKGROUND_OPACITY = new SliderPercentageOption("options.accessibility.text_background_opacity", 0.0D, 1.0D, 0.0F, (settings) -> {
      return settings.accessibilityTextBackgroundOpacity;
   }, (settings, optionValues) -> {
      settings.accessibilityTextBackgroundOpacity = optionValues;
      Minecraft.getInstance().ingameGUI.getChatGUI().refreshChat();
   }, (settings, optionValues) -> {
      return optionValues.getPercentValueComponent(optionValues.normalizeValue(optionValues.get(settings)));
   });
   public static final IteratableOption AO = new IteratableOption("options.ao", (settings, optionValues) -> {
      settings.ambientOcclusionStatus = AmbientOcclusionStatus.getValue(settings.ambientOcclusionStatus.getId() + optionValues);
      Minecraft.getInstance().worldRenderer.loadRenderers();
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.ambientOcclusionStatus.getResourceKey()));
   });
   public static final IteratableOption ATTACK_INDICATOR = new IteratableOption("options.attackIndicator", (settings, optionValues) -> {
      settings.attackIndicator = AttackIndicatorStatus.byId(settings.attackIndicator.getId() + optionValues);
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.attackIndicator.getResourceKey()));
   });
   public static final IteratableOption CHAT_VISIBILITY = new IteratableOption("options.chat.visibility", (settings, optionValues) -> {
      settings.chatVisibility = ChatVisibility.getValue((settings.chatVisibility.getId() + optionValues) % 3);
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.chatVisibility.getResourceKey()));
   });
   private static final ITextComponent FAST_GRAPHICS = new TranslationTextComponent("options.graphics.fast.tooltip");
   private static final ITextComponent FABULOUS_GRAPHICS = new TranslationTextComponent("options.graphics.fabulous.tooltip", (new TranslationTextComponent("options.graphics.fabulous")).mergeStyle(TextFormatting.ITALIC));
   private static final ITextComponent FANCY_GRAPHICS = new TranslationTextComponent("options.graphics.fancy.tooltip");
   public static final IteratableOption GRAPHICS = new IteratableOption("options.graphics", (settings, optionValues) -> {
      Minecraft minecraft = Minecraft.getInstance();
      GPUWarning gpuwarning = minecraft.getGPUWarning();
      if (settings.graphicFanciness == GraphicsFanciness.FANCY && gpuwarning.func_241695_b_()) {
         gpuwarning.func_241697_d_();
      } else {
         settings.graphicFanciness = settings.graphicFanciness.func_238166_c_();
         if (settings.graphicFanciness == GraphicsFanciness.FABULOUS && (!GlStateManager.isFabulous() || gpuwarning.func_241701_h_())) {
            settings.graphicFanciness = GraphicsFanciness.FAST;
         }

         minecraft.worldRenderer.loadRenderers();
      }
   }, (settings, optionValues) -> {
      switch(settings.graphicFanciness) {
      case FAST:
         optionValues.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FAST_GRAPHICS, 200));
         break;
      case FANCY:
         optionValues.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FANCY_GRAPHICS, 200));
         break;
      case FABULOUS:
         optionValues.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(FABULOUS_GRAPHICS, 200));
      }

      IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(settings.graphicFanciness.func_238164_b_());
      return settings.graphicFanciness == GraphicsFanciness.FABULOUS ? optionValues.getGenericValueComponent(iformattabletextcomponent.mergeStyle(TextFormatting.ITALIC)) : optionValues.getGenericValueComponent(iformattabletextcomponent);
   });
   public static final IteratableOption GUI_SCALE = new IteratableOption("options.guiScale", (settings, optionValues) -> {
      settings.guiScale = Integer.remainderUnsigned(settings.guiScale + optionValues, Minecraft.getInstance().getMainWindow().calcGuiScale(0, Minecraft.getInstance().getForceUnicodeFont()) + 1);
   }, (settings, optionValues) -> {
      return settings.guiScale == 0 ? optionValues.getGenericValueComponent(new TranslationTextComponent("options.guiScale.auto")) : optionValues.getMessageWithValue(settings.guiScale);
   });
   public static final IteratableOption MAIN_HAND = new IteratableOption("options.mainHand", (settings, optionValues) -> {
      settings.mainHand = settings.mainHand.opposite();
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(settings.mainHand.getHandName());
   });
   public static final IteratableOption NARRATOR = new IteratableOption("options.narrator", (settings, optionValues) -> {
      if (NarratorChatListener.INSTANCE.isActive()) {
         settings.narrator = NarratorStatus.byId(settings.narrator.getId() + optionValues);
      } else {
         settings.narrator = NarratorStatus.OFF;
      }

      NarratorChatListener.INSTANCE.announceMode(settings.narrator);
   }, (settings, optionValues) -> {
      return NarratorChatListener.INSTANCE.isActive() ? optionValues.getGenericValueComponent(settings.narrator.func_238233_b_()) : optionValues.getGenericValueComponent(new TranslationTextComponent("options.narrator.notavailable"));
   });
   public static final IteratableOption PARTICLES = new IteratableOption("options.particles", (settings, optionValues) -> {
      settings.particles = ParticleStatus.byId(settings.particles.getId() + optionValues);
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.particles.getResourceKey()));
   });
   public static final IteratableOption RENDER_CLOUDS = new IteratableOption("options.renderClouds", (settings, optionValues) -> {
      settings.cloudOption = CloudOption.byId(settings.cloudOption.getId() + optionValues);
      if (Minecraft.isFabulousGraphicsEnabled()) {
         Framebuffer framebuffer = Minecraft.getInstance().worldRenderer.func_239232_u_();
         if (framebuffer != null) {
            framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
         }
      }

   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.cloudOption.getKey()));
   });
   public static final IteratableOption ACCESSIBILITY_TEXT_BACKGROUND = new IteratableOption("options.accessibility.text_background", (settings, optionValues) -> {
      settings.accessibilityTextBackground = !settings.accessibilityTextBackground;
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.accessibilityTextBackground ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere"));
   });
   private static final ITextComponent field_244787_ad = new TranslationTextComponent("options.hideMatchedNames.tooltip");
   public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", (settings) -> {
      return settings.autoJump;
   }, (settings, optionValues) -> {
      settings.autoJump = optionValues;
   });
   public static final BooleanOption AUTO_SUGGEST_COMMANDS = new BooleanOption("options.autoSuggestCommands", (settings) -> {
      return settings.autoSuggestCommands;
   }, (settings, optionValues) -> {
      settings.autoSuggestCommands = optionValues;
   });
   public static final BooleanOption field_244786_G = new BooleanOption("options.hideMatchedNames", field_244787_ad, (p_244790_0_) -> {
      return p_244790_0_.field_244794_ae;
   }, (p_244791_0_, p_244791_1_) -> {
      p_244791_0_.field_244794_ae = p_244791_1_;
   });
   public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", (settings) -> {
      return settings.chatColor;
   }, (settings, optionValues) -> {
      settings.chatColor = optionValues;
   });
   public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", (settings) -> {
      return settings.chatLinks;
   }, (settings, optionValues) -> {
      settings.chatLinks = optionValues;
   });
   public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", (settings) -> {
      return settings.chatLinksPrompt;
   }, (settings, optionValues) -> {
      settings.chatLinksPrompt = optionValues;
   });
   public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", (settings) -> {
      return settings.discreteMouseScroll;
   }, (settings, optionValues) -> {
      settings.discreteMouseScroll = optionValues;
   });
   public static final BooleanOption VSYNC = new BooleanOption("options.vsync", (settings) -> {
      return settings.vsync;
   }, (settings, optionValues) -> {
      settings.vsync = optionValues;
      if (Minecraft.getInstance().getMainWindow() != null) {
         Minecraft.getInstance().getMainWindow().setVsync(settings.vsync);
      }

   });
   public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", (settings) -> {
      return settings.entityShadows;
   }, (settings, optionValues) -> {
      settings.entityShadows = optionValues;
   });
   public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", (settings) -> {
      return settings.forceUnicodeFont;
   }, (settings, optionValues) -> {
      settings.forceUnicodeFont = optionValues;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.getMainWindow() != null) {
         minecraft.forceUnicodeFont(optionValues);
      }

   });
   public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", (settings) -> {
      return settings.invertMouse;
   }, (settings, optionValues) -> {
      settings.invertMouse = optionValues;
   });
   public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", (settings) -> {
      return settings.realmsNotifications;
   }, (settings, optionValues) -> {
      settings.realmsNotifications = optionValues;
   });
   public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", (settings) -> {
      return settings.reducedDebugInfo;
   }, (settings, optionValues) -> {
      settings.reducedDebugInfo = optionValues;
   });
   public static final BooleanOption SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", (settings) -> {
      return settings.showSubtitles;
   }, (settings, optionValues) -> {
      settings.showSubtitles = optionValues;
   });
   public static final BooleanOption SNOOPER = new BooleanOption("options.snooper", (settings) -> {
      if (settings.snooper) {
      }

      return false;
   }, (settings, optionValues) -> {
      settings.snooper = optionValues;
   });
   public static final IteratableOption SNEAK = new IteratableOption("key.sneak", (settings, optionValues) -> {
      settings.toggleCrouch = !settings.toggleCrouch;
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.toggleCrouch ? "options.key.toggle" : "options.key.hold"));
   });
   public static final IteratableOption SPRINT = new IteratableOption("key.sprint", (settings, optionValues) -> {
      settings.toggleSprint = !settings.toggleSprint;
   }, (settings, optionValues) -> {
      return optionValues.getGenericValueComponent(new TranslationTextComponent(settings.toggleSprint ? "options.key.toggle" : "options.key.hold"));
   });
   public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", (settings) -> {
      return settings.touchscreen;
   }, (settings, optionValues) -> {
      settings.touchscreen = optionValues;
   });
   public static final BooleanOption FULLSCREEN = new BooleanOption("options.fullscreen", (settings) -> {
      return settings.fullscreen;
   }, (settings, optionValues) -> {
      settings.fullscreen = optionValues;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.getMainWindow() != null && minecraft.getMainWindow().isFullscreen() != settings.fullscreen) {
         minecraft.getMainWindow().toggleFullscreen();
         settings.fullscreen = minecraft.getMainWindow().isFullscreen();
      }

   });
   public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", (settings) -> {
      return settings.viewBobbing;
   }, (settings, optionValues) -> {
      settings.viewBobbing = optionValues;
   });
   private final ITextComponent translatedBaseMessage;
   private Optional<List<IReorderingProcessor>> optionValues = Optional.empty();

   public AbstractOption(String translationKeyIn) {
      this.translatedBaseMessage = new TranslationTextComponent(translationKeyIn);
   }

   public abstract Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn);

   protected ITextComponent getBaseMessageTranslation() {
      return this.translatedBaseMessage;
   }

   public void setOptionValues(List<IReorderingProcessor> values) {
      this.optionValues = Optional.of(values);
   }

   public Optional<List<IReorderingProcessor>> getOptionValues() {
      return this.optionValues;
   }

   protected ITextComponent getPixelValueComponent(int value) {
      return new TranslationTextComponent("options.pixel_value", this.getBaseMessageTranslation(), value);
   }

   protected ITextComponent getPercentValueComponent(double percentage) {
      return new TranslationTextComponent("options.percent_value", this.getBaseMessageTranslation(), (int)(percentage * 100.0D));
   }

   protected ITextComponent getPercentageAddMessage(int doubleIn) {
      return new TranslationTextComponent("options.percent_add_value", this.getBaseMessageTranslation(), doubleIn);
   }

   protected ITextComponent getGenericValueComponent(ITextComponent valueMessage) {
      return new TranslationTextComponent("options.generic_value", this.getBaseMessageTranslation(), valueMessage);
   }

   protected ITextComponent getMessageWithValue(int value) {
      return this.getGenericValueComponent(new StringTextComponent(Integer.toString(value)));
   }
}
