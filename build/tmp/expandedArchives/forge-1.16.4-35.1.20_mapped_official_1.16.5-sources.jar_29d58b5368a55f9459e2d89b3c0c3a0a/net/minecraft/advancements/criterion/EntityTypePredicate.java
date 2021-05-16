package net.minecraft.advancements.criterion;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public abstract class EntityTypePredicate {
   public static final EntityTypePredicate ANY = new EntityTypePredicate() {
      public boolean matches(EntityType<?> p_209368_1_) {
         return true;
      }

      public JsonElement serializeToJson() {
         return JsonNull.INSTANCE;
      }
   };
   private static final Joiner COMMA_JOINER = Joiner.on(", ");

   public abstract boolean matches(EntityType<?> p_209368_1_);

   public abstract JsonElement serializeToJson();

   public static EntityTypePredicate fromJson(@Nullable JsonElement p_209370_0_) {
      if (p_209370_0_ != null && !p_209370_0_.isJsonNull()) {
         String s = JSONUtils.convertToString(p_209370_0_, "type");
         if (s.startsWith("#")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(s.substring(1));
            return new EntityTypePredicate.TagPredicate(TagCollectionManager.getInstance().getEntityTypes().getTagOrEmpty(resourcelocation1));
         } else {
            ResourceLocation resourcelocation = new ResourceLocation(s);
            EntityType<?> entitytype = Registry.ENTITY_TYPE.getOptional(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown entity type '" + resourcelocation + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.keySet()));
            });
            return new EntityTypePredicate.TypePredicate(entitytype);
         }
      } else {
         return ANY;
      }
   }

   public static EntityTypePredicate of(EntityType<?> p_217999_0_) {
      return new EntityTypePredicate.TypePredicate(p_217999_0_);
   }

   public static EntityTypePredicate of(ITag<EntityType<?>> p_217998_0_) {
      return new EntityTypePredicate.TagPredicate(p_217998_0_);
   }

   static class TagPredicate extends EntityTypePredicate {
      private final ITag<EntityType<?>> tag;

      public TagPredicate(ITag<EntityType<?>> p_i50558_1_) {
         this.tag = p_i50558_1_;
      }

      public boolean matches(EntityType<?> p_209368_1_) {
         return this.tag.contains(p_209368_1_);
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive("#" + TagCollectionManager.getInstance().getEntityTypes().getIdOrThrow(this.tag));
      }
   }

   static class TypePredicate extends EntityTypePredicate {
      private final EntityType<?> type;

      public TypePredicate(EntityType<?> p_i50556_1_) {
         this.type = p_i50556_1_;
      }

      public boolean matches(EntityType<?> p_209368_1_) {
         return this.type == p_209368_1_;
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.type).toString());
      }
   }
}
