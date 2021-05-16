package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class ReplaceBlockConfig implements IFeatureConfig {
   public static final Codec<ReplaceBlockConfig> CODEC = RecordCodecBuilder.create((p_236606_0_) -> {
      return p_236606_0_.group(BlockState.CODEC.fieldOf("target").forGetter((p_236607_0_) -> {
         return p_236607_0_.target;
      }), BlockState.CODEC.fieldOf("state").forGetter((p_236605_0_) -> {
         return p_236605_0_.state;
      })).apply(p_236606_0_, ReplaceBlockConfig::new);
   });
   public final BlockState target;
   public final BlockState state;

   public ReplaceBlockConfig(BlockState p_i51445_1_, BlockState p_i51445_2_) {
      this.target = p_i51445_1_;
      this.state = p_i51445_2_;
   }
}
