package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IteratableOption extends AbstractOption {
   private final BiConsumer<GameSettings, Integer> setter;
   private final BiFunction<GameSettings, IteratableOption, ITextComponent> toString;

   public IteratableOption(String p_i51164_1_, BiConsumer<GameSettings, Integer> p_i51164_2_, BiFunction<GameSettings, IteratableOption, ITextComponent> p_i51164_3_) {
      super(p_i51164_1_);
      this.setter = p_i51164_2_;
      this.toString = p_i51164_3_;
   }

   public void toggle(GameSettings p_216722_1_, int p_216722_2_) {
      this.setter.accept(p_216722_1_, p_216722_2_);
      p_216722_1_.save();
   }

   public Widget createButton(GameSettings p_216586_1_, int p_216586_2_, int p_216586_3_, int p_216586_4_) {
      return new OptionButton(p_216586_2_, p_216586_3_, p_216586_4_, 20, this, this.getMessage(p_216586_1_), (p_216721_2_) -> {
         this.toggle(p_216586_1_, 1);
         p_216721_2_.setMessage(this.getMessage(p_216586_1_));
      });
   }

   public ITextComponent getMessage(GameSettings p_238157_1_) {
      return this.toString.apply(p_238157_1_, this);
   }
}
