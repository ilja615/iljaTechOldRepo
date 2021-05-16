package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.action.ResetWorldRealmsAction;
import net.minecraft.realms.action.SwitchMinigameRealmsAction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsResetWorldScreen extends NotifableRealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Screen lastScreen;
   private final RealmsServer serverData;
   private RealmsLabel titleLabel;
   private RealmsLabel subtitleLabel;
   private ITextComponent title = new TranslationTextComponent("mco.reset.world.title");
   private ITextComponent subtitle = new TranslationTextComponent("mco.reset.world.warning");
   private ITextComponent buttonTitle = DialogTexts.GUI_CANCEL;
   private int subtitleColor = 16711680;
   private static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/upload.png");
   private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/adventure.png");
   private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/survival_spawn.png");
   private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/new_world.png");
   private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/experience.png");
   private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("realms", "textures/gui/realms/inspiration.png");
   private WorldTemplatePaginatedList templates;
   private WorldTemplatePaginatedList adventuremaps;
   private WorldTemplatePaginatedList experiences;
   private WorldTemplatePaginatedList inspirations;
   public int slot = -1;
   private RealmsResetWorldScreen.ResetType typeToReset = RealmsResetWorldScreen.ResetType.NONE;
   private RealmsResetWorldScreen.ResetWorldInfo worldInfoToReset;
   private WorldTemplate worldTemplateToReset;
   @Nullable
   private ITextComponent resetTitle;
   private final Runnable resetWorldRunnable;
   private final Runnable callback;

   public RealmsResetWorldScreen(Screen p_i232215_1_, RealmsServer p_i232215_2_, Runnable p_i232215_3_, Runnable p_i232215_4_) {
      this.lastScreen = p_i232215_1_;
      this.serverData = p_i232215_2_;
      this.resetWorldRunnable = p_i232215_3_;
      this.callback = p_i232215_4_;
   }

   public RealmsResetWorldScreen(Screen p_i232216_1_, RealmsServer p_i232216_2_, ITextComponent p_i232216_3_, ITextComponent p_i232216_4_, int p_i232216_5_, ITextComponent p_i232216_6_, Runnable p_i232216_7_, Runnable p_i232216_8_) {
      this(p_i232216_1_, p_i232216_2_, p_i232216_7_, p_i232216_8_);
      this.title = p_i232216_3_;
      this.subtitle = p_i232216_4_;
      this.subtitleColor = p_i232216_5_;
      this.buttonTitle = p_i232216_6_;
   }

   public void setSlot(int p_224445_1_) {
      this.slot = p_224445_1_;
   }

   public void setResetTitle(ITextComponent p_224432_1_) {
      this.resetTitle = p_224432_1_;
   }

   public void init() {
      this.addButton(new Button(this.width / 2 - 40, row(14) - 10, 80, 20, this.buttonTitle, (p_237959_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      (new Thread("Realms-reset-world-fetcher") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               WorldTemplatePaginatedList worldtemplatepaginatedlist = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.NORMAL);
               WorldTemplatePaginatedList worldtemplatepaginatedlist1 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.ADVENTUREMAP);
               WorldTemplatePaginatedList worldtemplatepaginatedlist2 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.EXPERIENCE);
               WorldTemplatePaginatedList worldtemplatepaginatedlist3 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.ServerType.INSPIRATION);
               RealmsResetWorldScreen.this.minecraft.execute(() -> {
                  RealmsResetWorldScreen.this.templates = worldtemplatepaginatedlist;
                  RealmsResetWorldScreen.this.adventuremaps = worldtemplatepaginatedlist1;
                  RealmsResetWorldScreen.this.experiences = worldtemplatepaginatedlist2;
                  RealmsResetWorldScreen.this.inspirations = worldtemplatepaginatedlist3;
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)realmsserviceexception);
            }

         }
      }).start();
      this.titleLabel = this.addWidget(new RealmsLabel(this.title, this.width / 2, 7, 16777215));
      this.subtitleLabel = this.addWidget(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
      this.addButton(new RealmsResetWorldScreen.TexturedButton(this.frame(1), row(0) + 10, new TranslationTextComponent("mco.reset.world.generate"), NEW_WORLD_LOCATION, (p_237958_1_) -> {
         this.minecraft.setScreen(new RealmsResetNormalWorldScreen(this, this.title));
      }));
      this.addButton(new RealmsResetWorldScreen.TexturedButton(this.frame(2), row(0) + 10, new TranslationTextComponent("mco.reset.world.upload"), UPLOAD_LOCATION, (p_237957_1_) -> {
         Screen screen = new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.callback);
         this.minecraft.setScreen(screen);
      }));
      this.addButton(new RealmsResetWorldScreen.TexturedButton(this.frame(3), row(0) + 10, new TranslationTextComponent("mco.reset.world.template"), SURVIVAL_SPAWN_LOCATION, (p_237956_1_) -> {
         RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.NORMAL, this.templates);
         realmsselectworldtemplatescreen.setTitle(new TranslationTextComponent("mco.reset.world.template"));
         this.minecraft.setScreen(realmsselectworldtemplatescreen);
      }));
      this.addButton(new RealmsResetWorldScreen.TexturedButton(this.frame(1), row(6) + 20, new TranslationTextComponent("mco.reset.world.adventure"), ADVENTURE_MAP_LOCATION, (p_237955_1_) -> {
         RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.ADVENTUREMAP, this.adventuremaps);
         realmsselectworldtemplatescreen.setTitle(new TranslationTextComponent("mco.reset.world.adventure"));
         this.minecraft.setScreen(realmsselectworldtemplatescreen);
      }));
      this.addButton(new RealmsResetWorldScreen.TexturedButton(this.frame(2), row(6) + 20, new TranslationTextComponent("mco.reset.world.experience"), EXPERIENCE_LOCATION, (p_237954_1_) -> {
         RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.EXPERIENCE, this.experiences);
         realmsselectworldtemplatescreen.setTitle(new TranslationTextComponent("mco.reset.world.experience"));
         this.minecraft.setScreen(realmsselectworldtemplatescreen);
      }));
      this.addButton(new RealmsResetWorldScreen.TexturedButton(this.frame(3), row(6) + 20, new TranslationTextComponent("mco.reset.world.inspiration"), INSPIRATION_LOCATION, (p_237951_1_) -> {
         RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.INSPIRATION, this.inspirations);
         realmsselectworldtemplatescreen.setTitle(new TranslationTextComponent("mco.reset.world.inspiration"));
         this.minecraft.setScreen(realmsselectworldtemplatescreen);
      }));
      this.narrateLabels();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private int frame(int p_224434_1_) {
      return this.width / 2 - 130 + (p_224434_1_ - 1) * 100;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.titleLabel.render(this, p_230430_1_);
      this.subtitleLabel.render(this, p_230430_1_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private void drawFrame(MatrixStack p_237948_1_, int p_237948_2_, int p_237948_3_, ITextComponent p_237948_4_, ResourceLocation p_237948_5_, boolean p_237948_6_, boolean p_237948_7_) {
      this.minecraft.getTextureManager().bind(p_237948_5_);
      if (p_237948_6_) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      AbstractGui.blit(p_237948_1_, p_237948_2_ + 2, p_237948_3_ + 14, 0.0F, 0.0F, 56, 56, 56, 56);
      this.minecraft.getTextureManager().bind(SLOT_FRAME_LOCATION);
      if (p_237948_6_) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      AbstractGui.blit(p_237948_1_, p_237948_2_, p_237948_3_ + 12, 0.0F, 0.0F, 60, 60, 60, 60);
      int i = p_237948_6_ ? 10526880 : 16777215;
      drawCenteredString(p_237948_1_, this.font, p_237948_4_, p_237948_2_ + 30, p_237948_3_, i);
   }

   protected void callback(@Nullable WorldTemplate p_223627_1_) {
      if (p_223627_1_ != null) {
         if (this.slot == -1) {
            this.resetWorldWithTemplate(p_223627_1_);
         } else {
            switch(p_223627_1_.type) {
            case WORLD_TEMPLATE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN;
               break;
            case ADVENTUREMAP:
               this.typeToReset = RealmsResetWorldScreen.ResetType.ADVENTURE;
               break;
            case EXPERIENCE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.EXPERIENCE;
               break;
            case INSPIRATION:
               this.typeToReset = RealmsResetWorldScreen.ResetType.INSPIRATION;
            }

            this.worldTemplateToReset = p_223627_1_;
            this.switchSlot();
         }

      }
   }

   private void switchSlot() {
      this.switchSlot(() -> {
         switch(this.typeToReset) {
         case ADVENTURE:
         case SURVIVAL_SPAWN:
         case EXPERIENCE:
         case INSPIRATION:
            if (this.worldTemplateToReset != null) {
               this.resetWorldWithTemplate(this.worldTemplateToReset);
            }
            break;
         case GENERATE:
            if (this.worldInfoToReset != null) {
               this.triggerResetWorld(this.worldInfoToReset);
            }
         }

      });
   }

   public void switchSlot(Runnable p_237952_1_) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchMinigameRealmsAction(this.serverData.id, this.slot, p_237952_1_)));
   }

   public void resetWorldWithTemplate(WorldTemplate p_224435_1_) {
      this.resetWorld((String)null, p_224435_1_, -1, true);
   }

   private void triggerResetWorld(RealmsResetWorldScreen.ResetWorldInfo p_224437_1_) {
      this.resetWorld(p_224437_1_.seed, (WorldTemplate)null, p_224437_1_.levelType, p_224437_1_.generateStructures);
   }

   private void resetWorld(@Nullable String p_237953_1_, @Nullable WorldTemplate p_237953_2_, int p_237953_3_, boolean p_237953_4_) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ResetWorldRealmsAction(p_237953_1_, p_237953_2_, p_237953_3_, p_237953_4_, this.serverData.id, this.resetTitle, this.resetWorldRunnable)));
   }

   public void resetWorld(RealmsResetWorldScreen.ResetWorldInfo p_224438_1_) {
      if (this.slot == -1) {
         this.triggerResetWorld(p_224438_1_);
      } else {
         this.typeToReset = RealmsResetWorldScreen.ResetType.GENERATE;
         this.worldInfoToReset = p_224438_1_;
         this.switchSlot();
      }

   }

   @OnlyIn(Dist.CLIENT)
   static enum ResetType {
      NONE,
      GENERATE,
      UPLOAD,
      ADVENTURE,
      SURVIVAL_SPAWN,
      EXPERIENCE,
      INSPIRATION;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ResetWorldInfo {
      private final String seed;
      private final int levelType;
      private final boolean generateStructures;

      public ResetWorldInfo(String p_i51560_1_, int p_i51560_2_, boolean p_i51560_3_) {
         this.seed = p_i51560_1_;
         this.levelType = p_i51560_2_;
         this.generateStructures = p_i51560_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TexturedButton extends Button {
      private final ResourceLocation image;

      public TexturedButton(int p_i232218_2_, int p_i232218_3_, ITextComponent p_i232218_4_, ResourceLocation p_i232218_5_, Button.IPressable p_i232218_6_) {
         super(p_i232218_2_, p_i232218_3_, 60, 72, p_i232218_4_, p_i232218_6_);
         this.image = p_i232218_5_;
      }

      public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
         RealmsResetWorldScreen.this.drawFrame(p_230431_1_, this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver((double)p_230431_2_, (double)p_230431_3_));
      }
   }
}
