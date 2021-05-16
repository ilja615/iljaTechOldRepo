package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class FlatLayerInfo {
   public static final Codec<FlatLayerInfo> CODEC = RecordCodecBuilder.create((p_236930_0_) -> {
      return p_236930_0_.group(Codec.intRange(0, 256).fieldOf("height").forGetter(FlatLayerInfo::getHeight), Registry.BLOCK.fieldOf("block").orElse(Blocks.AIR).forGetter((p_236931_0_) -> {
         return p_236931_0_.getBlockState().getBlock();
      })).apply(p_236930_0_, FlatLayerInfo::new);
   });
   private final BlockState blockState;
   private final int height;
   private int start;

   public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_) {
      this.height = p_i45467_1_;
      this.blockState = p_i45467_2_.defaultBlockState();
   }

   public int getHeight() {
      return this.height;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public int getStart() {
      return this.start;
   }

   public void setStart(int p_82660_1_) {
      this.start = p_82660_1_;
   }

   public String toString() {
      return (this.height != 1 ? this.height + "*" : "") + Registry.BLOCK.getKey(this.blockState.getBlock());
   }
}
