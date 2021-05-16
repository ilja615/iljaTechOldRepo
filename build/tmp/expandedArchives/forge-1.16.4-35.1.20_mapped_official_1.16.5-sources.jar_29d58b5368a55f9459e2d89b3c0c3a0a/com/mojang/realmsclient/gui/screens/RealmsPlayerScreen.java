package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation OP_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/op_icon.png");
   private static final ResourceLocation USER_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/user_icon.png");
   private static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_player_icon.png");
   private static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("minecraft", "textures/gui/options_background.png");
   private static final ITextComponent NORMAL_USER_TOOLTIP = new TranslationTextComponent("mco.configure.world.invites.normal.tooltip");
   private static final ITextComponent OP_TOOLTIP = new TranslationTextComponent("mco.configure.world.invites.ops.tooltip");
   private static final ITextComponent REMOVE_ENTRY_TOOLTIP = new TranslationTextComponent("mco.configure.world.invites.remove.tooltip");
   private static final ITextComponent INVITED_LABEL = new TranslationTextComponent("mco.configure.world.invited");
   private ITextComponent toolTip;
   private final RealmsConfigureWorldScreen lastScreen;
   private final RealmsServer serverData;
   private RealmsPlayerScreen.InvitedList invitedObjectSelectionList;
   private int column1X;
   private int columnWidth;
   private int column2X;
   private Button removeButton;
   private Button opdeopButton;
   private int selectedInvitedIndex = -1;
   private String selectedInvited;
   private int player = -1;
   private boolean stateChanged;
   private RealmsLabel titleLabel;
   private RealmsPlayerScreen.GuestAction hoveredUserAction = RealmsPlayerScreen.GuestAction.NONE;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen p_i51760_1_, RealmsServer p_i51760_2_) {
      this.lastScreen = p_i51760_1_;
      this.serverData = p_i51760_2_;
   }

   public void init() {
      this.column1X = this.width / 2 - 160;
      this.columnWidth = 150;
      this.column2X = this.width / 2 + 12;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedList();
      this.invitedObjectSelectionList.setLeftPos(this.column1X);
      this.addWidget(this.invitedObjectSelectionList);

      for(PlayerInfo playerinfo : this.serverData.players) {
         this.invitedObjectSelectionList.addEntry(playerinfo);
      }

      this.addButton(new Button(this.column2X, row(1), this.columnWidth + 10, 20, new TranslationTextComponent("mco.configure.world.buttons.invite"), (p_237924_1_) -> {
         this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData));
      }));
      this.removeButton = this.addButton(new Button(this.column2X, row(7), this.columnWidth + 10, 20, new TranslationTextComponent("mco.configure.world.invites.remove.tooltip"), (p_237918_1_) -> {
         this.uninvite(this.player);
      }));
      this.opdeopButton = this.addButton(new Button(this.column2X, row(9), this.columnWidth + 10, 20, new TranslationTextComponent("mco.configure.world.invites.ops.tooltip"), (p_237912_1_) -> {
         if (this.serverData.players.get(this.player).isOperator()) {
            this.deop(this.player);
         } else {
            this.op(this.player);
         }

      }));
      this.addButton(new Button(this.column2X + this.columnWidth / 2 + 2, row(12), this.columnWidth / 2 + 10 - 2, 20, DialogTexts.GUI_BACK, (p_237907_1_) -> {
         this.backButtonClicked();
      }));
      this.titleLabel = this.addWidget(new RealmsLabel(new TranslationTextComponent("mco.configure.world.players.title"), this.width / 2, 17, 16777215));
      this.narrateLabels();
      this.updateButtonStates();
   }

   private void updateButtonStates() {
      this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
      this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
   }

   private boolean shouldRemoveAndOpdeopButtonBeVisible(int p_224296_1_) {
      return p_224296_1_ != -1;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.minecraft.setScreen(this.lastScreen.getNewScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   private void op(int p_224289_1_) {
      this.updateButtonStates();
      RealmsClient realmsclient = RealmsClient.create();
      String s = this.serverData.players.get(p_224289_1_).getUuid();

      try {
         this.updateOps(realmsclient.op(this.serverData.id, s));
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't op the user");
      }

   }

   private void deop(int p_224279_1_) {
      this.updateButtonStates();
      RealmsClient realmsclient = RealmsClient.create();
      String s = this.serverData.players.get(p_224279_1_).getUuid();

      try {
         this.updateOps(realmsclient.deop(this.serverData.id, s));
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't deop the user");
      }

   }

   private void updateOps(Ops p_224283_1_) {
      for(PlayerInfo playerinfo : this.serverData.players) {
         playerinfo.setOperator(p_224283_1_.ops.contains(playerinfo.getName()));
      }

   }

   private void uninvite(int p_224274_1_) {
      this.updateButtonStates();
      if (p_224274_1_ >= 0 && p_224274_1_ < this.serverData.players.size()) {
         PlayerInfo playerinfo = this.serverData.players.get(p_224274_1_);
         this.selectedInvited = playerinfo.getUuid();
         this.selectedInvitedIndex = p_224274_1_;
         RealmsConfirmScreen realmsconfirmscreen = new RealmsConfirmScreen((p_237919_1_) -> {
            if (p_237919_1_) {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  realmsclient.uninvite(this.serverData.id, this.selectedInvited);
               } catch (RealmsServiceException realmsserviceexception) {
                  LOGGER.error("Couldn't uninvite user");
               }

               this.deleteFromInvitedList(this.selectedInvitedIndex);
               this.player = -1;
               this.updateButtonStates();
            }

            this.stateChanged = true;
            this.minecraft.setScreen(this);
         }, new StringTextComponent("Question"), (new TranslationTextComponent("mco.configure.world.uninvite.question")).append(" '").append(playerinfo.getName()).append("' ?"));
         this.minecraft.setScreen(realmsconfirmscreen);
      }

   }

   private void deleteFromInvitedList(int p_224292_1_) {
      this.serverData.players.remove(p_224292_1_);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.toolTip = null;
      this.hoveredUserAction = RealmsPlayerScreen.GuestAction.NONE;
      this.renderBackground(p_230430_1_);
      if (this.invitedObjectSelectionList != null) {
         this.invitedObjectSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      }

      int i = row(12) + 20;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      this.minecraft.getTextureManager().bind(OPTIONS_BACKGROUND);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / 32.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, (double)i, 0.0D).uv((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex(0.0D, (double)i, 0.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      tessellator.end();
      this.titleLabel.render(this, p_230430_1_);
      if (this.serverData != null && this.serverData.players != null) {
         this.font.draw(p_230430_1_, (new StringTextComponent("")).append(INVITED_LABEL).append(" (").append(Integer.toString(this.serverData.players.size())).append(")"), (float)this.column1X, (float)row(0), 10526880);
      } else {
         this.font.draw(p_230430_1_, INVITED_LABEL, (float)this.column1X, (float)row(0), 10526880);
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.serverData != null) {
         this.renderMousehoverTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
      }
   }

   protected void renderMousehoverTooltip(MatrixStack p_237903_1_, @Nullable ITextComponent p_237903_2_, int p_237903_3_, int p_237903_4_) {
      if (p_237903_2_ != null) {
         int i = p_237903_3_ + 12;
         int j = p_237903_4_ - 12;
         int k = this.font.width(p_237903_2_);
         this.fillGradient(p_237903_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_237903_1_, p_237903_2_, (float)i, (float)j, 16777215);
      }
   }

   private void drawRemoveIcon(MatrixStack p_237914_1_, int p_237914_2_, int p_237914_3_, int p_237914_4_, int p_237914_5_) {
      boolean flag = p_237914_4_ >= p_237914_2_ && p_237914_4_ <= p_237914_2_ + 9 && p_237914_5_ >= p_237914_3_ && p_237914_5_ <= p_237914_3_ + 9 && p_237914_5_ < row(12) + 20 && p_237914_5_ > row(1);
      this.minecraft.getTextureManager().bind(CROSS_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 7.0F : 0.0F;
      AbstractGui.blit(p_237914_1_, p_237914_2_, p_237914_3_, 0.0F, f, 8, 7, 8, 14);
      if (flag) {
         this.toolTip = REMOVE_ENTRY_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.GuestAction.REMOVE;
      }

   }

   private void drawOpped(MatrixStack p_237921_1_, int p_237921_2_, int p_237921_3_, int p_237921_4_, int p_237921_5_) {
      boolean flag = p_237921_4_ >= p_237921_2_ && p_237921_4_ <= p_237921_2_ + 9 && p_237921_5_ >= p_237921_3_ && p_237921_5_ <= p_237921_3_ + 9 && p_237921_5_ < row(12) + 20 && p_237921_5_ > row(1);
      this.minecraft.getTextureManager().bind(OP_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 8.0F : 0.0F;
      AbstractGui.blit(p_237921_1_, p_237921_2_, p_237921_3_, 0.0F, f, 8, 8, 8, 16);
      if (flag) {
         this.toolTip = OP_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.GuestAction.TOGGLE_OP;
      }

   }

   private void drawNormal(MatrixStack p_237925_1_, int p_237925_2_, int p_237925_3_, int p_237925_4_, int p_237925_5_) {
      boolean flag = p_237925_4_ >= p_237925_2_ && p_237925_4_ <= p_237925_2_ + 9 && p_237925_5_ >= p_237925_3_ && p_237925_5_ <= p_237925_3_ + 9 && p_237925_5_ < row(12) + 20 && p_237925_5_ > row(1);
      this.minecraft.getTextureManager().bind(USER_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 8.0F : 0.0F;
      AbstractGui.blit(p_237925_1_, p_237925_2_, p_237925_3_, 0.0F, f, 8, 8, 8, 16);
      if (flag) {
         this.toolTip = NORMAL_USER_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.GuestAction.TOGGLE_OP;
      }

   }

   @OnlyIn(Dist.CLIENT)
   static enum GuestAction {
      TOGGLE_OP,
      REMOVE,
      NONE;
   }

   @OnlyIn(Dist.CLIENT)
   class InvitedEntry extends ExtendedList.AbstractListEntry<RealmsPlayerScreen.InvitedEntry> {
      private final PlayerInfo playerInfo;

      public InvitedEntry(PlayerInfo p_i51614_2_) {
         this.playerInfo = p_i51614_2_;
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderInvitedItem(p_230432_1_, this.playerInfo, p_230432_4_, p_230432_3_, p_230432_7_, p_230432_8_);
      }

      private void renderInvitedItem(MatrixStack p_237932_1_, PlayerInfo p_237932_2_, int p_237932_3_, int p_237932_4_, int p_237932_5_, int p_237932_6_) {
         int i;
         if (!p_237932_2_.getAccepted()) {
            i = 10526880;
         } else if (p_237932_2_.getOnline()) {
            i = 8388479;
         } else {
            i = 16777215;
         }

         RealmsPlayerScreen.this.font.draw(p_237932_1_, p_237932_2_.getName(), (float)(RealmsPlayerScreen.this.column1X + 3 + 12), (float)(p_237932_4_ + 1), i);
         if (p_237932_2_.isOperator()) {
            RealmsPlayerScreen.this.drawOpped(p_237932_1_, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, p_237932_4_ + 1, p_237932_5_, p_237932_6_);
         } else {
            RealmsPlayerScreen.this.drawNormal(p_237932_1_, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, p_237932_4_ + 1, p_237932_5_, p_237932_6_);
         }

         RealmsPlayerScreen.this.drawRemoveIcon(p_237932_1_, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 22, p_237932_4_ + 2, p_237932_5_, p_237932_6_);
         RealmsTextureManager.withBoundFace(p_237932_2_.getUuid(), () -> {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(p_237932_1_, RealmsPlayerScreen.this.column1X + 2 + 2, p_237932_4_ + 1, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
            AbstractGui.blit(p_237932_1_, RealmsPlayerScreen.this.column1X + 2 + 2, p_237932_4_ + 1, 8, 8, 40.0F, 8.0F, 8, 8, 64, 64);
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InvitedList extends RealmsObjectSelectionList<RealmsPlayerScreen.InvitedEntry> {
      public InvitedList() {
         super(RealmsPlayerScreen.this.columnWidth + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
      }

      public void addEntry(PlayerInfo p_223870_1_) {
         this.addEntry(RealmsPlayerScreen.this.new InvitedEntry(p_223870_1_));
      }

      public int getRowWidth() {
         return (int)((double)this.width * 1.0D);
      }

      public boolean isFocused() {
         return RealmsPlayerScreen.this.getFocused() == this;
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         if (p_231044_5_ == 0 && p_231044_1_ < (double)this.getScrollbarPosition() && p_231044_3_ >= (double)this.y0 && p_231044_3_ <= (double)this.y1) {
            int i = RealmsPlayerScreen.this.column1X;
            int j = RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth;
            int k = (int)Math.floor(p_231044_3_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int l = k / this.itemHeight;
            if (p_231044_1_ >= (double)i && p_231044_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.selectItem(l);
               this.itemClicked(k, l, p_231044_1_, p_231044_3_, this.width);
            }

            return true;
         } else {
            return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
         }
      }

      public void itemClicked(int p_231401_1_, int p_231401_2_, double p_231401_3_, double p_231401_5_, int p_231401_7_) {
         if (p_231401_2_ >= 0 && p_231401_2_ <= RealmsPlayerScreen.this.serverData.players.size() && RealmsPlayerScreen.this.hoveredUserAction != RealmsPlayerScreen.GuestAction.NONE) {
            if (RealmsPlayerScreen.this.hoveredUserAction == RealmsPlayerScreen.GuestAction.TOGGLE_OP) {
               if (RealmsPlayerScreen.this.serverData.players.get(p_231401_2_).isOperator()) {
                  RealmsPlayerScreen.this.deop(p_231401_2_);
               } else {
                  RealmsPlayerScreen.this.op(p_231401_2_);
               }
            } else if (RealmsPlayerScreen.this.hoveredUserAction == RealmsPlayerScreen.GuestAction.REMOVE) {
               RealmsPlayerScreen.this.uninvite(p_231401_2_);
            }

         }
      }

      public void selectItem(int p_231400_1_) {
         this.setSelectedItem(p_231400_1_);
         if (p_231400_1_ != -1) {
            RealmsNarratorHelper.now(I18n.get("narrator.select", RealmsPlayerScreen.this.serverData.players.get(p_231400_1_).getName()));
         }

         this.selectInviteListItem(p_231400_1_);
      }

      public void selectInviteListItem(int p_223869_1_) {
         RealmsPlayerScreen.this.player = p_223869_1_;
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsPlayerScreen.InvitedEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         RealmsPlayerScreen.this.player = this.children().indexOf(p_241215_1_);
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void renderBackground(MatrixStack p_230433_1_) {
         RealmsPlayerScreen.this.renderBackground(p_230433_1_);
      }

      public int getScrollbarPosition() {
         return RealmsPlayerScreen.this.column1X + this.width - 5;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 13;
      }
   }
}
