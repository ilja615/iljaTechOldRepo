package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BooleanOption extends AbstractOption {
   private final Predicate<GameSettings> getter;
   private final BiConsumer<GameSettings, Boolean> setter;
   @Nullable
   private final ITextComponent tooltipText;

   public BooleanOption(String p_i51167_1_, Predicate<GameSettings> p_i51167_2_, BiConsumer<GameSettings, Boolean> p_i51167_3_) {
      this(p_i51167_1_, (ITextComponent)null, p_i51167_2_, p_i51167_3_);
   }

   public BooleanOption(String p_i244779_1_, @Nullable ITextComponent p_i244779_2_, Predicate<GameSettings> p_i244779_3_, BiConsumer<GameSettings, Boolean> p_i244779_4_) {
      super(p_i244779_1_);
      this.getter = p_i244779_3_;
      this.setter = p_i244779_4_;
      this.tooltipText = p_i244779_2_;
   }

   public void set(GameSettings p_216742_1_, String p_216742_2_) {
      this.set(p_216742_1_, "true".equals(p_216742_2_));
   }

   public void toggle(GameSettings p_216740_1_) {
      this.set(p_216740_1_, !this.get(p_216740_1_));
      p_216740_1_.save();
   }

   private void set(GameSettings p_216744_1_, boolean p_216744_2_) {
      this.setter.accept(p_216744_1_, p_216744_2_);
   }

   public boolean get(GameSettings p_216741_1_) {
      return this.getter.test(p_216741_1_);
   }

   public Widget createButton(GameSettings p_216586_1_, int p_216586_2_, int p_216586_3_, int p_216586_4_) {
      if (this.tooltipText != null) {
         this.setTooltip(Minecraft.getInstance().font.split(this.tooltipText, 200));
      }

      return new OptionButton(p_216586_2_, p_216586_3_, p_216586_4_, 20, this, this.getMessage(p_216586_1_), (p_216745_2_) -> {
         this.toggle(p_216586_1_);
         p_216745_2_.setMessage(this.getMessage(p_216586_1_));
      });
   }

   public ITextComponent getMessage(GameSettings p_238152_1_) {
      return DialogTexts.optionStatus(this.getCaption(), this.get(p_238152_1_));
   }
}
