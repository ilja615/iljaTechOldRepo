package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamage extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RandomValueRange damage;

   private SetDamage(ILootCondition[] p_i46622_1_, RandomValueRange p_i46622_2_) {
      super(p_i46622_1_);
      this.damage = p_i46622_2_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_DAMAGE;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.isDamageableItem()) {
         float f = 1.0F - this.damage.getFloat(p_215859_2_.getRandom());
         p_215859_1_.setDamageValue(MathHelper.floor(f * (float)p_215859_1_.getMaxDamage()));
      } else {
         LOGGER.warn("Couldn't set damage of loot item {}", (Object)p_215859_1_);
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> setDamage(RandomValueRange p_215931_0_) {
      return simpleBuilder((p_215930_1_) -> {
         return new SetDamage(p_215930_1_, p_215931_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<SetDamage> {
      public void serialize(JsonObject p_230424_1_, SetDamage p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.add("damage", p_230424_3_.serialize(p_230424_2_.damage));
      }

      public SetDamage deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return new SetDamage(p_186530_3_, JSONUtils.getAsObject(p_186530_1_, "damage", p_186530_2_, RandomValueRange.class));
      }
   }
}
