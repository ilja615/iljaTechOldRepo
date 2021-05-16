package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class BinomialRange implements IRandomRange {
   private final int n;
   private final float p;

   public BinomialRange(int p_i51276_1_, float p_i51276_2_) {
      this.n = p_i51276_1_;
      this.p = p_i51276_2_;
   }

   public int getInt(Random p_186511_1_) {
      int i = 0;

      for(int j = 0; j < this.n; ++j) {
         if (p_186511_1_.nextFloat() < this.p) {
            ++i;
         }
      }

      return i;
   }

   public static BinomialRange binomial(int p_215838_0_, float p_215838_1_) {
      return new BinomialRange(p_215838_0_, p_215838_1_);
   }

   public ResourceLocation getType() {
      return BINOMIAL;
   }

   public static class Serializer implements JsonDeserializer<BinomialRange>, JsonSerializer<BinomialRange> {
      public BinomialRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "value");
         int i = JSONUtils.getAsInt(jsonobject, "n");
         float f = JSONUtils.getAsFloat(jsonobject, "p");
         return new BinomialRange(i, f);
      }

      public JsonElement serialize(BinomialRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("n", p_serialize_1_.n);
         jsonobject.addProperty("p", p_serialize_1_.p);
         return jsonobject;
      }
   }
}
