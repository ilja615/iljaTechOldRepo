package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JSONUtils;

public class DamagePredicate {
   public static final DamagePredicate ANY = DamagePredicate.Builder.damageInstance().build();
   private final MinMaxBounds.FloatBound dealtDamage;
   private final MinMaxBounds.FloatBound takenDamage;
   private final EntityPredicate sourceEntity;
   private final Boolean blocked;
   private final DamageSourcePredicate type;

   public DamagePredicate() {
      this.dealtDamage = MinMaxBounds.FloatBound.ANY;
      this.takenDamage = MinMaxBounds.FloatBound.ANY;
      this.sourceEntity = EntityPredicate.ANY;
      this.blocked = null;
      this.type = DamageSourcePredicate.ANY;
   }

   public DamagePredicate(MinMaxBounds.FloatBound p_i49725_1_, MinMaxBounds.FloatBound p_i49725_2_, EntityPredicate p_i49725_3_, @Nullable Boolean p_i49725_4_, DamageSourcePredicate p_i49725_5_) {
      this.dealtDamage = p_i49725_1_;
      this.takenDamage = p_i49725_2_;
      this.sourceEntity = p_i49725_3_;
      this.blocked = p_i49725_4_;
      this.type = p_i49725_5_;
   }

   public boolean matches(ServerPlayerEntity p_192365_1_, DamageSource p_192365_2_, float p_192365_3_, float p_192365_4_, boolean p_192365_5_) {
      if (this == ANY) {
         return true;
      } else if (!this.dealtDamage.matches(p_192365_3_)) {
         return false;
      } else if (!this.takenDamage.matches(p_192365_4_)) {
         return false;
      } else if (!this.sourceEntity.matches(p_192365_1_, p_192365_2_.getEntity())) {
         return false;
      } else if (this.blocked != null && this.blocked != p_192365_5_) {
         return false;
      } else {
         return this.type.matches(p_192365_1_, p_192365_2_);
      }
   }

   public static DamagePredicate fromJson(@Nullable JsonElement p_192364_0_) {
      if (p_192364_0_ != null && !p_192364_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_192364_0_, "damage");
         MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(jsonobject.get("dealt"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("taken"));
         Boolean obool = jsonobject.has("blocked") ? JSONUtils.getAsBoolean(jsonobject, "blocked") : null;
         EntityPredicate entitypredicate = EntityPredicate.fromJson(jsonobject.get("source_entity"));
         DamageSourcePredicate damagesourcepredicate = DamageSourcePredicate.fromJson(jsonobject.get("type"));
         return new DamagePredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, entitypredicate, obool, damagesourcepredicate);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("dealt", this.dealtDamage.serializeToJson());
         jsonobject.add("taken", this.takenDamage.serializeToJson());
         jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
         jsonobject.add("type", this.type.serializeToJson());
         if (this.blocked != null) {
            jsonobject.addProperty("blocked", this.blocked);
         }

         return jsonobject;
      }
   }

   public static class Builder {
      private MinMaxBounds.FloatBound dealtDamage = MinMaxBounds.FloatBound.ANY;
      private MinMaxBounds.FloatBound takenDamage = MinMaxBounds.FloatBound.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;
      private Boolean blocked;
      private DamageSourcePredicate type = DamageSourcePredicate.ANY;

      public static DamagePredicate.Builder damageInstance() {
         return new DamagePredicate.Builder();
      }

      public DamagePredicate.Builder blocked(Boolean p_203968_1_) {
         this.blocked = p_203968_1_;
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate.Builder p_203969_1_) {
         this.type = p_203969_1_.build();
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
      }
   }
}
