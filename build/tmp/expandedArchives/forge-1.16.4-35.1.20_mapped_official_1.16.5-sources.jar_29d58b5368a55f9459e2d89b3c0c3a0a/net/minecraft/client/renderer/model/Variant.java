package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Variant implements IModelTransform {
   private final ResourceLocation modelLocation;
   private final TransformationMatrix rotation;
   private final boolean uvLock;
   private final int weight;

   public Variant(ResourceLocation p_i226001_1_, TransformationMatrix p_i226001_2_, boolean p_i226001_3_, int p_i226001_4_) {
      this.modelLocation = p_i226001_1_;
      this.rotation = p_i226001_2_;
      this.uvLock = p_i226001_3_;
      this.weight = p_i226001_4_;
   }

   public ResourceLocation getModelLocation() {
      return this.modelLocation;
   }

   public TransformationMatrix getRotation() {
      return this.rotation;
   }

   public boolean isUvLocked() {
      return this.uvLock;
   }

   public int getWeight() {
      return this.weight;
   }

   public String toString() {
      return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Variant)) {
         return false;
      } else {
         Variant variant = (Variant)p_equals_1_;
         return this.modelLocation.equals(variant.modelLocation) && Objects.equals(this.rotation, variant.rotation) && this.uvLock == variant.uvLock && this.weight == variant.weight;
      }
   }

   public int hashCode() {
      int i = this.modelLocation.hashCode();
      i = 31 * i + this.rotation.hashCode();
      i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
      return 31 * i + this.weight;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Variant> {
      public Variant deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         ResourceLocation resourcelocation = this.getModel(jsonobject);
         ModelRotation modelrotation = this.getBlockRotation(jsonobject);
         boolean flag = this.getUvLock(jsonobject);
         int i = this.getWeight(jsonobject);
         return new Variant(resourcelocation, modelrotation.getRotation(), flag, i);
      }

      private boolean getUvLock(JsonObject p_188044_1_) {
         return JSONUtils.getAsBoolean(p_188044_1_, "uvlock", false);
      }

      protected ModelRotation getBlockRotation(JsonObject p_188042_1_) {
         int i = JSONUtils.getAsInt(p_188042_1_, "x", 0);
         int j = JSONUtils.getAsInt(p_188042_1_, "y", 0);
         ModelRotation modelrotation = ModelRotation.by(i, j);
         if (modelrotation == null) {
            throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
         } else {
            return modelrotation;
         }
      }

      protected ResourceLocation getModel(JsonObject p_188043_1_) {
         return new ResourceLocation(JSONUtils.getAsString(p_188043_1_, "model"));
      }

      protected int getWeight(JsonObject p_188045_1_) {
         int i = JSONUtils.getAsInt(p_188045_1_, "weight", 1);
         if (i < 1) {
            throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
         } else {
            return i;
         }
      }
   }
}
