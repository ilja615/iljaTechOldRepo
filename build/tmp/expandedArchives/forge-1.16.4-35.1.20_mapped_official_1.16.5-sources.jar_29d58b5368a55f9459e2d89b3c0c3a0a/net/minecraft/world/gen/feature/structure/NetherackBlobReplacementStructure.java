package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlobReplacementConfig;
import net.minecraft.world.gen.feature.Feature;

public class NetherackBlobReplacementStructure extends Feature<BlobReplacementConfig> {
   public NetherackBlobReplacementStructure(Codec<BlobReplacementConfig> p_i231982_1_) {
      super(p_i231982_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlobReplacementConfig p_241855_5_) {
      Block block = p_241855_5_.targetState.getBlock();
      BlockPos blockpos = findTarget(p_241855_1_, p_241855_4_.mutable().clamp(Direction.Axis.Y, 1, p_241855_1_.getMaxBuildHeight() - 1), block);
      if (blockpos == null) {
         return false;
      } else {
         int i = p_241855_5_.radius().sample(p_241855_3_);
         boolean flag = false;

         for(BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, i, i, i)) {
            if (blockpos1.distManhattan(blockpos) > i) {
               break;
            }

            BlockState blockstate = p_241855_1_.getBlockState(blockpos1);
            if (blockstate.is(block)) {
               this.setBlock(p_241855_1_, blockpos1, p_241855_5_.replaceState);
               flag = true;
            }
         }

         return flag;
      }
   }

   @Nullable
   private static BlockPos findTarget(IWorld p_236329_0_, BlockPos.Mutable p_236329_1_, Block p_236329_2_) {
      while(p_236329_1_.getY() > 1) {
         BlockState blockstate = p_236329_0_.getBlockState(p_236329_1_);
         if (blockstate.is(p_236329_2_)) {
            return p_236329_1_;
         }

         p_236329_1_.move(Direction.DOWN);
      }

      return null;
   }
}
