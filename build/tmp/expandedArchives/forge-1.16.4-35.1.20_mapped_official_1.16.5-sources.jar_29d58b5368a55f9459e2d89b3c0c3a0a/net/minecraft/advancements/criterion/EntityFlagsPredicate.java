package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;

public class EntityFlagsPredicate {
   public static final EntityFlagsPredicate ANY = (new EntityFlagsPredicate.Builder()).build();
   @Nullable
   private final Boolean isOnFire;
   @Nullable
   private final Boolean isCrouching;
   @Nullable
   private final Boolean isSprinting;
   @Nullable
   private final Boolean isSwimming;
   @Nullable
   private final Boolean isBaby;

   public EntityFlagsPredicate(@Nullable Boolean p_i50808_1_, @Nullable Boolean p_i50808_2_, @Nullable Boolean p_i50808_3_, @Nullable Boolean p_i50808_4_, @Nullable Boolean p_i50808_5_) {
      this.isOnFire = p_i50808_1_;
      this.isCrouching = p_i50808_2_;
      this.isSprinting = p_i50808_3_;
      this.isSwimming = p_i50808_4_;
      this.isBaby = p_i50808_5_;
   }

   public boolean matches(Entity p_217974_1_) {
      if (this.isOnFire != null && p_217974_1_.isOnFire() != this.isOnFire) {
         return false;
      } else if (this.isCrouching != null && p_217974_1_.isCrouching() != this.isCrouching) {
         return false;
      } else if (this.isSprinting != null && p_217974_1_.isSprinting() != this.isSprinting) {
         return false;
      } else if (this.isSwimming != null && p_217974_1_.isSwimming() != this.isSwimming) {
         return false;
      } else {
         return this.isBaby == null || !(p_217974_1_ instanceof LivingEntity) || ((LivingEntity)p_217974_1_).isBaby() == this.isBaby;
      }
   }

   @Nullable
   private static Boolean getOptionalBoolean(JsonObject p_217977_0_, String p_217977_1_) {
      return p_217977_0_.has(p_217977_1_) ? JSONUtils.getAsBoolean(p_217977_0_, p_217977_1_) : null;
   }

   public static EntityFlagsPredicate fromJson(@Nullable JsonElement p_217975_0_) {
      if (p_217975_0_ != null && !p_217975_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_217975_0_, "entity flags");
         Boolean obool = getOptionalBoolean(jsonobject, "is_on_fire");
         Boolean obool1 = getOptionalBoolean(jsonobject, "is_sneaking");
         Boolean obool2 = getOptionalBoolean(jsonobject, "is_sprinting");
         Boolean obool3 = getOptionalBoolean(jsonobject, "is_swimming");
         Boolean obool4 = getOptionalBoolean(jsonobject, "is_baby");
         return new EntityFlagsPredicate(obool, obool1, obool2, obool3, obool4);
      } else {
         return ANY;
      }
   }

   private void addOptionalBoolean(JsonObject p_217978_1_, String p_217978_2_, @Nullable Boolean p_217978_3_) {
      if (p_217978_3_ != null) {
         p_217978_1_.addProperty(p_217978_2_, p_217978_3_);
      }

   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         this.addOptionalBoolean(jsonobject, "is_on_fire", this.isOnFire);
         this.addOptionalBoolean(jsonobject, "is_sneaking", this.isCrouching);
         this.addOptionalBoolean(jsonobject, "is_sprinting", this.isSprinting);
         this.addOptionalBoolean(jsonobject, "is_swimming", this.isSwimming);
         this.addOptionalBoolean(jsonobject, "is_baby", this.isBaby);
         return jsonobject;
      }
   }

   public static class Builder {
      @Nullable
      private Boolean isOnFire;
      @Nullable
      private Boolean isCrouching;
      @Nullable
      private Boolean isSprinting;
      @Nullable
      private Boolean isSwimming;
      @Nullable
      private Boolean isBaby;

      public static EntityFlagsPredicate.Builder flags() {
         return new EntityFlagsPredicate.Builder();
      }

      public EntityFlagsPredicate.Builder setOnFire(@Nullable Boolean p_217968_1_) {
         this.isOnFire = p_217968_1_;
         return this;
      }

      public EntityFlagsPredicate.Builder setIsBaby(@Nullable Boolean p_241396_1_) {
         this.isBaby = p_241396_1_;
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
      }
   }
}
