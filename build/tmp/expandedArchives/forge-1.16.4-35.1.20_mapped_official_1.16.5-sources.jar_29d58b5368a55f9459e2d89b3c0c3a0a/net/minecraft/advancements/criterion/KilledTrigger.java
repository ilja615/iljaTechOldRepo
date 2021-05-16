package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class KilledTrigger extends AbstractCriterionTrigger<KilledTrigger.Instance> {
   private final ResourceLocation id;

   public KilledTrigger(ResourceLocation p_i47433_1_) {
      this.id = p_i47433_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public KilledTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      return new KilledTrigger.Instance(this.id, p_230241_2_, EntityPredicate.AndPredicate.fromJson(p_230241_1_, "entity", p_230241_3_), DamageSourcePredicate.fromJson(p_230241_1_.get("killing_blow")));
   }

   public void trigger(ServerPlayerEntity p_192211_1_, Entity p_192211_2_, DamageSource p_192211_3_) {
      LootContext lootcontext = EntityPredicate.createContext(p_192211_1_, p_192211_2_);
      this.trigger(p_192211_1_, (p_226846_3_) -> {
         return p_226846_3_.matches(p_192211_1_, lootcontext, p_192211_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate entityPredicate;
      private final DamageSourcePredicate killingBlow;

      public Instance(ResourceLocation p_i231630_1_, EntityPredicate.AndPredicate p_i231630_2_, EntityPredicate.AndPredicate p_i231630_3_, DamageSourcePredicate p_i231630_4_) {
         super(p_i231630_1_, p_i231630_2_);
         this.entityPredicate = p_i231630_3_;
         this.killingBlow = p_i231630_4_;
      }

      public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder p_203928_0_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.wrap(p_203928_0_.build()), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance playerKilledEntity() {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder p_203929_0_, DamageSourcePredicate.Builder p_203929_1_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.wrap(p_203929_0_.build()), p_203929_1_.build());
      }

      public static KilledTrigger.Instance entityKilledPlayer() {
         return new KilledTrigger.Instance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public boolean matches(ServerPlayerEntity p_235050_1_, LootContext p_235050_2_, DamageSource p_235050_3_) {
         return !this.killingBlow.matches(p_235050_1_, p_235050_3_) ? false : this.entityPredicate.matches(p_235050_2_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("entity", this.entityPredicate.toJson(p_230240_1_));
         jsonobject.add("killing_blow", this.killingBlow.serializeToJson());
         return jsonobject;
      }
   }
}
