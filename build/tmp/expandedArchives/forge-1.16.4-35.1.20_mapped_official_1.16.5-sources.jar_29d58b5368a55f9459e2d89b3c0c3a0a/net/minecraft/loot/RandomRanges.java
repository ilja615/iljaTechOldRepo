package net.minecraft.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RandomRanges {
   private static final Map<ResourceLocation, Class<? extends IRandomRange>> GENERATORS = Maps.newHashMap();

   public static IRandomRange deserialize(JsonElement p_216130_0_, JsonDeserializationContext p_216130_1_) throws JsonParseException {
      if (p_216130_0_.isJsonPrimitive()) {
         return p_216130_1_.deserialize(p_216130_0_, ConstantRange.class);
      } else {
         JsonObject jsonobject = p_216130_0_.getAsJsonObject();
         String s = JSONUtils.getAsString(jsonobject, "type", IRandomRange.UNIFORM.toString());
         Class<? extends IRandomRange> oclass = GENERATORS.get(new ResourceLocation(s));
         if (oclass == null) {
            throw new JsonParseException("Unknown generator: " + s);
         } else {
            return p_216130_1_.deserialize(jsonobject, oclass);
         }
      }
   }

   public static JsonElement serialize(IRandomRange p_216131_0_, JsonSerializationContext p_216131_1_) {
      JsonElement jsonelement = p_216131_1_.serialize(p_216131_0_);
      if (jsonelement.isJsonObject()) {
         jsonelement.getAsJsonObject().addProperty("type", p_216131_0_.getType().toString());
      }

      return jsonelement;
   }

   static {
      GENERATORS.put(IRandomRange.UNIFORM, RandomValueRange.class);
      GENERATORS.put(IRandomRange.BINOMIAL, BinomialRange.class);
      GENERATORS.put(IRandomRange.CONSTANT, ConstantRange.class);
   }
}
