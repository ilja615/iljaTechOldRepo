package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
   private final Screen lastScreen;
   private final Backup backup;
   private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

   public RealmsBackupInfoScreen(Screen p_i232197_1_, Backup p_i232197_2_) {
      this.lastScreen = p_i232197_1_;
      this.backup = p_i232197_2_;
   }

   public void tick() {
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, DialogTexts.GUI_BACK, (p_237731_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.minecraft);
      this.addWidget(this.backupInfoList);
      this.magicalSpecialHackyFocus(this.backupInfoList);
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
      drawCenteredString(p_230430_1_, this.font, "Changes from last backup", this.width / 2, 10, 16777215);
      this.backupInfoList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private ITextComponent checkForSpecificMetadata(String p_237733_1_, String p_237733_2_) {
      String s = p_237733_1_.toLowerCase(Locale.ROOT);
      if (s.contains("game") && s.contains("mode")) {
         return this.gameModeMetadata(p_237733_2_);
      } else {
         return (ITextComponent)(s.contains("game") && s.contains("difficulty") ? this.gameDifficultyMetadata(p_237733_2_) : new StringTextComponent(p_237733_2_));
      }
   }

   private ITextComponent gameDifficultyMetadata(String p_237732_1_) {
      try {
         return RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(p_237732_1_)];
      } catch (Exception exception) {
         return new StringTextComponent("UNKNOWN");
      }
   }

   private ITextComponent gameModeMetadata(String p_237735_1_) {
      try {
         return RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(p_237735_1_)];
      } catch (Exception exception) {
         return new StringTextComponent("UNKNOWN");
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupInfoEntry extends ExtendedList.AbstractListEntry<RealmsBackupInfoScreen.BackupInfoEntry> {
      private final String key;
      private final String value;

      public BackupInfoEntry(String p_i232199_2_, String p_i232199_3_) {
         this.key = p_i232199_2_;
         this.value = p_i232199_3_;
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         FontRenderer fontrenderer = RealmsBackupInfoScreen.this.minecraft.font;
         AbstractGui.drawString(p_230432_1_, fontrenderer, this.key, p_230432_4_, p_230432_3_, 10526880);
         AbstractGui.drawString(p_230432_1_, fontrenderer, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), p_230432_4_, p_230432_3_ + 12, 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupInfoList extends ExtendedList<RealmsBackupInfoScreen.BackupInfoEntry> {
      public BackupInfoList(Minecraft p_i232198_2_) {
         super(p_i232198_2_, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
         this.setRenderSelection(false);
         if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup.changeList.forEach((p_237736_1_, p_237736_2_) -> {
               this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoEntry(p_237736_1_, p_237736_2_));
            });
         }

      }
   }
}
