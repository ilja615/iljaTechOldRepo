package net.minecraft.block;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class PumpkinBlock extends StemGrownBlock {
   public PumpkinBlock(AbstractBlock.Properties p_i48347_1_) {
      super(p_i48347_1_);
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack itemstack = p_225533_4_.getItemInHand(p_225533_5_);
      if (itemstack.getItem() == Items.SHEARS) {
         if (!p_225533_2_.isClientSide) {
            Direction direction = p_225533_6_.getDirection();
            Direction direction1 = direction.getAxis() == Direction.Axis.Y ? p_225533_4_.getDirection().getOpposite() : direction;
            p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            p_225533_2_.setBlock(p_225533_3_, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction1), 11);
            ItemEntity itementity = new ItemEntity(p_225533_2_, (double)p_225533_3_.getX() + 0.5D + (double)direction1.getStepX() * 0.65D, (double)p_225533_3_.getY() + 0.1D, (double)p_225533_3_.getZ() + 0.5D + (double)direction1.getStepZ() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            itementity.setDeltaMovement(0.05D * (double)direction1.getStepX() + p_225533_2_.random.nextDouble() * 0.02D, 0.05D, 0.05D * (double)direction1.getStepZ() + p_225533_2_.random.nextDouble() * 0.02D);
            p_225533_2_.addFreshEntity(itementity);
            itemstack.hurtAndBreak(1, p_225533_4_, (p_220282_1_) -> {
               p_220282_1_.broadcastBreakEvent(p_225533_5_);
            });
         }

         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         return super.use(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_4_, p_225533_5_, p_225533_6_);
      }
   }

   public StemBlock getStem() {
      return (StemBlock)Blocks.PUMPKIN_STEM;
   }

   public AttachedStemBlock getAttachedStem() {
      return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
   }
}
