package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemCameraTransforms {
   public static final ItemCameraTransforms NO_TRANSFORMS = new ItemCameraTransforms();
   public final ItemTransformVec3f thirdPersonLeftHand;
   public final ItemTransformVec3f thirdPersonRightHand;
   public final ItemTransformVec3f firstPersonLeftHand;
   public final ItemTransformVec3f firstPersonRightHand;
   public final ItemTransformVec3f head;
   public final ItemTransformVec3f gui;
   public final ItemTransformVec3f ground;
   public final ItemTransformVec3f fixed;

   private ItemCameraTransforms() {
      this(ItemTransformVec3f.NO_TRANSFORM, ItemTransformVec3f.NO_TRANSFORM, ItemTransformVec3f.NO_TRANSFORM, ItemTransformVec3f.NO_TRANSFORM, ItemTransformVec3f.NO_TRANSFORM, ItemTransformVec3f.NO_TRANSFORM, ItemTransformVec3f.NO_TRANSFORM, ItemTransformVec3f.NO_TRANSFORM);
   }

   @Deprecated
   public ItemCameraTransforms(ItemCameraTransforms p_i46443_1_) {
      this.thirdPersonLeftHand = p_i46443_1_.thirdPersonLeftHand;
      this.thirdPersonRightHand = p_i46443_1_.thirdPersonRightHand;
      this.firstPersonLeftHand = p_i46443_1_.firstPersonLeftHand;
      this.firstPersonRightHand = p_i46443_1_.firstPersonRightHand;
      this.head = p_i46443_1_.head;
      this.gui = p_i46443_1_.gui;
      this.ground = p_i46443_1_.ground;
      this.fixed = p_i46443_1_.fixed;
   }

   @Deprecated
   public ItemCameraTransforms(ItemTransformVec3f p_i46569_1_, ItemTransformVec3f p_i46569_2_, ItemTransformVec3f p_i46569_3_, ItemTransformVec3f p_i46569_4_, ItemTransformVec3f p_i46569_5_, ItemTransformVec3f p_i46569_6_, ItemTransformVec3f p_i46569_7_, ItemTransformVec3f p_i46569_8_) {
      this.thirdPersonLeftHand = p_i46569_1_;
      this.thirdPersonRightHand = p_i46569_2_;
      this.firstPersonLeftHand = p_i46569_3_;
      this.firstPersonRightHand = p_i46569_4_;
      this.head = p_i46569_5_;
      this.gui = p_i46569_6_;
      this.ground = p_i46569_7_;
      this.fixed = p_i46569_8_;
   }

   @Deprecated
   public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType p_181688_1_) {
      switch(p_181688_1_) {
      case THIRD_PERSON_LEFT_HAND:
         return this.thirdPersonLeftHand;
      case THIRD_PERSON_RIGHT_HAND:
         return this.thirdPersonRightHand;
      case FIRST_PERSON_LEFT_HAND:
         return this.firstPersonLeftHand;
      case FIRST_PERSON_RIGHT_HAND:
         return this.firstPersonRightHand;
      case HEAD:
         return this.head;
      case GUI:
         return this.gui;
      case GROUND:
         return this.ground;
      case FIXED:
         return this.fixed;
      default:
         return ItemTransformVec3f.NO_TRANSFORM;
      }
   }

   public boolean hasTransform(ItemCameraTransforms.TransformType p_181687_1_) {
      return this.getTransform(p_181687_1_) != ItemTransformVec3f.NO_TRANSFORM;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      public ItemCameraTransforms deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         ItemTransformVec3f itemtransformvec3f = this.getTransform(p_deserialize_3_, jsonobject, "thirdperson_righthand");
         ItemTransformVec3f itemtransformvec3f1 = this.getTransform(p_deserialize_3_, jsonobject, "thirdperson_lefthand");
         if (itemtransformvec3f1 == ItemTransformVec3f.NO_TRANSFORM) {
            itemtransformvec3f1 = itemtransformvec3f;
         }

         ItemTransformVec3f itemtransformvec3f2 = this.getTransform(p_deserialize_3_, jsonobject, "firstperson_righthand");
         ItemTransformVec3f itemtransformvec3f3 = this.getTransform(p_deserialize_3_, jsonobject, "firstperson_lefthand");
         if (itemtransformvec3f3 == ItemTransformVec3f.NO_TRANSFORM) {
            itemtransformvec3f3 = itemtransformvec3f2;
         }

         ItemTransformVec3f itemtransformvec3f4 = this.getTransform(p_deserialize_3_, jsonobject, "head");
         ItemTransformVec3f itemtransformvec3f5 = this.getTransform(p_deserialize_3_, jsonobject, "gui");
         ItemTransformVec3f itemtransformvec3f6 = this.getTransform(p_deserialize_3_, jsonobject, "ground");
         ItemTransformVec3f itemtransformvec3f7 = this.getTransform(p_deserialize_3_, jsonobject, "fixed");
         return new ItemCameraTransforms(itemtransformvec3f1, itemtransformvec3f, itemtransformvec3f3, itemtransformvec3f2, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
      }

      private ItemTransformVec3f getTransform(JsonDeserializationContext p_181683_1_, JsonObject p_181683_2_, String p_181683_3_) {
         return p_181683_2_.has(p_181683_3_) ? p_181683_1_.deserialize(p_181683_2_.get(p_181683_3_), ItemTransformVec3f.class) : ItemTransformVec3f.NO_TRANSFORM;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum TransformType {
      NONE,
      THIRD_PERSON_LEFT_HAND,
      THIRD_PERSON_RIGHT_HAND,
      FIRST_PERSON_LEFT_HAND,
      FIRST_PERSON_RIGHT_HAND,
      HEAD,
      GUI,
      GROUND,
      FIXED;

      public boolean firstPerson() {
         return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
      }
   }
}
