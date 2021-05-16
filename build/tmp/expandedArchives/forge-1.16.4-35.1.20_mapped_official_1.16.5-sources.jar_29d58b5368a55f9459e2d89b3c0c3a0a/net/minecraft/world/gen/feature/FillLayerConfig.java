package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class FillLayerConfig implements IFeatureConfig {
   public static final Codec<FillLayerConfig> CODEC = RecordCodecBuilder.create((p_236539_0_) -> {
      return p_236539_0_.group(Codec.intRange(0, 255).fieldOf("height").forGetter((p_236540_0_) -> {
         return p_236540_0_.height;
      }), BlockState.CODEC.fieldOf("state").forGetter((p_236538_0_) -> {
         return p_236538_0_.state;
      })).apply(p_236539_0_, FillLayerConfig::new);
   });
   public final int height;
   public final BlockState state;

   public FillLayerConfig(int p_i51484_1_, BlockState p_i51484_2_) {
      this.height = p_i51484_1_;
      this.state = p_i51484_2_;
   }
}
