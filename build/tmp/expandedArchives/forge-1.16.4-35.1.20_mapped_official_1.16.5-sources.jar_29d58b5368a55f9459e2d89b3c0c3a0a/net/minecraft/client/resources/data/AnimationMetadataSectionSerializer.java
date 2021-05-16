package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSectionSerializer implements IMetadataSectionSerializer<AnimationMetadataSection> {
   public AnimationMetadataSection fromJson(JsonObject p_195812_1_) {
      List<AnimationFrame> list = Lists.newArrayList();
      int i = JSONUtils.getAsInt(p_195812_1_, "frametime", 1);
      if (i != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
      }

      if (p_195812_1_.has("frames")) {
         try {
            JsonArray jsonarray = JSONUtils.getAsJsonArray(p_195812_1_, "frames");

            for(int j = 0; j < jsonarray.size(); ++j) {
               JsonElement jsonelement = jsonarray.get(j);
               AnimationFrame animationframe = this.getFrame(j, jsonelement);
               if (animationframe != null) {
                  list.add(animationframe);
               }
            }
         } catch (ClassCastException classcastexception) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + p_195812_1_.get("frames"), classcastexception);
         }
      }

      int k = JSONUtils.getAsInt(p_195812_1_, "width", -1);
      int l = JSONUtils.getAsInt(p_195812_1_, "height", -1);
      if (k != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)k, "Invalid width");
      }

      if (l != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)l, "Invalid height");
      }

      boolean flag = JSONUtils.getAsBoolean(p_195812_1_, "interpolate", false);
      return new AnimationMetadataSection(list, k, l, i, flag);
   }

   private AnimationFrame getFrame(int p_110492_1_, JsonElement p_110492_2_) {
      if (p_110492_2_.isJsonPrimitive()) {
         return new AnimationFrame(JSONUtils.convertToInt(p_110492_2_, "frames[" + p_110492_1_ + "]"));
      } else if (p_110492_2_.isJsonObject()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_110492_2_, "frames[" + p_110492_1_ + "]");
         int i = JSONUtils.getAsInt(jsonobject, "time", -1);
         if (jsonobject.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid frame time");
         }

         int j = JSONUtils.getAsInt(jsonobject, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)j, "Invalid frame index");
         return new AnimationFrame(j, i);
      } else {
         return null;
      }
   }

   public String getMetadataSectionName() {
      return "animation";
   }
}
