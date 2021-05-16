package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PlayerHurtEntityTrigger extends AbstractCriterionTrigger<PlayerHurtEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public PlayerHurtEntityTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      DamagePredicate damagepredicate = DamagePredicate.fromJson(p_230241_1_.get("damage"));
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "entity", p_230241_3_);
      return new PlayerHurtEntityTrigger.Instance(p_230241_2_, damagepredicate, entitypredicate$andpredicate);
   }

   public void trigger(ServerPlayerEntity p_192220_1_, Entity p_192220_2_, DamageSource p_192220_3_, float p_192220_4_, float p_192220_5_, boolean p_192220_6_) {
      LootContext lootcontext = EntityPredicate.createContext(p_192220_1_, p_192220_2_);
      this.trigger(p_192220_1_, (p_226956_6_) -> {
         return p_226956_6_.matches(p_192220_1_, lootcontext, p_192220_3_, p_192220_4_, p_192220_5_, p_192220_6_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final DamagePredicate damage;
      private final EntityPredicate.AndPredicate entity;

      public Instance(EntityPredicate.AndPredicate p_i241190_1_, DamagePredicate p_i241190_2_, EntityPredicate.AndPredicate p_i241190_3_) {
         super(PlayerHurtEntityTrigger.ID, p_i241190_1_);
         this.damage = p_i241190_2_;
         this.entity = p_i241190_3_;
      }

      public static PlayerHurtEntityTrigger.Instance playerHurtEntity(DamagePredicate.Builder p_203936_0_) {
         return new PlayerHurtEntityTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_203936_0_.build(), EntityPredicate.AndPredicate.ANY);
      }

      public boolean matches(ServerPlayerEntity p_235609_1_, LootContext p_235609_2_, DamageSource p_235609_3_, float p_235609_4_, float p_235609_5_, boolean p_235609_6_) {
         if (!this.damage.matches(p_235609_1_, p_235609_3_, p_235609_4_, p_235609_5_, p_235609_6_)) {
            return false;
         } else {
            return this.entity.matches(p_235609_2_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("damage", this.damage.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
