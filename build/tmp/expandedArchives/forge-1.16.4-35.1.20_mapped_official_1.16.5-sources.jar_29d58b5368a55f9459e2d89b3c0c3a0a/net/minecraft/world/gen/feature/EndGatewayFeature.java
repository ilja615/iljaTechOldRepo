package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class EndGatewayFeature extends Feature<EndGatewayConfig> {
   public EndGatewayFeature(Codec<EndGatewayConfig> p_i231951_1_) {
      super(p_i231951_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, EndGatewayConfig p_241855_5_) {
      for(BlockPos blockpos : BlockPos.betweenClosed(p_241855_4_.offset(-1, -2, -1), p_241855_4_.offset(1, 2, 1))) {
         boolean flag = blockpos.getX() == p_241855_4_.getX();
         boolean flag1 = blockpos.getY() == p_241855_4_.getY();
         boolean flag2 = blockpos.getZ() == p_241855_4_.getZ();
         boolean flag3 = Math.abs(blockpos.getY() - p_241855_4_.getY()) == 2;
         if (flag && flag1 && flag2) {
            BlockPos blockpos1 = blockpos.immutable();
            this.setBlock(p_241855_1_, blockpos1, Blocks.END_GATEWAY.defaultBlockState());
            p_241855_5_.getExit().ifPresent((p_236280_3_) -> {
               TileEntity tileentity = p_241855_1_.getBlockEntity(blockpos1);
               if (tileentity instanceof EndGatewayTileEntity) {
                  EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileentity;
                  endgatewaytileentity.setExitPosition(p_236280_3_, p_241855_5_.isExitExact());
                  tileentity.setChanged();
               }

            });
         } else if (flag1) {
            this.setBlock(p_241855_1_, blockpos, Blocks.AIR.defaultBlockState());
         } else if (flag3 && flag && flag2) {
            this.setBlock(p_241855_1_, blockpos, Blocks.BEDROCK.defaultBlockState());
         } else if ((flag || flag2) && !flag3) {
            this.setBlock(p_241855_1_, blockpos, Blocks.BEDROCK.defaultBlockState());
         } else {
            this.setBlock(p_241855_1_, blockpos, Blocks.AIR.defaultBlockState());
         }
      }

      return true;
   }
}
