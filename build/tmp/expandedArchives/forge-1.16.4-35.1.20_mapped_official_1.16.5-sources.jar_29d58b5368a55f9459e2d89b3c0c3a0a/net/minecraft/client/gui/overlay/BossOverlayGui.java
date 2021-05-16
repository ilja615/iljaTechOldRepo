package net.minecraft.client.gui.overlay;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BossOverlayGui extends AbstractGui {
   private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
   private final Minecraft minecraft;
   private final Map<UUID, ClientBossInfo> events = Maps.newLinkedHashMap();

   public BossOverlayGui(Minecraft p_i46606_1_) {
      this.minecraft = p_i46606_1_;
   }

   public void render(MatrixStack p_238484_1_) {
      if (!this.events.isEmpty()) {
         int i = this.minecraft.getWindow().getGuiScaledWidth();
         int j = 12;

         for(ClientBossInfo clientbossinfo : this.events.values()) {
            int k = i / 2 - 91;
            net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event =
               net.minecraftforge.client.ForgeHooksClient.bossBarRenderPre(p_238484_1_, this.minecraft.getWindow(), clientbossinfo, k, j, 10 + this.minecraft.font.lineHeight);
            if (!event.isCanceled()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(GUI_BARS_LOCATION);
            this.drawBar(p_238484_1_, k, j, clientbossinfo);
            ITextComponent itextcomponent = clientbossinfo.getName();
            int l = this.minecraft.font.width(itextcomponent);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            this.minecraft.font.drawShadow(p_238484_1_, itextcomponent, (float)i1, (float)j1, 16777215);
            }
            j += event.getIncrement();
            net.minecraftforge.client.ForgeHooksClient.bossBarRenderPost(p_238484_1_, this.minecraft.getWindow());
            if (j >= this.minecraft.getWindow().getGuiScaledHeight() / 3) {
               break;
            }
         }

      }
   }

   private void drawBar(MatrixStack p_238485_1_, int p_238485_2_, int p_238485_3_, BossInfo p_238485_4_) {
      this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, p_238485_4_.getColor().ordinal() * 5 * 2, 182, 5);
      if (p_238485_4_.getOverlay() != BossInfo.Overlay.PROGRESS) {
         this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, 80 + (p_238485_4_.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int i = (int)(p_238485_4_.getPercent() * 183.0F);
      if (i > 0) {
         this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, p_238485_4_.getColor().ordinal() * 5 * 2 + 5, i, 5);
         if (p_238485_4_.getOverlay() != BossInfo.Overlay.PROGRESS) {
            this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, 80 + (p_238485_4_.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
         }
      }

   }

   public void update(SUpdateBossInfoPacket p_184055_1_) {
      if (p_184055_1_.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
         this.events.put(p_184055_1_.getId(), new ClientBossInfo(p_184055_1_));
      } else if (p_184055_1_.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
         this.events.remove(p_184055_1_.getId());
      } else {
         this.events.get(p_184055_1_.getId()).update(p_184055_1_);
      }

   }

   public void reset() {
      this.events.clear();
   }

   public boolean shouldPlayMusic() {
      if (!this.events.isEmpty()) {
         for(BossInfo bossinfo : this.events.values()) {
            if (bossinfo.shouldPlayBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenScreen() {
      if (!this.events.isEmpty()) {
         for(BossInfo bossinfo : this.events.values()) {
            if (bossinfo.shouldDarkenScreen()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateWorldFog() {
      if (!this.events.isEmpty()) {
         for(BossInfo bossinfo : this.events.values()) {
            if (bossinfo.shouldCreateWorldFog()) {
               return true;
            }
         }
      }

      return false;
   }
}
