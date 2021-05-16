package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class BasaltDeltasFeature implements IFeatureConfig {
   public static final Codec<BasaltDeltasFeature> CODEC = RecordCodecBuilder.create((p_242803_0_) -> {
      return p_242803_0_.group(BlockState.CODEC.fieldOf("contents").forGetter((p_236506_0_) -> {
         return p_236506_0_.contents;
      }), BlockState.CODEC.fieldOf("rim").forGetter((p_236505_0_) -> {
         return p_236505_0_.rim;
      }), FeatureSpread.codec(0, 8, 8).fieldOf("size").forGetter((p_242805_0_) -> {
         return p_242805_0_.size;
      }), FeatureSpread.codec(0, 8, 8).fieldOf("rim_size").forGetter((p_242802_0_) -> {
         return p_242802_0_.rimSize;
      })).apply(p_242803_0_, BasaltDeltasFeature::new);
   });
   private final BlockState contents;
   private final BlockState rim;
   private final FeatureSpread size;
   private final FeatureSpread rimSize;

   public BasaltDeltasFeature(BlockState p_i241985_1_, BlockState p_i241985_2_, FeatureSpread p_i241985_3_, FeatureSpread p_i241985_4_) {
      this.contents = p_i241985_1_;
      this.rim = p_i241985_2_;
      this.size = p_i241985_3_;
      this.rimSize = p_i241985_4_;
   }

   public BlockState contents() {
      return this.contents;
   }

   public BlockState rim() {
      return this.rim;
   }

   public FeatureSpread size() {
      return this.size;
   }

   public FeatureSpread rimSize() {
      return this.rimSize;
   }
}
