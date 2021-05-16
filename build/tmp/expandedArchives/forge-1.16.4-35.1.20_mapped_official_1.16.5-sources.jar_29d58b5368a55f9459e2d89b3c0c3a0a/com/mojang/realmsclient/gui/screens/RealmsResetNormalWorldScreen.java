package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private static final ITextComponent SEED_LABEL = new TranslationTextComponent("mco.reset.world.seed");
   private static final ITextComponent[] LEVEL_TYPES = new ITextComponent[]{new TranslationTextComponent("generator.default"), new TranslationTextComponent("generator.flat"), new TranslationTextComponent("generator.large_biomes"), new TranslationTextComponent("generator.amplified")};
   private final RealmsResetWorldScreen lastScreen;
   private RealmsLabel titleLabel;
   private TextFieldWidget seedEdit;
   private Boolean generateStructures = true;
   private Integer levelTypeIndex = 0;
   private ITextComponent buttonTitle;

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen p_i232214_1_, ITextComponent p_i232214_2_) {
      this.lastScreen = p_i232214_1_;
      this.buttonTitle = p_i232214_2_;
   }

   public void tick() {
      this.seedEdit.tick();
      super.tick();
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.titleLabel = new RealmsLabel(new TranslationTextComponent("mco.reset.world.generate"), this.width / 2, 17, 16777215);
      this.addWidget(this.titleLabel);
      this.seedEdit = new TextFieldWidget(this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.addWidget(this.seedEdit);
      this.setInitialFocus(this.seedEdit);
      this.addButton(new Button(this.width / 2 - 102, row(4), 205, 20, this.levelTypeTitle(), (p_237936_1_) -> {
         this.levelTypeIndex = (this.levelTypeIndex + 1) % LEVEL_TYPES.length;
         p_237936_1_.setMessage(this.levelTypeTitle());
      }));
      this.addButton(new Button(this.width / 2 - 102, row(6) - 2, 205, 20, this.generateStructuresTitle(), (p_237935_1_) -> {
         this.generateStructures = !this.generateStructures;
         p_237935_1_.setMessage(this.generateStructuresTitle());
      }));
      this.addButton(new Button(this.width / 2 - 102, row(12), 97, 20, this.buttonTitle, (p_237934_1_) -> {
         this.lastScreen.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getValue(), this.levelTypeIndex, this.generateStructures));
      }));
      this.addButton(new Button(this.width / 2 + 8, row(12), 97, 20, DialogTexts.GUI_BACK, (p_237933_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
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

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.titleLabel.render(this, p_230430_1_);
      this.font.draw(p_230430_1_, SEED_LABEL, (float)(this.width / 2 - 100), (float)row(1), 10526880);
      this.seedEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private ITextComponent levelTypeTitle() {
      return (new TranslationTextComponent("selectWorld.mapType")).append(" ").append(LEVEL_TYPES[this.levelTypeIndex]);
   }

   private ITextComponent generateStructuresTitle() {
      return DialogTexts.optionStatus(new TranslationTextComponent("selectWorld.mapFeatures"), this.generateStructures);
   }
}
