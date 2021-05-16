package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.IReorderingProcessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TextComponent implements IFormattableTextComponent {
   protected final List<ITextComponent> siblings = Lists.newArrayList();
   private IReorderingProcessor visualOrderText = IReorderingProcessor.EMPTY;
   @Nullable
   @OnlyIn(Dist.CLIENT)
   private LanguageMap decomposedWith;
   private Style style = Style.EMPTY;

   public IFormattableTextComponent append(ITextComponent p_230529_1_) {
      this.siblings.add(p_230529_1_);
      return this;
   }

   public String getContents() {
      return "";
   }

   public List<ITextComponent> getSiblings() {
      return this.siblings;
   }

   public IFormattableTextComponent setStyle(Style p_230530_1_) {
      this.style = p_230530_1_;
      return this;
   }

   public Style getStyle() {
      return this.style;
   }

   public abstract TextComponent plainCopy();

   public final IFormattableTextComponent copy() {
      TextComponent textcomponent = this.plainCopy();
      textcomponent.siblings.addAll(this.siblings);
      textcomponent.setStyle(this.style);
      return textcomponent;
   }

   @OnlyIn(Dist.CLIENT)
   public IReorderingProcessor getVisualOrderText() {
      LanguageMap languagemap = LanguageMap.getInstance();
      if (this.decomposedWith != languagemap) {
         this.visualOrderText = languagemap.getVisualOrder(this);
         this.decomposedWith = languagemap;
      }

      return this.visualOrderText;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponent)) {
         return false;
      } else {
         TextComponent textcomponent = (TextComponent)p_equals_1_;
         return this.siblings.equals(textcomponent.siblings) && Objects.equals(this.getStyle(), textcomponent.getStyle());
      }
   }

   public int hashCode() {
      return Objects.hash(this.getStyle(), this.siblings);
   }

   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}
