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
   public static final SliderPercentageOption BIOME_BLEND_RADIUS = new SliderPercentageOption("options.biomeBlendRadius", 0.0D, 7.0D, 1.0F, (p_216607_0_) -> {
      return (double)p_216607_0_.biomeBlendRadius;
   }, (p_216660_0_, p_216660_1_) -> {
      p_216660_0_.biomeBlendRadius = MathHelper.clamp((int)p_216660_1_.doubleValue(), 0, 7);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (p_216595_0_, p_216595_1_) -> {
      double d0 = p_216595_1_.get(p_216595_0_);
      int i = (int)d0 * 2 + 1;
      return p_216595_1_.genericValueLabel(new TranslationTextComponent("options.biomeBlendRadius." + i));
   });
   public static final SliderPercentageOption CHAT_HEIGHT_FOCUSED = new SliderPercentageOption("options.chat.height.focused", 0.0D, 1.0D, 0.0F, (p_216587_0_) -> {
      return p_216587_0_.chatHeightFocused;
   }, (p_216600_0_, p_216600_1_) -> {
      p_216600_0_.chatHeightFocused = p_216600_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216642_0_, p_216642_1_) -> {
      double d0 = p_216642_1_.toPct(p_216642_1_.get(p_216642_0_));
      return p_216642_1_.pixelValueLabel(NewChatGui.getHeight(d0));
   });
   public static final SliderPercentageOption CHAT_HEIGHT_UNFOCUSED = new SliderPercentageOption("options.chat.height.unfocused", 0.0D, 1.0D, 0.0F, (p_216611_0_) -> {
      return p_216611_0_.chatHeightUnfocused;
   }, (p_216650_0_, p_216650_1_) -> {
      p_216650_0_.chatHeightUnfocused = p_216650_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216604_0_, p_216604_1_) -> {
      double d0 = p_216604_1_.toPct(p_216604_1_.get(p_216604_0_));
      return p_216604_1_.pixelValueLabel(NewChatGui.getHeight(d0));
   });
   public static final SliderPercentageOption CHAT_OPACITY = new SliderPercentageOption("options.chat.opacity", 0.0D, 1.0D, 0.0F, (p_216649_0_) -> {
      return p_216649_0_.chatOpacity;
   }, (p_216578_0_, p_216578_1_) -> {
      p_216578_0_.chatOpacity = p_216578_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216592_0_, p_216592_1_) -> {
      double d0 = p_216592_1_.toPct(p_216592_1_.get(p_216592_0_));
      return p_216592_1_.percentValueLabel(d0 * 0.9D + 0.1D);
   });
   public static final SliderPercentageOption CHAT_SCALE = new SliderPercentageOption("options.chat.scale", 0.0D, 1.0D, 0.0F, (p_216591_0_) -> {
      return p_216591_0_.chatScale;
   }, (p_216624_0_, p_216624_1_) -> {
      p_216624_0_.chatScale = p_216624_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216637_0_, p_216637_1_) -> {
      double d0 = p_216637_1_.toPct(p_216637_1_.get(p_216637_0_));
      return (ITextComponent)(d0 == 0.0D ? DialogTexts.optionStatus(p_216637_1_.getCaption(), false) : p_216637_1_.percentValueLabel(d0));
   });
   public static final SliderPercentageOption CHAT_WIDTH = new SliderPercentageOption("options.chat.width", 0.0D, 1.0D, 0.0F, (p_216601_0_) -> {
      return p_216601_0_.chatWidth;
   }, (p_216620_0_, p_216620_1_) -> {
      p_216620_0_.chatWidth = p_216620_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216673_0_, p_216673_1_) -> {
      double d0 = p_216673_1_.toPct(p_216673_1_.get(p_216673_0_));
      return p_216673_1_.pixelValueLabel(NewChatGui.getWidth(d0));
   });
   public static final SliderPercentageOption CHAT_LINE_SPACING = new SliderPercentageOption("options.chat.line_spacing", 0.0D, 1.0D, 0.0F, (p_238287_0_) -> {
      return p_238287_0_.chatLineSpacing;
   }, (p_238282_0_, p_238282_1_) -> {
      p_238282_0_.chatLineSpacing = p_238282_1_;
   }, (p_238297_0_, p_238297_1_) -> {
      return p_238297_1_.percentValueLabel(p_238297_1_.toPct(p_238297_1_.get(p_238297_0_)));
   });
   public static final SliderPercentageOption CHAT_DELAY = new SliderPercentageOption("options.chat.delay_instant", 0.0D, 6.0D, 0.1F, (p_238271_0_) -> {
      return p_238271_0_.chatDelay;
   }, (p_238273_0_, p_238273_1_) -> {
      p_238273_0_.chatDelay = p_238273_1_;
   }, (p_238292_0_, p_238292_1_) -> {
      double d0 = p_238292_1_.get(p_238292_0_);
      return d0 <= 0.0D ? new TranslationTextComponent("options.chat.delay_none") : new TranslationTextComponent("options.chat.delay", String.format("%.1f", d0));
   });
   public static final SliderPercentageOption FOV = new SliderPercentageOption("options.fov", 30.0D, 110.0D, 1.0F, (p_216655_0_) -> {
      return p_216655_0_.fov;
   }, (p_216612_0_, p_216612_1_) -> {
      p_216612_0_.fov = p_216612_1_;
   }, (p_216590_0_, p_216590_1_) -> {
      double d0 = p_216590_1_.get(p_216590_0_);
      if (d0 == 70.0D) {
         return p_216590_1_.genericValueLabel(new TranslationTextComponent("options.fov.min"));
      } else {
         return d0 == p_216590_1_.getMaxValue() ? p_216590_1_.genericValueLabel(new TranslationTextComponent("options.fov.max")) : p_216590_1_.genericValueLabel((int)d0);
      }
   });
   private static final ITextComponent ACCESSIBILITY_TOOLTIP_FOV_EFFECT = new TranslationTextComponent("options.fovEffectScale.tooltip");
   public static final SliderPercentageOption FOV_EFFECTS_SCALE = new SliderPercentageOption("options.fovEffectScale", 0.0D, 1.0D, 0.0F, (p_216672_0_) -> {
      return Math.pow((double)p_216672_0_.fovEffectScale, 2.0D);
   }, (p_216608_0_, p_216608_1_) -> {
      p_216608_0_.fovEffectScale = MathHelper.sqrt(p_216608_1_);
   }, (p_216645_0_, p_216645_1_) -> {
      p_216645_1_.setTooltip(Minecraft.getInstance().font.split(ACCESSIBILITY_TOOLTIP_FOV_EFFECT, 200));
      double d0 = p_216645_1_.toPct(p_216645_1_.get(p_216645_0_));
      return d0 == 0.0D ? p_216645_1_.genericValueLabel(new TranslationTextComponent("options.fovEffectScale.off")) : p_216645_1_.percentValueLabel(d0);
   });
   private static final ITextComponent ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = new TranslationTextComponent("options.screenEffectScale.tooltip");
   public static final SliderPercentageOption SCREEN_EFFECTS_SCALE = new SliderPercentageOption("options.screenEffectScale", 0.0D, 1.0D, 0.0F, (p_244419_0_) -> {
      return (double)p_244419_0_.screenEffectScale;
   }, (p_244415_0_, p_244415_1_) -> {
      p_244415_0_.screenEffectScale = p_244415_1_.floatValue();
   }, (p_244416_0_, p_244416_1_) -> {
      p_244416_1_.setTooltip(Minecraft.getInstance().font.split(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT, 200));
      double d0 = p_244416_1_.toPct(p_244416_1_.get(p_244416_0_));
      return d0 == 0.0D ? p_244416_1_.genericValueLabel(new TranslationTextComponent("options.screenEffectScale.off")) : p_244416_1_.percentValueLabel(d0);
   });
   public static final SliderPercentageOption FRAMERATE_LIMIT = new SliderPercentageOption("options.framerateLimit", 10.0D, 260.0D, 10.0F, (p_244406_0_) -> {
      return (double)p_244406_0_.framerateLimit;
   }, (p_244411_0_, p_244411_1_) -> {
      p_244411_0_.framerateLimit = (int)p_244411_1_.doubleValue();
      Minecraft.getInstance().getWindow().setFramerateLimit(p_244411_0_.framerateLimit);
   }, (p_244413_0_, p_244413_1_) -> {
      double d0 = p_244413_1_.get(p_244413_0_);
      return d0 == p_244413_1_.getMaxValue() ? p_244413_1_.genericValueLabel(new TranslationTextComponent("options.framerateLimit.max")) : p_244413_1_.genericValueLabel(new TranslationTextComponent("options.framerate", (int)d0));
   });
   public static final SliderPercentageOption GAMMA = new SliderPercentageOption("options.gamma", 0.0D, 1.0D, 0.0F, (p_216636_0_) -> {
      return p_216636_0_.gamma;
   }, (p_216651_0_, p_216651_1_) -> {
      p_216651_0_.gamma = p_216651_1_;
   }, (p_216594_0_, p_216594_1_) -> {
      double d0 = p_216594_1_.toPct(p_216594_1_.get(p_216594_0_));
      if (d0 == 0.0D) {
         return p_216594_1_.genericValueLabel(new TranslationTextComponent("options.gamma.min"));
      } else {
         return d0 == 1.0D ? p_216594_1_.genericValueLabel(new TranslationTextComponent("options.gamma.max")) : p_216594_1_.percentAddValueLabel((int)(d0 * 100.0D));
      }
   });
   public static final SliderPercentageOption MIPMAP_LEVELS = new SliderPercentageOption("options.mipmapLevels", 0.0D, 4.0D, 1.0F, (p_216667_0_) -> {
      return (double)p_216667_0_.mipmapLevels;
   }, (p_216585_0_, p_216585_1_) -> {
      p_216585_0_.mipmapLevels = (int)p_216585_1_.doubleValue();
   }, (p_216629_0_, p_216629_1_) -> {
      double d0 = p_216629_1_.get(p_216629_0_);
      return (ITextComponent)(d0 == 0.0D ? DialogTexts.optionStatus(p_216629_1_.getCaption(), false) : p_216629_1_.genericValueLabel((int)d0));
   });
   public static final SliderPercentageOption MOUSE_WHEEL_SENSITIVITY = new SliderMultiplierOption("options.mouseWheelSensitivity", 0.01D, 10.0D, 0.01F, (p_216581_0_) -> {
      return p_216581_0_.mouseWheelSensitivity;
   }, (p_216628_0_, p_216628_1_) -> {
      p_216628_0_.mouseWheelSensitivity = p_216628_1_;
   }, (p_216675_0_, p_216675_1_) -> {
      double d0 = p_216675_1_.toPct(p_216675_1_.get(p_216675_0_));
      return p_216675_1_.genericValueLabel(new StringTextComponent(String.format("%.2f", p_216675_1_.toValue(d0))));
   });
   public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", (p_225287_0_) -> {
      return p_225287_0_.rawMouseInput;
   }, (p_225259_0_, p_225259_1_) -> {
      p_225259_0_.rawMouseInput = p_225259_1_;
      MainWindow mainwindow = Minecraft.getInstance().getWindow();
      if (mainwindow != null) {
         mainwindow.updateRawMouseInput(p_225259_1_);
      }

   });
   public static final SliderPercentageOption RENDER_DISTANCE = new SliderPercentageOption("options.renderDistance", 2.0D, 16.0D, 1.0F, (p_216658_0_) -> {
      return (double)p_216658_0_.renderDistance;
   }, (p_216579_0_, p_216579_1_) -> {
      p_216579_0_.renderDistance = (int)p_216579_1_.doubleValue();
      Minecraft.getInstance().levelRenderer.needsUpdate();
   }, (p_216664_0_, p_216664_1_) -> {
      double d0 = p_216664_1_.get(p_216664_0_);
      return p_216664_1_.genericValueLabel(new TranslationTextComponent("options.chunks", (int)d0));
   });
   public static final SliderPercentageOption ENTITY_DISTANCE_SCALING = new SliderPercentageOption("options.entityDistanceScaling", 0.5D, 5.0D, 0.25F, (p_238324_0_) -> {
      return (double)p_238324_0_.entityDistanceScaling;
   }, (p_238256_0_, p_238256_1_) -> {
      p_238256_0_.entityDistanceScaling = (float)p_238256_1_.doubleValue();
   }, (p_238254_0_, p_238254_1_) -> {
      double d0 = p_238254_1_.get(p_238254_0_);
      return p_238254_1_.percentValueLabel(d0);
   });
   public static final SliderPercentageOption SENSITIVITY = new SliderPercentageOption("options.sensitivity", 0.0D, 1.0D, 0.0F, (p_216654_0_) -> {
      return p_216654_0_.sensitivity;
   }, (p_216644_0_, p_216644_1_) -> {
      p_216644_0_.sensitivity = p_216644_1_;
   }, (p_216641_0_, p_216641_1_) -> {
      double d0 = p_216641_1_.toPct(p_216641_1_.get(p_216641_0_));
      if (d0 == 0.0D) {
         return p_216641_1_.genericValueLabel(new TranslationTextComponent("options.sensitivity.min"));
      } else {
         return d0 == 1.0D ? p_216641_1_.genericValueLabel(new TranslationTextComponent("options.sensitivity.max")) : p_216641_1_.percentValueLabel(2.0D * d0);
      }
   });
   public static final SliderPercentageOption TEXT_BACKGROUND_OPACITY = new SliderPercentageOption("options.accessibility.text_background_opacity", 0.0D, 1.0D, 0.0F, (p_216597_0_) -> {
      return p_216597_0_.textBackgroundOpacity;
   }, (p_216593_0_, p_216593_1_) -> {
      p_216593_0_.textBackgroundOpacity = p_216593_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216626_0_, p_216626_1_) -> {
      return p_216626_1_.percentValueLabel(p_216626_1_.toPct(p_216626_1_.get(p_216626_0_)));
   });
   public static final IteratableOption AMBIENT_OCCLUSION = new IteratableOption("options.ao", (p_216653_0_, p_216653_1_) -> {
      p_216653_0_.ambientOcclusion = AmbientOcclusionStatus.byId(p_216653_0_.ambientOcclusion.getId() + p_216653_1_);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (p_216630_0_, p_216630_1_) -> {
      return p_216630_1_.genericValueLabel(new TranslationTextComponent(p_216630_0_.ambientOcclusion.getKey()));
   });
   public static final IteratableOption ATTACK_INDICATOR = new IteratableOption("options.attackIndicator", (p_216615_0_, p_216615_1_) -> {
      p_216615_0_.attackIndicator = AttackIndicatorStatus.byId(p_216615_0_.attackIndicator.getId() + p_216615_1_);
   }, (p_216609_0_, p_216609_1_) -> {
      return p_216609_1_.genericValueLabel(new TranslationTextComponent(p_216609_0_.attackIndicator.getKey()));
   });
   public static final IteratableOption CHAT_VISIBILITY = new IteratableOption("options.chat.visibility", (p_216640_0_, p_216640_1_) -> {
      p_216640_0_.chatVisibility = ChatVisibility.byId((p_216640_0_.chatVisibility.getId() + p_216640_1_) % 3);
   }, (p_216598_0_, p_216598_1_) -> {
      return p_216598_1_.genericValueLabel(new TranslationTextComponent(p_216598_0_.chatVisibility.getKey()));
   });
   private static final ITextComponent GRAPHICS_TOOLTIP_FAST = new TranslationTextComponent("options.graphics.fast.tooltip");
   private static final ITextComponent GRAPHICS_TOOLTIP_FABULOUS = new TranslationTextComponent("options.graphics.fabulous.tooltip", (new TranslationTextComponent("options.graphics.fabulous")).withStyle(TextFormatting.ITALIC));
   private static final ITextComponent GRAPHICS_TOOLTIP_FANCY = new TranslationTextComponent("options.graphics.fancy.tooltip");
   public static final IteratableOption GRAPHICS = new IteratableOption("options.graphics", (p_216577_0_, p_216577_1_) -> {
      Minecraft minecraft = Minecraft.getInstance();
      GPUWarning gpuwarning = minecraft.getGpuWarnlistManager();
      if (p_216577_0_.graphicsMode == GraphicsFanciness.FANCY && gpuwarning.willShowWarning()) {
         gpuwarning.showWarning();
      } else {
         p_216577_0_.graphicsMode = p_216577_0_.graphicsMode.cycleNext();
         if (p_216577_0_.graphicsMode == GraphicsFanciness.FABULOUS && (!GlStateManager.supportsFramebufferBlit() || gpuwarning.isSkippingFabulous())) {
            p_216577_0_.graphicsMode = GraphicsFanciness.FAST;
         }

         minecraft.levelRenderer.allChanged();
      }
   }, (p_216633_0_, p_216633_1_) -> {
      switch(p_216633_0_.graphicsMode) {
      case FAST:
         p_216633_1_.setTooltip(Minecraft.getInstance().font.split(GRAPHICS_TOOLTIP_FAST, 200));
         break;
      case FANCY:
         p_216633_1_.setTooltip(Minecraft.getInstance().font.split(GRAPHICS_TOOLTIP_FANCY, 200));
         break;
      case FABULOUS:
         p_216633_1_.setTooltip(Minecraft.getInstance().font.split(GRAPHICS_TOOLTIP_FABULOUS, 200));
      }

      IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(p_216633_0_.graphicsMode.getKey());
      return p_216633_0_.graphicsMode == GraphicsFanciness.FABULOUS ? p_216633_1_.genericValueLabel(iformattabletextcomponent.withStyle(TextFormatting.ITALIC)) : p_216633_1_.genericValueLabel(iformattabletextcomponent);
   });
   public static final IteratableOption GUI_SCALE = new IteratableOption("options.guiScale", (p_216674_0_, p_216674_1_) -> {
      p_216674_0_.guiScale = Integer.remainderUnsigned(p_216674_0_.guiScale + p_216674_1_, Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode()) + 1);
   }, (p_216668_0_, p_216668_1_) -> {
      return p_216668_0_.guiScale == 0 ? p_216668_1_.genericValueLabel(new TranslationTextComponent("options.guiScale.auto")) : p_216668_1_.genericValueLabel(p_216668_0_.guiScale);
   });
   public static final IteratableOption MAIN_HAND = new IteratableOption("options.mainHand", (p_216584_0_, p_216584_1_) -> {
      p_216584_0_.mainHand = p_216584_0_.mainHand.getOpposite();
   }, (p_216596_0_, p_216596_1_) -> {
      return p_216596_1_.genericValueLabel(p_216596_0_.mainHand.getName());
   });
   public static final IteratableOption NARRATOR = new IteratableOption("options.narrator", (p_216648_0_, p_216648_1_) -> {
      if (NarratorChatListener.INSTANCE.isActive()) {
         p_216648_0_.narratorStatus = NarratorStatus.byId(p_216648_0_.narratorStatus.getId() + p_216648_1_);
      } else {
         p_216648_0_.narratorStatus = NarratorStatus.OFF;
      }

      NarratorChatListener.INSTANCE.updateNarratorStatus(p_216648_0_.narratorStatus);
   }, (p_216632_0_, p_216632_1_) -> {
      return NarratorChatListener.INSTANCE.isActive() ? p_216632_1_.genericValueLabel(p_216632_0_.narratorStatus.getName()) : p_216632_1_.genericValueLabel(new TranslationTextComponent("options.narrator.notavailable"));
   });
   public static final IteratableOption PARTICLES = new IteratableOption("options.particles", (p_216622_0_, p_216622_1_) -> {
      p_216622_0_.particles = ParticleStatus.byId(p_216622_0_.particles.getId() + p_216622_1_);
   }, (p_216616_0_, p_216616_1_) -> {
      return p_216616_1_.genericValueLabel(new TranslationTextComponent(p_216616_0_.particles.getKey()));
   });
   public static final IteratableOption RENDER_CLOUDS = new IteratableOption("options.renderClouds", (p_216605_0_, p_216605_1_) -> {
      p_216605_0_.renderClouds = CloudOption.byId(p_216605_0_.renderClouds.getId() + p_216605_1_);
      if (Minecraft.useShaderTransparency()) {
         Framebuffer framebuffer = Minecraft.getInstance().levelRenderer.getCloudsTarget();
         if (framebuffer != null) {
            framebuffer.clear(Minecraft.ON_OSX);
         }
      }

   }, (p_216602_0_, p_216602_1_) -> {
      return p_216602_1_.genericValueLabel(new TranslationTextComponent(p_216602_0_.renderClouds.getKey()));
   });
   public static final IteratableOption TEXT_BACKGROUND = new IteratableOption("options.accessibility.text_background", (p_216665_0_, p_216665_1_) -> {
      p_216665_0_.backgroundForChatOnly = !p_216665_0_.backgroundForChatOnly;
   }, (p_216639_0_, p_216639_1_) -> {
      return p_216639_1_.genericValueLabel(new TranslationTextComponent(p_216639_0_.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere"));
   });
   private static final ITextComponent CHAT_TOOLTIP_HIDE_MATCHED_NAMES = new TranslationTextComponent("options.hideMatchedNames.tooltip");
   public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", (p_216619_0_) -> {
      return p_216619_0_.autoJump;
   }, (p_216621_0_, p_216621_1_) -> {
      p_216621_0_.autoJump = p_216621_1_;
   });
   public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", (p_216643_0_) -> {
      return p_216643_0_.autoSuggestions;
   }, (p_216656_0_, p_216656_1_) -> {
      p_216656_0_.autoSuggestions = p_216656_1_;
   });
   public static final BooleanOption HIDE_MATCHED_NAMES = new BooleanOption("options.hideMatchedNames", CHAT_TOOLTIP_HIDE_MATCHED_NAMES, (p_244790_0_) -> {
      return p_244790_0_.hideMatchedNames;
   }, (p_244791_0_, p_244791_1_) -> {
      p_244791_0_.hideMatchedNames = p_244791_1_;
   });
   public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", (p_216669_0_) -> {
      return p_216669_0_.chatColors;
   }, (p_216659_0_, p_216659_1_) -> {
      p_216659_0_.chatColors = p_216659_1_;
   });
   public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", (p_216583_0_) -> {
      return p_216583_0_.chatLinks;
   }, (p_216670_0_, p_216670_1_) -> {
      p_216670_0_.chatLinks = p_216670_1_;
   });
   public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", (p_216610_0_) -> {
      return p_216610_0_.chatLinksPrompt;
   }, (p_216652_0_, p_216652_1_) -> {
      p_216652_0_.chatLinksPrompt = p_216652_1_;
   });
   public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", (p_216634_0_) -> {
      return p_216634_0_.discreteMouseScroll;
   }, (p_216625_0_, p_216625_1_) -> {
      p_216625_0_.discreteMouseScroll = p_216625_1_;
   });
   public static final BooleanOption ENABLE_VSYNC = new BooleanOption("options.vsync", (p_216661_0_) -> {
      return p_216661_0_.enableVsync;
   }, (p_216635_0_, p_216635_1_) -> {
      p_216635_0_.enableVsync = p_216635_1_;
      if (Minecraft.getInstance().getWindow() != null) {
         Minecraft.getInstance().getWindow().updateVsync(p_216635_0_.enableVsync);
      }

   });
   public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", (p_216576_0_) -> {
      return p_216576_0_.entityShadows;
   }, (p_216588_0_, p_216588_1_) -> {
      p_216588_0_.entityShadows = p_216588_1_;
   });
   public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", (p_216657_0_) -> {
      return p_216657_0_.forceUnicodeFont;
   }, (p_216631_0_, p_216631_1_) -> {
      p_216631_0_.forceUnicodeFont = p_216631_1_;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.getWindow() != null) {
         minecraft.selectMainFont(p_216631_1_);
      }

   });
   public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", (p_216627_0_) -> {
      return p_216627_0_.invertYMouse;
   }, (p_216603_0_, p_216603_1_) -> {
      p_216603_0_.invertYMouse = p_216603_1_;
   });
   public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", (p_216606_0_) -> {
      return p_216606_0_.realmsNotifications;
   }, (p_216618_0_, p_216618_1_) -> {
      p_216618_0_.realmsNotifications = p_216618_1_;
   });
   public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", (p_216582_0_) -> {
      return p_216582_0_.reducedDebugInfo;
   }, (p_216613_0_, p_216613_1_) -> {
      p_216613_0_.reducedDebugInfo = p_216613_1_;
   });
   public static final BooleanOption SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", (p_216663_0_) -> {
      return p_216663_0_.showSubtitles;
   }, (p_216662_0_, p_216662_1_) -> {
      p_216662_0_.showSubtitles = p_216662_1_;
   });
   public static final BooleanOption SNOOPER_ENABLED = new BooleanOption("options.snooper", (p_216638_0_) -> {
      if (p_216638_0_.snooperEnabled) {
      }

      return false;
   }, (p_216676_0_, p_216676_1_) -> {
      p_216676_0_.snooperEnabled = p_216676_1_;
   });
   public static final IteratableOption TOGGLE_CROUCH = new IteratableOption("key.sneak", (p_228043_0_, p_228043_1_) -> {
      p_228043_0_.toggleCrouch = !p_228043_0_.toggleCrouch;
   }, (p_228041_0_, p_228041_1_) -> {
      return p_228041_1_.genericValueLabel(new TranslationTextComponent(p_228041_0_.toggleCrouch ? "options.key.toggle" : "options.key.hold"));
   });
   public static final IteratableOption TOGGLE_SPRINT = new IteratableOption("key.sprint", (p_228039_0_, p_228039_1_) -> {
      p_228039_0_.toggleSprint = !p_228039_0_.toggleSprint;
   }, (p_228037_0_, p_228037_1_) -> {
      return p_228037_1_.genericValueLabel(new TranslationTextComponent(p_228037_0_.toggleSprint ? "options.key.toggle" : "options.key.hold"));
   });
   public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", (p_216647_0_) -> {
      return p_216647_0_.touchscreen;
   }, (p_216580_0_, p_216580_1_) -> {
      p_216580_0_.touchscreen = p_216580_1_;
   });
   public static final BooleanOption USE_FULLSCREEN = new BooleanOption("options.fullscreen", (p_228040_0_) -> {
      return p_228040_0_.fullscreen;
   }, (p_228042_0_, p_228042_1_) -> {
      p_228042_0_.fullscreen = p_228042_1_;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.getWindow() != null && minecraft.getWindow().isFullscreen() != p_228042_0_.fullscreen) {
         minecraft.getWindow().toggleFullScreen();
         p_228042_0_.fullscreen = minecraft.getWindow().isFullscreen();
      }

   });
   public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", (p_228036_0_) -> {
      return p_228036_0_.bobView;
   }, (p_228038_0_, p_228038_1_) -> {
      p_228038_0_.bobView = p_228038_1_;
   });
   private final ITextComponent caption;
   private Optional<List<IReorderingProcessor>> toolTip = Optional.empty();

   public AbstractOption(String p_i51158_1_) {
      this.caption = new TranslationTextComponent(p_i51158_1_);
   }

   public abstract Widget createButton(GameSettings p_216586_1_, int p_216586_2_, int p_216586_3_, int p_216586_4_);

   protected ITextComponent getCaption() {
      return this.caption;
   }

   public void setTooltip(List<IReorderingProcessor> p_241567_1_) {
      this.toolTip = Optional.of(p_241567_1_);
   }

   public Optional<List<IReorderingProcessor>> getTooltip() {
      return this.toolTip;
   }

   protected ITextComponent pixelValueLabel(int p_243221_1_) {
      return new TranslationTextComponent("options.pixel_value", this.getCaption(), p_243221_1_);
   }

   protected ITextComponent percentValueLabel(double p_243224_1_) {
      return new TranslationTextComponent("options.percent_value", this.getCaption(), (int)(p_243224_1_ * 100.0D));
   }

   protected ITextComponent percentAddValueLabel(int p_243223_1_) {
      return new TranslationTextComponent("options.percent_add_value", this.getCaption(), p_243223_1_);
   }

   protected ITextComponent genericValueLabel(ITextComponent p_243222_1_) {
      return new TranslationTextComponent("options.generic_value", this.getCaption(), p_243222_1_);
   }

   protected ITextComponent genericValueLabel(int p_243225_1_) {
      return this.genericValueLabel(new StringTextComponent(Integer.toString(p_243225_1_)));
   }
}
