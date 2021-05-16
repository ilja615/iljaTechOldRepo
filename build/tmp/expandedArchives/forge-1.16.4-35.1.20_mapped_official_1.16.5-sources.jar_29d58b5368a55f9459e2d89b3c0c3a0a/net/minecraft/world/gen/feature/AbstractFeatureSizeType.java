package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;

public abstract class AbstractFeatureSizeType {
   public static final Codec<AbstractFeatureSizeType> CODEC = Registry.FEATURE_SIZE_TYPES.dispatch(AbstractFeatureSizeType::type, FeatureSizeType::codec);
   protected final OptionalInt minClippedHeight;

   protected static <S extends AbstractFeatureSizeType> RecordCodecBuilder<S, OptionalInt> minClippedHeightCodec() {
      return Codec.intRange(0, 80).optionalFieldOf("min_clipped_height").xmap((p_236708_0_) -> {
         return p_236708_0_.map(OptionalInt::of).orElse(OptionalInt.empty());
      }, (p_236709_0_) -> {
         return p_236709_0_.isPresent() ? Optional.of(p_236709_0_.getAsInt()) : Optional.empty();
      }).forGetter((p_236707_0_) -> {
         return p_236707_0_.minClippedHeight;
      });
   }

   public AbstractFeatureSizeType(OptionalInt p_i232022_1_) {
      this.minClippedHeight = p_i232022_1_;
   }

   protected abstract FeatureSizeType<?> type();

   public abstract int getSizeAtHeight(int p_230369_1_, int p_230369_2_);

   public OptionalInt minClippedHeight() {
      return this.minClippedHeight;
   }
}
