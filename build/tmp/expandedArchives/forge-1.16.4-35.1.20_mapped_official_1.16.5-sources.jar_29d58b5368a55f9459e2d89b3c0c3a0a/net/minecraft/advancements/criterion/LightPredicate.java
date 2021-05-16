package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class LightPredicate {
   public static final LightPredicate ANY = new LightPredicate(MinMaxBounds.IntBound.ANY);
   private final MinMaxBounds.IntBound composite;

   private LightPredicate(MinMaxBounds.IntBound p_i225753_1_) {
      this.composite = p_i225753_1_;
   }

   public boolean matches(ServerWorld p_226858_1_, BlockPos p_226858_2_) {
      if (this == ANY) {
         return true;
      } else if (!p_226858_1_.isLoaded(p_226858_2_)) {
         return false;
      } else {
         return this.composite.matches(p_226858_1_.getMaxLocalRawBrightness(p_226858_2_));
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("light", this.composite.serializeToJson());
         return jsonobject;
      }
   }

   public static LightPredicate fromJson(@Nullable JsonElement p_226857_0_) {
      if (p_226857_0_ != null && !p_226857_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_226857_0_, "light");
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("light"));
         return new LightPredicate(minmaxbounds$intbound);
      } else {
         return ANY;
      }
   }
}
