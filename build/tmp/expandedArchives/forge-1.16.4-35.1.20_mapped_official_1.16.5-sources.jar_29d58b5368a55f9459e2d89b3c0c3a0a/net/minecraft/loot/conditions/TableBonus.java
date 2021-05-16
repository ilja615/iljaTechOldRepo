package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class TableBonus implements ILootCondition {
   private final Enchantment enchantment;
   private final float[] values;

   private TableBonus(Enchantment p_i51207_1_, float[] p_i51207_2_) {
      this.enchantment = p_i51207_1_;
      this.values = p_i51207_2_;
   }

   public LootConditionType getType() {
      return LootConditionManager.TABLE_BONUS;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public boolean test(LootContext p_test_1_) {
      ItemStack itemstack = p_test_1_.getParamOrNull(LootParameters.TOOL);
      int i = itemstack != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, itemstack) : 0;
      float f = this.values[Math.min(i, this.values.length - 1)];
      return p_test_1_.getRandom().nextFloat() < f;
   }

   public static ILootCondition.IBuilder bonusLevelFlatChance(Enchantment p_215955_0_, float... p_215955_1_) {
      return () -> {
         return new TableBonus(p_215955_0_, p_215955_1_);
      };
   }

   public static class Serializer implements ILootSerializer<TableBonus> {
      public void serialize(JsonObject p_230424_1_, TableBonus p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.addProperty("enchantment", Registry.ENCHANTMENT.getKey(p_230424_2_.enchantment).toString());
         p_230424_1_.add("chances", p_230424_3_.serialize(p_230424_2_.values));
      }

      public TableBonus deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_230423_1_, "enchantment"));
         Enchantment enchantment = Registry.ENCHANTMENT.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + resourcelocation);
         });
         float[] afloat = JSONUtils.getAsObject(p_230423_1_, "chances", p_230423_2_, float[].class);
         return new TableBonus(enchantment, afloat);
      }
   }
}
