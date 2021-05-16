package net.minecraft.client.tutorial;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tutorial {
   private final Minecraft minecraft;
   @Nullable
   private ITutorialStep instance;
   private List<Tutorial.ToastTimeInfo> timedToasts = Lists.newArrayList();

   public Tutorial(Minecraft p_i47578_1_) {
      this.minecraft = p_i47578_1_;
   }

   public void onInput(MovementInput p_193293_1_) {
      if (this.instance != null) {
         this.instance.onInput(p_193293_1_);
      }

   }

   public void onMouse(double p_195872_1_, double p_195872_3_) {
      if (this.instance != null) {
         this.instance.onMouse(p_195872_1_, p_195872_3_);
      }

   }

   public void onLookAt(@Nullable ClientWorld p_193297_1_, @Nullable RayTraceResult p_193297_2_) {
      if (this.instance != null && p_193297_2_ != null && p_193297_1_ != null) {
         this.instance.onLookAt(p_193297_1_, p_193297_2_);
      }

   }

   public void onDestroyBlock(ClientWorld p_193294_1_, BlockPos p_193294_2_, BlockState p_193294_3_, float p_193294_4_) {
      if (this.instance != null) {
         this.instance.onDestroyBlock(p_193294_1_, p_193294_2_, p_193294_3_, p_193294_4_);
      }

   }

   public void onOpenInventory() {
      if (this.instance != null) {
         this.instance.onOpenInventory();
      }

   }

   public void onGetItem(ItemStack p_193301_1_) {
      if (this.instance != null) {
         this.instance.onGetItem(p_193301_1_);
      }

   }

   public void stop() {
      if (this.instance != null) {
         this.instance.clear();
         this.instance = null;
      }
   }

   public void start() {
      if (this.instance != null) {
         this.stop();
      }

      this.instance = this.minecraft.options.tutorialStep.create(this);
   }

   public void addTimedToast(TutorialToast p_244698_1_, int p_244698_2_) {
      this.timedToasts.add(new Tutorial.ToastTimeInfo(p_244698_1_, p_244698_2_));
      this.minecraft.getToasts().addToast(p_244698_1_);
   }

   public void removeTimedToast(TutorialToast p_244697_1_) {
      this.timedToasts.removeIf((p_244699_1_) -> {
         return p_244699_1_.toast == p_244697_1_;
      });
      p_244697_1_.hide();
   }

   public void tick() {
      this.timedToasts.removeIf((p_244700_0_) -> {
         return p_244700_0_.updateProgress();
      });
      if (this.instance != null) {
         if (this.minecraft.level != null) {
            this.instance.tick();
         } else {
            this.stop();
         }
      } else if (this.minecraft.level != null) {
         this.start();
      }

   }

   public void setStep(TutorialSteps p_193292_1_) {
      this.minecraft.options.tutorialStep = p_193292_1_;
      this.minecraft.options.save();
      if (this.instance != null) {
         this.instance.clear();
         this.instance = p_193292_1_.create(this);
      }

   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public GameType getGameMode() {
      return this.minecraft.gameMode == null ? GameType.NOT_SET : this.minecraft.gameMode.getPlayerMode();
   }

   public static ITextComponent key(String p_193291_0_) {
      return (new KeybindTextComponent("key." + p_193291_0_)).withStyle(TextFormatting.BOLD);
   }

   @OnlyIn(Dist.CLIENT)
   static final class ToastTimeInfo {
      private final TutorialToast toast;
      private final int durationTicks;
      private int progress;

      private ToastTimeInfo(TutorialToast p_i244518_1_, int p_i244518_2_) {
         this.toast = p_i244518_1_;
         this.durationTicks = p_i244518_2_;
      }

      private boolean updateProgress() {
         this.toast.updateProgress(Math.min((float)(++this.progress) / (float)this.durationTicks, 1.0F));
         if (this.progress > this.durationTicks) {
            this.toast.hide();
            return true;
         } else {
            return false;
         }
      }
   }
}
