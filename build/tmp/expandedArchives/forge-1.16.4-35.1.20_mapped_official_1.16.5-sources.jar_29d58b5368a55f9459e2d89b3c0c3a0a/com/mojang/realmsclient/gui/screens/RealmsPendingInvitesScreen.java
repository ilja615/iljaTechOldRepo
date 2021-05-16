package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.ListButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsPendingInvitesScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation ACCEPT_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/accept_icon.png");
   private static final ResourceLocation REJECT_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/reject_icon.png");
   private static final ITextComponent NO_PENDING_INVITES_TEXT = new TranslationTextComponent("mco.invites.nopending");
   private static final ITextComponent ACCEPT_INVITE_TOOLTIP = new TranslationTextComponent("mco.invites.button.accept");
   private static final ITextComponent REJECT_INVITE_TOOLTIP = new TranslationTextComponent("mco.invites.button.reject");
   private final Screen lastScreen;
   @Nullable
   private ITextComponent toolTip;
   private boolean loaded;
   private RealmsPendingInvitesScreen.InvitationList pendingInvitationSelectionList;
   private RealmsLabel titleLabel;
   private int selectedInvite = -1;
   private Button acceptButton;
   private Button rejectButton;

   public RealmsPendingInvitesScreen(Screen p_i232211_1_) {
      this.lastScreen = p_i232211_1_;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.pendingInvitationSelectionList = new RealmsPendingInvitesScreen.InvitationList();
      (new Thread("Realms-pending-invitations-fetcher") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               List<PendingInvite> list = realmsclient.pendingInvites().pendingInvites;
               List<RealmsPendingInvitesScreen.InvitationEntry> list1 = list.stream().map((p_225146_1_) -> {
                  return RealmsPendingInvitesScreen.this.new InvitationEntry(p_225146_1_);
               }).collect(Collectors.toList());
               RealmsPendingInvitesScreen.this.minecraft.execute(() -> {
                  RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(list1);
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsPendingInvitesScreen.LOGGER.error("Couldn't list invites");
            } finally {
               RealmsPendingInvitesScreen.this.loaded = true;
            }

         }
      }).start();
      this.addWidget(this.pendingInvitationSelectionList);
      this.acceptButton = this.addButton(new Button(this.width / 2 - 174, this.height - 32, 100, 20, new TranslationTextComponent("mco.invites.button.accept"), (p_237878_1_) -> {
         this.accept(this.selectedInvite);
         this.selectedInvite = -1;
         this.updateButtonStates();
      }));
      this.addButton(new Button(this.width / 2 - 50, this.height - 32, 100, 20, DialogTexts.GUI_DONE, (p_237875_1_) -> {
         this.minecraft.setScreen(new RealmsMainScreen(this.lastScreen));
      }));
      this.rejectButton = this.addButton(new Button(this.width / 2 + 74, this.height - 32, 100, 20, new TranslationTextComponent("mco.invites.button.reject"), (p_237871_1_) -> {
         this.reject(this.selectedInvite);
         this.selectedInvite = -1;
         this.updateButtonStates();
      }));
      this.titleLabel = new RealmsLabel(new TranslationTextComponent("mco.invites.title"), this.width / 2, 12, 16777215);
      this.addWidget(this.titleLabel);
      this.narrateLabels();
      this.updateButtonStates();
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.minecraft.setScreen(new RealmsMainScreen(this.lastScreen));
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private void updateList(int p_224318_1_) {
      this.pendingInvitationSelectionList.removeAtIndex(p_224318_1_);
   }

   private void reject(final int p_224321_1_) {
      if (p_224321_1_ < this.pendingInvitationSelectionList.getItemCount()) {
         (new Thread("Realms-reject-invitation") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.rejectInvitation((RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(p_224321_1_)).pendingInvite.invitationId);
                  RealmsPendingInvitesScreen.this.minecraft.execute(() -> {
                     RealmsPendingInvitesScreen.this.updateList(p_224321_1_);
                  });
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsPendingInvitesScreen.LOGGER.error("Couldn't reject invite");
               }

            }
         }).start();
      }

   }

   private void accept(final int p_224329_1_) {
      if (p_224329_1_ < this.pendingInvitationSelectionList.getItemCount()) {
         (new Thread("Realms-accept-invitation") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.acceptInvitation((RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(p_224329_1_)).pendingInvite.invitationId);
                  RealmsPendingInvitesScreen.this.minecraft.execute(() -> {
                     RealmsPendingInvitesScreen.this.updateList(p_224329_1_);
                  });
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsPendingInvitesScreen.LOGGER.error("Couldn't accept invite");
               }

            }
         }).start();
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.toolTip = null;
      this.renderBackground(p_230430_1_);
      this.pendingInvitationSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.titleLabel.render(this, p_230430_1_);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
      }

      if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
         drawCenteredString(p_230430_1_, this.font, NO_PENDING_INVITES_TEXT, this.width / 2, this.height / 2 - 20, 16777215);
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   protected void renderMousehoverTooltip(MatrixStack p_237866_1_, @Nullable ITextComponent p_237866_2_, int p_237866_3_, int p_237866_4_) {
      if (p_237866_2_ != null) {
         int i = p_237866_3_ + 12;
         int j = p_237866_4_ - 12;
         int k = this.font.width(p_237866_2_);
         this.fillGradient(p_237866_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_237866_1_, p_237866_2_, (float)i, (float)j, 16777215);
      }
   }

   private void updateButtonStates() {
      this.acceptButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
      this.rejectButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
   }

   private boolean shouldAcceptAndRejectButtonBeVisible(int p_224316_1_) {
      return p_224316_1_ != -1;
   }

   @OnlyIn(Dist.CLIENT)
   class InvitationEntry extends ExtendedList.AbstractListEntry<RealmsPendingInvitesScreen.InvitationEntry> {
      private final PendingInvite pendingInvite;
      private final List<ListButton> rowButtons;

      InvitationEntry(PendingInvite p_i51623_2_) {
         this.pendingInvite = p_i51623_2_;
         this.rowButtons = Arrays.asList(new RealmsPendingInvitesScreen.InvitationEntry.AcceptButton(), new RealmsPendingInvitesScreen.InvitationEntry.RejectButton());
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderPendingInvitationItem(p_230432_1_, this.pendingInvite, p_230432_4_, p_230432_3_, p_230432_7_, p_230432_8_);
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         ListButton.rowButtonMouseClicked(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, p_231044_5_, p_231044_1_, p_231044_3_);
         return true;
      }

      private void renderPendingInvitationItem(MatrixStack p_237893_1_, PendingInvite p_237893_2_, int p_237893_3_, int p_237893_4_, int p_237893_5_, int p_237893_6_) {
         RealmsPendingInvitesScreen.this.font.draw(p_237893_1_, p_237893_2_.worldName, (float)(p_237893_3_ + 38), (float)(p_237893_4_ + 1), 16777215);
         RealmsPendingInvitesScreen.this.font.draw(p_237893_1_, p_237893_2_.worldOwnerName, (float)(p_237893_3_ + 38), (float)(p_237893_4_ + 12), 7105644);
         RealmsPendingInvitesScreen.this.font.draw(p_237893_1_, RealmsUtil.convertToAgePresentationFromInstant(p_237893_2_.date), (float)(p_237893_3_ + 38), (float)(p_237893_4_ + 24), 7105644);
         ListButton.drawButtonsInRow(p_237893_1_, this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, p_237893_3_, p_237893_4_, p_237893_5_, p_237893_6_);
         RealmsTextureManager.withBoundFace(p_237893_2_.worldOwnerUuid, () -> {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(p_237893_1_, p_237893_3_, p_237893_4_, 32, 32, 8.0F, 8.0F, 8, 8, 64, 64);
            AbstractGui.blit(p_237893_1_, p_237893_3_, p_237893_4_, 32, 32, 40.0F, 8.0F, 8, 8, 64, 64);
         });
      }

      @OnlyIn(Dist.CLIENT)
      class AcceptButton extends ListButton {
         AcceptButton() {
            super(15, 15, 215, 5);
         }

         protected void draw(MatrixStack p_230435_1_, int p_230435_2_, int p_230435_3_, boolean p_230435_4_) {
            RealmsPendingInvitesScreen.this.minecraft.getTextureManager().bind(RealmsPendingInvitesScreen.ACCEPT_ICON_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f = p_230435_4_ ? 19.0F : 0.0F;
            AbstractGui.blit(p_230435_1_, p_230435_2_, p_230435_3_, f, 0.0F, 18, 18, 37, 18);
            if (p_230435_4_) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsPendingInvitesScreen.ACCEPT_INVITE_TOOLTIP;
            }

         }

         public void onClick(int p_225121_1_) {
            RealmsPendingInvitesScreen.this.accept(p_225121_1_);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RejectButton extends ListButton {
         RejectButton() {
            super(15, 15, 235, 5);
         }

         protected void draw(MatrixStack p_230435_1_, int p_230435_2_, int p_230435_3_, boolean p_230435_4_) {
            RealmsPendingInvitesScreen.this.minecraft.getTextureManager().bind(RealmsPendingInvitesScreen.REJECT_ICON_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f = p_230435_4_ ? 19.0F : 0.0F;
            AbstractGui.blit(p_230435_1_, p_230435_2_, p_230435_3_, f, 0.0F, 18, 18, 37, 18);
            if (p_230435_4_) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsPendingInvitesScreen.REJECT_INVITE_TOOLTIP;
            }

         }

         public void onClick(int p_225121_1_) {
            RealmsPendingInvitesScreen.this.reject(p_225121_1_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InvitationList extends RealmsObjectSelectionList<RealmsPendingInvitesScreen.InvitationEntry> {
      public InvitationList() {
         super(RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height, 32, RealmsPendingInvitesScreen.this.height - 40, 36);
      }

      public void removeAtIndex(int p_223872_1_) {
         this.remove(p_223872_1_);
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 260;
      }

      public boolean isFocused() {
         return RealmsPendingInvitesScreen.this.getFocused() == this;
      }

      public void renderBackground(MatrixStack p_230433_1_) {
         RealmsPendingInvitesScreen.this.renderBackground(p_230433_1_);
      }

      public void selectItem(int p_231400_1_) {
         this.setSelectedItem(p_231400_1_);
         if (p_231400_1_ != -1) {
            List<RealmsPendingInvitesScreen.InvitationEntry> list = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
            PendingInvite pendinginvite = (list.get(p_231400_1_)).pendingInvite;
            String s = I18n.get("narrator.select.list.position", p_231400_1_ + 1, list.size());
            String s1 = RealmsNarratorHelper.join(Arrays.asList(pendinginvite.worldName, pendinginvite.worldOwnerName, RealmsUtil.convertToAgePresentationFromInstant(pendinginvite.date), s));
            RealmsNarratorHelper.now(I18n.get("narrator.select", s1));
         }

         this.selectInviteListItem(p_231400_1_);
      }

      public void selectInviteListItem(int p_223873_1_) {
         RealmsPendingInvitesScreen.this.selectedInvite = p_223873_1_;
         RealmsPendingInvitesScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsPendingInvitesScreen.InvitationEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         RealmsPendingInvitesScreen.this.selectedInvite = this.children().indexOf(p_241215_1_);
         RealmsPendingInvitesScreen.this.updateButtonStates();
      }
   }
}
