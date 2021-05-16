package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IRandomRange;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomRanges;
import net.minecraft.loot.conditions.ILootCondition;

public class SetCount extends LootFunction {
   private final IRandomRange value;

   private SetCount(ILootCondition[] p_i51222_1_, IRandomRange p_i51222_2_) {
      super(p_i51222_1_);
      this.value = p_i51222_2_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_COUNT;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      p_215859_1_.setCount(this.value.getInt(p_215859_2_.getRandom()));
      return p_215859_1_;
   }

   public static LootFunction.Builder<?> setCount(IRandomRange p_215932_0_) {
      return simpleBuilder((p_215934_1_) -> {
         return new SetCount(p_215934_1_, p_215932_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<SetCount> {
      public void serialize(JsonObject p_230424_1_, SetCount p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.add("count", RandomRanges.serialize(p_230424_2_.value, p_230424_3_));
      }

      public SetCount deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         IRandomRange irandomrange = RandomRanges.deserialize(p_186530_1_.get("count"), p_186530_2_);
         return new SetCount(p_186530_3_, irandomrange);
      }
   }
}
