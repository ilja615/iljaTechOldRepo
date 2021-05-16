package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NewChatGui extends AbstractGui {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final List<String> recentChat = Lists.newArrayList();
   private final List<ChatLine<ITextComponent>> allMessages = Lists.newArrayList();
   private final List<ChatLine<IReorderingProcessor>> trimmedMessages = Lists.newArrayList();
   private final Deque<ITextComponent> chatQueue = Queues.newArrayDeque();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;
   private long lastMessage = 0L;

   public NewChatGui(Minecraft p_i1022_1_) {
      this.minecraft = p_i1022_1_;
   }

   public void render(MatrixStack p_238492_1_, int p_238492_2_) {
      if (!this.isChatHidden()) {
         this.processPendingMessages();
         int i = this.getLinesPerPage();
         int j = this.trimmedMessages.size();
         if (j > 0) {
            boolean flag = false;
            if (this.isChatFocused()) {
               flag = true;
            }

            double d0 = this.getScale();
            int k = MathHelper.ceil((double)this.getWidth() / d0);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(2.0F, 8.0F, 0.0F);
            RenderSystem.scaled(d0, d0, 1.0D);
            double d1 = this.minecraft.options.chatOpacity * (double)0.9F + (double)0.1F;
            double d2 = this.minecraft.options.textBackgroundOpacity;
            double d3 = 9.0D * (this.minecraft.options.chatLineSpacing + 1.0D);
            double d4 = -8.0D * (this.minecraft.options.chatLineSpacing + 1.0D) + 4.0D * this.minecraft.options.chatLineSpacing;
            int l = 0;

            for(int i1 = 0; i1 + this.chatScrollbarPos < this.trimmedMessages.size() && i1 < i; ++i1) {
               ChatLine<IReorderingProcessor> chatline = this.trimmedMessages.get(i1 + this.chatScrollbarPos);
               if (chatline != null) {
                  int j1 = p_238492_2_ - chatline.getAddedTime();
                  if (j1 < 200 || flag) {
                     double d5 = flag ? 1.0D : getTimeFactor(j1);
                     int l1 = (int)(255.0D * d5 * d1);
                     int i2 = (int)(255.0D * d5 * d2);
                     ++l;
                     if (l1 > 3) {
                        int j2 = 0;
                        double d6 = (double)(-i1) * d3;
                        p_238492_1_.pushPose();
                        p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                        fill(p_238492_1_, -2, (int)(d6 - d3), 0 + k + 4, (int)d6, i2 << 24);
                        RenderSystem.enableBlend();
                        p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                        this.minecraft.font.drawShadow(p_238492_1_, chatline.getMessage(), 0.0F, (float)((int)(d6 + d4)), 16777215 + (l1 << 24));
                        RenderSystem.disableAlphaTest();
                        RenderSystem.disableBlend();
                        p_238492_1_.popPose();
                     }
                  }
               }
            }

            if (!this.chatQueue.isEmpty()) {
               int k2 = (int)(128.0D * d1);
               int i3 = (int)(255.0D * d2);
               p_238492_1_.pushPose();
               p_238492_1_.translate(0.0D, 0.0D, 50.0D);
               fill(p_238492_1_, -2, 0, k + 4, 9, i3 << 24);
               RenderSystem.enableBlend();
               p_238492_1_.translate(0.0D, 0.0D, 50.0D);
               this.minecraft.font.drawShadow(p_238492_1_, new TranslationTextComponent("chat.queue", this.chatQueue.size()), 0.0F, 1.0F, 16777215 + (k2 << 24));
               p_238492_1_.popPose();
               RenderSystem.disableAlphaTest();
               RenderSystem.disableBlend();
            }

            if (flag) {
               int l2 = 9;
               RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
               int j3 = j * l2 + j;
               int k3 = l * l2 + l;
               int l3 = this.chatScrollbarPos * k3 / j;
               int k1 = k3 * k3 / j3;
               if (j3 != k3) {
                  int i4 = l3 > 0 ? 170 : 96;
                  int j4 = this.newMessageSinceScroll ? 13382451 : 3355562;
                  fill(p_238492_1_, 0, -l3, 2, -l3 - k1, j4 + (i4 << 24));
                  fill(p_238492_1_, 2, -l3, 1, -l3 - k1, 13421772 + (i4 << 24));
               }
            }

            RenderSystem.popMatrix();
         }
      }
   }

   private boolean isChatHidden() {
      return this.minecraft.options.chatVisibility == ChatVisibility.HIDDEN;
   }

   private static double getTimeFactor(int p_212915_0_) {
      double d0 = (double)p_212915_0_ / 200.0D;
      d0 = 1.0D - d0;
      d0 = d0 * 10.0D;
      d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
      return d0 * d0;
   }

   public void clearMessages(boolean p_146231_1_) {
      this.chatQueue.clear();
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if (p_146231_1_) {
         this.recentChat.clear();
      }

   }

   public void addMessage(ITextComponent p_146227_1_) {
      this.addMessage(p_146227_1_, 0);
   }

   private void addMessage(ITextComponent p_146234_1_, int p_146234_2_) {
      this.addMessage(p_146234_1_, p_146234_2_, this.minecraft.gui.getGuiTicks(), false);
      LOGGER.info("[CHAT] {}", (Object)p_146234_1_.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void addMessage(ITextComponent p_238493_1_, int p_238493_2_, int p_238493_3_, boolean p_238493_4_) {
      if (p_238493_2_ != 0) {
         this.removeById(p_238493_2_);
      }

      int i = MathHelper.floor((double)this.getWidth() / this.getScale());
      List<IReorderingProcessor> list = RenderComponentsUtil.wrapComponents(p_238493_1_, i, this.minecraft.font);
      boolean flag = this.isChatFocused();

      for(IReorderingProcessor ireorderingprocessor : list) {
         if (flag && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1.0D);
         }

         this.trimmedMessages.add(0, new ChatLine<>(p_238493_3_, ireorderingprocessor, p_238493_2_));
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
      }

      if (!p_238493_4_) {
         this.allMessages.add(0, new ChatLine<>(p_238493_3_, p_238493_1_, p_238493_2_));

         while(this.allMessages.size() > 100) {
            this.allMessages.remove(this.allMessages.size() - 1);
         }
      }

   }

   public void rescaleChat() {
      this.trimmedMessages.clear();
      this.resetChatScroll();

      for(int i = this.allMessages.size() - 1; i >= 0; --i) {
         ChatLine<ITextComponent> chatline = this.allMessages.get(i);
         this.addMessage(chatline.getMessage(), chatline.getId(), chatline.getAddedTime(), true);
      }

   }

   public List<String> getRecentChat() {
      return this.recentChat;
   }

   public void addRecentChat(String p_146239_1_) {
      if (this.recentChat.isEmpty() || !this.recentChat.get(this.recentChat.size() - 1).equals(p_146239_1_)) {
         this.recentChat.add(p_146239_1_);
      }

   }

   public void resetChatScroll() {
      this.chatScrollbarPos = 0;
      this.newMessageSinceScroll = false;
   }

   public void scrollChat(double p_194813_1_) {
      this.chatScrollbarPos = (int)((double)this.chatScrollbarPos + p_194813_1_);
      int i = this.trimmedMessages.size();
      if (this.chatScrollbarPos > i - this.getLinesPerPage()) {
         this.chatScrollbarPos = i - this.getLinesPerPage();
      }

      if (this.chatScrollbarPos <= 0) {
         this.chatScrollbarPos = 0;
         this.newMessageSinceScroll = false;
      }

   }

   public boolean handleChatQueueClicked(double p_238491_1_, double p_238491_3_) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden() && !this.chatQueue.isEmpty()) {
         double d0 = p_238491_1_ - 2.0D;
         double d1 = (double)this.minecraft.getWindow().getGuiScaledHeight() - p_238491_3_ - 40.0D;
         if (d0 <= (double)MathHelper.floor((double)this.getWidth() / this.getScale()) && d1 < 0.0D && d1 > (double)MathHelper.floor(-9.0D * this.getScale())) {
            this.addMessage(this.chatQueue.remove());
            this.lastMessage = System.currentTimeMillis();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Nullable
   public Style getClickedComponentStyleAt(double p_238494_1_, double p_238494_3_) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
         double d0 = p_238494_1_ - 2.0D;
         double d1 = (double)this.minecraft.getWindow().getGuiScaledHeight() - p_238494_3_ - 40.0D;
         d0 = (double)MathHelper.floor(d0 / this.getScale());
         d1 = (double)MathHelper.floor(d1 / (this.getScale() * (this.minecraft.options.chatLineSpacing + 1.0D)));
         if (!(d0 < 0.0D) && !(d1 < 0.0D)) {
            int i = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
            if (d0 <= (double)MathHelper.floor((double)this.getWidth() / this.getScale()) && d1 < (double)(9 * i + i)) {
               int j = (int)(d1 / 9.0D + (double)this.chatScrollbarPos);
               if (j >= 0 && j < this.trimmedMessages.size()) {
                  ChatLine<IReorderingProcessor> chatline = this.trimmedMessages.get(j);
                  return this.minecraft.font.getSplitter().componentStyleAtWidth(chatline.getMessage(), (int)d0);
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean isChatFocused() {
      return this.minecraft.screen instanceof ChatScreen;
   }

   private void removeById(int p_146242_1_) {
      this.trimmedMessages.removeIf((p_243251_1_) -> {
         return p_243251_1_.getId() == p_146242_1_;
      });
      this.allMessages.removeIf((p_243250_1_) -> {
         return p_243250_1_.getId() == p_146242_1_;
      });
   }

   public int getWidth() {
      return getWidth(this.minecraft.options.chatWidth);
   }

   public int getHeight() {
      return getHeight((this.isChatFocused() ? this.minecraft.options.chatHeightFocused : this.minecraft.options.chatHeightUnfocused) / (this.minecraft.options.chatLineSpacing + 1.0D));
   }

   public double getScale() {
      return this.minecraft.options.chatScale;
   }

   public static int getWidth(double p_194814_0_) {
      int i = 320;
      int j = 40;
      return MathHelper.floor(p_194814_0_ * 280.0D + 40.0D);
   }

   public static int getHeight(double p_194816_0_) {
      int i = 180;
      int j = 20;
      return MathHelper.floor(p_194816_0_ * 160.0D + 20.0D);
   }

   public int getLinesPerPage() {
      return this.getHeight() / 9;
   }

   private long getChatRateMillis() {
      return (long)(this.minecraft.options.chatDelay * 1000.0D);
   }

   private void processPendingMessages() {
      if (!this.chatQueue.isEmpty()) {
         long i = System.currentTimeMillis();
         if (i - this.lastMessage >= this.getChatRateMillis()) {
            this.addMessage(this.chatQueue.remove());
            this.lastMessage = i;
         }

      }
   }

   public void enqueueMessage(ITextComponent p_238495_1_) {
      if (this.minecraft.options.chatDelay <= 0.0D) {
         this.addMessage(p_238495_1_);
      } else {
         long i = System.currentTimeMillis();
         if (i - this.lastMessage >= this.getChatRateMillis()) {
            this.addMessage(p_238495_1_);
            this.lastMessage = i;
         } else {
            this.chatQueue.add(p_238495_1_);
         }
      }

   }
}
