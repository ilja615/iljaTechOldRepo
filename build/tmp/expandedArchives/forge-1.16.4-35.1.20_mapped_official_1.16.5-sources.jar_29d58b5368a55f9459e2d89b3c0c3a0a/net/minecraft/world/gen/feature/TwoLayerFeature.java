package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class TwoLayerFeature extends AbstractFeatureSizeType {
   public static final Codec<TwoLayerFeature> CODEC = RecordCodecBuilder.create((p_236732_0_) -> {
      return p_236732_0_.group(Codec.intRange(0, 81).fieldOf("limit").orElse(1).forGetter((p_236735_0_) -> {
         return p_236735_0_.limit;
      }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((p_236734_0_) -> {
         return p_236734_0_.lowerSize;
      }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((p_236733_0_) -> {
         return p_236733_0_.upperSize;
      }), minClippedHeightCodec()).apply(p_236732_0_, TwoLayerFeature::new);
   });
   private final int limit;
   private final int lowerSize;
   private final int upperSize;

   public TwoLayerFeature(int p_i232025_1_, int p_i232025_2_, int p_i232025_3_) {
      this(p_i232025_1_, p_i232025_2_, p_i232025_3_, OptionalInt.empty());
   }

   public TwoLayerFeature(int p_i232026_1_, int p_i232026_2_, int p_i232026_3_, OptionalInt p_i232026_4_) {
      super(p_i232026_4_);
      this.limit = p_i232026_1_;
      this.lowerSize = p_i232026_2_;
      this.upperSize = p_i232026_3_;
   }

   protected FeatureSizeType<?> type() {
      return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
   }

   public int getSizeAtHeight(int p_230369_1_, int p_230369_2_) {
      return p_230369_2_ < this.limit ? this.lowerSize : this.upperSize;
   }
}
