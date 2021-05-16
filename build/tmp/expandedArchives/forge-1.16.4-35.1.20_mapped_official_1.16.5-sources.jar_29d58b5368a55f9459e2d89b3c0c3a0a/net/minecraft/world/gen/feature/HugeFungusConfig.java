package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class HugeFungusConfig implements IFeatureConfig {
   public static final Codec<HugeFungusConfig> CODEC = RecordCodecBuilder.create((p_236309_0_) -> {
      return p_236309_0_.group(BlockState.CODEC.fieldOf("valid_base_block").forGetter((p_236313_0_) -> {
         return p_236313_0_.validBaseState;
      }), BlockState.CODEC.fieldOf("stem_state").forGetter((p_236312_0_) -> {
         return p_236312_0_.stemState;
      }), BlockState.CODEC.fieldOf("hat_state").forGetter((p_236311_0_) -> {
         return p_236311_0_.hatState;
      }), BlockState.CODEC.fieldOf("decor_state").forGetter((p_236310_0_) -> {
         return p_236310_0_.decorState;
      }), Codec.BOOL.fieldOf("planted").orElse(false).forGetter((p_236308_0_) -> {
         return p_236308_0_.planted;
      })).apply(p_236309_0_, HugeFungusConfig::new);
   });
   public static final HugeFungusConfig HUGE_CRIMSON_FUNGI_PLANTED_CONFIG = new HugeFungusConfig(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true);
   public static final HugeFungusConfig HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG;
   public static final HugeFungusConfig HUGE_WARPED_FUNGI_PLANTED_CONFIG = new HugeFungusConfig(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true);
   public static final HugeFungusConfig HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG;
   public final BlockState validBaseState;
   public final BlockState stemState;
   public final BlockState hatState;
   public final BlockState decorState;
   public final boolean planted;

   public HugeFungusConfig(BlockState p_i231958_1_, BlockState p_i231958_2_, BlockState p_i231958_3_, BlockState p_i231958_4_, boolean p_i231958_5_) {
      this.validBaseState = p_i231958_1_;
      this.stemState = p_i231958_2_;
      this.hatState = p_i231958_3_;
      this.decorState = p_i231958_4_;
      this.planted = p_i231958_5_;
   }

   static {
      HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfig(HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.validBaseState, HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.stemState, HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.hatState, HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.decorState, false);
      HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfig(HUGE_WARPED_FUNGI_PLANTED_CONFIG.validBaseState, HUGE_WARPED_FUNGI_PLANTED_CONFIG.stemState, HUGE_WARPED_FUNGI_PLANTED_CONFIG.hatState, HUGE_WARPED_FUNGI_PLANTED_CONFIG.decorState, false);
   }
}
