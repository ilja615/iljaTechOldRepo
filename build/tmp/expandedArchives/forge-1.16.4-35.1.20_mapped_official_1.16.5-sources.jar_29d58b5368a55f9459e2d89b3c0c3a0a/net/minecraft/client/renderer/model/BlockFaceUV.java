package net.minecraft.client.renderer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockFaceUV {
   public float[] uvs;
   public final int rotation;

   public BlockFaceUV(@Nullable float[] p_i46228_1_, int p_i46228_2_) {
      this.uvs = p_i46228_1_;
      this.rotation = p_i46228_2_;
   }

   public float getU(int p_178348_1_) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int i = this.getShiftedIndex(p_178348_1_);
         return this.uvs[i != 0 && i != 1 ? 2 : 0];
      }
   }

   public float getV(int p_178346_1_) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int i = this.getShiftedIndex(p_178346_1_);
         return this.uvs[i != 0 && i != 3 ? 3 : 1];
      }
   }

   private int getShiftedIndex(int p_178347_1_) {
      return (p_178347_1_ + this.rotation / 90) % 4;
   }

   public int getReverseIndex(int p_178345_1_) {
      return (p_178345_1_ + 4 - this.rotation / 90) % 4;
   }

   public void setMissingUv(float[] p_178349_1_) {
      if (this.uvs == null) {
         this.uvs = p_178349_1_;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      public BlockFaceUV deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         float[] afloat = this.getUVs(jsonobject);
         int i = this.getRotation(jsonobject);
         return new BlockFaceUV(afloat, i);
      }

      protected int getRotation(JsonObject p_178291_1_) {
         int i = JSONUtils.getAsInt(p_178291_1_, "rotation", 0);
         if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
            return i;
         } else {
            throw new JsonParseException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
         }
      }

      @Nullable
      private float[] getUVs(JsonObject p_178292_1_) {
         if (!p_178292_1_.has("uv")) {
            return null;
         } else {
            JsonArray jsonarray = JSONUtils.getAsJsonArray(p_178292_1_, "uv");
            if (jsonarray.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[4];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = JSONUtils.convertToFloat(jsonarray.get(i), "uv[" + i + "]");
               }

               return afloat;
            }
         }
      }
   }
}
