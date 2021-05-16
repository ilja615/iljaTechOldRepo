package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<Effect, MobEffectsPredicate.InstancePredicate> effects;

   public MobEffectsPredicate(Map<Effect, MobEffectsPredicate.InstancePredicate> p_i47538_1_) {
      this.effects = p_i47538_1_;
   }

   public static MobEffectsPredicate effects() {
      return new MobEffectsPredicate(Maps.newLinkedHashMap());
   }

   public MobEffectsPredicate and(Effect p_204015_1_) {
      this.effects.put(p_204015_1_, new MobEffectsPredicate.InstancePredicate());
      return this;
   }

   public boolean matches(Entity p_193469_1_) {
      if (this == ANY) {
         return true;
      } else {
         return p_193469_1_ instanceof LivingEntity ? this.matches(((LivingEntity)p_193469_1_).getActiveEffectsMap()) : false;
      }
   }

   public boolean matches(LivingEntity p_193472_1_) {
      return this == ANY ? true : this.matches(p_193472_1_.getActiveEffectsMap());
   }

   public boolean matches(Map<Effect, EffectInstance> p_193470_1_) {
      if (this == ANY) {
         return true;
      } else {
         for(Entry<Effect, MobEffectsPredicate.InstancePredicate> entry : this.effects.entrySet()) {
            EffectInstance effectinstance = p_193470_1_.get(entry.getKey());
            if (!entry.getValue().matches(effectinstance)) {
               return false;
            }
         }

         return true;
      }
   }

   public static MobEffectsPredicate fromJson(@Nullable JsonElement p_193471_0_) {
      if (p_193471_0_ != null && !p_193471_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_193471_0_, "effects");
         Map<Effect, MobEffectsPredicate.InstancePredicate> map = Maps.newLinkedHashMap();

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            ResourceLocation resourcelocation = new ResourceLocation(entry.getKey());
            Effect effect = Registry.MOB_EFFECT.getOptional(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown effect '" + resourcelocation + "'");
            });
            MobEffectsPredicate.InstancePredicate mobeffectspredicate$instancepredicate = MobEffectsPredicate.InstancePredicate.fromJson(JSONUtils.convertToJsonObject(entry.getValue(), entry.getKey()));
            map.put(effect, mobeffectspredicate$instancepredicate);
         }

         return new MobEffectsPredicate(map);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();

         for(Entry<Effect, MobEffectsPredicate.InstancePredicate> entry : this.effects.entrySet()) {
            jsonobject.add(Registry.MOB_EFFECT.getKey(entry.getKey()).toString(), entry.getValue().serializeToJson());
         }

         return jsonobject;
      }
   }

   public static class InstancePredicate {
      private final MinMaxBounds.IntBound amplifier;
      private final MinMaxBounds.IntBound duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public InstancePredicate(MinMaxBounds.IntBound p_i49709_1_, MinMaxBounds.IntBound p_i49709_2_, @Nullable Boolean p_i49709_3_, @Nullable Boolean p_i49709_4_) {
         this.amplifier = p_i49709_1_;
         this.duration = p_i49709_2_;
         this.ambient = p_i49709_3_;
         this.visible = p_i49709_4_;
      }

      public InstancePredicate() {
         this(MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, (Boolean)null, (Boolean)null);
      }

      public boolean matches(@Nullable EffectInstance p_193463_1_) {
         if (p_193463_1_ == null) {
            return false;
         } else if (!this.amplifier.matches(p_193463_1_.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(p_193463_1_.getDuration())) {
            return false;
         } else if (this.ambient != null && this.ambient != p_193463_1_.isAmbient()) {
            return false;
         } else {
            return this.visible == null || this.visible == p_193463_1_.isVisible();
         }
      }

      public JsonElement serializeToJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("amplifier", this.amplifier.serializeToJson());
         jsonobject.add("duration", this.duration.serializeToJson());
         jsonobject.addProperty("ambient", this.ambient);
         jsonobject.addProperty("visible", this.visible);
         return jsonobject;
      }

      public static MobEffectsPredicate.InstancePredicate fromJson(JsonObject p_193464_0_) {
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_193464_0_.get("amplifier"));
         MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(p_193464_0_.get("duration"));
         Boolean obool = p_193464_0_.has("ambient") ? JSONUtils.getAsBoolean(p_193464_0_, "ambient") : null;
         Boolean obool1 = p_193464_0_.has("visible") ? JSONUtils.getAsBoolean(p_193464_0_, "visible") : null;
         return new MobEffectsPredicate.InstancePredicate(minmaxbounds$intbound, minmaxbounds$intbound1, obool, obool1);
      }
   }
}
