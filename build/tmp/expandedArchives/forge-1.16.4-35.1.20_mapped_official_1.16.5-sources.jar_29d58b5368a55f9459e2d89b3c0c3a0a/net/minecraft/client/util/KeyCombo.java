package net.minecraft.client.util;

import java.util.Arrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyCombo {
   private final char[] chars;
   private int matchIndex;
   private final Runnable onCompletion;

   public KeyCombo(char[] p_i51793_1_, Runnable p_i51793_2_) {
      this.onCompletion = p_i51793_2_;
      if (p_i51793_1_.length < 1) {
         throw new IllegalArgumentException("Must have at least one char");
      } else {
         this.chars = p_i51793_1_;
      }
   }

   public boolean keyPressed(char p_224799_1_) {
      if (p_224799_1_ == this.chars[this.matchIndex++]) {
         if (this.matchIndex == this.chars.length) {
            this.reset();
            this.onCompletion.run();
            return true;
         }
      } else {
         this.reset();
      }

      return false;
   }

   public void reset() {
      this.matchIndex = 0;
   }

   public String toString() {
      return "KeyCombo{chars=" + Arrays.toString(this.chars) + ", matchIndex=" + this.matchIndex + '}';
   }
}
