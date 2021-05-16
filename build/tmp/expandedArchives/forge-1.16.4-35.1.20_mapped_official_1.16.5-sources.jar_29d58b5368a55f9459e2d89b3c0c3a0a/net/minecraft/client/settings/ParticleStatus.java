package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ParticleStatus {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   private static final ParticleStatus[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ParticleStatus::getId)).toArray((p_216834_0_) -> {
      return new ParticleStatus[p_216834_0_];
   });
   private final int id;
   private final String key;

   private ParticleStatus(int p_i51156_3_, String p_i51156_4_) {
      this.id = p_i51156_3_;
      this.key = p_i51156_4_;
   }

   public String getKey() {
      return this.key;
   }

   public int getId() {
      return this.id;
   }

   public static ParticleStatus byId(int p_216833_0_) {
      return BY_ID[MathHelper.positiveModulo(p_216833_0_, BY_ID.length)];
   }
}
