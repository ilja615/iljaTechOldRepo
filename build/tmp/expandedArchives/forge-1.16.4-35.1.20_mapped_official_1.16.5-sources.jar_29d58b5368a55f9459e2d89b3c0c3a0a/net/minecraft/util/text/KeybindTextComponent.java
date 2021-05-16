package net.minecraft.util.text;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeybindTextComponent extends TextComponent {
   private static Function<String, Supplier<ITextComponent>> keyResolver = (p_193635_0_) -> {
      return () -> {
         return new StringTextComponent(p_193635_0_);
      };
   };
   private final String name;
   private Supplier<ITextComponent> nameResolver;

   public KeybindTextComponent(String p_i47521_1_) {
      this.name = p_i47521_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static void setKeyResolver(Function<String, Supplier<ITextComponent>> p_240696_0_) {
      keyResolver = p_240696_0_;
   }

   private ITextComponent getNestedComponent() {
      if (this.nameResolver == null) {
         this.nameResolver = keyResolver.apply(this.name);
      }

      return this.nameResolver.get();
   }

   public <T> Optional<T> visitSelf(ITextProperties.ITextAcceptor<T> p_230533_1_) {
      return this.getNestedComponent().visit(p_230533_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public <T> Optional<T> visitSelf(ITextProperties.IStyledTextAcceptor<T> p_230534_1_, Style p_230534_2_) {
      return this.getNestedComponent().visit(p_230534_1_, p_230534_2_);
   }

   public KeybindTextComponent plainCopy() {
      return new KeybindTextComponent(this.name);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof KeybindTextComponent)) {
         return false;
      } else {
         KeybindTextComponent keybindtextcomponent = (KeybindTextComponent)p_equals_1_;
         return this.name.equals(keybindtextcomponent.name) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "KeybindComponent{keybind='" + this.name + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getName() {
      return this.name;
   }
}
