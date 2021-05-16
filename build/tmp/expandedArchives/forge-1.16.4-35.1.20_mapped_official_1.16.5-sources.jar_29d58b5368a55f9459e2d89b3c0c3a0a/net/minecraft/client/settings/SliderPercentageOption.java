package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SliderPercentageOption extends AbstractOption {
   protected final float steps;
   protected final double minValue;
   protected double maxValue;
   private final Function<GameSettings, Double> getter;
   private final BiConsumer<GameSettings, Double> setter;
   private final BiFunction<GameSettings, SliderPercentageOption, ITextComponent> toString;

   public SliderPercentageOption(String p_i51155_1_, double p_i51155_2_, double p_i51155_4_, float p_i51155_6_, Function<GameSettings, Double> p_i51155_7_, BiConsumer<GameSettings, Double> p_i51155_8_, BiFunction<GameSettings, SliderPercentageOption, ITextComponent> p_i51155_9_) {
      super(p_i51155_1_);
      this.minValue = p_i51155_2_;
      this.maxValue = p_i51155_4_;
      this.steps = p_i51155_6_;
      this.getter = p_i51155_7_;
      this.setter = p_i51155_8_;
      this.toString = p_i51155_9_;
   }

   public Widget createButton(GameSettings p_216586_1_, int p_216586_2_, int p_216586_3_, int p_216586_4_) {
      return new OptionSlider(p_216586_1_, p_216586_2_, p_216586_3_, p_216586_4_, 20, this);
   }

   public double toPct(double p_216726_1_) {
      return MathHelper.clamp((this.clamp(p_216726_1_) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double p_216725_1_) {
      return this.clamp(MathHelper.lerp(MathHelper.clamp(p_216725_1_, 0.0D, 1.0D), this.minValue, this.maxValue));
   }

   private double clamp(double p_216731_1_) {
      if (this.steps > 0.0F) {
         p_216731_1_ = (double)(this.steps * (float)Math.round(p_216731_1_ / (double)this.steps));
      }

      return MathHelper.clamp(p_216731_1_, this.minValue, this.maxValue);
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(float p_216728_1_) {
      this.maxValue = (double)p_216728_1_;
   }

   public void set(GameSettings p_216727_1_, double p_216727_2_) {
      this.setter.accept(p_216727_1_, p_216727_2_);
   }

   public double get(GameSettings p_216729_1_) {
      return this.getter.apply(p_216729_1_);
   }

   public ITextComponent getMessage(GameSettings p_238334_1_) {
      return this.toString.apply(p_238334_1_, this);
   }
}
