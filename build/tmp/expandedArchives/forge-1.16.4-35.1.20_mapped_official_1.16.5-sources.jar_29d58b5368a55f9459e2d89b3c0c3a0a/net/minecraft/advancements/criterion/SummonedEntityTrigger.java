package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class SummonedEntityTrigger extends AbstractCriterionTrigger<SummonedEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("summoned_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public SummonedEntityTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "entity", p_230241_3_);
      return new SummonedEntityTrigger.Instance(p_230241_2_, entitypredicate$andpredicate);
   }

   public void trigger(ServerPlayerEntity p_192229_1_, Entity p_192229_2_) {
      LootContext lootcontext = EntityPredicate.createContext(p_192229_1_, p_192229_2_);
      this.trigger(p_192229_1_, (p_227229_1_) -> {
         return p_227229_1_.matches(lootcontext);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate entity;

      public Instance(EntityPredicate.AndPredicate p_i231938_1_, EntityPredicate.AndPredicate p_i231938_2_) {
         super(SummonedEntityTrigger.ID, p_i231938_1_);
         this.entity = p_i231938_2_;
      }

      public static SummonedEntityTrigger.Instance summonedEntity(EntityPredicate.Builder p_203937_0_) {
         return new SummonedEntityTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.wrap(p_203937_0_.build()));
      }

      public boolean matches(LootContext p_236273_1_) {
         return this.entity.matches(p_236273_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("entity", this.entity.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
