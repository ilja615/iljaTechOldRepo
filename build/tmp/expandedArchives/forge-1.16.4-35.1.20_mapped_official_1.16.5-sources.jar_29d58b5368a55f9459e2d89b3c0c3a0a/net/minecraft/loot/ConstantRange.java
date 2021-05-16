package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class ConstantRange implements IRandomRange {
   private final int value;

   public ConstantRange(int p_i51275_1_) {
      this.value = p_i51275_1_;
   }

   public int getInt(Random p_186511_1_) {
      return this.value;
   }

   public ResourceLocation getType() {
      return CONSTANT;
   }

   public static ConstantRange exactly(int p_215835_0_) {
      return new ConstantRange(p_215835_0_);
   }

   public static class Serializer implements JsonDeserializer<ConstantRange>, JsonSerializer<ConstantRange> {
      public ConstantRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new ConstantRange(JSONUtils.convertToInt(p_deserialize_1_, "value"));
      }

      public JsonElement serialize(ConstantRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return new JsonPrimitive(p_serialize_1_.value);
      }
   }
}
