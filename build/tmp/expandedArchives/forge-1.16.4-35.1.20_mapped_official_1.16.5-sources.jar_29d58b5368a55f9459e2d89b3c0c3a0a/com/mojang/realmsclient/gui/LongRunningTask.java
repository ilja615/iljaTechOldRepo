package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.IErrorConsumer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class LongRunningTask implements IErrorConsumer, Runnable {
   public static final Logger LOGGER = LogManager.getLogger();
   protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

   protected static void pause(int p_238125_0_) {
      try {
         Thread.sleep((long)(p_238125_0_ * 1000));
      } catch (InterruptedException interruptedexception) {
         LOGGER.error("", (Throwable)interruptedexception);
      }

   }

   public static void setScreen(Screen p_238127_0_) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.execute(() -> {
         minecraft.setScreen(p_238127_0_);
      });
   }

   public void setScreen(RealmsLongRunningMcoTaskScreen p_224987_1_) {
      this.longRunningMcoTaskScreen = p_224987_1_;
   }

   public void error(ITextComponent p_230434_1_) {
      this.longRunningMcoTaskScreen.error(p_230434_1_);
   }

   public void setTitle(ITextComponent p_224989_1_) {
      this.longRunningMcoTaskScreen.setTitle(p_224989_1_);
   }

   public boolean aborted() {
      return this.longRunningMcoTaskScreen.aborted();
   }

   public void tick() {
   }

   public void init() {
   }

   public void abortTask() {
   }
}
