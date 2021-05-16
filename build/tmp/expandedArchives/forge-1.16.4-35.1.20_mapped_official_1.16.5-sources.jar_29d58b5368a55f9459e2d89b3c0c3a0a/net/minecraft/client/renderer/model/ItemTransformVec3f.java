package net.minecraft.client.renderer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.lang.reflect.Type;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @deprecated use {@link net.minecraft.util.math.vector.TransformationMatrix} through {@link net.minecraftforge.client.extensions.IForgeBakedModel#handlePerspective}
 */
@OnlyIn(Dist.CLIENT)
@Deprecated
public class ItemTransformVec3f {
   public static final ItemTransformVec3f NO_TRANSFORM = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f rotation;
   public final Vector3f translation;
   public final Vector3f scale;

   public ItemTransformVec3f(Vector3f p_i47622_1_, Vector3f p_i47622_2_, Vector3f p_i47622_3_) {
      this.rotation = p_i47622_1_.copy();
      this.translation = p_i47622_2_.copy();
      this.scale = p_i47622_3_.copy();
   }

   public void apply(boolean p_228830_1_, MatrixStack p_228830_2_) {
      if (this != NO_TRANSFORM) {
         float f = this.rotation.x();
         float f1 = this.rotation.y();
         float f2 = this.rotation.z();
         if (p_228830_1_) {
            f1 = -f1;
            f2 = -f2;
         }

         int i = p_228830_1_ ? -1 : 1;
         p_228830_2_.translate((double)((float)i * this.translation.x()), (double)this.translation.y(), (double)this.translation.z());
         p_228830_2_.mulPose(new Quaternion(f, f1, f2, true));
         p_228830_2_.scale(this.scale.x(), this.scale.y(), this.scale.z());
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (this.getClass() != p_equals_1_.getClass()) {
         return false;
      } else {
         ItemTransformVec3f itemtransformvec3f = (ItemTransformVec3f)p_equals_1_;
         return this.rotation.equals(itemtransformvec3f.rotation) && this.scale.equals(itemtransformvec3f.scale) && this.translation.equals(itemtransformvec3f.translation);
      }
   }

   public int hashCode() {
      int i = this.rotation.hashCode();
      i = 31 * i + this.translation.hashCode();
      return 31 * i + this.scale.hashCode();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemTransformVec3f> {
      public static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

      public ItemTransformVec3f deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Vector3f vector3f = this.getVector3f(jsonobject, "rotation", DEFAULT_ROTATION);
         Vector3f vector3f1 = this.getVector3f(jsonobject, "translation", DEFAULT_TRANSLATION);
         vector3f1.mul(0.0625F);
         vector3f1.clamp(-5.0F, 5.0F);
         Vector3f vector3f2 = this.getVector3f(jsonobject, "scale", DEFAULT_SCALE);
         vector3f2.clamp(-4.0F, 4.0F);
         return new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
      }

      private Vector3f getVector3f(JsonObject p_199340_1_, String p_199340_2_, Vector3f p_199340_3_) {
         if (!p_199340_1_.has(p_199340_2_)) {
            return p_199340_3_;
         } else {
            JsonArray jsonarray = JSONUtils.getAsJsonArray(p_199340_1_, p_199340_2_);
            if (jsonarray.size() != 3) {
               throw new JsonParseException("Expected 3 " + p_199340_2_ + " values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[3];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = JSONUtils.convertToFloat(jsonarray.get(i), p_199340_2_ + "[" + i + "]");
               }

               return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
         }
      }
   }
}
