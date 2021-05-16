package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class TargetHitTrigger extends AbstractCriterionTrigger<TargetHitTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("target_hit");

   public ResourceLocation getId() {
      return ID;
   }

   public TargetHitTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_230241_1_.get("signal_strength"));
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "projectile", p_230241_3_);
      return new TargetHitTrigger.Instance(p_230241_2_, minmaxbounds$intbound, entitypredicate$andpredicate);
   }

   public void trigger(ServerPlayerEntity p_236350_1_, Entity p_236350_2_, Vector3d p_236350_3_, int p_236350_4_) {
      LootContext lootcontext = EntityPredicate.createContext(p_236350_1_, p_236350_2_);
      this.trigger(p_236350_1_, (p_236349_3_) -> {
         return p_236349_3_.matches(lootcontext, p_236350_3_, p_236350_4_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound signalStrength;
      private final EntityPredicate.AndPredicate projectile;

      public Instance(EntityPredicate.AndPredicate p_i231990_1_, MinMaxBounds.IntBound p_i231990_2_, EntityPredicate.AndPredicate p_i231990_3_) {
         super(TargetHitTrigger.ID, p_i231990_1_);
         this.signalStrength = p_i231990_2_;
         this.projectile = p_i231990_3_;
      }

      public static TargetHitTrigger.Instance targetHit(MinMaxBounds.IntBound p_236354_0_, EntityPredicate.AndPredicate p_236354_1_) {
         return new TargetHitTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_236354_0_, p_236354_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("signal_strength", this.signalStrength.serializeToJson());
         jsonobject.add("projectile", this.projectile.toJson(p_230240_1_));
         return jsonobject;
      }

      public boolean matches(LootContext p_236355_1_, Vector3d p_236355_2_, int p_236355_3_) {
         if (!this.signalStrength.matches(p_236355_3_)) {
            return false;
         } else {
            return this.projectile.matches(p_236355_1_);
         }
      }
   }
}
