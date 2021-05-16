package net.minecraft.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface INestedGuiEventHandler extends IGuiEventListener {
   List<? extends IGuiEventListener> children();

   default Optional<IGuiEventListener> getChildAt(double p_212930_1_, double p_212930_3_) {
      for(IGuiEventListener iguieventlistener : this.children()) {
         if (iguieventlistener.isMouseOver(p_212930_1_, p_212930_3_)) {
            return Optional.of(iguieventlistener);
         }
      }

      return Optional.empty();
   }

   default boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      for(IGuiEventListener iguieventlistener : this.children()) {
         if (iguieventlistener.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)) {
            this.setFocused(iguieventlistener);
            if (p_231044_5_ == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   default boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      this.setDragging(false);
      return this.getChildAt(p_231048_1_, p_231048_3_).filter((p_212931_5_) -> {
         return p_212931_5_.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
      }).isPresent();
   }

   default boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
      return this.getFocused() != null && this.isDragging() && p_231045_5_ == 0 ? this.getFocused().mouseDragged(p_231045_1_, p_231045_3_, p_231045_5_, p_231045_6_, p_231045_8_) : false;
   }

   boolean isDragging();

   void setDragging(boolean p_231037_1_);

   default boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
      return this.getChildAt(p_231043_1_, p_231043_3_).filter((p_212929_6_) -> {
         return p_212929_6_.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
      }).isPresent();
   }

   default boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      return this.getFocused() != null && this.getFocused().keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
   }

   default boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
      return this.getFocused() != null && this.getFocused().keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
   }

   default boolean charTyped(char p_231042_1_, int p_231042_2_) {
      return this.getFocused() != null && this.getFocused().charTyped(p_231042_1_, p_231042_2_);
   }

   @Nullable
   IGuiEventListener getFocused();

   void setFocused(@Nullable IGuiEventListener p_231035_1_);

   default void setInitialFocus(@Nullable IGuiEventListener p_212928_1_) {
      this.setFocused(p_212928_1_);
      p_212928_1_.changeFocus(true);
   }

   default void magicalSpecialHackyFocus(@Nullable IGuiEventListener p_212932_1_) {
      this.setFocused(p_212932_1_);
   }

   default boolean changeFocus(boolean p_231049_1_) {
      IGuiEventListener iguieventlistener = this.getFocused();
      boolean flag = iguieventlistener != null;
      if (flag && iguieventlistener.changeFocus(p_231049_1_)) {
         return true;
      } else {
         List<? extends IGuiEventListener> list = this.children();
         int j = list.indexOf(iguieventlistener);
         int i;
         if (flag && j >= 0) {
            i = j + (p_231049_1_ ? 1 : 0);
         } else if (p_231049_1_) {
            i = 0;
         } else {
            i = list.size();
         }

         ListIterator<? extends IGuiEventListener> listiterator = list.listIterator(i);
         BooleanSupplier booleansupplier = p_231049_1_ ? listiterator::hasNext : listiterator::hasPrevious;
         Supplier<? extends IGuiEventListener> supplier = p_231049_1_ ? listiterator::next : listiterator::previous;

         while(booleansupplier.getAsBoolean()) {
            IGuiEventListener iguieventlistener1 = supplier.get();
            if (iguieventlistener1.changeFocus(p_231049_1_)) {
               this.setFocused(iguieventlistener1);
               return true;
            }
         }

         this.setFocused((IGuiEventListener)null);
         return false;
      }
   }
}
