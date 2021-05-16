package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
   public static final EnchantmentPredicate[] NONE = new EnchantmentPredicate[0];
   private final Enchantment enchantment;
   private final MinMaxBounds.IntBound level;

   public EnchantmentPredicate() {
      this.enchantment = null;
      this.level = MinMaxBounds.IntBound.ANY;
   }

   public EnchantmentPredicate(@Nullable Enchantment p_i49723_1_, MinMaxBounds.IntBound p_i49723_2_) {
      this.enchantment = p_i49723_1_;
      this.level = p_i49723_2_;
   }

   public boolean containedIn(Map<Enchantment, Integer> p_192463_1_) {
      if (this.enchantment != null) {
         if (!p_192463_1_.containsKey(this.enchantment)) {
            return false;
         }

         int i = p_192463_1_.get(this.enchantment);
         if (this.level != null && !this.level.matches(i)) {
            return false;
         }
      } else if (this.level != null) {
         for(Integer integer : p_192463_1_.values()) {
            if (this.level.matches(integer)) {
               return true;
            }
         }

         return false;
      }

      return true;
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.enchantment != null) {
            jsonobject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(this.enchantment).toString());
         }

         jsonobject.add("levels", this.level.serializeToJson());
         return jsonobject;
      }
   }

   public static EnchantmentPredicate fromJson(@Nullable JsonElement p_192464_0_) {
      if (p_192464_0_ != null && !p_192464_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_192464_0_, "enchantment");
         Enchantment enchantment = null;
         if (jsonobject.has("enchantment")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject, "enchantment"));
            enchantment = Registry.ENCHANTMENT.getOptional(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown enchantment '" + resourcelocation + "'");
            });
         }

         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("levels"));
         return new EnchantmentPredicate(enchantment, minmaxbounds$intbound);
      } else {
         return ANY;
      }
   }

   public static EnchantmentPredicate[] fromJsonArray(@Nullable JsonElement p_192465_0_) {
      if (p_192465_0_ != null && !p_192465_0_.isJsonNull()) {
         JsonArray jsonarray = JSONUtils.convertToJsonArray(p_192465_0_, "enchantments");
         EnchantmentPredicate[] aenchantmentpredicate = new EnchantmentPredicate[jsonarray.size()];

         for(int i = 0; i < aenchantmentpredicate.length; ++i) {
            aenchantmentpredicate[i] = fromJson(jsonarray.get(i));
         }

         return aenchantmentpredicate;
      } else {
         return NONE;
      }
   }
}
