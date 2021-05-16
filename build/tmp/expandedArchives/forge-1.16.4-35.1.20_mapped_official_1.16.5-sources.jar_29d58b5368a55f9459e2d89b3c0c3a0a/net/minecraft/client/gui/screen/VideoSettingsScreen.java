package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.FullscreenResolutionOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.renderer.GPUWarning;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VideoSettingsScreen extends SettingsScreen {
   private static final ITextComponent FABULOUS = (new TranslationTextComponent("options.graphics.fabulous")).withStyle(TextFormatting.ITALIC);
   private static final ITextComponent WARNING_MESSAGE = new TranslationTextComponent("options.graphics.warning.message", FABULOUS, FABULOUS);
   private static final ITextComponent WARNING_TITLE = (new TranslationTextComponent("options.graphics.warning.title")).withStyle(TextFormatting.RED);
   private static final ITextComponent BUTTON_ACCEPT = new TranslationTextComponent("options.graphics.warning.accept");
   private static final ITextComponent BUTTON_CANCEL = new TranslationTextComponent("options.graphics.warning.cancel");
   private static final ITextComponent NEW_LINE = new StringTextComponent("\n");
   private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.GRAPHICS, AbstractOption.RENDER_DISTANCE, AbstractOption.AMBIENT_OCCLUSION, AbstractOption.FRAMERATE_LIMIT, AbstractOption.ENABLE_VSYNC, AbstractOption.VIEW_BOBBING, AbstractOption.GUI_SCALE, AbstractOption.ATTACK_INDICATOR, AbstractOption.GAMMA, AbstractOption.RENDER_CLOUDS, AbstractOption.USE_FULLSCREEN, AbstractOption.PARTICLES, AbstractOption.MIPMAP_LEVELS, AbstractOption.ENTITY_SHADOWS, AbstractOption.SCREEN_EFFECTS_SCALE, AbstractOption.ENTITY_DISTANCE_SCALING, AbstractOption.FOV_EFFECTS_SCALE};
   private OptionsRowList list;
   private final GPUWarning gpuWarnlistManager;
   private final int oldMipmaps;

   public VideoSettingsScreen(Screen p_i1062_1_, GameSettings p_i1062_2_) {
      super(p_i1062_1_, p_i1062_2_, new TranslationTextComponent("options.videoTitle"));
      this.gpuWarnlistManager = p_i1062_1_.minecraft.getGpuWarnlistManager();
      this.gpuWarnlistManager.resetWarnings();
      if (p_i1062_2_.graphicsMode == GraphicsFanciness.FABULOUS) {
         this.gpuWarnlistManager.dismissWarning();
      }

      this.oldMipmaps = p_i1062_2_.mipmapLevels;
   }

   protected void init() {
      this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addBig(new FullscreenResolutionOption(this.minecraft.getWindow()));
      this.list.addBig(AbstractOption.BIOME_BLEND_RADIUS);
      this.list.addSmall(OPTIONS);
      this.children.add(this.list);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (p_213106_1_) -> {
         this.minecraft.options.save();
         this.minecraft.getWindow().changeFullscreenVideoMode();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      if (this.options.mipmapLevels != this.oldMipmaps) {
         this.minecraft.updateMaxMipLevel(this.options.mipmapLevels);
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      int i = this.options.guiScale;
      if (super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)) {
         if (this.options.guiScale != i) {
            this.minecraft.resizeDisplay();
         }

         if (this.gpuWarnlistManager.isShowingWarning()) {
            List<ITextProperties> list = Lists.newArrayList(WARNING_MESSAGE, NEW_LINE);
            String s = this.gpuWarnlistManager.getRendererWarnings();
            if (s != null) {
               list.add(NEW_LINE);
               list.add((new TranslationTextComponent("options.graphics.warning.renderer", s)).withStyle(TextFormatting.GRAY));
            }

            String s1 = this.gpuWarnlistManager.getVendorWarnings();
            if (s1 != null) {
               list.add(NEW_LINE);
               list.add((new TranslationTextComponent("options.graphics.warning.vendor", s1)).withStyle(TextFormatting.GRAY));
            }

            String s2 = this.gpuWarnlistManager.getVersionWarnings();
            if (s2 != null) {
               list.add(NEW_LINE);
               list.add((new TranslationTextComponent("options.graphics.warning.version", s2)).withStyle(TextFormatting.GRAY));
            }

            this.minecraft.setScreen(new GPUWarningScreen(WARNING_TITLE, list, ImmutableList.of(new GPUWarningScreen.Option(BUTTON_ACCEPT, (p_241606_1_) -> {
               this.options.graphicsMode = GraphicsFanciness.FABULOUS;
               Minecraft.getInstance().levelRenderer.allChanged();
               this.gpuWarnlistManager.dismissWarning();
               this.minecraft.setScreen(this);
            }), new GPUWarningScreen.Option(BUTTON_CANCEL, (p_241605_1_) -> {
               this.gpuWarnlistManager.dismissWarningAndSkipFabulous();
               this.minecraft.setScreen(this);
            }))));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      int i = this.options.guiScale;
      if (super.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_)) {
         return true;
      } else if (this.list.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_)) {
         if (this.options.guiScale != i) {
            this.minecraft.resizeDisplay();
         }

         return true;
      } else {
         return false;
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 5, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      List<IReorderingProcessor> list = tooltipAt(this.list, p_230430_2_, p_230430_3_);
      if (list != null) {
         this.renderTooltip(p_230430_1_, list, p_230430_2_, p_230430_3_);
      }

   }
}
