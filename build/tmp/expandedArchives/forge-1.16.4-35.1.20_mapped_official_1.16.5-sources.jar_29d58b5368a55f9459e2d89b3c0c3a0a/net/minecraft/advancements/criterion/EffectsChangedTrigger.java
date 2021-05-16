package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class EffectsChangedTrigger extends AbstractCriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.fromJson(p_230241_1_.get("effects"));
      return new EffectsChangedTrigger.Instance(p_230241_2_, mobeffectspredicate);
   }

   public void trigger(ServerPlayerEntity p_193153_1_) {
      this.trigger(p_193153_1_, (p_226524_1_) -> {
         return p_226524_1_.matches(p_193153_1_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MobEffectsPredicate effects;

      public Instance(EntityPredicate.AndPredicate p_i231553_1_, MobEffectsPredicate p_i231553_2_) {
         super(EffectsChangedTrigger.ID, p_i231553_1_);
         this.effects = p_i231553_2_;
      }

      public static EffectsChangedTrigger.Instance hasEffects(MobEffectsPredicate p_203917_0_) {
         return new EffectsChangedTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_203917_0_);
      }

      public boolean matches(ServerPlayerEntity p_193195_1_) {
         return this.effects.matches(p_193195_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("effects", this.effects.serializeToJson());
         return jsonobject;
      }
   }
}
