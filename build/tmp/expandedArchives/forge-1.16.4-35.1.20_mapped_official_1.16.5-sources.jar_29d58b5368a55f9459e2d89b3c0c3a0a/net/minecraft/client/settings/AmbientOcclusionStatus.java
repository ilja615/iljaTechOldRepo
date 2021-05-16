package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum AmbientOcclusionStatus {
   OFF(0, "options.ao.off"),
   MIN(1, "options.ao.min"),
   MAX(2, "options.ao.max");

   private static final AmbientOcclusionStatus[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(AmbientOcclusionStatus::getId)).toArray((p_216571_0_) -> {
      return new AmbientOcclusionStatus[p_216571_0_];
   });
   private final int id;
   private final String key;

   private AmbientOcclusionStatus(int p_i51169_3_, String p_i51169_4_) {
      this.id = p_i51169_3_;
      this.key = p_i51169_4_;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static AmbientOcclusionStatus byId(int p_216570_0_) {
      return BY_ID[MathHelper.positiveModulo(p_216570_0_, BY_ID.length)];
   }
}
