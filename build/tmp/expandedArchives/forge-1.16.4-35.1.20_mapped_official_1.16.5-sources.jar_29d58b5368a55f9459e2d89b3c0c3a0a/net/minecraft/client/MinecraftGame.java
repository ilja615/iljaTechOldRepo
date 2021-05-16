package net.minecraft.client;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecraftGame implements RunningGame {
   private final Minecraft minecraft;
   @Nullable
   private final Launcher launcher;
   private SessionEventListener listener = SessionEventListener.NONE;

   public MinecraftGame(Minecraft p_i51163_1_) {
      this.minecraft = p_i51163_1_;
      this.launcher = Bridge.getLauncher();
      if (this.launcher != null) {
         this.launcher.registerGame(this);
      }

   }

   public GameVersion getVersion() {
      return SharedConstants.getCurrentVersion();
   }

   public Language getSelectedLanguage() {
      return this.minecraft.getLanguageManager().getSelected();
   }

   @Nullable
   public GameSession getCurrentSession() {
      ClientWorld clientworld = this.minecraft.level;
      return clientworld == null ? null : new ClientGameSession(clientworld, this.minecraft.player, this.minecraft.player.connection);
   }

   public PerformanceMetrics getPerformanceMetrics() {
      FrameTimer frametimer = this.minecraft.getFrameTimer();
      long i = 2147483647L;
      long j = -2147483648L;
      long k = 0L;

      for(long l : frametimer.getLog()) {
         i = Math.min(i, l);
         j = Math.max(j, l);
         k += l;
      }

      return new MinecraftGame.MinecraftPerformanceMetrics((int)i, (int)j, (int)(k / (long)frametimer.getLog().length), frametimer.getLog().length);
   }

   public void setSessionEventListener(SessionEventListener p_setSessionEventListener_1_) {
      this.listener = p_setSessionEventListener_1_;
   }

   public void onStartGameSession() {
      this.listener.onStartGameSession(this.getCurrentSession());
   }

   public void onLeaveGameSession() {
      this.listener.onLeaveGameSession(this.getCurrentSession());
   }

   @OnlyIn(Dist.CLIENT)
   static class MinecraftPerformanceMetrics implements PerformanceMetrics {
      private final int min;
      private final int max;
      private final int average;
      private final int samples;

      public MinecraftPerformanceMetrics(int p_i51282_1_, int p_i51282_2_, int p_i51282_3_, int p_i51282_4_) {
         this.min = p_i51282_1_;
         this.max = p_i51282_2_;
         this.average = p_i51282_3_;
         this.samples = p_i51282_4_;
      }

      public int getMinTime() {
         return this.min;
      }

      public int getMaxTime() {
         return this.max;
      }

      public int getAverageTime() {
         return this.average;
      }

      public int getSampleCount() {
         return this.samples;
      }
   }
}
