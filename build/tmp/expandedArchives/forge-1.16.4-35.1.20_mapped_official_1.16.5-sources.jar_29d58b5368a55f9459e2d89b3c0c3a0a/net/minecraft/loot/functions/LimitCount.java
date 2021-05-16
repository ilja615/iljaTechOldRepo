package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IntClamper;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public class LimitCount extends LootFunction {
   private final IntClamper limiter;

   private LimitCount(ILootCondition[] p_i51232_1_, IntClamper p_i51232_2_) {
      super(p_i51232_1_);
      this.limiter = p_i51232_2_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.LIMIT_COUNT;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      int i = this.limiter.applyAsInt(p_215859_1_.getCount());
      p_215859_1_.setCount(i);
      return p_215859_1_;
   }

   public static LootFunction.Builder<?> limitCount(IntClamper p_215911_0_) {
      return simpleBuilder((p_215912_1_) -> {
         return new LimitCount(p_215912_1_, p_215911_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<LimitCount> {
      public void serialize(JsonObject p_230424_1_, LimitCount p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.add("limit", p_230424_3_.serialize(p_230424_2_.limiter));
      }

      public LimitCount deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         IntClamper intclamper = JSONUtils.getAsObject(p_186530_1_, "limit", p_186530_2_, IntClamper.class);
         return new LimitCount(p_186530_3_, intclamper);
      }
   }
}
