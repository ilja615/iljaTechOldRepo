package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class LevitationTrigger extends AbstractCriterionTrigger<LevitationTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("levitation");

   public ResourceLocation getId() {
      return ID;
   }

   public LevitationTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      DistancePredicate distancepredicate = DistancePredicate.fromJson(p_230241_1_.get("distance"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_230241_1_.get("duration"));
      return new LevitationTrigger.Instance(p_230241_2_, distancepredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_193162_1_, Vector3d p_193162_2_, int p_193162_3_) {
      this.trigger(p_193162_1_, (p_226852_3_) -> {
         return p_226852_3_.matches(p_193162_1_, p_193162_2_, p_193162_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.IntBound duration;

      public Instance(EntityPredicate.AndPredicate p_i231638_1_, DistancePredicate p_i231638_2_, MinMaxBounds.IntBound p_i231638_3_) {
         super(LevitationTrigger.ID, p_i231638_1_);
         this.distance = p_i231638_2_;
         this.duration = p_i231638_3_;
      }

      public static LevitationTrigger.Instance levitated(DistancePredicate p_203930_0_) {
         return new LevitationTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_203930_0_, MinMaxBounds.IntBound.ANY);
      }

      public boolean matches(ServerPlayerEntity p_193201_1_, Vector3d p_193201_2_, int p_193201_3_) {
         if (!this.distance.matches(p_193201_2_.x, p_193201_2_.y, p_193201_2_.z, p_193201_1_.getX(), p_193201_1_.getY(), p_193201_1_.getZ())) {
            return false;
         } else {
            return this.duration.matches(p_193201_3_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("distance", this.distance.serializeToJson());
         jsonobject.add("duration", this.duration.serializeToJson());
         return jsonobject;
      }
   }
}
