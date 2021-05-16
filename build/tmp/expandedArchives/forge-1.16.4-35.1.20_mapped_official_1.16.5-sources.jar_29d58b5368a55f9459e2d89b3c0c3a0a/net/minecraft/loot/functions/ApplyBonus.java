package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ApplyBonus extends LootFunction {
   private static final Map<ResourceLocation, ApplyBonus.IFormulaDeserializer> FORMULAS = Maps.newHashMap();
   private final Enchantment enchantment;
   private final ApplyBonus.IFormula formula;

   private ApplyBonus(ILootCondition[] p_i51246_1_, Enchantment p_i51246_2_, ApplyBonus.IFormula p_i51246_3_) {
      super(p_i51246_1_);
      this.enchantment = p_i51246_2_;
      this.formula = p_i51246_3_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.APPLY_BONUS;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      ItemStack itemstack = p_215859_2_.getParamOrNull(LootParameters.TOOL);
      if (itemstack != null) {
         int i = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, itemstack);
         int j = this.formula.calculateNewCount(p_215859_2_.getRandom(), p_215859_1_.getCount(), i);
         p_215859_1_.setCount(j);
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> addBonusBinomialDistributionCount(Enchantment p_215870_0_, float p_215870_1_, int p_215870_2_) {
      return simpleBuilder((p_215864_3_) -> {
         return new ApplyBonus(p_215864_3_, p_215870_0_, new ApplyBonus.BinomialWithBonusCountFormula(p_215870_2_, p_215870_1_));
      });
   }

   public static LootFunction.Builder<?> addOreBonusCount(Enchantment p_215869_0_) {
      return simpleBuilder((p_215866_1_) -> {
         return new ApplyBonus(p_215866_1_, p_215869_0_, new ApplyBonus.OreDropsFormula());
      });
   }

   public static LootFunction.Builder<?> addUniformBonusCount(Enchantment p_215871_0_) {
      return simpleBuilder((p_215872_1_) -> {
         return new ApplyBonus(p_215872_1_, p_215871_0_, new ApplyBonus.UniformBonusCountFormula(1));
      });
   }

   public static LootFunction.Builder<?> addUniformBonusCount(Enchantment p_215865_0_, int p_215865_1_) {
      return simpleBuilder((p_215868_2_) -> {
         return new ApplyBonus(p_215868_2_, p_215865_0_, new ApplyBonus.UniformBonusCountFormula(p_215865_1_));
      });
   }

   static {
      FORMULAS.put(ApplyBonus.BinomialWithBonusCountFormula.TYPE, ApplyBonus.BinomialWithBonusCountFormula::deserialize);
      FORMULAS.put(ApplyBonus.OreDropsFormula.TYPE, ApplyBonus.OreDropsFormula::deserialize);
      FORMULAS.put(ApplyBonus.UniformBonusCountFormula.TYPE, ApplyBonus.UniformBonusCountFormula::deserialize);
   }

   static final class BinomialWithBonusCountFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation TYPE = new ResourceLocation("binomial_with_bonus_count");
      private final int extraRounds;
      private final float probability;

      public BinomialWithBonusCountFormula(int p_i50983_1_, float p_i50983_2_) {
         this.extraRounds = p_i50983_1_;
         this.probability = p_i50983_2_;
      }

      public int calculateNewCount(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         for(int i = 0; i < p_216204_3_ + this.extraRounds; ++i) {
            if (p_216204_1_.nextFloat() < this.probability) {
               ++p_216204_2_;
            }
         }

         return p_216204_2_;
      }

      public void serializeParams(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
         p_216202_1_.addProperty("extra", this.extraRounds);
         p_216202_1_.addProperty("probability", this.probability);
      }

      public static ApplyBonus.IFormula deserialize(JsonObject p_216210_0_, JsonDeserializationContext p_216210_1_) {
         int i = JSONUtils.getAsInt(p_216210_0_, "extra");
         float f = JSONUtils.getAsFloat(p_216210_0_, "probability");
         return new ApplyBonus.BinomialWithBonusCountFormula(i, f);
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }

   interface IFormula {
      int calculateNewCount(Random p_216204_1_, int p_216204_2_, int p_216204_3_);

      void serializeParams(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_);

      ResourceLocation getType();
   }

   interface IFormulaDeserializer {
      ApplyBonus.IFormula deserialize(JsonObject p_deserialize_1_, JsonDeserializationContext p_deserialize_2_);
   }

   static final class OreDropsFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation TYPE = new ResourceLocation("ore_drops");

      private OreDropsFormula() {
      }

      public int calculateNewCount(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         if (p_216204_3_ > 0) {
            int i = p_216204_1_.nextInt(p_216204_3_ + 2) - 1;
            if (i < 0) {
               i = 0;
            }

            return p_216204_2_ * (i + 1);
         } else {
            return p_216204_2_;
         }
      }

      public void serializeParams(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
      }

      public static ApplyBonus.IFormula deserialize(JsonObject p_216205_0_, JsonDeserializationContext p_216205_1_) {
         return new ApplyBonus.OreDropsFormula();
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }

   public static class Serializer extends LootFunction.Serializer<ApplyBonus> {
      public void serialize(JsonObject p_230424_1_, ApplyBonus p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.addProperty("enchantment", Registry.ENCHANTMENT.getKey(p_230424_2_.enchantment).toString());
         p_230424_1_.addProperty("formula", p_230424_2_.formula.getType().toString());
         JsonObject jsonobject = new JsonObject();
         p_230424_2_.formula.serializeParams(jsonobject, p_230424_3_);
         if (jsonobject.size() > 0) {
            p_230424_1_.add("parameters", jsonobject);
         }

      }

      public ApplyBonus deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_186530_1_, "enchantment"));
         Enchantment enchantment = Registry.ENCHANTMENT.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + resourcelocation);
         });
         ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getAsString(p_186530_1_, "formula"));
         ApplyBonus.IFormulaDeserializer applybonus$iformuladeserializer = ApplyBonus.FORMULAS.get(resourcelocation1);
         if (applybonus$iformuladeserializer == null) {
            throw new JsonParseException("Invalid formula id: " + resourcelocation1);
         } else {
            ApplyBonus.IFormula applybonus$iformula;
            if (p_186530_1_.has("parameters")) {
               applybonus$iformula = applybonus$iformuladeserializer.deserialize(JSONUtils.getAsJsonObject(p_186530_1_, "parameters"), p_186530_2_);
            } else {
               applybonus$iformula = applybonus$iformuladeserializer.deserialize(new JsonObject(), p_186530_2_);
            }

            return new ApplyBonus(p_186530_3_, enchantment, applybonus$iformula);
         }
      }
   }

   static final class UniformBonusCountFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation TYPE = new ResourceLocation("uniform_bonus_count");
      private final int bonusMultiplier;

      public UniformBonusCountFormula(int p_i50981_1_) {
         this.bonusMultiplier = p_i50981_1_;
      }

      public int calculateNewCount(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         return p_216204_2_ + p_216204_1_.nextInt(this.bonusMultiplier * p_216204_3_ + 1);
      }

      public void serializeParams(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
         p_216202_1_.addProperty("bonusMultiplier", this.bonusMultiplier);
      }

      public static ApplyBonus.IFormula deserialize(JsonObject p_216207_0_, JsonDeserializationContext p_216207_1_) {
         int i = JSONUtils.getAsInt(p_216207_0_, "bonusMultiplier");
         return new ApplyBonus.UniformBonusCountFormula(i);
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }
}
