package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;

public class RandomChanceWithLooting implements ILootCondition {
   private final float percent;
   private final float lootingMultiplier;

   private RandomChanceWithLooting(float p_i46614_1_, float p_i46614_2_) {
      this.percent = p_i46614_1_;
      this.lootingMultiplier = p_i46614_2_;
   }

   public LootConditionType getType() {
      return LootConditionManager.RANDOM_CHANCE_WITH_LOOTING;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.KILLER_ENTITY);
   }

   public boolean test(LootContext p_test_1_) {
      int i = p_test_1_.getLootingModifier();
      return p_test_1_.getRandom().nextFloat() < this.percent + (float)i * this.lootingMultiplier;
   }

   public static ILootCondition.IBuilder randomChanceAndLootingBoost(float p_216003_0_, float p_216003_1_) {
      return () -> {
         return new RandomChanceWithLooting(p_216003_0_, p_216003_1_);
      };
   }

   public static class Serializer implements ILootSerializer<RandomChanceWithLooting> {
      public void serialize(JsonObject p_230424_1_, RandomChanceWithLooting p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.addProperty("chance", p_230424_2_.percent);
         p_230424_1_.addProperty("looting_multiplier", p_230424_2_.lootingMultiplier);
      }

      public RandomChanceWithLooting deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         return new RandomChanceWithLooting(JSONUtils.getAsFloat(p_230423_1_, "chance"), JSONUtils.getAsFloat(p_230423_1_, "looting_multiplier"));
      }
   }
}
