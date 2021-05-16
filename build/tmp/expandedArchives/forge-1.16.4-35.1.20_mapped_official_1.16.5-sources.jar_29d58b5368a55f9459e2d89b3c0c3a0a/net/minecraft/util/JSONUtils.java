package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JSONUtils {
   private static final Gson GSON = (new GsonBuilder()).create();

   public static boolean isStringValue(JsonObject p_151205_0_, String p_151205_1_) {
      return !isValidPrimitive(p_151205_0_, p_151205_1_) ? false : p_151205_0_.getAsJsonPrimitive(p_151205_1_).isString();
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isStringValue(JsonElement p_151211_0_) {
      return !p_151211_0_.isJsonPrimitive() ? false : p_151211_0_.getAsJsonPrimitive().isString();
   }

   public static boolean isNumberValue(JsonElement p_188175_0_) {
      return !p_188175_0_.isJsonPrimitive() ? false : p_188175_0_.getAsJsonPrimitive().isNumber();
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isBooleanValue(JsonObject p_180199_0_, String p_180199_1_) {
      return !isValidPrimitive(p_180199_0_, p_180199_1_) ? false : p_180199_0_.getAsJsonPrimitive(p_180199_1_).isBoolean();
   }

   public static boolean isArrayNode(JsonObject p_151202_0_, String p_151202_1_) {
      return !isValidNode(p_151202_0_, p_151202_1_) ? false : p_151202_0_.get(p_151202_1_).isJsonArray();
   }

   public static boolean isValidPrimitive(JsonObject p_151201_0_, String p_151201_1_) {
      return !isValidNode(p_151201_0_, p_151201_1_) ? false : p_151201_0_.get(p_151201_1_).isJsonPrimitive();
   }

   public static boolean isValidNode(JsonObject p_151204_0_, String p_151204_1_) {
      if (p_151204_0_ == null) {
         return false;
      } else {
         return p_151204_0_.get(p_151204_1_) != null;
      }
   }

   public static String convertToString(JsonElement p_151206_0_, String p_151206_1_) {
      if (p_151206_0_.isJsonPrimitive()) {
         return p_151206_0_.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + p_151206_1_ + " to be a string, was " + getType(p_151206_0_));
      }
   }

   public static String getAsString(JsonObject p_151200_0_, String p_151200_1_) {
      if (p_151200_0_.has(p_151200_1_)) {
         return convertToString(p_151200_0_.get(p_151200_1_), p_151200_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151200_1_ + ", expected to find a string");
      }
   }

   public static String getAsString(JsonObject p_151219_0_, String p_151219_1_, String p_151219_2_) {
      return p_151219_0_.has(p_151219_1_) ? convertToString(p_151219_0_.get(p_151219_1_), p_151219_1_) : p_151219_2_;
   }

   public static Item convertToItem(JsonElement p_188172_0_, String p_188172_1_) {
      if (p_188172_0_.isJsonPrimitive()) {
         String s = p_188172_0_.getAsString();
         return Registry.ITEM.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Expected " + p_188172_1_ + " to be an item, was unknown string '" + s + "'");
         });
      } else {
         throw new JsonSyntaxException("Expected " + p_188172_1_ + " to be an item, was " + getType(p_188172_0_));
      }
   }

   public static Item getAsItem(JsonObject p_188180_0_, String p_188180_1_) {
      if (p_188180_0_.has(p_188180_1_)) {
         return convertToItem(p_188180_0_.get(p_188180_1_), p_188180_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_188180_1_ + ", expected to find an item");
      }
   }

   public static boolean convertToBoolean(JsonElement p_151216_0_, String p_151216_1_) {
      if (p_151216_0_.isJsonPrimitive()) {
         return p_151216_0_.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + p_151216_1_ + " to be a Boolean, was " + getType(p_151216_0_));
      }
   }

   public static boolean getAsBoolean(JsonObject p_151212_0_, String p_151212_1_) {
      if (p_151212_0_.has(p_151212_1_)) {
         return convertToBoolean(p_151212_0_.get(p_151212_1_), p_151212_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151212_1_ + ", expected to find a Boolean");
      }
   }

   public static boolean getAsBoolean(JsonObject p_151209_0_, String p_151209_1_, boolean p_151209_2_) {
      return p_151209_0_.has(p_151209_1_) ? convertToBoolean(p_151209_0_.get(p_151209_1_), p_151209_1_) : p_151209_2_;
   }

   public static float convertToFloat(JsonElement p_151220_0_, String p_151220_1_) {
      if (p_151220_0_.isJsonPrimitive() && p_151220_0_.getAsJsonPrimitive().isNumber()) {
         return p_151220_0_.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + p_151220_1_ + " to be a Float, was " + getType(p_151220_0_));
      }
   }

   public static float getAsFloat(JsonObject p_151217_0_, String p_151217_1_) {
      if (p_151217_0_.has(p_151217_1_)) {
         return convertToFloat(p_151217_0_.get(p_151217_1_), p_151217_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151217_1_ + ", expected to find a Float");
      }
   }

   public static float getAsFloat(JsonObject p_151221_0_, String p_151221_1_, float p_151221_2_) {
      return p_151221_0_.has(p_151221_1_) ? convertToFloat(p_151221_0_.get(p_151221_1_), p_151221_1_) : p_151221_2_;
   }

   public static long convertToLong(JsonElement p_219794_0_, String p_219794_1_) {
      if (p_219794_0_.isJsonPrimitive() && p_219794_0_.getAsJsonPrimitive().isNumber()) {
         return p_219794_0_.getAsLong();
      } else {
         throw new JsonSyntaxException("Expected " + p_219794_1_ + " to be a Long, was " + getType(p_219794_0_));
      }
   }

   public static long getAsLong(JsonObject p_226161_0_, String p_226161_1_) {
      if (p_226161_0_.has(p_226161_1_)) {
         return convertToLong(p_226161_0_.get(p_226161_1_), p_226161_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_226161_1_ + ", expected to find a Long");
      }
   }

   public static long getAsLong(JsonObject p_219796_0_, String p_219796_1_, long p_219796_2_) {
      return p_219796_0_.has(p_219796_1_) ? convertToLong(p_219796_0_.get(p_219796_1_), p_219796_1_) : p_219796_2_;
   }

   public static int convertToInt(JsonElement p_151215_0_, String p_151215_1_) {
      if (p_151215_0_.isJsonPrimitive() && p_151215_0_.getAsJsonPrimitive().isNumber()) {
         return p_151215_0_.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + p_151215_1_ + " to be a Int, was " + getType(p_151215_0_));
      }
   }

   public static int getAsInt(JsonObject p_151203_0_, String p_151203_1_) {
      if (p_151203_0_.has(p_151203_1_)) {
         return convertToInt(p_151203_0_.get(p_151203_1_), p_151203_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151203_1_ + ", expected to find a Int");
      }
   }

   public static int getAsInt(JsonObject p_151208_0_, String p_151208_1_, int p_151208_2_) {
      return p_151208_0_.has(p_151208_1_) ? convertToInt(p_151208_0_.get(p_151208_1_), p_151208_1_) : p_151208_2_;
   }

   public static byte convertToByte(JsonElement p_204332_0_, String p_204332_1_) {
      if (p_204332_0_.isJsonPrimitive() && p_204332_0_.getAsJsonPrimitive().isNumber()) {
         return p_204332_0_.getAsByte();
      } else {
         throw new JsonSyntaxException("Expected " + p_204332_1_ + " to be a Byte, was " + getType(p_204332_0_));
      }
   }

   public static byte getAsByte(JsonObject p_219795_0_, String p_219795_1_, byte p_219795_2_) {
      return p_219795_0_.has(p_219795_1_) ? convertToByte(p_219795_0_.get(p_219795_1_), p_219795_1_) : p_219795_2_;
   }

   public static JsonObject convertToJsonObject(JsonElement p_151210_0_, String p_151210_1_) {
      if (p_151210_0_.isJsonObject()) {
         return p_151210_0_.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + p_151210_1_ + " to be a JsonObject, was " + getType(p_151210_0_));
      }
   }

   public static JsonObject getAsJsonObject(JsonObject p_152754_0_, String p_152754_1_) {
      if (p_152754_0_.has(p_152754_1_)) {
         return convertToJsonObject(p_152754_0_.get(p_152754_1_), p_152754_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_152754_1_ + ", expected to find a JsonObject");
      }
   }

   public static JsonObject getAsJsonObject(JsonObject p_151218_0_, String p_151218_1_, JsonObject p_151218_2_) {
      return p_151218_0_.has(p_151218_1_) ? convertToJsonObject(p_151218_0_.get(p_151218_1_), p_151218_1_) : p_151218_2_;
   }

   public static JsonArray convertToJsonArray(JsonElement p_151207_0_, String p_151207_1_) {
      if (p_151207_0_.isJsonArray()) {
         return p_151207_0_.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + p_151207_1_ + " to be a JsonArray, was " + getType(p_151207_0_));
      }
   }

   public static JsonArray getAsJsonArray(JsonObject p_151214_0_, String p_151214_1_) {
      if (p_151214_0_.has(p_151214_1_)) {
         return convertToJsonArray(p_151214_0_.get(p_151214_1_), p_151214_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151214_1_ + ", expected to find a JsonArray");
      }
   }

   @Nullable
   public static JsonArray getAsJsonArray(JsonObject p_151213_0_, String p_151213_1_, @Nullable JsonArray p_151213_2_) {
      return p_151213_0_.has(p_151213_1_) ? convertToJsonArray(p_151213_0_.get(p_151213_1_), p_151213_1_) : p_151213_2_;
   }

   public static <T> T convertToObject(@Nullable JsonElement p_188179_0_, String p_188179_1_, JsonDeserializationContext p_188179_2_, Class<? extends T> p_188179_3_) {
      if (p_188179_0_ != null) {
         return p_188179_2_.deserialize(p_188179_0_, p_188179_3_);
      } else {
         throw new JsonSyntaxException("Missing " + p_188179_1_);
      }
   }

   public static <T> T getAsObject(JsonObject p_188174_0_, String p_188174_1_, JsonDeserializationContext p_188174_2_, Class<? extends T> p_188174_3_) {
      if (p_188174_0_.has(p_188174_1_)) {
         return convertToObject(p_188174_0_.get(p_188174_1_), p_188174_1_, p_188174_2_, p_188174_3_);
      } else {
         throw new JsonSyntaxException("Missing " + p_188174_1_);
      }
   }

   public static <T> T getAsObject(JsonObject p_188177_0_, String p_188177_1_, T p_188177_2_, JsonDeserializationContext p_188177_3_, Class<? extends T> p_188177_4_) {
      return (T)(p_188177_0_.has(p_188177_1_) ? convertToObject(p_188177_0_.get(p_188177_1_), p_188177_1_, p_188177_3_, p_188177_4_) : p_188177_2_);
   }

   public static String getType(JsonElement p_151222_0_) {
      String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf((Object)p_151222_0_), "...", 10);
      if (p_151222_0_ == null) {
         return "null (missing)";
      } else if (p_151222_0_.isJsonNull()) {
         return "null (json)";
      } else if (p_151222_0_.isJsonArray()) {
         return "an array (" + s + ")";
      } else if (p_151222_0_.isJsonObject()) {
         return "an object (" + s + ")";
      } else {
         if (p_151222_0_.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = p_151222_0_.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               return "a number (" + s + ")";
            }

            if (jsonprimitive.isBoolean()) {
               return "a boolean (" + s + ")";
            }
         }

         return s;
      }
   }

   @Nullable
   public static <T> T fromJson(Gson p_188173_0_, Reader p_188173_1_, Class<T> p_188173_2_, boolean p_188173_3_) {
      try {
         JsonReader jsonreader = new JsonReader(p_188173_1_);
         jsonreader.setLenient(p_188173_3_);
         return p_188173_0_.getAdapter(p_188173_2_).read(jsonreader);
      } catch (IOException ioexception) {
         throw new JsonParseException(ioexception);
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson p_233011_0_, Reader p_233011_1_, TypeToken<T> p_233011_2_, boolean p_233011_3_) {
      try {
         JsonReader jsonreader = new JsonReader(p_233011_1_);
         jsonreader.setLenient(p_233011_3_);
         return p_233011_0_.getAdapter(p_233011_2_).read(jsonreader);
      } catch (IOException ioexception) {
         throw new JsonParseException(ioexception);
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson p_233013_0_, String p_233013_1_, TypeToken<T> p_233013_2_, boolean p_233013_3_) {
      return fromJson(p_233013_0_, new StringReader(p_233013_1_), p_233013_2_, p_233013_3_);
   }

   @Nullable
   public static <T> T fromJson(Gson p_188176_0_, String p_188176_1_, Class<T> p_188176_2_, boolean p_188176_3_) {
      return fromJson(p_188176_0_, new StringReader(p_188176_1_), p_188176_2_, p_188176_3_);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson p_233010_0_, Reader p_233010_1_, TypeToken<T> p_233010_2_) {
      return fromJson(p_233010_0_, p_233010_1_, p_233010_2_, false);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson p_233012_0_, String p_233012_1_, TypeToken<T> p_233012_2_) {
      return fromJson(p_233012_0_, p_233012_1_, p_233012_2_, false);
   }

   @Nullable
   public static <T> T fromJson(Gson p_193839_0_, Reader p_193839_1_, Class<T> p_193839_2_) {
      return fromJson(p_193839_0_, p_193839_1_, p_193839_2_, false);
   }

   @Nullable
   public static <T> T fromJson(Gson p_188178_0_, String p_188178_1_, Class<T> p_188178_2_) {
      return fromJson(p_188178_0_, p_188178_1_, p_188178_2_, false);
   }

   public static JsonObject parse(String p_212746_0_, boolean p_212746_1_) {
      return parse(new StringReader(p_212746_0_), p_212746_1_);
   }

   public static JsonObject parse(Reader p_212744_0_, boolean p_212744_1_) {
      return fromJson(GSON, p_212744_0_, JsonObject.class, p_212744_1_);
   }

   public static JsonObject parse(String p_212745_0_) {
      return parse(p_212745_0_, false);
   }

   public static JsonObject parse(Reader p_212743_0_) {
      return parse(p_212743_0_, false);
   }
}
