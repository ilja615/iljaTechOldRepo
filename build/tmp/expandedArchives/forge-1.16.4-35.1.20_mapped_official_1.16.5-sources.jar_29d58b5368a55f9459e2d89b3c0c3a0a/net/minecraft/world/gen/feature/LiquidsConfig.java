package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.registry.Registry;

public class LiquidsConfig implements IFeatureConfig {
   public static final Codec<LiquidsConfig> CODEC = RecordCodecBuilder.create((p_236650_0_) -> {
      return p_236650_0_.group(FluidState.CODEC.fieldOf("state").forGetter((p_236655_0_) -> {
         return p_236655_0_.state;
      }), Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter((p_236654_0_) -> {
         return p_236654_0_.requiresBlockBelow;
      }), Codec.INT.fieldOf("rock_count").orElse(4).forGetter((p_236653_0_) -> {
         return p_236653_0_.rockCount;
      }), Codec.INT.fieldOf("hole_count").orElse(1).forGetter((p_236652_0_) -> {
         return p_236652_0_.holeCount;
      }), Registry.BLOCK.listOf().fieldOf("valid_blocks").<Set<Block>>xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter((p_236651_0_) -> {
         return p_236651_0_.validBlocks;
      })).apply(p_236650_0_, LiquidsConfig::new);
   });
   public final FluidState state;
   public final boolean requiresBlockBelow;
   public final int rockCount;
   public final int holeCount;
   public final Set<Block> validBlocks;

   public LiquidsConfig(FluidState p_i225841_1_, boolean p_i225841_2_, int p_i225841_3_, int p_i225841_4_, Set<Block> p_i225841_5_) {
      this.state = p_i225841_1_;
      this.requiresBlockBelow = p_i225841_2_;
      this.rockCount = p_i225841_3_;
      this.holeCount = p_i225841_4_;
      this.validBlocks = p_i225841_5_;
   }
}
