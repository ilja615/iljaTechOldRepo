package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootTypesManager {
   public static <E, T extends LootType<E>> LootTypesManager.LootTypeRegistryWrapper<E, T> builder(Registry<T> p_237389_0_, String p_237389_1_, String p_237389_2_, Function<E, T> p_237389_3_) {
      return new LootTypesManager.LootTypeRegistryWrapper<>(p_237389_0_, p_237389_1_, p_237389_2_, p_237389_3_);
   }

   public interface ISerializer<T> {
      JsonElement serialize(T p_237397_1_, JsonSerializationContext p_237397_2_);

      T deserialize(JsonElement p_237396_1_, JsonDeserializationContext p_237396_2_);
   }

   public static class LootTypeRegistryWrapper<E, T extends LootType<E>> {
      private final Registry<T> registry;
      private final String elementName;
      private final String typeKey;
      private final Function<E, T> typeGetter;
      @Nullable
      private Pair<T, LootTypesManager.ISerializer<? extends E>> defaultType;

      private LootTypeRegistryWrapper(Registry<T> p_i232160_1_, String p_i232160_2_, String p_i232160_3_, Function<E, T> p_i232160_4_) {
         this.registry = p_i232160_1_;
         this.elementName = p_i232160_2_;
         this.typeKey = p_i232160_3_;
         this.typeGetter = p_i232160_4_;
      }

      public Object build() {
         return new LootTypesManager.Serializer(this.registry, this.elementName, this.typeKey, this.typeGetter, this.defaultType);
      }
   }

   static class Serializer<E, T extends LootType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {
      private final Registry<T> registry;
      private final String elementName;
      private final String typeKey;
      private final Function<E, T> typeGetter;
      @Nullable
      private final Pair<T, LootTypesManager.ISerializer<? extends E>> defaultType;

      private Serializer(Registry<T> p_i232162_1_, String p_i232162_2_, String p_i232162_3_, Function<E, T> p_i232162_4_, @Nullable Pair<T, LootTypesManager.ISerializer<? extends E>> p_i232162_5_) {
         this.registry = p_i232162_1_;
         this.elementName = p_i232162_2_;
         this.typeKey = p_i232162_3_;
         this.typeGetter = p_i232162_4_;
         this.defaultType = p_i232162_5_;
      }

      public E deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonObject()) {
            JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, this.elementName);
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject, this.typeKey));
            T t = this.registry.get(resourcelocation);
            if (t == null) {
               throw new JsonSyntaxException("Unknown type '" + resourcelocation + "'");
            } else {
               return t.getSerializer().deserialize(jsonobject, p_deserialize_3_);
            }
         } else if (this.defaultType == null) {
            throw new UnsupportedOperationException("Object " + p_deserialize_1_ + " can't be deserialized");
         } else {
            return this.defaultType.getSecond().deserialize(p_deserialize_1_, p_deserialize_3_);
         }
      }

      public JsonElement serialize(E p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         T t = this.typeGetter.apply(p_serialize_1_);
         if (this.defaultType != null && this.defaultType.getFirst() == t) {
            return ((ISerializer<E>)this.defaultType.getSecond()).serialize(p_serialize_1_, p_serialize_3_);
         } else if (t == null) {
            throw new JsonSyntaxException("Unknown type: " + p_serialize_1_);
         } else {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(this.typeKey, this.registry.getKey(t).toString());
            ((LootType)t).getSerializer().serialize(jsonobject, p_serialize_1_, p_serialize_3_);
            return jsonobject;
         }
      }
   }
}
