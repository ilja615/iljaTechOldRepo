package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IRandomRange;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomRanges;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public class EnchantWithLevels extends LootFunction {
   private final IRandomRange levels;
   private final boolean treasure;

   private EnchantWithLevels(ILootCondition[] p_i51236_1_, IRandomRange p_i51236_2_, boolean p_i51236_3_) {
      super(p_i51236_1_);
      this.levels = p_i51236_2_;
      this.treasure = p_i51236_3_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.ENCHANT_WITH_LEVELS;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Random random = p_215859_2_.getRandom();
      return EnchantmentHelper.enchantItem(random, p_215859_1_, this.levels.getInt(random), this.treasure);
   }

   public static EnchantWithLevels.Builder enchantWithLevels(IRandomRange p_215895_0_) {
      return new EnchantWithLevels.Builder(p_215895_0_);
   }

   public static class Builder extends LootFunction.Builder<EnchantWithLevels.Builder> {
      private final IRandomRange levels;
      private boolean treasure;

      public Builder(IRandomRange p_i51494_1_) {
         this.levels = p_i51494_1_;
      }

      protected EnchantWithLevels.Builder getThis() {
         return this;
      }

      public EnchantWithLevels.Builder allowTreasure() {
         this.treasure = true;
         return this;
      }

      public ILootFunction build() {
         return new EnchantWithLevels(this.getConditions(), this.levels, this.treasure);
      }
   }

   public static class Serializer extends LootFunction.Serializer<EnchantWithLevels> {
      public void serialize(JsonObject p_230424_1_, EnchantWithLevels p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.add("levels", RandomRanges.serialize(p_230424_2_.levels, p_230424_3_));
         p_230424_1_.addProperty("treasure", p_230424_2_.treasure);
      }

      public EnchantWithLevels deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         IRandomRange irandomrange = RandomRanges.deserialize(p_186530_1_.get("levels"), p_186530_2_);
         boolean flag = JSONUtils.getAsBoolean(p_186530_1_, "treasure", false);
         return new EnchantWithLevels(p_186530_3_, irandomrange, flag);
      }
   }
}
