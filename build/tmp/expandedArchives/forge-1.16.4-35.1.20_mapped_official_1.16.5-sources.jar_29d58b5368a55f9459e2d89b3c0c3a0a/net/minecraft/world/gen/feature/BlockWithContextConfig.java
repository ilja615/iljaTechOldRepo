package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;

public class BlockWithContextConfig implements IFeatureConfig {
   public static final Codec<BlockWithContextConfig> CODEC = RecordCodecBuilder.create((p_236638_0_) -> {
      return p_236638_0_.group(BlockState.CODEC.fieldOf("to_place").forGetter((p_236641_0_) -> {
         return p_236641_0_.toPlace;
      }), BlockState.CODEC.listOf().fieldOf("place_on").forGetter((p_236640_0_) -> {
         return p_236640_0_.placeOn;
      }), BlockState.CODEC.listOf().fieldOf("place_in").forGetter((p_236639_0_) -> {
         return p_236639_0_.placeIn;
      }), BlockState.CODEC.listOf().fieldOf("place_under").forGetter((p_236637_0_) -> {
         return p_236637_0_.placeUnder;
      })).apply(p_236638_0_, BlockWithContextConfig::new);
   });
   public final BlockState toPlace;
   public final List<BlockState> placeOn;
   public final List<BlockState> placeIn;
   public final List<BlockState> placeUnder;

   public BlockWithContextConfig(BlockState p_i51439_1_, List<BlockState> p_i51439_2_, List<BlockState> p_i51439_3_, List<BlockState> p_i51439_4_) {
      this.toPlace = p_i51439_1_;
      this.placeOn = p_i51439_2_;
      this.placeIn = p_i51439_3_;
      this.placeUnder = p_i51439_4_;
   }
}
