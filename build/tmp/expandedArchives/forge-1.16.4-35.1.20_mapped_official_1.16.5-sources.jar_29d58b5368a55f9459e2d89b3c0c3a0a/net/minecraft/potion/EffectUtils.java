package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EffectUtils {
   @OnlyIn(Dist.CLIENT)
   public static String formatDuration(EffectInstance p_188410_0_, float p_188410_1_) {
      if (p_188410_0_.isNoCounter()) {
         return "**:**";
      } else {
         int i = MathHelper.floor((float)p_188410_0_.getDuration() * p_188410_1_);
         return StringUtils.formatTickDuration(i);
      }
   }

   public static boolean hasDigSpeed(LivingEntity p_205135_0_) {
      return p_205135_0_.hasEffect(Effects.DIG_SPEED) || p_205135_0_.hasEffect(Effects.CONDUIT_POWER);
   }

   public static int getDigSpeedAmplification(LivingEntity p_205134_0_) {
      int i = 0;
      int j = 0;
      if (p_205134_0_.hasEffect(Effects.DIG_SPEED)) {
         i = p_205134_0_.getEffect(Effects.DIG_SPEED).getAmplifier();
      }

      if (p_205134_0_.hasEffect(Effects.CONDUIT_POWER)) {
         j = p_205134_0_.getEffect(Effects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(i, j);
   }

   public static boolean hasWaterBreathing(LivingEntity p_205133_0_) {
      return p_205133_0_.hasEffect(Effects.WATER_BREATHING) || p_205133_0_.hasEffect(Effects.CONDUIT_POWER);
   }
}
