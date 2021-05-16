package net.minecraft.client.gui.overlay;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerTabOverlayGui extends AbstractGui {
   private static final Ordering<NetworkPlayerInfo> PLAYER_ORDERING = Ordering.from(new PlayerTabOverlayGui.PlayerComparator());
   private final Minecraft minecraft;
   private final IngameGui gui;
   private ITextComponent footer;
   private ITextComponent header;
   private long visibilityId;
   private boolean visible;

   public PlayerTabOverlayGui(Minecraft p_i45529_1_, IngameGui p_i45529_2_) {
      this.minecraft = p_i45529_1_;
      this.gui = p_i45529_2_;
   }

   public ITextComponent getNameForDisplay(NetworkPlayerInfo p_200262_1_) {
      return p_200262_1_.getTabListDisplayName() != null ? this.decorateName(p_200262_1_, p_200262_1_.getTabListDisplayName().copy()) : this.decorateName(p_200262_1_, ScorePlayerTeam.formatNameForTeam(p_200262_1_.getTeam(), new StringTextComponent(p_200262_1_.getProfile().getName())));
   }

   private ITextComponent decorateName(NetworkPlayerInfo p_238524_1_, IFormattableTextComponent p_238524_2_) {
      return p_238524_1_.getGameMode() == GameType.SPECTATOR ? p_238524_2_.withStyle(TextFormatting.ITALIC) : p_238524_2_;
   }

   public void setVisible(boolean p_175246_1_) {
      if (p_175246_1_ && !this.visible) {
         this.visibilityId = Util.getMillis();
      }

      this.visible = p_175246_1_;
   }

   public void render(MatrixStack p_238523_1_, int p_238523_2_, Scoreboard p_238523_3_, @Nullable ScoreObjective p_238523_4_) {
      ClientPlayNetHandler clientplaynethandler = this.minecraft.player.connection;
      List<NetworkPlayerInfo> list = PLAYER_ORDERING.sortedCopy(clientplaynethandler.getOnlinePlayers());
      int i = 0;
      int j = 0;

      for(NetworkPlayerInfo networkplayerinfo : list) {
         int k = this.minecraft.font.width(this.getNameForDisplay(networkplayerinfo));
         i = Math.max(i, k);
         if (p_238523_4_ != null && p_238523_4_.getRenderType() != ScoreCriteria.RenderType.HEARTS) {
            k = this.minecraft.font.width(" " + p_238523_3_.getOrCreatePlayerScore(networkplayerinfo.getProfile().getName(), p_238523_4_).getScore());
            j = Math.max(j, k);
         }
      }

      list = list.subList(0, Math.min(list.size(), 80));
      int i4 = list.size();
      int j4 = i4;

      int k4;
      for(k4 = 1; j4 > 20; j4 = (i4 + k4 - 1) / k4) {
         ++k4;
      }

      boolean flag = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int l;
      if (p_238523_4_ != null) {
         if (p_238523_4_.getRenderType() == ScoreCriteria.RenderType.HEARTS) {
            l = 90;
         } else {
            l = j;
         }
      } else {
         l = 0;
      }

      int i1 = Math.min(k4 * ((flag ? 9 : 0) + i + l + 13), p_238523_2_ - 50) / k4;
      int j1 = p_238523_2_ / 2 - (i1 * k4 + (k4 - 1) * 5) / 2;
      int k1 = 10;
      int l1 = i1 * k4 + (k4 - 1) * 5;
      List<IReorderingProcessor> list1 = null;
      if (this.header != null) {
         list1 = this.minecraft.font.split(this.header, p_238523_2_ - 50);

         for(IReorderingProcessor ireorderingprocessor : list1) {
            l1 = Math.max(l1, this.minecraft.font.width(ireorderingprocessor));
         }
      }

      List<IReorderingProcessor> list2 = null;
      if (this.footer != null) {
         list2 = this.minecraft.font.split(this.footer, p_238523_2_ - 50);

         for(IReorderingProcessor ireorderingprocessor1 : list2) {
            l1 = Math.max(l1, this.minecraft.font.width(ireorderingprocessor1));
         }
      }

      if (list1 != null) {
         fill(p_238523_1_, p_238523_2_ / 2 - l1 / 2 - 1, k1 - 1, p_238523_2_ / 2 + l1 / 2 + 1, k1 + list1.size() * 9, Integer.MIN_VALUE);

         for(IReorderingProcessor ireorderingprocessor2 : list1) {
            int i2 = this.minecraft.font.width(ireorderingprocessor2);
            this.minecraft.font.drawShadow(p_238523_1_, ireorderingprocessor2, (float)(p_238523_2_ / 2 - i2 / 2), (float)k1, -1);
            k1 += 9;
         }

         ++k1;
      }

      fill(p_238523_1_, p_238523_2_ / 2 - l1 / 2 - 1, k1 - 1, p_238523_2_ / 2 + l1 / 2 + 1, k1 + j4 * 9, Integer.MIN_VALUE);
      int l4 = this.minecraft.options.getBackgroundColor(553648127);

      for(int i5 = 0; i5 < i4; ++i5) {
         int j5 = i5 / j4;
         int j2 = i5 % j4;
         int k2 = j1 + j5 * i1 + j5 * 5;
         int l2 = k1 + j2 * 9;
         fill(p_238523_1_, k2, l2, k2 + i1, l2 + 8, l4);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableAlphaTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         if (i5 < list.size()) {
            NetworkPlayerInfo networkplayerinfo1 = list.get(i5);
            GameProfile gameprofile = networkplayerinfo1.getProfile();
            if (flag) {
               PlayerEntity playerentity = this.minecraft.level.getPlayerByUUID(gameprofile.getId());
               boolean flag1 = playerentity != null && playerentity.isModelPartShown(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameprofile.getName()) || "Grumm".equals(gameprofile.getName()));
               this.minecraft.getTextureManager().bind(networkplayerinfo1.getSkinLocation());
               int i3 = 8 + (flag1 ? 8 : 0);
               int j3 = 8 * (flag1 ? -1 : 1);
               AbstractGui.blit(p_238523_1_, k2, l2, 8, 8, 8.0F, (float)i3, 8, j3, 64, 64);
               if (playerentity != null && playerentity.isModelPartShown(PlayerModelPart.HAT)) {
                  int k3 = 8 + (flag1 ? 8 : 0);
                  int l3 = 8 * (flag1 ? -1 : 1);
                  AbstractGui.blit(p_238523_1_, k2, l2, 8, 8, 40.0F, (float)k3, 8, l3, 64, 64);
               }

               k2 += 9;
            }

            this.minecraft.font.drawShadow(p_238523_1_, this.getNameForDisplay(networkplayerinfo1), (float)k2, (float)l2, networkplayerinfo1.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if (p_238523_4_ != null && networkplayerinfo1.getGameMode() != GameType.SPECTATOR) {
               int l5 = k2 + i + 1;
               int i6 = l5 + l;
               if (i6 - l5 > 5) {
                  this.renderTablistScore(p_238523_4_, l2, gameprofile.getName(), l5, i6, networkplayerinfo1, p_238523_1_);
               }
            }

            this.renderPingIcon(p_238523_1_, i1, k2 - (flag ? 9 : 0), l2, networkplayerinfo1);
         }
      }

      if (list2 != null) {
         k1 = k1 + j4 * 9 + 1;
         fill(p_238523_1_, p_238523_2_ / 2 - l1 / 2 - 1, k1 - 1, p_238523_2_ / 2 + l1 / 2 + 1, k1 + list2.size() * 9, Integer.MIN_VALUE);

         for(IReorderingProcessor ireorderingprocessor3 : list2) {
            int k5 = this.minecraft.font.width(ireorderingprocessor3);
            this.minecraft.font.drawShadow(p_238523_1_, ireorderingprocessor3, (float)(p_238523_2_ / 2 - k5 / 2), (float)k1, -1);
            k1 += 9;
         }
      }

   }

   protected void renderPingIcon(MatrixStack p_238522_1_, int p_238522_2_, int p_238522_3_, int p_238522_4_, NetworkPlayerInfo p_238522_5_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
      int i = 0;
      int j;
      if (p_238522_5_.getLatency() < 0) {
         j = 5;
      } else if (p_238522_5_.getLatency() < 150) {
         j = 0;
      } else if (p_238522_5_.getLatency() < 300) {
         j = 1;
      } else if (p_238522_5_.getLatency() < 600) {
         j = 2;
      } else if (p_238522_5_.getLatency() < 1000) {
         j = 3;
      } else {
         j = 4;
      }

      this.setBlitOffset(this.getBlitOffset() + 100);
      this.blit(p_238522_1_, p_238522_3_ + p_238522_2_ - 11, p_238522_4_, 0, 176 + j * 8, 10, 8);
      this.setBlitOffset(this.getBlitOffset() - 100);
   }

   private void renderTablistScore(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_, MatrixStack p_175247_7_) {
      int i = p_175247_1_.getScoreboard().getOrCreatePlayerScore(p_175247_3_, p_175247_1_).getScore();
      if (p_175247_1_.getRenderType() == ScoreCriteria.RenderType.HEARTS) {
         this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
         long j = Util.getMillis();
         if (this.visibilityId == p_175247_6_.getRenderVisibilityId()) {
            if (i < p_175247_6_.getLastHealth()) {
               p_175247_6_.setLastHealthTime(j);
               p_175247_6_.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 20));
            } else if (i > p_175247_6_.getLastHealth()) {
               p_175247_6_.setLastHealthTime(j);
               p_175247_6_.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 10));
            }
         }

         if (j - p_175247_6_.getLastHealthTime() > 1000L || this.visibilityId != p_175247_6_.getRenderVisibilityId()) {
            p_175247_6_.setLastHealth(i);
            p_175247_6_.setDisplayHealth(i);
            p_175247_6_.setLastHealthTime(j);
         }

         p_175247_6_.setRenderVisibilityId(this.visibilityId);
         p_175247_6_.setLastHealth(i);
         int k = MathHelper.ceil((float)Math.max(i, p_175247_6_.getDisplayHealth()) / 2.0F);
         int l = Math.max(MathHelper.ceil((float)(i / 2)), Math.max(MathHelper.ceil((float)(p_175247_6_.getDisplayHealth() / 2)), 10));
         boolean flag = p_175247_6_.getHealthBlinkTime() > (long)this.gui.getGuiTicks() && (p_175247_6_.getHealthBlinkTime() - (long)this.gui.getGuiTicks()) / 3L % 2L == 1L;
         if (k > 0) {
            int i1 = MathHelper.floor(Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)l, 9.0F));
            if (i1 > 3) {
               for(int j1 = k; j1 < l; ++j1) {
                  this.blit(p_175247_7_, p_175247_4_ + j1 * i1, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
               }

               for(int l1 = 0; l1 < k; ++l1) {
                  this.blit(p_175247_7_, p_175247_4_ + l1 * i1, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                  if (flag) {
                     if (l1 * 2 + 1 < p_175247_6_.getDisplayHealth()) {
                        this.blit(p_175247_7_, p_175247_4_ + l1 * i1, p_175247_2_, 70, 0, 9, 9);
                     }

                     if (l1 * 2 + 1 == p_175247_6_.getDisplayHealth()) {
                        this.blit(p_175247_7_, p_175247_4_ + l1 * i1, p_175247_2_, 79, 0, 9, 9);
                     }
                  }

                  if (l1 * 2 + 1 < i) {
                     this.blit(p_175247_7_, p_175247_4_ + l1 * i1, p_175247_2_, l1 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (l1 * 2 + 1 == i) {
                     this.blit(p_175247_7_, p_175247_4_ + l1 * i1, p_175247_2_, l1 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float f = MathHelper.clamp((float)i / 20.0F, 0.0F, 1.0F);
               int k1 = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
               String s = "" + (float)i / 2.0F;
               if (p_175247_5_ - this.minecraft.font.width(s + "hp") >= p_175247_4_) {
                  s = s + "hp";
               }

               this.minecraft.font.drawShadow(p_175247_7_, s, (float)((p_175247_5_ + p_175247_4_) / 2 - this.minecraft.font.width(s) / 2), (float)p_175247_2_, k1);
            }
         }
      } else {
         String s1 = TextFormatting.YELLOW + "" + i;
         this.minecraft.font.drawShadow(p_175247_7_, s1, (float)(p_175247_5_ - this.minecraft.font.width(s1)), (float)p_175247_2_, 16777215);
      }

   }

   public void setFooter(@Nullable ITextComponent p_175248_1_) {
      this.footer = p_175248_1_;
   }

   public void setHeader(@Nullable ITextComponent p_175244_1_) {
      this.header = p_175244_1_;
   }

   public void reset() {
      this.header = null;
      this.footer = null;
   }

   @OnlyIn(Dist.CLIENT)
   static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      private PlayerComparator() {
      }

      public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
         ScorePlayerTeam scoreplayerteam = p_compare_1_.getTeam();
         ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getTeam();
         return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameMode() != GameType.SPECTATOR, p_compare_2_.getGameMode() != GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getName() : "").compare(p_compare_1_.getProfile().getName(), p_compare_2_.getProfile().getName(), String::compareToIgnoreCase).result();
      }
   }
}
