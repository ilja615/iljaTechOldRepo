package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.potion.Effect;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SetStewEffect extends LootFunction {
   private final Map<Effect, RandomValueRange> effectDurationMap;

   private SetStewEffect(ILootCondition[] p_i51215_1_, Map<Effect, RandomValueRange> p_i51215_2_) {
      super(p_i51215_1_);
      this.effectDurationMap = ImmutableMap.copyOf(p_i51215_2_);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_STEW_EFFECT;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.getItem() == Items.SUSPICIOUS_STEW && !this.effectDurationMap.isEmpty()) {
         Random random = p_215859_2_.getRandom();
         int i = random.nextInt(this.effectDurationMap.size());
         Entry<Effect, RandomValueRange> entry = Iterables.get(this.effectDurationMap.entrySet(), i);
         Effect effect = entry.getKey();
         int j = entry.getValue().getInt(random);
         if (!effect.isInstantenous()) {
            j *= 20;
         }

         SuspiciousStewItem.saveMobEffect(p_215859_1_, effect, j);
         return p_215859_1_;
      } else {
         return p_215859_1_;
      }
   }

   public static SetStewEffect.Builder stewEffect() {
      return new SetStewEffect.Builder();
   }

   public static class Builder extends LootFunction.Builder<SetStewEffect.Builder> {
      private final Map<Effect, RandomValueRange> effectDurationMap = Maps.newHashMap();

      protected SetStewEffect.Builder getThis() {
         return this;
      }

      public SetStewEffect.Builder withEffect(Effect p_216077_1_, RandomValueRange p_216077_2_) {
         this.effectDurationMap.put(p_216077_1_, p_216077_2_);
         return this;
      }

      public ILootFunction build() {
         return new SetStewEffect(this.getConditions(), this.effectDurationMap);
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetStewEffect> {
      public void serialize(JsonObject p_230424_1_, SetStewEffect p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         if (!p_230424_2_.effectDurationMap.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(Effect effect : p_230424_2_.effectDurationMap.keySet()) {
               JsonObject jsonobject = new JsonObject();
               ResourceLocation resourcelocation = Registry.MOB_EFFECT.getKey(effect);
               if (resourcelocation == null) {
                  throw new IllegalArgumentException("Don't know how to serialize mob effect " + effect);
               }

               jsonobject.add("type", new JsonPrimitive(resourcelocation.toString()));
               jsonobject.add("duration", p_230424_3_.serialize(p_230424_2_.effectDurationMap.get(effect)));
               jsonarray.add(jsonobject);
            }

            p_230424_1_.add("effects", jsonarray);
         }

      }

      public SetStewEffect deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         Map<Effect, RandomValueRange> map = Maps.newHashMap();
         if (p_186530_1_.has("effects")) {
            for(JsonElement jsonelement : JSONUtils.getAsJsonArray(p_186530_1_, "effects")) {
               String s = JSONUtils.getAsString(jsonelement.getAsJsonObject(), "type");
               Effect effect = Registry.MOB_EFFECT.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown mob effect '" + s + "'");
               });
               RandomValueRange randomvaluerange = JSONUtils.getAsObject(jsonelement.getAsJsonObject(), "duration", p_186530_2_, RandomValueRange.class);
               map.put(effect, randomvaluerange);
            }
         }

         return new SetStewEffect(p_186530_3_, map);
      }
   }
}
