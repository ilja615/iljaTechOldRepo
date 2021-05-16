package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class ThreeLayerFeature extends AbstractFeatureSizeType {
   public static final Codec<ThreeLayerFeature> CODEC = RecordCodecBuilder.create((p_236722_0_) -> {
      return p_236722_0_.group(Codec.intRange(0, 80).fieldOf("limit").orElse(1).forGetter((p_236727_0_) -> {
         return p_236727_0_.limit;
      }), Codec.intRange(0, 80).fieldOf("upper_limit").orElse(1).forGetter((p_236726_0_) -> {
         return p_236726_0_.upperLimit;
      }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((p_236725_0_) -> {
         return p_236725_0_.lowerSize;
      }), Codec.intRange(0, 16).fieldOf("middle_size").orElse(1).forGetter((p_236724_0_) -> {
         return p_236724_0_.middleSize;
      }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((p_236723_0_) -> {
         return p_236723_0_.upperSize;
      }), minClippedHeightCodec()).apply(p_236722_0_, ThreeLayerFeature::new);
   });
   private final int limit;
   private final int upperLimit;
   private final int lowerSize;
   private final int middleSize;
   private final int upperSize;

   public ThreeLayerFeature(int p_i232024_1_, int p_i232024_2_, int p_i232024_3_, int p_i232024_4_, int p_i232024_5_, OptionalInt p_i232024_6_) {
      super(p_i232024_6_);
      this.limit = p_i232024_1_;
      this.upperLimit = p_i232024_2_;
      this.lowerSize = p_i232024_3_;
      this.middleSize = p_i232024_4_;
      this.upperSize = p_i232024_5_;
   }

   protected FeatureSizeType<?> type() {
      return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
   }

   public int getSizeAtHeight(int p_230369_1_, int p_230369_2_) {
      if (p_230369_2_ < this.limit) {
         return this.lowerSize;
      } else {
         return p_230369_2_ >= p_230369_1_ - this.upperLimit ? this.upperSize : this.middleSize;
      }
   }
}
