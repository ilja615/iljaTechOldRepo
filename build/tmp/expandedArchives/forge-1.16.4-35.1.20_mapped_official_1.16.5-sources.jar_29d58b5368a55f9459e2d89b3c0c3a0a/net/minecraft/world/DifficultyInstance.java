package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class DifficultyInstance {
   private final Difficulty base;
   private final float effectiveDifficulty;

   public DifficultyInstance(Difficulty p_i45904_1_, long p_i45904_2_, long p_i45904_4_, float p_i45904_6_) {
      this.base = p_i45904_1_;
      this.effectiveDifficulty = this.calculateDifficulty(p_i45904_1_, p_i45904_2_, p_i45904_4_, p_i45904_6_);
   }

   public Difficulty getDifficulty() {
      return this.base;
   }

   public float getEffectiveDifficulty() {
      return this.effectiveDifficulty;
   }

   public boolean isHarderThan(float p_193845_1_) {
      return this.effectiveDifficulty > p_193845_1_;
   }

   public float getSpecialMultiplier() {
      if (this.effectiveDifficulty < 2.0F) {
         return 0.0F;
      } else {
         return this.effectiveDifficulty > 4.0F ? 1.0F : (this.effectiveDifficulty - 2.0F) / 2.0F;
      }
   }

   private float calculateDifficulty(Difficulty p_180169_1_, long p_180169_2_, long p_180169_4_, float p_180169_6_) {
      if (p_180169_1_ == Difficulty.PEACEFUL) {
         return 0.0F;
      } else {
         boolean flag = p_180169_1_ == Difficulty.HARD;
         float f = 0.75F;
         float f1 = MathHelper.clamp(((float)p_180169_2_ + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
         f = f + f1;
         float f2 = 0.0F;
         f2 = f2 + MathHelper.clamp((float)p_180169_4_ / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
         f2 = f2 + MathHelper.clamp(p_180169_6_ * 0.25F, 0.0F, f1);
         if (p_180169_1_ == Difficulty.EASY) {
            f2 *= 0.5F;
         }

         f = f + f2;
         return (float)p_180169_1_.getId() * f;
      }
   }
}
