package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class PositionTrigger extends AbstractCriterionTrigger<PositionTrigger.Instance> {
   private final ResourceLocation id;

   public PositionTrigger(ResourceLocation p_i47432_1_) {
      this.id = p_i47432_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public PositionTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      JsonObject jsonobject = JSONUtils.getAsJsonObject(p_230241_1_, "location", p_230241_1_);
      LocationPredicate locationpredicate = LocationPredicate.fromJson(jsonobject);
      return new PositionTrigger.Instance(this.id, p_230241_2_, locationpredicate);
   }

   public void trigger(ServerPlayerEntity p_192215_1_) {
      this.trigger(p_192215_1_, (p_226923_1_) -> {
         return p_226923_1_.matches(p_192215_1_.getLevel(), p_192215_1_.getX(), p_192215_1_.getY(), p_192215_1_.getZ());
      });
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate location;

      public Instance(ResourceLocation p_i231661_1_, EntityPredicate.AndPredicate p_i231661_2_, LocationPredicate p_i231661_3_) {
         super(p_i231661_1_, p_i231661_2_);
         this.location = p_i231661_3_;
      }

      public static PositionTrigger.Instance located(LocationPredicate p_203932_0_) {
         return new PositionTrigger.Instance(CriteriaTriggers.LOCATION.id, EntityPredicate.AndPredicate.ANY, p_203932_0_);
      }

      public static PositionTrigger.Instance sleptInBed() {
         return new PositionTrigger.Instance(CriteriaTriggers.SLEPT_IN_BED.id, EntityPredicate.AndPredicate.ANY, LocationPredicate.ANY);
      }

      public static PositionTrigger.Instance raidWon() {
         return new PositionTrigger.Instance(CriteriaTriggers.RAID_WIN.id, EntityPredicate.AndPredicate.ANY, LocationPredicate.ANY);
      }

      public boolean matches(ServerWorld p_193204_1_, double p_193204_2_, double p_193204_4_, double p_193204_6_) {
         return this.location.matches(p_193204_1_, p_193204_2_, p_193204_4_, p_193204_6_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("location", this.location.serializeToJson());
         return jsonobject;
      }
   }
}
