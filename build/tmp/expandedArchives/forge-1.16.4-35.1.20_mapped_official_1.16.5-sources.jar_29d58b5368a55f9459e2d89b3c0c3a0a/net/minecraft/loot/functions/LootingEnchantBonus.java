package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public class LootingEnchantBonus extends LootFunction {
   private final RandomValueRange value;
   private final int limit;

   private LootingEnchantBonus(ILootCondition[] p_i47145_1_, RandomValueRange p_i47145_2_, int p_i47145_3_) {
      super(p_i47145_1_);
      this.value = p_i47145_2_;
      this.limit = p_i47145_3_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.LOOTING_ENCHANT;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.KILLER_ENTITY);
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Entity entity = p_215859_2_.getParamOrNull(LootParameters.KILLER_ENTITY);
      if (entity instanceof LivingEntity) {
         int i = p_215859_2_.getLootingModifier();
         if (i == 0) {
            return p_215859_1_;
         }

         float f = (float)i * this.value.getFloat(p_215859_2_.getRandom());
         p_215859_1_.grow(Math.round(f));
         if (this.hasLimit() && p_215859_1_.getCount() > this.limit) {
            p_215859_1_.setCount(this.limit);
         }
      }

      return p_215859_1_;
   }

   public static LootingEnchantBonus.Builder lootingMultiplier(RandomValueRange p_215915_0_) {
      return new LootingEnchantBonus.Builder(p_215915_0_);
   }

   public static class Builder extends LootFunction.Builder<LootingEnchantBonus.Builder> {
      private final RandomValueRange count;
      private int limit = 0;

      public Builder(RandomValueRange p_i50932_1_) {
         this.count = p_i50932_1_;
      }

      protected LootingEnchantBonus.Builder getThis() {
         return this;
      }

      public LootingEnchantBonus.Builder setLimit(int p_216072_1_) {
         this.limit = p_216072_1_;
         return this;
      }

      public ILootFunction build() {
         return new LootingEnchantBonus(this.getConditions(), this.count, this.limit);
      }
   }

   public static class Serializer extends LootFunction.Serializer<LootingEnchantBonus> {
      public void serialize(JsonObject p_230424_1_, LootingEnchantBonus p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.add("count", p_230424_3_.serialize(p_230424_2_.value));
         if (p_230424_2_.hasLimit()) {
            p_230424_1_.add("limit", p_230424_3_.serialize(p_230424_2_.limit));
         }

      }

      public LootingEnchantBonus deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         int i = JSONUtils.getAsInt(p_186530_1_, "limit", 0);
         return new LootingEnchantBonus(p_186530_3_, JSONUtils.getAsObject(p_186530_1_, "count", p_186530_2_, RandomValueRange.class), i);
      }
   }
}
