package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StructureSpreadSettings {
   public static final Codec<StructureSpreadSettings> CODEC = RecordCodecBuilder.create((p_236661_0_) -> {
      return p_236661_0_.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(StructureSpreadSettings::distance), Codec.intRange(0, 1023).fieldOf("spread").forGetter(StructureSpreadSettings::spread), Codec.intRange(1, 4095).fieldOf("count").forGetter(StructureSpreadSettings::count)).apply(p_236661_0_, StructureSpreadSettings::new);
   });
   private final int distance;
   private final int spread;
   private final int count;

   public StructureSpreadSettings(int p_i232018_1_, int p_i232018_2_, int p_i232018_3_) {
      this.distance = p_i232018_1_;
      this.spread = p_i232018_2_;
      this.count = p_i232018_3_;
   }

   public int distance() {
      return this.distance;
   }

   public int spread() {
      return this.spread;
   }

   public int count() {
      return this.count;
   }
}
