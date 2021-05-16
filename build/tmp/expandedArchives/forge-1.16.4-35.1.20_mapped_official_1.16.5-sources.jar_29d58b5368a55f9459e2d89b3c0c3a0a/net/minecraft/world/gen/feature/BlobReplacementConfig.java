package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class BlobReplacementConfig implements IFeatureConfig {
   public static final Codec<BlobReplacementConfig> CODEC = RecordCodecBuilder.create((p_242822_0_) -> {
      return p_242822_0_.group(BlockState.CODEC.fieldOf("target").forGetter((p_242825_0_) -> {
         return p_242825_0_.targetState;
      }), BlockState.CODEC.fieldOf("state").forGetter((p_242824_0_) -> {
         return p_242824_0_.replaceState;
      }), FeatureSpread.CODEC.fieldOf("radius").forGetter((p_242821_0_) -> {
         return p_242821_0_.radius;
      })).apply(p_242822_0_, BlobReplacementConfig::new);
   });
   public final BlockState targetState;
   public final BlockState replaceState;
   private final FeatureSpread radius;

   public BlobReplacementConfig(BlockState p_i241993_1_, BlockState p_i241993_2_, FeatureSpread p_i241993_3_) {
      this.targetState = p_i241993_1_;
      this.replaceState = p_i241993_2_;
      this.radius = p_i241993_3_;
   }

   public FeatureSpread radius() {
      return this.radius;
   }
}
