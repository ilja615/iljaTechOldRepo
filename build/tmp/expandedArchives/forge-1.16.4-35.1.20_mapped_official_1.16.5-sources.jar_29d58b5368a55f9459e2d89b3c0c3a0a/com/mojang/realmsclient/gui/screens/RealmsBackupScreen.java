package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.action.PrepareDownloadRealmsAction;
import net.minecraft.realms.action.RestoringBackupRealmsAction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation PLUS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/plus_icon.png");
   private static final ResourceLocation RESTORE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/restore_icon.png");
   private static final ITextComponent RESTORE_TOOLTIP = new TranslationTextComponent("mco.backup.button.restore");
   private static final ITextComponent HAS_CHANGES_TOOLTIP = new TranslationTextComponent("mco.backup.changes.tooltip");
   private static final ITextComponent TITLE = new TranslationTextComponent("mco.configure.world.backup");
   private static final ITextComponent NO_BACKUPS_LABEL = new TranslationTextComponent("mco.backup.nobackups");
   private static int lastScrollPosition = -1;
   private final RealmsConfigureWorldScreen lastScreen;
   private List<Backup> backups = Collections.emptyList();
   @Nullable
   private ITextComponent toolTip;
   private RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
   private int selectedBackup = -1;
   private final int slotId;
   private Button downloadButton;
   private Button restoreButton;
   private Button changesButton;
   private Boolean noBackups = false;
   private final RealmsServer serverData;
   private RealmsLabel titleLabel;

   public RealmsBackupScreen(RealmsConfigureWorldScreen p_i51777_1_, RealmsServer p_i51777_2_, int p_i51777_3_) {
      this.lastScreen = p_i51777_1_;
      this.serverData = p_i51777_2_;
      this.slotId = p_i51777_3_;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
      if (lastScrollPosition != -1) {
         this.backupObjectSelectionList.setScrollAmount((double)lastScrollPosition);
      }

      (new Thread("Realms-fetch-backups") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               List<Backup> list = realmsclient.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
               RealmsBackupScreen.this.minecraft.execute(() -> {
                  RealmsBackupScreen.this.backups = list;
                  RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                  RealmsBackupScreen.this.backupObjectSelectionList.clear();

                  for(Backup backup : RealmsBackupScreen.this.backups) {
                     RealmsBackupScreen.this.backupObjectSelectionList.addEntry(backup);
                  }

                  RealmsBackupScreen.this.generateChangeList();
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsBackupScreen.LOGGER.error("Couldn't request backups", (Throwable)realmsserviceexception);
            }

         }
      }).start();
      this.downloadButton = this.addButton(new Button(this.width - 135, row(1), 120, 20, new TranslationTextComponent("mco.backup.button.download"), (p_237758_1_) -> {
         this.downloadClicked();
      }));
      this.restoreButton = this.addButton(new Button(this.width - 135, row(3), 120, 20, new TranslationTextComponent("mco.backup.button.restore"), (p_237754_1_) -> {
         this.restoreClicked(this.selectedBackup);
      }));
      this.changesButton = this.addButton(new Button(this.width - 135, row(5), 120, 20, new TranslationTextComponent("mco.backup.changes.tooltip"), (p_237752_1_) -> {
         this.minecraft.setScreen(new RealmsBackupInfoScreen(this, this.backups.get(this.selectedBackup)));
         this.selectedBackup = -1;
      }));
      this.addButton(new Button(this.width - 100, this.height - 35, 85, 20, DialogTexts.GUI_BACK, (p_237748_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.addWidget(this.backupObjectSelectionList);
      this.titleLabel = this.addWidget(new RealmsLabel(new TranslationTextComponent("mco.configure.world.backup"), this.width / 2, 12, 16777215));
      this.magicalSpecialHackyFocus(this.backupObjectSelectionList);
      this.updateButtonStates();
      this.narrateLabels();
   }

   private void generateChangeList() {
      if (this.backups.size() > 1) {
         for(int i = 0; i < this.backups.size() - 1; ++i) {
            Backup backup = this.backups.get(i);
            Backup backup1 = this.backups.get(i + 1);
            if (!backup.metadata.isEmpty() && !backup1.metadata.isEmpty()) {
               for(String s : backup.metadata.keySet()) {
                  if (!s.contains("Uploaded") && backup1.metadata.containsKey(s)) {
                     if (!backup.metadata.get(s).equals(backup1.metadata.get(s))) {
                        this.addToChangeList(backup, s);
                     }
                  } else {
                     this.addToChangeList(backup, s);
                  }
               }
            }
         }

      }
   }

   private void addToChangeList(Backup p_224103_1_, String p_224103_2_) {
      if (p_224103_2_.contains("Uploaded")) {
         String s = DateFormat.getDateTimeInstance(3, 3).format(p_224103_1_.lastModifiedDate);
         p_224103_1_.changeList.put(p_224103_2_, s);
         p_224103_1_.setUploadedVersion(true);
      } else {
         p_224103_1_.changeList.put(p_224103_2_, p_224103_1_.metadata.get(p_224103_2_));
      }

   }

   private void updateButtonStates() {
      this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
      this.changesButton.visible = this.shouldChangesButtonBeVisible();
   }

   private boolean shouldChangesButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !(this.backups.get(this.selectedBackup)).changeList.isEmpty();
      }
   }

   private boolean shouldRestoreButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !this.serverData.expired;
      }
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private void restoreClicked(int p_224104_1_) {
      if (p_224104_1_ >= 0 && p_224104_1_ < this.backups.size() && !this.serverData.expired) {
         this.selectedBackup = p_224104_1_;
         Date date = (this.backups.get(p_224104_1_)).lastModifiedDate;
         String s = DateFormat.getDateTimeInstance(3, 3).format(date);
         String s1 = RealmsUtil.convertToAgePresentationFromInstant(date);
         ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.restore.question.line1", s, s1);
         ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.restore.question.line2");
         this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_237759_1_) -> {
            if (p_237759_1_) {
               this.restore();
            } else {
               this.selectedBackup = -1;
               this.minecraft.setScreen(this);
            }

         }, RealmsLongConfirmationScreen.Type.Warning, itextcomponent, itextcomponent1, true));
      }

   }

   private void downloadClicked() {
      ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.restore.download.question.line1");
      ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.restore.download.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_237755_1_) -> {
         if (p_237755_1_) {
            this.downloadWorldData();
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
   }

   private void downloadWorldData() {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new PrepareDownloadRealmsAction(this.serverData.id, this.slotId, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")", this)));
   }

   private void restore() {
      Backup backup = this.backups.get(this.selectedBackup);
      this.selectedBackup = -1;
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoringBackupRealmsAction(backup, this.serverData.id, this.lastScreen)));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.toolTip = null;
      this.renderBackground(p_230430_1_);
      this.backupObjectSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.titleLabel.render(this, p_230430_1_);
      this.font.draw(p_230430_1_, TITLE, (float)((this.width - 150) / 2 - 90), 20.0F, 10526880);
      if (this.noBackups) {
         this.font.draw(p_230430_1_, NO_BACKUPS_LABEL, 20.0F, (float)(this.height / 2 - 10), 16777215);
      }

      this.downloadButton.active = !this.noBackups;
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
      }

   }

   protected void renderMousehoverTooltip(MatrixStack p_237744_1_, @Nullable ITextComponent p_237744_2_, int p_237744_3_, int p_237744_4_) {
      if (p_237744_2_ != null) {
         int i = p_237744_3_ + 12;
         int j = p_237744_4_ - 12;
         int k = this.font.width(p_237744_2_);
         this.fillGradient(p_237744_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_237744_1_, p_237744_2_, (float)i, (float)j, 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupObjectSelectionList extends RealmsObjectSelectionList<RealmsBackupScreen.BackupObjectSelectionListEntry> {
      public BackupObjectSelectionList() {
         super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
      }

      public void addEntry(Backup p_223867_1_) {
         this.addEntry(RealmsBackupScreen.this.new BackupObjectSelectionListEntry(p_223867_1_));
      }

      public int getRowWidth() {
         return (int)((double)this.width * 0.93D);
      }

      public boolean isFocused() {
         return RealmsBackupScreen.this.getFocused() == this;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground(MatrixStack p_230433_1_) {
         RealmsBackupScreen.this.renderBackground(p_230433_1_);
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         if (p_231044_5_ != 0) {
            return false;
         } else if (p_231044_1_ < (double)this.getScrollbarPosition() && p_231044_3_ >= (double)this.y0 && p_231044_3_ <= (double)this.y1) {
            int i = this.width / 2 - 92;
            int j = this.width;
            int k = (int)Math.floor(p_231044_3_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount();
            int l = k / this.itemHeight;
            if (p_231044_1_ >= (double)i && p_231044_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.selectItem(l);
               this.itemClicked(k, l, p_231044_1_, p_231044_3_, this.width);
            }

            return true;
         } else {
            return false;
         }
      }

      public int getScrollbarPosition() {
         return this.width - 5;
      }

      public void itemClicked(int p_231401_1_, int p_231401_2_, double p_231401_3_, double p_231401_5_, int p_231401_7_) {
         int i = this.width - 35;
         int j = p_231401_2_ * this.itemHeight + 36 - (int)this.getScrollAmount();
         int k = i + 10;
         int l = j - 3;
         if (p_231401_3_ >= (double)i && p_231401_3_ <= (double)(i + 9) && p_231401_5_ >= (double)j && p_231401_5_ <= (double)(j + 9)) {
            if (!(RealmsBackupScreen.this.backups.get(p_231401_2_)).changeList.isEmpty()) {
               RealmsBackupScreen.this.selectedBackup = -1;
               RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
               this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, RealmsBackupScreen.this.backups.get(p_231401_2_)));
            }
         } else if (p_231401_3_ >= (double)k && p_231401_3_ < (double)(k + 13) && p_231401_5_ >= (double)l && p_231401_5_ < (double)(l + 15)) {
            RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
            RealmsBackupScreen.this.restoreClicked(p_231401_2_);
         }

      }

      public void selectItem(int p_231400_1_) {
         this.setSelectedItem(p_231400_1_);
         if (p_231400_1_ != -1) {
            RealmsNarratorHelper.now(I18n.get("narrator.select", (RealmsBackupScreen.this.backups.get(p_231400_1_)).lastModifiedDate.toString()));
         }

         this.selectInviteListItem(p_231400_1_);
      }

      public void selectInviteListItem(int p_223866_1_) {
         RealmsBackupScreen.this.selectedBackup = p_223866_1_;
         RealmsBackupScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsBackupScreen.BackupObjectSelectionListEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         RealmsBackupScreen.this.selectedBackup = this.children().indexOf(p_241215_1_);
         RealmsBackupScreen.this.updateButtonStates();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BackupObjectSelectionListEntry extends ExtendedList.AbstractListEntry<RealmsBackupScreen.BackupObjectSelectionListEntry> {
      private final Backup backup;

      public BackupObjectSelectionListEntry(Backup p_i51657_2_) {
         this.backup = p_i51657_2_;
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderBackupItem(p_230432_1_, this.backup, p_230432_4_ - 40, p_230432_3_, p_230432_7_, p_230432_8_);
      }

      private void renderBackupItem(MatrixStack p_237767_1_, Backup p_237767_2_, int p_237767_3_, int p_237767_4_, int p_237767_5_, int p_237767_6_) {
         int i = p_237767_2_.isUploadedVersion() ? -8388737 : 16777215;
         RealmsBackupScreen.this.font.draw(p_237767_1_, "Backup (" + RealmsUtil.convertToAgePresentationFromInstant(p_237767_2_.lastModifiedDate) + ")", (float)(p_237767_3_ + 40), (float)(p_237767_4_ + 1), i);
         RealmsBackupScreen.this.font.draw(p_237767_1_, this.getMediumDatePresentation(p_237767_2_.lastModifiedDate), (float)(p_237767_3_ + 40), (float)(p_237767_4_ + 12), 5000268);
         int j = RealmsBackupScreen.this.width - 175;
         int k = -3;
         int l = j - 10;
         int i1 = 0;
         if (!RealmsBackupScreen.this.serverData.expired) {
            this.drawRestore(p_237767_1_, j, p_237767_4_ + -3, p_237767_5_, p_237767_6_);
         }

         if (!p_237767_2_.changeList.isEmpty()) {
            this.drawInfo(p_237767_1_, l, p_237767_4_ + 0, p_237767_5_, p_237767_6_);
         }

      }

      private String getMediumDatePresentation(Date p_223738_1_) {
         return DateFormat.getDateTimeInstance(3, 3).format(p_223738_1_);
      }

      private void drawRestore(MatrixStack p_237766_1_, int p_237766_2_, int p_237766_3_, int p_237766_4_, int p_237766_5_) {
         boolean flag = p_237766_4_ >= p_237766_2_ && p_237766_4_ <= p_237766_2_ + 12 && p_237766_5_ >= p_237766_3_ && p_237766_5_ <= p_237766_3_ + 14 && p_237766_5_ < RealmsBackupScreen.this.height - 15 && p_237766_5_ > 32;
         RealmsBackupScreen.this.minecraft.getTextureManager().bind(RealmsBackupScreen.RESTORE_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         float f = flag ? 28.0F : 0.0F;
         AbstractGui.blit(p_237766_1_, p_237766_2_ * 2, p_237766_3_ * 2, 0.0F, f, 23, 28, 23, 56);
         RenderSystem.popMatrix();
         if (flag) {
            RealmsBackupScreen.this.toolTip = RealmsBackupScreen.RESTORE_TOOLTIP;
         }

      }

      private void drawInfo(MatrixStack p_237768_1_, int p_237768_2_, int p_237768_3_, int p_237768_4_, int p_237768_5_) {
         boolean flag = p_237768_4_ >= p_237768_2_ && p_237768_4_ <= p_237768_2_ + 8 && p_237768_5_ >= p_237768_3_ && p_237768_5_ <= p_237768_3_ + 8 && p_237768_5_ < RealmsBackupScreen.this.height - 15 && p_237768_5_ > 32;
         RealmsBackupScreen.this.minecraft.getTextureManager().bind(RealmsBackupScreen.PLUS_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         float f = flag ? 15.0F : 0.0F;
         AbstractGui.blit(p_237768_1_, p_237768_2_ * 2, p_237768_3_ * 2, 0.0F, f, 15, 15, 15, 30);
         RenderSystem.popMatrix();
         if (flag) {
            RealmsBackupScreen.this.toolTip = RealmsBackupScreen.HAS_CHANGES_TOOLTIP;
         }

      }
   }
}
