package net.minecraft.world.gen.feature;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class FeatureSpread {
   public static final Codec<FeatureSpread> CODEC = Codec.either(Codec.INT, RecordCodecBuilder.<FeatureSpread>create((p_242258_0_) -> {
      return p_242258_0_.group(Codec.INT.fieldOf("base").forGetter((p_242263_0_) -> {
         return p_242263_0_.baseValue;
      }), Codec.INT.fieldOf("spread").forGetter((p_242262_0_) -> {
         return p_242262_0_.spread;
      })).apply(p_242258_0_, FeatureSpread::new);
   }).comapFlatMap((p_242261_0_) -> {
      return p_242261_0_.spread < 0 ? DataResult.error("Spread must be non-negative, got: " + p_242261_0_.spread) : DataResult.success(p_242261_0_);
   }, Function.identity())).xmap((p_242257_0_) -> {
      return p_242257_0_.map(FeatureSpread::fixed, (p_242260_0_) -> {
         return p_242260_0_;
      });
   }, (p_242256_0_) -> {
      return p_242256_0_.spread == 0 ? Either.left(p_242256_0_.baseValue) : Either.right(p_242256_0_);
   });
   private final int baseValue;
   private final int spread;

   public static Codec<FeatureSpread> codec(int p_242254_0_, int p_242254_1_, int p_242254_2_) {
      Function<FeatureSpread, DataResult<FeatureSpread>> function = (p_242255_3_) -> {
         if (p_242255_3_.baseValue >= p_242254_0_ && p_242255_3_.baseValue <= p_242254_1_) {
            return p_242255_3_.spread <= p_242254_2_ ? DataResult.success(p_242255_3_) : DataResult.error("Spread too big: " + p_242255_3_.spread + " > " + p_242254_2_);
         } else {
            return DataResult.error("Base value out of range: " + p_242255_3_.baseValue + " [" + p_242254_0_ + "-" + p_242254_1_ + "]");
         }
      };
      return CODEC.flatXmap(function, function);
   }

   private FeatureSpread(int p_i241900_1_, int p_i241900_2_) {
      this.baseValue = p_i241900_1_;
      this.spread = p_i241900_2_;
   }

   public static FeatureSpread fixed(int p_242252_0_) {
      return new FeatureSpread(p_242252_0_, 0);
   }

   public static FeatureSpread of(int p_242253_0_, int p_242253_1_) {
      return new FeatureSpread(p_242253_0_, p_242253_1_);
   }

   public int sample(Random p_242259_1_) {
      return this.spread == 0 ? this.baseValue : this.baseValue + p_242259_1_.nextInt(this.spread + 1);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         FeatureSpread featurespread = (FeatureSpread)p_equals_1_;
         return this.baseValue == featurespread.baseValue && this.spread == featurespread.spread;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.baseValue, this.spread);
   }

   public String toString() {
      return "[" + this.baseValue + '-' + (this.baseValue + this.spread) + ']';
   }
}
