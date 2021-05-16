package net.minecraft.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.JSONUtils;

public class FishingPredicate {
   public static final FishingPredicate ANY = new FishingPredicate(false);
   private boolean inOpenWater;

   private FishingPredicate(boolean p_i231586_1_) {
      this.inOpenWater = p_i231586_1_;
   }

   public static FishingPredicate inOpenWater(boolean p_234640_0_) {
      return new FishingPredicate(p_234640_0_);
   }

   public static FishingPredicate fromJson(@Nullable JsonElement p_234639_0_) {
      if (p_234639_0_ != null && !p_234639_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_234639_0_, "fishing_hook");
         JsonElement jsonelement = jsonobject.get("in_open_water");
         return jsonelement != null ? new FishingPredicate(JSONUtils.convertToBoolean(jsonelement, "in_open_water")) : ANY;
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("in_open_water", new JsonPrimitive(this.inOpenWater));
         return jsonobject;
      }
   }

   public boolean matches(Entity p_234638_1_) {
      if (this == ANY) {
         return true;
      } else if (!(p_234638_1_ instanceof FishingBobberEntity)) {
         return false;
      } else {
         FishingBobberEntity fishingbobberentity = (FishingBobberEntity)p_234638_1_;
         return this.inOpenWater == fishingbobberentity.isOpenWaterFishing();
      }
   }
}
