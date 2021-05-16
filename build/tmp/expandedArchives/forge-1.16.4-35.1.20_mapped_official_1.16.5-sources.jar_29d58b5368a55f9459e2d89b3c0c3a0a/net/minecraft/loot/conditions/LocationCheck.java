package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class LocationCheck implements ILootCondition {
   private final LocationPredicate predicate;
   private final BlockPos offset;

   private LocationCheck(LocationPredicate p_i225895_1_, BlockPos p_i225895_2_) {
      this.predicate = p_i225895_1_;
      this.offset = p_i225895_2_;
   }

   public LootConditionType getType() {
      return LootConditionManager.LOCATION_CHECK;
   }

   public boolean test(LootContext p_test_1_) {
      Vector3d vector3d = p_test_1_.getParamOrNull(LootParameters.ORIGIN);
      return vector3d != null && this.predicate.matches(p_test_1_.getLevel(), vector3d.x() + (double)this.offset.getX(), vector3d.y() + (double)this.offset.getY(), vector3d.z() + (double)this.offset.getZ());
   }

   public static ILootCondition.IBuilder checkLocation(LocationPredicate.Builder p_215975_0_) {
      return () -> {
         return new LocationCheck(p_215975_0_.build(), BlockPos.ZERO);
      };
   }

   public static ILootCondition.IBuilder checkLocation(LocationPredicate.Builder p_241547_0_, BlockPos p_241547_1_) {
      return () -> {
         return new LocationCheck(p_241547_0_.build(), p_241547_1_);
      };
   }

   public static class Serializer implements ILootSerializer<LocationCheck> {
      public void serialize(JsonObject p_230424_1_, LocationCheck p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.add("predicate", p_230424_2_.predicate.serializeToJson());
         if (p_230424_2_.offset.getX() != 0) {
            p_230424_1_.addProperty("offsetX", p_230424_2_.offset.getX());
         }

         if (p_230424_2_.offset.getY() != 0) {
            p_230424_1_.addProperty("offsetY", p_230424_2_.offset.getY());
         }

         if (p_230424_2_.offset.getZ() != 0) {
            p_230424_1_.addProperty("offsetZ", p_230424_2_.offset.getZ());
         }

      }

      public LocationCheck deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         LocationPredicate locationpredicate = LocationPredicate.fromJson(p_230423_1_.get("predicate"));
         int i = JSONUtils.getAsInt(p_230423_1_, "offsetX", 0);
         int j = JSONUtils.getAsInt(p_230423_1_, "offsetY", 0);
         int k = JSONUtils.getAsInt(p_230423_1_, "offsetZ", 0);
         return new LocationCheck(locationpredicate, new BlockPos(i, j, k));
      }
   }
}
