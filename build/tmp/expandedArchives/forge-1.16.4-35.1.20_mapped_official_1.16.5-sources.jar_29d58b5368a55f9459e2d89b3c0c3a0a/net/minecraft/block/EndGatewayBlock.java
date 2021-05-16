package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndGatewayBlock extends ContainerBlock {
   public EndGatewayBlock(AbstractBlock.Properties p_i48407_1_) {
      super(p_i48407_1_);
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new EndGatewayTileEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      TileEntity tileentity = p_180655_2_.getBlockEntity(p_180655_3_);
      if (tileentity instanceof EndGatewayTileEntity) {
         int i = ((EndGatewayTileEntity)tileentity).getParticleAmount();

         for(int j = 0; j < i; ++j) {
            double d0 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
            double d1 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
            double d2 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
            double d3 = (p_180655_4_.nextDouble() - 0.5D) * 0.5D;
            double d4 = (p_180655_4_.nextDouble() - 0.5D) * 0.5D;
            double d5 = (p_180655_4_.nextDouble() - 0.5D) * 0.5D;
            int k = p_180655_4_.nextInt(2) * 2 - 1;
            if (p_180655_4_.nextBoolean()) {
               d2 = (double)p_180655_3_.getZ() + 0.5D + 0.25D * (double)k;
               d5 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)k);
            } else {
               d0 = (double)p_180655_3_.getX() + 0.5D + 0.25D * (double)k;
               d3 = (double)(p_180655_4_.nextFloat() * 2.0F * (float)k);
            }

            p_180655_2_.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
         }

      }
   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   public boolean canBeReplaced(BlockState p_225541_1_, Fluid p_225541_2_) {
      return false;
   }
}
