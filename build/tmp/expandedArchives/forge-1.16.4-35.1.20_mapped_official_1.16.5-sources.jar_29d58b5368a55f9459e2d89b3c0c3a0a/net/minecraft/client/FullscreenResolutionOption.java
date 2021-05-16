package net.minecraft.client;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FullscreenResolutionOption extends SliderPercentageOption {
   public FullscreenResolutionOption(MainWindow p_i51744_1_) {
      this(p_i51744_1_, p_i51744_1_.findBestMonitor());
   }

   private FullscreenResolutionOption(MainWindow p_i51745_1_, @Nullable Monitor p_i51745_2_) {
      super("options.fullscreen.resolution", -1.0D, p_i51745_2_ != null ? (double)(p_i51745_2_.getModeCount() - 1) : -1.0D, 1.0F, (p_225306_2_) -> {
         if (p_i51745_2_ == null) {
            return -1.0D;
         } else {
            Optional<VideoMode> optional = p_i51745_1_.getPreferredFullscreenVideoMode();
            return optional.map((p_225304_1_) -> {
               return (double)p_i51745_2_.getVideoModeIndex(p_225304_1_);
            }).orElse(-1.0D);
         }
      }, (p_225303_2_, p_225303_3_) -> {
         if (p_i51745_2_ != null) {
            if (p_225303_3_ == -1.0D) {
               p_i51745_1_.setPreferredFullscreenVideoMode(Optional.empty());
            } else {
               p_i51745_1_.setPreferredFullscreenVideoMode(Optional.of(p_i51745_2_.getMode(p_225303_3_.intValue())));
            }

         }
      }, (p_225305_1_, p_225305_2_) -> {
         if (p_i51745_2_ == null) {
            return new TranslationTextComponent("options.fullscreen.unavailable");
         } else {
            double d0 = p_225305_2_.get(p_225305_1_);
            return d0 == -1.0D ? p_225305_2_.genericValueLabel(new TranslationTextComponent("options.fullscreen.current")) : p_225305_2_.genericValueLabel(new StringTextComponent(p_i51745_2_.getMode((int)d0).toString()));
         }
      });
   }
}
