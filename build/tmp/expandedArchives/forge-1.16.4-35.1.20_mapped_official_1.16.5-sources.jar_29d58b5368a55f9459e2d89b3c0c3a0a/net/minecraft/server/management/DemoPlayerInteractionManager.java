package net.minecraft.server.management;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DemoPlayerInteractionManager extends PlayerInteractionManager {
   private boolean displayedIntro;
   private boolean demoHasEnded;
   private int demoEndedReminder;
   private int gameModeTicks;

   public DemoPlayerInteractionManager(ServerWorld p_i50709_1_) {
      super(p_i50709_1_);
   }

   public void tick() {
      super.tick();
      ++this.gameModeTicks;
      long i = this.level.getGameTime();
      long j = i / 24000L + 1L;
      if (!this.displayedIntro && this.gameModeTicks > 20) {
         this.displayedIntro = true;
         this.player.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.DEMO_EVENT, 0.0F));
      }

      this.demoHasEnded = i > 120500L;
      if (this.demoHasEnded) {
         ++this.demoEndedReminder;
      }

      if (i % 24000L == 500L) {
         if (j <= 6L) {
            if (j == 6L) {
               this.player.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.DEMO_EVENT, 104.0F));
            } else {
               this.player.sendMessage(new TranslationTextComponent("demo.day." + j), Util.NIL_UUID);
            }
         }
      } else if (j == 1L) {
         if (i == 100L) {
            this.player.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.DEMO_EVENT, 101.0F));
         } else if (i == 175L) {
            this.player.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.DEMO_EVENT, 102.0F));
         } else if (i == 250L) {
            this.player.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.DEMO_EVENT, 103.0F));
         }
      } else if (j == 5L && i % 24000L == 22000L) {
         this.player.sendMessage(new TranslationTextComponent("demo.day.warning"), Util.NIL_UUID);
      }

   }

   private void outputDemoReminder() {
      if (this.demoEndedReminder > 100) {
         this.player.sendMessage(new TranslationTextComponent("demo.reminder"), Util.NIL_UUID);
         this.demoEndedReminder = 0;
      }

   }

   public void handleBlockBreakAction(BlockPos p_225416_1_, CPlayerDiggingPacket.Action p_225416_2_, Direction p_225416_3_, int p_225416_4_) {
      if (this.demoHasEnded) {
         this.outputDemoReminder();
      } else {
         super.handleBlockBreakAction(p_225416_1_, p_225416_2_, p_225416_3_, p_225416_4_);
      }
   }

   public ActionResultType useItem(ServerPlayerEntity p_187250_1_, World p_187250_2_, ItemStack p_187250_3_, Hand p_187250_4_) {
      if (this.demoHasEnded) {
         this.outputDemoReminder();
         return ActionResultType.PASS;
      } else {
         return super.useItem(p_187250_1_, p_187250_2_, p_187250_3_, p_187250_4_);
      }
   }

   public ActionResultType useItemOn(ServerPlayerEntity p_219441_1_, World p_219441_2_, ItemStack p_219441_3_, Hand p_219441_4_, BlockRayTraceResult p_219441_5_) {
      if (this.demoHasEnded) {
         this.outputDemoReminder();
         return ActionResultType.PASS;
      } else {
         return super.useItemOn(p_219441_1_, p_219441_2_, p_219441_3_, p_219441_4_, p_219441_5_);
      }
   }
}
