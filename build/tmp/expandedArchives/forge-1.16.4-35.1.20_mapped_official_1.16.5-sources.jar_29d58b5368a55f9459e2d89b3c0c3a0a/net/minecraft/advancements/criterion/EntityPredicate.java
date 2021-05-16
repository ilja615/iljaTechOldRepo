package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.FishingPredicate;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class EntityPredicate {
   public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NBTPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, FishingPredicate.ANY, (String)null, (ResourceLocation)null);
   private final EntityTypePredicate entityType;
   private final DistancePredicate distanceToPlayer;
   private final LocationPredicate location;
   private final MobEffectsPredicate effects;
   private final NBTPredicate nbt;
   private final EntityFlagsPredicate flags;
   private final EntityEquipmentPredicate equipment;
   private final PlayerPredicate player;
   private final FishingPredicate fishingHook;
   private final EntityPredicate vehicle;
   private final EntityPredicate targetedEntity;
   @Nullable
   private final String team;
   @Nullable
   private final ResourceLocation catType;

   private EntityPredicate(EntityTypePredicate p_i241236_1_, DistancePredicate p_i241236_2_, LocationPredicate p_i241236_3_, MobEffectsPredicate p_i241236_4_, NBTPredicate p_i241236_5_, EntityFlagsPredicate p_i241236_6_, EntityEquipmentPredicate p_i241236_7_, PlayerPredicate p_i241236_8_, FishingPredicate p_i241236_9_, @Nullable String p_i241236_10_, @Nullable ResourceLocation p_i241236_11_) {
      this.entityType = p_i241236_1_;
      this.distanceToPlayer = p_i241236_2_;
      this.location = p_i241236_3_;
      this.effects = p_i241236_4_;
      this.nbt = p_i241236_5_;
      this.flags = p_i241236_6_;
      this.equipment = p_i241236_7_;
      this.player = p_i241236_8_;
      this.fishingHook = p_i241236_9_;
      this.vehicle = this;
      this.targetedEntity = this;
      this.team = p_i241236_10_;
      this.catType = p_i241236_11_;
   }

   private EntityPredicate(EntityTypePredicate p_i231578_1_, DistancePredicate p_i231578_2_, LocationPredicate p_i231578_3_, MobEffectsPredicate p_i231578_4_, NBTPredicate p_i231578_5_, EntityFlagsPredicate p_i231578_6_, EntityEquipmentPredicate p_i231578_7_, PlayerPredicate p_i231578_8_, FishingPredicate p_i231578_9_, EntityPredicate p_i231578_10_, EntityPredicate p_i231578_11_, @Nullable String p_i231578_12_, @Nullable ResourceLocation p_i231578_13_) {
      this.entityType = p_i231578_1_;
      this.distanceToPlayer = p_i231578_2_;
      this.location = p_i231578_3_;
      this.effects = p_i231578_4_;
      this.nbt = p_i231578_5_;
      this.flags = p_i231578_6_;
      this.equipment = p_i231578_7_;
      this.player = p_i231578_8_;
      this.fishingHook = p_i231578_9_;
      this.vehicle = p_i231578_10_;
      this.targetedEntity = p_i231578_11_;
      this.team = p_i231578_12_;
      this.catType = p_i231578_13_;
   }

   public boolean matches(ServerPlayerEntity p_192482_1_, @Nullable Entity p_192482_2_) {
      return this.matches(p_192482_1_.getLevel(), p_192482_1_.position(), p_192482_2_);
   }

   public boolean matches(ServerWorld p_217993_1_, @Nullable Vector3d p_217993_2_, @Nullable Entity p_217993_3_) {
      if (this == ANY) {
         return true;
      } else if (p_217993_3_ == null) {
         return false;
      } else if (!this.entityType.matches(p_217993_3_.getType())) {
         return false;
      } else {
         if (p_217993_2_ == null) {
            if (this.distanceToPlayer != DistancePredicate.ANY) {
               return false;
            }
         } else if (!this.distanceToPlayer.matches(p_217993_2_.x, p_217993_2_.y, p_217993_2_.z, p_217993_3_.getX(), p_217993_3_.getY(), p_217993_3_.getZ())) {
            return false;
         }

         if (!this.location.matches(p_217993_1_, p_217993_3_.getX(), p_217993_3_.getY(), p_217993_3_.getZ())) {
            return false;
         } else if (!this.effects.matches(p_217993_3_)) {
            return false;
         } else if (!this.nbt.matches(p_217993_3_)) {
            return false;
         } else if (!this.flags.matches(p_217993_3_)) {
            return false;
         } else if (!this.equipment.matches(p_217993_3_)) {
            return false;
         } else if (!this.player.matches(p_217993_3_)) {
            return false;
         } else if (!this.fishingHook.matches(p_217993_3_)) {
            return false;
         } else if (!this.vehicle.matches(p_217993_1_, p_217993_2_, p_217993_3_.getVehicle())) {
            return false;
         } else if (!this.targetedEntity.matches(p_217993_1_, p_217993_2_, p_217993_3_ instanceof MobEntity ? ((MobEntity)p_217993_3_).getTarget() : null)) {
            return false;
         } else {
            if (this.team != null) {
               Team team = p_217993_3_.getTeam();
               if (team == null || !this.team.equals(team.getName())) {
                  return false;
               }
            }

            return this.catType == null || p_217993_3_ instanceof CatEntity && ((CatEntity)p_217993_3_).getResourceLocation().equals(this.catType);
         }
      }
   }

   public static EntityPredicate fromJson(@Nullable JsonElement p_192481_0_) {
      if (p_192481_0_ != null && !p_192481_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_192481_0_, "entity");
         EntityTypePredicate entitytypepredicate = EntityTypePredicate.fromJson(jsonobject.get("type"));
         DistancePredicate distancepredicate = DistancePredicate.fromJson(jsonobject.get("distance"));
         LocationPredicate locationpredicate = LocationPredicate.fromJson(jsonobject.get("location"));
         MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.fromJson(jsonobject.get("effects"));
         NBTPredicate nbtpredicate = NBTPredicate.fromJson(jsonobject.get("nbt"));
         EntityFlagsPredicate entityflagspredicate = EntityFlagsPredicate.fromJson(jsonobject.get("flags"));
         EntityEquipmentPredicate entityequipmentpredicate = EntityEquipmentPredicate.fromJson(jsonobject.get("equipment"));
         PlayerPredicate playerpredicate = PlayerPredicate.fromJson(jsonobject.get("player"));
         FishingPredicate fishingpredicate = FishingPredicate.fromJson(jsonobject.get("fishing_hook"));
         EntityPredicate entitypredicate = fromJson(jsonobject.get("vehicle"));
         EntityPredicate entitypredicate1 = fromJson(jsonobject.get("targeted_entity"));
         String s = JSONUtils.getAsString(jsonobject, "team", (String)null);
         ResourceLocation resourcelocation = jsonobject.has("catType") ? new ResourceLocation(JSONUtils.getAsString(jsonobject, "catType")) : null;
         return (new EntityPredicate.Builder()).entityType(entitytypepredicate).distance(distancepredicate).located(locationpredicate).effects(mobeffectspredicate).nbt(nbtpredicate).flags(entityflagspredicate).equipment(entityequipmentpredicate).player(playerpredicate).fishingHook(fishingpredicate).team(s).vehicle(entitypredicate).targetedEntity(entitypredicate1).catType(resourcelocation).build();
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("type", this.entityType.serializeToJson());
         jsonobject.add("distance", this.distanceToPlayer.serializeToJson());
         jsonobject.add("location", this.location.serializeToJson());
         jsonobject.add("effects", this.effects.serializeToJson());
         jsonobject.add("nbt", this.nbt.serializeToJson());
         jsonobject.add("flags", this.flags.serializeToJson());
         jsonobject.add("equipment", this.equipment.serializeToJson());
         jsonobject.add("player", this.player.serializeToJson());
         jsonobject.add("fishing_hook", this.fishingHook.serializeToJson());
         jsonobject.add("vehicle", this.vehicle.serializeToJson());
         jsonobject.add("targeted_entity", this.targetedEntity.serializeToJson());
         jsonobject.addProperty("team", this.team);
         if (this.catType != null) {
            jsonobject.addProperty("catType", this.catType.toString());
         }

         return jsonobject;
      }
   }

   public static LootContext createContext(ServerPlayerEntity p_234575_0_, Entity p_234575_1_) {
      return (new LootContext.Builder(p_234575_0_.getLevel())).withParameter(LootParameters.THIS_ENTITY, p_234575_1_).withParameter(LootParameters.ORIGIN, p_234575_0_.position()).withRandom(p_234575_0_.getRandom()).create(LootParameterSets.ADVANCEMENT_ENTITY);
   }

   public static class AndPredicate {
      public static final EntityPredicate.AndPredicate ANY = new EntityPredicate.AndPredicate(new ILootCondition[0]);
      private final ILootCondition[] conditions;
      private final Predicate<LootContext> compositePredicates;

      private AndPredicate(ILootCondition[] p_i231580_1_) {
         this.conditions = p_i231580_1_;
         this.compositePredicates = LootConditionManager.andConditions(p_i231580_1_);
      }

      public static EntityPredicate.AndPredicate create(ILootCondition... p_234591_0_) {
         return new EntityPredicate.AndPredicate(p_234591_0_);
      }

      public static EntityPredicate.AndPredicate fromJson(JsonObject p_234587_0_, String p_234587_1_, ConditionArrayParser p_234587_2_) {
         JsonElement jsonelement = p_234587_0_.get(p_234587_1_);
         return fromElement(p_234587_1_, p_234587_2_, jsonelement);
      }

      public static EntityPredicate.AndPredicate[] fromJsonArray(JsonObject p_234592_0_, String p_234592_1_, ConditionArrayParser p_234592_2_) {
         JsonElement jsonelement = p_234592_0_.get(p_234592_1_);
         if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = JSONUtils.convertToJsonArray(jsonelement, p_234592_1_);
            EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[jsonarray.size()];

            for(int i = 0; i < jsonarray.size(); ++i) {
               aentitypredicate$andpredicate[i] = fromElement(p_234592_1_ + "[" + i + "]", p_234592_2_, jsonarray.get(i));
            }

            return aentitypredicate$andpredicate;
         } else {
            return new EntityPredicate.AndPredicate[0];
         }
      }

      private static EntityPredicate.AndPredicate fromElement(String p_234589_0_, ConditionArrayParser p_234589_1_, @Nullable JsonElement p_234589_2_) {
         if (p_234589_2_ != null && p_234589_2_.isJsonArray()) {
            ILootCondition[] ailootcondition = p_234589_1_.deserializeConditions(p_234589_2_.getAsJsonArray(), p_234589_1_.getAdvancementId().toString() + "/" + p_234589_0_, LootParameterSets.ADVANCEMENT_ENTITY);
            return new EntityPredicate.AndPredicate(ailootcondition);
         } else {
            EntityPredicate entitypredicate = EntityPredicate.fromJson(p_234589_2_);
            return wrap(entitypredicate);
         }
      }

      public static EntityPredicate.AndPredicate wrap(EntityPredicate p_234585_0_) {
         if (p_234585_0_ == EntityPredicate.ANY) {
            return ANY;
         } else {
            ILootCondition ilootcondition = EntityHasProperty.hasProperties(LootContext.EntityTarget.THIS, p_234585_0_).build();
            return new EntityPredicate.AndPredicate(new ILootCondition[]{ilootcondition});
         }
      }

      public boolean matches(LootContext p_234588_1_) {
         return this.compositePredicates.test(p_234588_1_);
      }

      public JsonElement toJson(ConditionArraySerializer p_234586_1_) {
         return (JsonElement)(this.conditions.length == 0 ? JsonNull.INSTANCE : p_234586_1_.serializeConditions(this.conditions));
      }

      public static JsonElement toJson(EntityPredicate.AndPredicate[] p_234590_0_, ConditionArraySerializer p_234590_1_) {
         if (p_234590_0_.length == 0) {
            return JsonNull.INSTANCE;
         } else {
            JsonArray jsonarray = new JsonArray();

            for(EntityPredicate.AndPredicate entitypredicate$andpredicate : p_234590_0_) {
               jsonarray.add(entitypredicate$andpredicate.toJson(p_234590_1_));
            }

            return jsonarray;
         }
      }
   }

   public static class Builder {
      private EntityTypePredicate entityType = EntityTypePredicate.ANY;
      private DistancePredicate distanceToPlayer = DistancePredicate.ANY;
      private LocationPredicate location = LocationPredicate.ANY;
      private MobEffectsPredicate effects = MobEffectsPredicate.ANY;
      private NBTPredicate nbt = NBTPredicate.ANY;
      private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
      private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
      private PlayerPredicate player = PlayerPredicate.ANY;
      private FishingPredicate fishingHook = FishingPredicate.ANY;
      private EntityPredicate vehicle = EntityPredicate.ANY;
      private EntityPredicate targetedEntity = EntityPredicate.ANY;
      private String team;
      private ResourceLocation catType;

      public static EntityPredicate.Builder entity() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder of(EntityType<?> p_203998_1_) {
         this.entityType = EntityTypePredicate.of(p_203998_1_);
         return this;
      }

      public EntityPredicate.Builder of(ITag<EntityType<?>> p_217989_1_) {
         this.entityType = EntityTypePredicate.of(p_217989_1_);
         return this;
      }

      public EntityPredicate.Builder of(ResourceLocation p_217986_1_) {
         this.catType = p_217986_1_;
         return this;
      }

      public EntityPredicate.Builder entityType(EntityTypePredicate p_209366_1_) {
         this.entityType = p_209366_1_;
         return this;
      }

      public EntityPredicate.Builder distance(DistancePredicate p_203997_1_) {
         this.distanceToPlayer = p_203997_1_;
         return this;
      }

      public EntityPredicate.Builder located(LocationPredicate p_203999_1_) {
         this.location = p_203999_1_;
         return this;
      }

      public EntityPredicate.Builder effects(MobEffectsPredicate p_209367_1_) {
         this.effects = p_209367_1_;
         return this;
      }

      public EntityPredicate.Builder nbt(NBTPredicate p_209365_1_) {
         this.nbt = p_209365_1_;
         return this;
      }

      public EntityPredicate.Builder flags(EntityFlagsPredicate p_217987_1_) {
         this.flags = p_217987_1_;
         return this;
      }

      public EntityPredicate.Builder equipment(EntityEquipmentPredicate p_217985_1_) {
         this.equipment = p_217985_1_;
         return this;
      }

      public EntityPredicate.Builder player(PlayerPredicate p_226613_1_) {
         this.player = p_226613_1_;
         return this;
      }

      public EntityPredicate.Builder fishingHook(FishingPredicate p_234580_1_) {
         this.fishingHook = p_234580_1_;
         return this;
      }

      public EntityPredicate.Builder vehicle(EntityPredicate p_234579_1_) {
         this.vehicle = p_234579_1_;
         return this;
      }

      public EntityPredicate.Builder targetedEntity(EntityPredicate p_234581_1_) {
         this.targetedEntity = p_234581_1_;
         return this;
      }

      public EntityPredicate.Builder team(@Nullable String p_226614_1_) {
         this.team = p_226614_1_;
         return this;
      }

      public EntityPredicate.Builder catType(@Nullable ResourceLocation p_217988_1_) {
         this.catType = p_217988_1_;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.effects, this.nbt, this.flags, this.equipment, this.player, this.fishingHook, this.vehicle, this.targetedEntity, this.team, this.catType);
      }
   }
}
