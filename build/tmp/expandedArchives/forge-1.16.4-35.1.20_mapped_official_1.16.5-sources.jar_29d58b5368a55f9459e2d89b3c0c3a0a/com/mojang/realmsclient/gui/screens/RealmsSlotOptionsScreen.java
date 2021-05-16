package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSlotOptionsScreen extends RealmsScreen {
   public static final ITextComponent[] DIFFICULTIES = new ITextComponent[]{new TranslationTextComponent("options.difficulty.peaceful"), new TranslationTextComponent("options.difficulty.easy"), new TranslationTextComponent("options.difficulty.normal"), new TranslationTextComponent("options.difficulty.hard")};
   public static final ITextComponent[] GAME_MODES = new ITextComponent[]{new TranslationTextComponent("selectWorld.gameMode.survival"), new TranslationTextComponent("selectWorld.gameMode.creative"), new TranslationTextComponent("selectWorld.gameMode.adventure")};
   private static final ITextComponent TEXT_ON = new TranslationTextComponent("mco.configure.world.on");
   private static final ITextComponent TEXT_OFF = new TranslationTextComponent("mco.configure.world.off");
   private static final ITextComponent GAME_MODE_LABEL = new TranslationTextComponent("selectWorld.gameMode");
   private static final ITextComponent NAME_LABEL = new TranslationTextComponent("mco.configure.world.edit.slot.name");
   private TextFieldWidget nameEdit;
   protected final RealmsConfigureWorldScreen parent;
   private int column1X;
   private int columnWidth;
   private int column2X;
   private final RealmsWorldOptions options;
   private final RealmsServer.ServerType worldType;
   private final int activeSlot;
   private int difficulty;
   private int gameMode;
   private Boolean pvp;
   private Boolean spawnNPCs;
   private Boolean spawnAnimals;
   private Boolean spawnMonsters;
   private Integer spawnProtection;
   private Boolean commandBlocks;
   private Boolean forceGameMode;
   private Button pvpButton;
   private Button spawnAnimalsButton;
   private Button spawnMonstersButton;
   private Button spawnNPCsButton;
   private RealmsSlotOptionsScreen.SettingsSlider spawnProtectionButton;
   private Button commandBlocksButton;
   private Button forceGameModeButton;
   private RealmsLabel titleLabel;
   private RealmsLabel warningLabel;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen p_i51750_1_, RealmsWorldOptions p_i51750_2_, RealmsServer.ServerType p_i51750_3_, int p_i51750_4_) {
      this.parent = p_i51750_1_;
      this.options = p_i51750_2_;
      this.worldType = p_i51750_3_;
      this.activeSlot = p_i51750_4_;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void tick() {
      this.nameEdit.tick();
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.minecraft.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public void init() {
      this.columnWidth = 170;
      this.column1X = this.width / 2 - this.columnWidth;
      this.column2X = this.width / 2 + 10;
      this.difficulty = this.options.difficulty;
      this.gameMode = this.options.gameMode;
      if (this.worldType == RealmsServer.ServerType.NORMAL) {
         this.pvp = this.options.pvp;
         this.spawnProtection = this.options.spawnProtection;
         this.forceGameMode = this.options.forceGameMode;
         this.spawnAnimals = this.options.spawnAnimals;
         this.spawnMonsters = this.options.spawnMonsters;
         this.spawnNPCs = this.options.spawnNPCs;
         this.commandBlocks = this.options.commandBlocks;
      } else {
         ITextComponent itextcomponent;
         if (this.worldType == RealmsServer.ServerType.ADVENTUREMAP) {
            itextcomponent = new TranslationTextComponent("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType == RealmsServer.ServerType.INSPIRATION) {
            itextcomponent = new TranslationTextComponent("mco.configure.world.edit.subscreen.inspiration");
         } else {
            itextcomponent = new TranslationTextComponent("mco.configure.world.edit.subscreen.experience");
         }

         this.warningLabel = new RealmsLabel(itextcomponent, this.width / 2, 26, 16711680);
         this.pvp = true;
         this.spawnProtection = 0;
         this.forceGameMode = false;
         this.spawnAnimals = true;
         this.spawnMonsters = true;
         this.spawnNPCs = true;
         this.commandBlocks = true;
      }

      this.nameEdit = new TextFieldWidget(this.minecraft.font, this.column1X + 2, row(1), this.columnWidth - 4, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.configure.world.edit.slot.name"));
      this.nameEdit.setMaxLength(10);
      this.nameEdit.setValue(this.options.getSlotName(this.activeSlot));
      this.magicalSpecialHackyFocus(this.nameEdit);
      this.pvpButton = this.addButton(new Button(this.column2X, row(1), this.columnWidth, 20, this.pvpTitle(), (p_238059_1_) -> {
         this.pvp = !this.pvp;
         p_238059_1_.setMessage(this.pvpTitle());
      }));
      this.addButton(new Button(this.column1X, row(3), this.columnWidth, 20, this.gameModeTitle(), (p_238057_1_) -> {
         this.gameMode = (this.gameMode + 1) % GAME_MODES.length;
         p_238057_1_.setMessage(this.gameModeTitle());
      }));
      this.spawnAnimalsButton = this.addButton(new Button(this.column2X, row(3), this.columnWidth, 20, this.spawnAnimalsTitle(), (p_238056_1_) -> {
         this.spawnAnimals = !this.spawnAnimals;
         p_238056_1_.setMessage(this.spawnAnimalsTitle());
      }));
      this.addButton(new Button(this.column1X, row(5), this.columnWidth, 20, this.difficultyTitle(), (p_238055_1_) -> {
         this.difficulty = (this.difficulty + 1) % DIFFICULTIES.length;
         p_238055_1_.setMessage(this.difficultyTitle());
         if (this.worldType == RealmsServer.ServerType.NORMAL) {
            this.spawnMonstersButton.active = this.difficulty != 0;
            this.spawnMonstersButton.setMessage(this.spawnMonstersTitle());
         }

      }));
      this.spawnMonstersButton = this.addButton(new Button(this.column2X, row(5), this.columnWidth, 20, this.spawnMonstersTitle(), (p_238053_1_) -> {
         this.spawnMonsters = !this.spawnMonsters;
         p_238053_1_.setMessage(this.spawnMonstersTitle());
      }));
      this.spawnProtectionButton = this.addButton(new RealmsSlotOptionsScreen.SettingsSlider(this.column1X, row(7), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      this.spawnNPCsButton = this.addButton(new Button(this.column2X, row(7), this.columnWidth, 20, this.spawnNPCsTitle(), (p_238052_1_) -> {
         this.spawnNPCs = !this.spawnNPCs;
         p_238052_1_.setMessage(this.spawnNPCsTitle());
      }));
      this.forceGameModeButton = this.addButton(new Button(this.column1X, row(9), this.columnWidth, 20, this.forceGameModeTitle(), (p_238051_1_) -> {
         this.forceGameMode = !this.forceGameMode;
         p_238051_1_.setMessage(this.forceGameModeTitle());
      }));
      this.commandBlocksButton = this.addButton(new Button(this.column2X, row(9), this.columnWidth, 20, this.commandBlocksTitle(), (p_238049_1_) -> {
         this.commandBlocks = !this.commandBlocks;
         p_238049_1_.setMessage(this.commandBlocksTitle());
      }));
      if (this.worldType != RealmsServer.ServerType.NORMAL) {
         this.pvpButton.active = false;
         this.spawnAnimalsButton.active = false;
         this.spawnNPCsButton.active = false;
         this.spawnMonstersButton.active = false;
         this.spawnProtectionButton.active = false;
         this.commandBlocksButton.active = false;
         this.forceGameModeButton.active = false;
      }

      if (this.difficulty == 0) {
         this.spawnMonstersButton.active = false;
      }

      this.addButton(new Button(this.column1X, row(13), this.columnWidth, 20, new TranslationTextComponent("mco.configure.world.buttons.done"), (p_238048_1_) -> {
         this.saveSettings();
      }));
      this.addButton(new Button(this.column2X, row(13), this.columnWidth, 20, DialogTexts.GUI_CANCEL, (p_238046_1_) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.addWidget(this.nameEdit);
      this.titleLabel = this.addWidget(new RealmsLabel(new TranslationTextComponent("mco.configure.world.buttons.options"), this.width / 2, 17, 16777215));
      if (this.warningLabel != null) {
         this.addWidget(this.warningLabel);
      }

      this.narrateLabels();
   }

   private ITextComponent difficultyTitle() {
      return (new TranslationTextComponent("options.difficulty")).append(": ").append(DIFFICULTIES[this.difficulty]);
   }

   private ITextComponent gameModeTitle() {
      return new TranslationTextComponent("options.generic_value", GAME_MODE_LABEL, GAME_MODES[this.gameMode]);
   }

   private ITextComponent pvpTitle() {
      return (new TranslationTextComponent("mco.configure.world.pvp")).append(": ").append(getOnOff(this.pvp));
   }

   private ITextComponent spawnAnimalsTitle() {
      return (new TranslationTextComponent("mco.configure.world.spawnAnimals")).append(": ").append(getOnOff(this.spawnAnimals));
   }

   private ITextComponent spawnMonstersTitle() {
      return this.difficulty == 0 ? (new TranslationTextComponent("mco.configure.world.spawnMonsters")).append(": ").append(new TranslationTextComponent("mco.configure.world.off")) : (new TranslationTextComponent("mco.configure.world.spawnMonsters")).append(": ").append(getOnOff(this.spawnMonsters));
   }

   private ITextComponent spawnNPCsTitle() {
      return (new TranslationTextComponent("mco.configure.world.spawnNPCs")).append(": ").append(getOnOff(this.spawnNPCs));
   }

   private ITextComponent commandBlocksTitle() {
      return (new TranslationTextComponent("mco.configure.world.commandBlocks")).append(": ").append(getOnOff(this.commandBlocks));
   }

   private ITextComponent forceGameModeTitle() {
      return (new TranslationTextComponent("mco.configure.world.forceGameMode")).append(": ").append(getOnOff(this.forceGameMode));
   }

   private static ITextComponent getOnOff(boolean p_238050_0_) {
      return p_238050_0_ ? TEXT_ON : TEXT_OFF;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.font.draw(p_230430_1_, NAME_LABEL, (float)(this.column1X + this.columnWidth / 2 - this.font.width(NAME_LABEL) / 2), (float)(row(0) - 5), 16777215);
      this.titleLabel.render(this, p_230430_1_);
      if (this.warningLabel != null) {
         this.warningLabel.render(this, p_230430_1_);
      }

      this.nameEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private String getSlotName() {
      return this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot)) ? "" : this.nameEdit.getValue();
   }

   private void saveSettings() {
      if (this.worldType != RealmsServer.ServerType.ADVENTUREMAP && this.worldType != RealmsServer.ServerType.EXPERIENCE && this.worldType != RealmsServer.ServerType.INSPIRATION) {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.getSlotName()));
      } else {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficulty, this.gameMode, this.options.forceGameMode, this.getSlotName()));
      }

   }

   @OnlyIn(Dist.CLIENT)
   class SettingsSlider extends AbstractSlider {
      private final double minValue;
      private final double maxValue;

      public SettingsSlider(int p_i232222_2_, int p_i232222_3_, int p_i232222_4_, int p_i232222_5_, float p_i232222_6_, float p_i232222_7_) {
         super(p_i232222_2_, p_i232222_3_, p_i232222_4_, 20, StringTextComponent.EMPTY, 0.0D);
         this.minValue = (double)p_i232222_6_;
         this.maxValue = (double)p_i232222_7_;
         this.value = (double)((MathHelper.clamp((float)p_i232222_5_, p_i232222_6_, p_i232222_7_) - p_i232222_6_) / (p_i232222_7_ - p_i232222_6_));
         this.updateMessage();
      }

      public void applyValue() {
         if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
            RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0D, 1.0D), this.minValue, this.maxValue);
         }
      }

      protected void updateMessage() {
         this.setMessage((new TranslationTextComponent("mco.configure.world.spawnProtection")).append(": ").append((ITextComponent)(RealmsSlotOptionsScreen.this.spawnProtection == 0 ? new TranslationTextComponent("mco.configure.world.off") : new StringTextComponent(String.valueOf((Object)RealmsSlotOptionsScreen.this.spawnProtection)))));
      }

      public void onClick(double p_230982_1_, double p_230982_3_) {
      }

      public void onRelease(double p_231000_1_, double p_231000_3_) {
      }
   }
}
