package net.minecraft.resources;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public interface IPackNameDecorator {
   IPackNameDecorator DEFAULT = passThrough();
   IPackNameDecorator BUILT_IN = decorating("pack.source.builtin");
   IPackNameDecorator WORLD = decorating("pack.source.world");
   IPackNameDecorator SERVER = decorating("pack.source.server");

   ITextComponent decorate(ITextComponent p_decorate_1_);

   static IPackNameDecorator passThrough() {
      return (p_232631_0_) -> {
         return p_232631_0_;
      };
   }

   static IPackNameDecorator decorating(String p_232630_0_) {
      ITextComponent itextcomponent = new TranslationTextComponent(p_232630_0_);
      return (p_232632_1_) -> {
         return (new TranslationTextComponent("pack.nameAndSource", p_232632_1_, itextcomponent)).withStyle(TextFormatting.GRAY);
      };
   }
}
