package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ITextComponent WORLD_TEXT = new TranslationTextComponent("selectWorld.world");
   private static final ITextComponent REQUIRES_CONVERSION_TEXT = new TranslationTextComponent("selectWorld.conversion");
   private static final ITextComponent HARDCORE_TEXT = (new TranslationTextComponent("mco.upload.hardcore")).withStyle(TextFormatting.DARK_RED);
   private static final ITextComponent CHEATS_TEXT = new TranslationTextComponent("selectWorld.cheats");
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private final RealmsResetWorldScreen lastScreen;
   private final long worldId;
   private final int slotId;
   private Button uploadButton;
   private List<WorldSummary> levelList = Lists.newArrayList();
   private int selectedWorld = -1;
   private RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
   private RealmsLabel titleLabel;
   private RealmsLabel subtitleLabel;
   private RealmsLabel noWorldsLabel;
   private final Runnable callback;

   public RealmsSelectFileToUploadScreen(long p_i232219_1_, int p_i232219_3_, RealmsResetWorldScreen p_i232219_4_, Runnable p_i232219_5_) {
      this.lastScreen = p_i232219_4_;
      this.worldId = p_i232219_1_;
      this.slotId = p_i232219_3_;
      this.callback = p_i232219_5_;
   }

   private void loadLevelList() throws Exception {
      this.levelList = this.minecraft.getLevelSource().getLevelList().stream().sorted((p_237970_0_, p_237970_1_) -> {
         if (p_237970_0_.getLastPlayed() < p_237970_1_.getLastPlayed()) {
            return 1;
         } else {
            return p_237970_0_.getLastPlayed() > p_237970_1_.getLastPlayed() ? -1 : p_237970_0_.getLevelId().compareTo(p_237970_1_.getLevelId());
         }
      }).collect(Collectors.toList());

      for(WorldSummary worldsummary : this.levelList) {
         this.worldSelectionList.addEntry(worldsummary);
      }

   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

      try {
         this.loadLevelList();
      } catch (Exception exception) {
         LOGGER.error("Couldn't load level list", (Throwable)exception);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(new StringTextComponent("Unable to load worlds"), ITextComponent.nullToEmpty(exception.getMessage()), this.lastScreen));
         return;
      }

      this.addWidget(this.worldSelectionList);
      this.uploadButton = this.addButton(new Button(this.width / 2 - 154, this.height - 32, 153, 20, new TranslationTextComponent("mco.upload.button.name"), (p_237976_1_) -> {
         this.upload();
      }));
      this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
      this.addButton(new Button(this.width / 2 + 6, this.height - 32, 153, 20, DialogTexts.GUI_BACK, (p_237973_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.titleLabel = this.addWidget(new RealmsLabel(new TranslationTextComponent("mco.upload.select.world.title"), this.width / 2, 13, 16777215));
      this.subtitleLabel = this.addWidget(new RealmsLabel(new TranslationTextComponent("mco.upload.select.world.subtitle"), this.width / 2, row(-1), 10526880));
      if (this.levelList.isEmpty()) {
         this.noWorldsLabel = this.addWidget(new RealmsLabel(new TranslationTextComponent("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215));
      } else {
         this.noWorldsLabel = null;
      }

      this.narrateLabels();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void upload() {
      if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
         WorldSummary worldsummary = this.levelList.get(this.selectedWorld);
         this.minecraft.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, worldsummary, this.callback));
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.worldSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.titleLabel.render(this, p_230430_1_);
      this.subtitleLabel.render(this, p_230430_1_);
      if (this.noWorldsLabel != null) {
         this.noWorldsLabel.render(this, p_230430_1_);
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private static ITextComponent gameModeName(WorldSummary p_237977_0_) {
      return p_237977_0_.getGameMode().getDisplayName();
   }

   private static String formatLastPlayed(WorldSummary p_237979_0_) {
      return DATE_FORMAT.format(new Date(p_237979_0_.getLastPlayed()));
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionEntry extends ExtendedList.AbstractListEntry<RealmsSelectFileToUploadScreen.WorldSelectionEntry> {
      private final WorldSummary levelSummary;
      private final String name;
      private final String id;
      private final ITextComponent info;

      public WorldSelectionEntry(WorldSummary p_i232220_2_) {
         this.levelSummary = p_i232220_2_;
         this.name = p_i232220_2_.getLevelName();
         this.id = p_i232220_2_.getLevelId() + " (" + RealmsSelectFileToUploadScreen.formatLastPlayed(p_i232220_2_) + ")";
         if (p_i232220_2_.isRequiresConversion()) {
            this.info = RealmsSelectFileToUploadScreen.REQUIRES_CONVERSION_TEXT;
         } else {
            ITextComponent itextcomponent;
            if (p_i232220_2_.isHardcore()) {
               itextcomponent = RealmsSelectFileToUploadScreen.HARDCORE_TEXT;
            } else {
               itextcomponent = RealmsSelectFileToUploadScreen.gameModeName(p_i232220_2_);
            }

            if (p_i232220_2_.hasCheats()) {
               itextcomponent = itextcomponent.copy().append(", ").append(RealmsSelectFileToUploadScreen.CHEATS_TEXT);
            }

            this.info = itextcomponent;
         }

      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderItem(p_230432_1_, this.levelSummary, p_230432_2_, p_230432_4_, p_230432_3_);
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
         return true;
      }

      protected void renderItem(MatrixStack p_237985_1_, WorldSummary p_237985_2_, int p_237985_3_, int p_237985_4_, int p_237985_5_) {
         String s;
         if (this.name.isEmpty()) {
            s = RealmsSelectFileToUploadScreen.WORLD_TEXT + " " + (p_237985_3_ + 1);
         } else {
            s = this.name;
         }

         RealmsSelectFileToUploadScreen.this.font.draw(p_237985_1_, s, (float)(p_237985_4_ + 2), (float)(p_237985_5_ + 1), 16777215);
         RealmsSelectFileToUploadScreen.this.font.draw(p_237985_1_, this.id, (float)(p_237985_4_ + 2), (float)(p_237985_5_ + 12), 8421504);
         RealmsSelectFileToUploadScreen.this.font.draw(p_237985_1_, this.info, (float)(p_237985_4_ + 2), (float)(p_237985_5_ + 12 + 10), 8421504);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionList extends RealmsObjectSelectionList<RealmsSelectFileToUploadScreen.WorldSelectionEntry> {
      public WorldSelectionList() {
         super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
      }

      public void addEntry(WorldSummary p_237986_1_) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldSelectionEntry(p_237986_1_));
      }

      public int getMaxPosition() {
         return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
      }

      public boolean isFocused() {
         return RealmsSelectFileToUploadScreen.this.getFocused() == this;
      }

      public void renderBackground(MatrixStack p_230433_1_) {
         RealmsSelectFileToUploadScreen.this.renderBackground(p_230433_1_);
      }

      public void selectItem(int p_231400_1_) {
         this.setSelectedItem(p_231400_1_);
         if (p_231400_1_ != -1) {
            WorldSummary worldsummary = RealmsSelectFileToUploadScreen.this.levelList.get(p_231400_1_);
            String s = I18n.get("narrator.select.list.position", p_231400_1_ + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
            String s1 = RealmsNarratorHelper.join(Arrays.asList(worldsummary.getLevelName(), RealmsSelectFileToUploadScreen.formatLastPlayed(worldsummary), RealmsSelectFileToUploadScreen.gameModeName(worldsummary).getString(), s));
            RealmsNarratorHelper.now(I18n.get("narrator.select", s1));
         }

      }

      public void setSelected(@Nullable RealmsSelectFileToUploadScreen.WorldSelectionEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(p_241215_1_);
         RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld).isHardcore();
      }
   }
}
