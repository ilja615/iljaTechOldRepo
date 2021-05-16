package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
   public static final DistancePredicate ANY = new DistancePredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY);
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   private final MinMaxBounds.FloatBound horizontal;
   private final MinMaxBounds.FloatBound absolute;

   public DistancePredicate(MinMaxBounds.FloatBound p_i49724_1_, MinMaxBounds.FloatBound p_i49724_2_, MinMaxBounds.FloatBound p_i49724_3_, MinMaxBounds.FloatBound p_i49724_4_, MinMaxBounds.FloatBound p_i49724_5_) {
      this.x = p_i49724_1_;
      this.y = p_i49724_2_;
      this.z = p_i49724_3_;
      this.horizontal = p_i49724_4_;
      this.absolute = p_i49724_5_;
   }

   public static DistancePredicate horizontal(MinMaxBounds.FloatBound p_203995_0_) {
      return new DistancePredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, p_203995_0_, MinMaxBounds.FloatBound.ANY);
   }

   public static DistancePredicate vertical(MinMaxBounds.FloatBound p_203993_0_) {
      return new DistancePredicate(MinMaxBounds.FloatBound.ANY, p_203993_0_, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY);
   }

   public boolean matches(double p_193422_1_, double p_193422_3_, double p_193422_5_, double p_193422_7_, double p_193422_9_, double p_193422_11_) {
      float f = (float)(p_193422_1_ - p_193422_7_);
      float f1 = (float)(p_193422_3_ - p_193422_9_);
      float f2 = (float)(p_193422_5_ - p_193422_11_);
      if (this.x.matches(MathHelper.abs(f)) && this.y.matches(MathHelper.abs(f1)) && this.z.matches(MathHelper.abs(f2))) {
         if (!this.horizontal.matchesSqr((double)(f * f + f2 * f2))) {
            return false;
         } else {
            return this.absolute.matchesSqr((double)(f * f + f1 * f1 + f2 * f2));
         }
      } else {
         return false;
      }
   }

   public static DistancePredicate fromJson(@Nullable JsonElement p_193421_0_) {
      if (p_193421_0_ != null && !p_193421_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_193421_0_, "distance");
         MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(jsonobject.get("x"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("y"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("z"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound3 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("horizontal"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound4 = MinMaxBounds.FloatBound.fromJson(jsonobject.get("absolute"));
         return new DistancePredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, minmaxbounds$floatbound3, minmaxbounds$floatbound4);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("x", this.x.serializeToJson());
         jsonobject.add("y", this.y.serializeToJson());
         jsonobject.add("z", this.z.serializeToJson());
         jsonobject.add("horizontal", this.horizontal.serializeToJson());
         jsonobject.add("absolute", this.absolute.serializeToJson());
         return jsonobject;
      }
   }
}
