package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   private final Boolean isProjectile;
   private final Boolean isExplosion;
   private final Boolean bypassesArmor;
   private final Boolean bypassesInvulnerability;
   private final Boolean bypassesMagic;
   private final Boolean isFire;
   private final Boolean isMagic;
   private final Boolean isLightning;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean p_i50810_1_, @Nullable Boolean p_i50810_2_, @Nullable Boolean p_i50810_3_, @Nullable Boolean p_i50810_4_, @Nullable Boolean p_i50810_5_, @Nullable Boolean p_i50810_6_, @Nullable Boolean p_i50810_7_, @Nullable Boolean p_i50810_8_, EntityPredicate p_i50810_9_, EntityPredicate p_i50810_10_) {
      this.isProjectile = p_i50810_1_;
      this.isExplosion = p_i50810_2_;
      this.bypassesArmor = p_i50810_3_;
      this.bypassesInvulnerability = p_i50810_4_;
      this.bypassesMagic = p_i50810_5_;
      this.isFire = p_i50810_6_;
      this.isMagic = p_i50810_7_;
      this.isLightning = p_i50810_8_;
      this.directEntity = p_i50810_9_;
      this.sourceEntity = p_i50810_10_;
   }

   public boolean matches(ServerPlayerEntity p_193418_1_, DamageSource p_193418_2_) {
      return this.matches(p_193418_1_.getLevel(), p_193418_1_.position(), p_193418_2_);
   }

   public boolean matches(ServerWorld p_217952_1_, Vector3d p_217952_2_, DamageSource p_217952_3_) {
      if (this == ANY) {
         return true;
      } else if (this.isProjectile != null && this.isProjectile != p_217952_3_.isProjectile()) {
         return false;
      } else if (this.isExplosion != null && this.isExplosion != p_217952_3_.isExplosion()) {
         return false;
      } else if (this.bypassesArmor != null && this.bypassesArmor != p_217952_3_.isBypassArmor()) {
         return false;
      } else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != p_217952_3_.isBypassInvul()) {
         return false;
      } else if (this.bypassesMagic != null && this.bypassesMagic != p_217952_3_.isBypassMagic()) {
         return false;
      } else if (this.isFire != null && this.isFire != p_217952_3_.isFire()) {
         return false;
      } else if (this.isMagic != null && this.isMagic != p_217952_3_.isMagic()) {
         return false;
      } else if (this.isLightning != null && this.isLightning != (p_217952_3_ == DamageSource.LIGHTNING_BOLT)) {
         return false;
      } else if (!this.directEntity.matches(p_217952_1_, p_217952_2_, p_217952_3_.getDirectEntity())) {
         return false;
      } else {
         return this.sourceEntity.matches(p_217952_1_, p_217952_2_, p_217952_3_.getEntity());
      }
   }

   public static DamageSourcePredicate fromJson(@Nullable JsonElement p_192447_0_) {
      if (p_192447_0_ != null && !p_192447_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_192447_0_, "damage type");
         Boolean obool = getOptionalBoolean(jsonobject, "is_projectile");
         Boolean obool1 = getOptionalBoolean(jsonobject, "is_explosion");
         Boolean obool2 = getOptionalBoolean(jsonobject, "bypasses_armor");
         Boolean obool3 = getOptionalBoolean(jsonobject, "bypasses_invulnerability");
         Boolean obool4 = getOptionalBoolean(jsonobject, "bypasses_magic");
         Boolean obool5 = getOptionalBoolean(jsonobject, "is_fire");
         Boolean obool6 = getOptionalBoolean(jsonobject, "is_magic");
         Boolean obool7 = getOptionalBoolean(jsonobject, "is_lightning");
         EntityPredicate entitypredicate = EntityPredicate.fromJson(jsonobject.get("direct_entity"));
         EntityPredicate entitypredicate1 = EntityPredicate.fromJson(jsonobject.get("source_entity"));
         return new DamageSourcePredicate(obool, obool1, obool2, obool3, obool4, obool5, obool6, obool7, entitypredicate, entitypredicate1);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean getOptionalBoolean(JsonObject p_192448_0_, String p_192448_1_) {
      return p_192448_0_.has(p_192448_1_) ? JSONUtils.getAsBoolean(p_192448_0_, p_192448_1_) : null;
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         this.addOptionally(jsonobject, "is_projectile", this.isProjectile);
         this.addOptionally(jsonobject, "is_explosion", this.isExplosion);
         this.addOptionally(jsonobject, "bypasses_armor", this.bypassesArmor);
         this.addOptionally(jsonobject, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addOptionally(jsonobject, "bypasses_magic", this.bypassesMagic);
         this.addOptionally(jsonobject, "is_fire", this.isFire);
         this.addOptionally(jsonobject, "is_magic", this.isMagic);
         this.addOptionally(jsonobject, "is_lightning", this.isLightning);
         jsonobject.add("direct_entity", this.directEntity.serializeToJson());
         jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
         return jsonobject;
      }
   }

   private void addOptionally(JsonObject p_203992_1_, String p_203992_2_, @Nullable Boolean p_203992_3_) {
      if (p_203992_3_ != null) {
         p_203992_1_.addProperty(p_203992_2_, p_203992_3_);
      }

   }

   public static class Builder {
      private Boolean isProjectile;
      private Boolean isExplosion;
      private Boolean bypassesArmor;
      private Boolean bypassesInvulnerability;
      private Boolean bypassesMagic;
      private Boolean isFire;
      private Boolean isMagic;
      private Boolean isLightning;
      private EntityPredicate directEntity = EntityPredicate.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder isProjectile(Boolean p_203978_1_) {
         this.isProjectile = p_203978_1_;
         return this;
      }

      public DamageSourcePredicate.Builder isLightning(Boolean p_217950_1_) {
         this.isLightning = p_217950_1_;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder p_203980_1_) {
         this.directEntity = p_203980_1_.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
      }
   }
}
