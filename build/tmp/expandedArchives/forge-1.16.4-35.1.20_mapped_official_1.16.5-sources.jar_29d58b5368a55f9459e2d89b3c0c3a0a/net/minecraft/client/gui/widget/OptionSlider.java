package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionSlider extends GameSettingsSlider implements IBidiTooltip {
   private final SliderPercentageOption option;

   public OptionSlider(GameSettings p_i51129_1_, int p_i51129_2_, int p_i51129_3_, int p_i51129_4_, int p_i51129_5_, SliderPercentageOption p_i51129_6_) {
      super(p_i51129_1_, p_i51129_2_, p_i51129_3_, p_i51129_4_, p_i51129_5_, (double)((float)p_i51129_6_.toPct(p_i51129_6_.get(p_i51129_1_))));
      this.option = p_i51129_6_;
      this.updateMessage();
   }

   protected void applyValue() {
      this.option.set(this.options, this.option.toValue(this.value));
      this.options.save();
   }

   protected void updateMessage() {
      this.setMessage(this.option.getMessage(this.options));
   }

   public Optional<List<IReorderingProcessor>> getTooltip() {
      return this.option.getTooltip();
   }
}
