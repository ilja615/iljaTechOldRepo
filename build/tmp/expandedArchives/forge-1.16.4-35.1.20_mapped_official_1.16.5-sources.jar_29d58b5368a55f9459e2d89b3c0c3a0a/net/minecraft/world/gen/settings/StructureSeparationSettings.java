package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class StructureSeparationSettings {
   public static final Codec<StructureSeparationSettings> CODEC = RecordCodecBuilder.<StructureSeparationSettings>create((p_236669_0_) -> {
      return p_236669_0_.group(Codec.intRange(0, 4096).fieldOf("spacing").forGetter((p_236675_0_) -> {
         return p_236675_0_.spacing;
      }), Codec.intRange(0, 4096).fieldOf("separation").forGetter((p_236674_0_) -> {
         return p_236674_0_.separation;
      }), Codec.intRange(0, Integer.MAX_VALUE).fieldOf("salt").forGetter((p_236672_0_) -> {
         return p_236672_0_.salt;
      })).apply(p_236669_0_, StructureSeparationSettings::new);
   }).comapFlatMap((p_236670_0_) -> {
      return p_236670_0_.spacing <= p_236670_0_.separation ? DataResult.error("Spacing has to be smaller than separation") : DataResult.success(p_236670_0_);
   }, Function.identity());
   private final int spacing;
   private final int separation;
   private final int salt;

   public StructureSeparationSettings(int p_i232019_1_, int p_i232019_2_, int p_i232019_3_) {
      this.spacing = p_i232019_1_;
      this.separation = p_i232019_2_;
      this.salt = p_i232019_3_;
   }

   public int spacing() {
      return this.spacing;
   }

   public int separation() {
      return this.separation;
   }

   public int salt() {
      return this.salt;
   }
}
