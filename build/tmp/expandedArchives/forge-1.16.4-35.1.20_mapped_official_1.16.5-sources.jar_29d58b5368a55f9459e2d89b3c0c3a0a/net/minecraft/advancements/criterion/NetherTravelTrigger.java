package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class NetherTravelTrigger extends AbstractCriterionTrigger<NetherTravelTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");

   public ResourceLocation getId() {
      return ID;
   }

   public NetherTravelTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_230241_1_.get("entered"));
      LocationPredicate locationpredicate1 = LocationPredicate.fromJson(p_230241_1_.get("exited"));
      DistancePredicate distancepredicate = DistancePredicate.fromJson(p_230241_1_.get("distance"));
      return new NetherTravelTrigger.Instance(p_230241_2_, locationpredicate, locationpredicate1, distancepredicate);
   }

   public void trigger(ServerPlayerEntity p_193168_1_, Vector3d p_193168_2_) {
      this.trigger(p_193168_1_, (p_226945_2_) -> {
         return p_226945_2_.matches(p_193168_1_.getLevel(), p_193168_2_, p_193168_1_.getX(), p_193168_1_.getY(), p_193168_1_.getZ());
      });
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public Instance(EntityPredicate.AndPredicate p_i231785_1_, LocationPredicate p_i231785_2_, LocationPredicate p_i231785_3_, DistancePredicate p_i231785_4_) {
         super(NetherTravelTrigger.ID, p_i231785_1_);
         this.entered = p_i231785_2_;
         this.exited = p_i231785_3_;
         this.distance = p_i231785_4_;
      }

      public static NetherTravelTrigger.Instance travelledThroughNether(DistancePredicate p_203933_0_) {
         return new NetherTravelTrigger.Instance(EntityPredicate.AndPredicate.ANY, LocationPredicate.ANY, LocationPredicate.ANY, p_203933_0_);
      }

      public boolean matches(ServerWorld p_193206_1_, Vector3d p_193206_2_, double p_193206_3_, double p_193206_5_, double p_193206_7_) {
         if (!this.entered.matches(p_193206_1_, p_193206_2_.x, p_193206_2_.y, p_193206_2_.z)) {
            return false;
         } else if (!this.exited.matches(p_193206_1_, p_193206_3_, p_193206_5_, p_193206_7_)) {
            return false;
         } else {
            return this.distance.matches(p_193206_2_.x, p_193206_2_.y, p_193206_2_.z, p_193206_3_, p_193206_5_, p_193206_7_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("entered", this.entered.serializeToJson());
         jsonobject.add("exited", this.exited.serializeToJson());
         jsonobject.add("distance", this.distance.serializeToJson());
         return jsonobject;
      }
   }
}
