package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger extends AbstractCriterionTrigger<ConstructBeaconTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ResourceLocation getId() {
      return ID;
   }

   public ConstructBeaconTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_230241_1_.get("level"));
      return new ConstructBeaconTrigger.Instance(p_230241_2_, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_192180_1_, BeaconTileEntity p_192180_2_) {
      this.trigger(p_192180_1_, (p_226308_1_) -> {
         return p_226308_1_.matches(p_192180_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound level;

      public Instance(EntityPredicate.AndPredicate p_i231507_1_, MinMaxBounds.IntBound p_i231507_2_) {
         super(ConstructBeaconTrigger.ID, p_i231507_1_);
         this.level = p_i231507_2_;
      }

      public static ConstructBeaconTrigger.Instance constructedBeacon(MinMaxBounds.IntBound p_203912_0_) {
         return new ConstructBeaconTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_203912_0_);
      }

      public boolean matches(BeaconTileEntity p_192252_1_) {
         return this.level.matches(p_192252_1_.getLevels());
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}
