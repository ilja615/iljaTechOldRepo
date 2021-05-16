package net.minecraft.client.gui.widget.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsRowList extends AbstractOptionList<OptionsRowList.Row> {
   public OptionsRowList(Minecraft p_i51130_1_, int p_i51130_2_, int p_i51130_3_, int p_i51130_4_, int p_i51130_5_, int p_i51130_6_) {
      super(p_i51130_1_, p_i51130_2_, p_i51130_3_, p_i51130_4_, p_i51130_5_, p_i51130_6_);
      this.centerListVertically = false;
   }

   public int addBig(AbstractOption p_214333_1_) {
      return this.addEntry(OptionsRowList.Row.big(this.minecraft.options, this.width, p_214333_1_));
   }

   public void addSmall(AbstractOption p_214334_1_, @Nullable AbstractOption p_214334_2_) {
      this.addEntry(OptionsRowList.Row.small(this.minecraft.options, this.width, p_214334_1_, p_214334_2_));
   }

   public void addSmall(AbstractOption[] p_214335_1_) {
      for(int i = 0; i < p_214335_1_.length; i += 2) {
         this.addSmall(p_214335_1_[i], i < p_214335_1_.length - 1 ? p_214335_1_[i + 1] : null);
      }

   }

   public int getRowWidth() {
      return 400;
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Nullable
   public Widget findOption(AbstractOption p_243271_1_) {
      for(OptionsRowList.Row optionsrowlist$row : this.children()) {
         for(Widget widget : optionsrowlist$row.children) {
            if (widget instanceof OptionButton && ((OptionButton)widget).getOption() == p_243271_1_) {
               return widget;
            }
         }
      }

      return null;
   }

   public Optional<Widget> getMouseOver(double p_238518_1_, double p_238518_3_) {
      for(OptionsRowList.Row optionsrowlist$row : this.children()) {
         for(Widget widget : optionsrowlist$row.children) {
            if (widget.isMouseOver(p_238518_1_, p_238518_3_)) {
               return Optional.of(widget);
            }
         }
      }

      return Optional.empty();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Row extends AbstractOptionList.Entry<OptionsRowList.Row> {
      private final List<Widget> children;

      private Row(List<Widget> p_i50481_1_) {
         this.children = p_i50481_1_;
      }

      public static OptionsRowList.Row big(GameSettings p_214384_0_, int p_214384_1_, AbstractOption p_214384_2_) {
         return new OptionsRowList.Row(ImmutableList.of(p_214384_2_.createButton(p_214384_0_, p_214384_1_ / 2 - 155, 0, 310)));
      }

      public static OptionsRowList.Row small(GameSettings p_214382_0_, int p_214382_1_, AbstractOption p_214382_2_, @Nullable AbstractOption p_214382_3_) {
         Widget widget = p_214382_2_.createButton(p_214382_0_, p_214382_1_ / 2 - 155, 0, 150);
         return p_214382_3_ == null ? new OptionsRowList.Row(ImmutableList.of(widget)) : new OptionsRowList.Row(ImmutableList.of(widget, p_214382_3_.createButton(p_214382_0_, p_214382_1_ / 2 - 155 + 160, 0, 150)));
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.children.forEach((p_238519_5_) -> {
            p_238519_5_.y = p_230432_3_;
            p_238519_5_.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
         });
      }

      public List<? extends IGuiEventListener> children() {
         return this.children;
      }
   }
}
