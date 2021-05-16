package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

public class ExplosionDecay extends LootFunction {
   private ExplosionDecay(ILootCondition[] p_i51244_1_) {
      super(p_i51244_1_);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.EXPLOSION_DECAY;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Float f = p_215859_2_.getParamOrNull(LootParameters.EXPLOSION_RADIUS);
      if (f != null) {
         Random random = p_215859_2_.getRandom();
         float f1 = 1.0F / f;
         int i = p_215859_1_.getCount();
         int j = 0;

         for(int k = 0; k < i; ++k) {
            if (random.nextFloat() <= f1) {
               ++j;
            }
         }

         p_215859_1_.setCount(j);
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> explosionDecay() {
      return simpleBuilder(ExplosionDecay::new);
   }

   public static class Serializer extends LootFunction.Serializer<ExplosionDecay> {
      public ExplosionDecay deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return new ExplosionDecay(p_186530_3_);
      }
   }
}
