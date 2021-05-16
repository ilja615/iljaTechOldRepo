package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3d;

public class EntityHasProperty implements ILootCondition {
   private final EntityPredicate predicate;
   private final LootContext.EntityTarget entityTarget;

   private EntityHasProperty(EntityPredicate p_i51196_1_, LootContext.EntityTarget p_i51196_2_) {
      this.predicate = p_i51196_1_;
      this.entityTarget = p_i51196_2_;
   }

   public LootConditionType getType() {
      return LootConditionManager.ENTITY_PROPERTIES;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.ORIGIN, this.entityTarget.getParam());
   }

   public boolean test(LootContext p_test_1_) {
      Entity entity = p_test_1_.getParamOrNull(this.entityTarget.getParam());
      Vector3d vector3d = p_test_1_.getParamOrNull(LootParameters.ORIGIN);
      return this.predicate.matches(p_test_1_.getLevel(), vector3d, entity);
   }

   public static ILootCondition.IBuilder entityPresent(LootContext.EntityTarget p_215998_0_) {
      return hasProperties(p_215998_0_, EntityPredicate.Builder.entity());
   }

   public static ILootCondition.IBuilder hasProperties(LootContext.EntityTarget p_215999_0_, EntityPredicate.Builder p_215999_1_) {
      return () -> {
         return new EntityHasProperty(p_215999_1_.build(), p_215999_0_);
      };
   }

   public static ILootCondition.IBuilder hasProperties(LootContext.EntityTarget p_237477_0_, EntityPredicate p_237477_1_) {
      return () -> {
         return new EntityHasProperty(p_237477_1_, p_237477_0_);
      };
   }

   public static class Serializer implements ILootSerializer<EntityHasProperty> {
      public void serialize(JsonObject p_230424_1_, EntityHasProperty p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.add("predicate", p_230424_2_.predicate.serializeToJson());
         p_230424_1_.add("entity", p_230424_3_.serialize(p_230424_2_.entityTarget));
      }

      public EntityHasProperty deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         EntityPredicate entitypredicate = EntityPredicate.fromJson(p_230423_1_.get("predicate"));
         return new EntityHasProperty(entitypredicate, JSONUtils.getAsObject(p_230423_1_, "entity", p_230423_2_, LootContext.EntityTarget.class));
      }
   }
}
