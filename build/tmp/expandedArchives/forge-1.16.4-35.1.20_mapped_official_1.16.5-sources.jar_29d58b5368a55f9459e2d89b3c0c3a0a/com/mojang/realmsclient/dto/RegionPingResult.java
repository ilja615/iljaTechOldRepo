package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Locale;
import net.minecraft.realms.IPersistentSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionPingResult extends ValueObject implements IPersistentSerializable {
   @SerializedName("regionName")
   private final String regionName;
   @SerializedName("ping")
   private final int ping;

   public RegionPingResult(String p_i51641_1_, int p_i51641_2_) {
      this.regionName = p_i51641_1_;
      this.ping = p_i51641_2_;
   }

   public int ping() {
      return this.ping;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, (float)this.ping);
   }
}
