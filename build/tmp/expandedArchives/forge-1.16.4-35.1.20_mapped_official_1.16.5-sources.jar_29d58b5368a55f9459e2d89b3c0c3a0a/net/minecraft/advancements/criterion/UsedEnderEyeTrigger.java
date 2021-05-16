package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger extends AbstractCriterionTrigger<UsedEnderEyeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedEnderEyeTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(p_230241_1_.get("distance"));
      return new UsedEnderEyeTrigger.Instance(p_230241_2_, minmaxbounds$floatbound);
   }

   public void trigger(ServerPlayerEntity p_192239_1_, BlockPos p_192239_2_) {
      double d0 = p_192239_1_.getX() - (double)p_192239_2_.getX();
      double d1 = p_192239_1_.getZ() - (double)p_192239_2_.getZ();
      double d2 = d0 * d0 + d1 * d1;
      this.trigger(p_192239_1_, (p_227325_2_) -> {
         return p_227325_2_.matches(d2);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.FloatBound level;

      public Instance(EntityPredicate.AndPredicate p_i232030_1_, MinMaxBounds.FloatBound p_i232030_2_) {
         super(UsedEnderEyeTrigger.ID, p_i232030_1_);
         this.level = p_i232030_2_;
      }

      public boolean matches(double p_192288_1_) {
         return this.level.matchesSqr(p_192288_1_);
      }
   }
}
