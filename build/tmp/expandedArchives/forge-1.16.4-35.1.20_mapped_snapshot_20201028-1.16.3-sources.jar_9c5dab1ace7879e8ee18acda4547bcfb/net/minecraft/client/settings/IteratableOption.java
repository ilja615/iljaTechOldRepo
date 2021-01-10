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
   private final BiFunction<GameSettings, IteratableOption, ITextComponent> getter;

   public IteratableOption(String translationKeyIn, BiConsumer<GameSettings, Integer> setterIn, BiFunction<GameSettings, IteratableOption, ITextComponent> getterIn) {
      super(translationKeyIn);
      this.setter = setterIn;
      this.getter = getterIn;
   }

   public void setValueIndex(GameSettings options, int valueIn) {
      this.setter.accept(options, valueIn);
      options.saveOptions();
   }

   public Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn) {
      return new OptionButton(xIn, yIn, widthIn, 20, this, this.getName(options), (button) -> {
         this.setValueIndex(options, 1);
         button.setMessage(this.getName(options));
      });
   }

   public ITextComponent getName(GameSettings settings) {
      return this.getter.apply(settings, this);
   }
}
