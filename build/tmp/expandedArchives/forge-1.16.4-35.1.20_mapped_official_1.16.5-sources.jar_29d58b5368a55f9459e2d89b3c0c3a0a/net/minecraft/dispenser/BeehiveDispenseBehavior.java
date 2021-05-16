package net.minecraft.dispenser;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.IShearable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BeehiveDispenseBehavior extends OptionalDispenseBehavior {
   protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      World world = p_82487_1_.getLevel();
      if (!world.isClientSide()) {
         BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
         this.setSuccess(tryShearBeehive((ServerWorld)world, blockpos) || tryShearLivingEntity((ServerWorld)world, blockpos));
         if (this.isSuccess() && p_82487_2_.hurt(1, world.getRandom(), (ServerPlayerEntity)null)) {
            p_82487_2_.setCount(0);
         }
      }

      return p_82487_2_;
   }

   private static boolean tryShearBeehive(ServerWorld p_239797_0_, BlockPos p_239797_1_) {
      BlockState blockstate = p_239797_0_.getBlockState(p_239797_1_);
      if (blockstate.is(BlockTags.BEEHIVES)) {
         int i = blockstate.getValue(BeehiveBlock.HONEY_LEVEL);
         if (i >= 5) {
            p_239797_0_.playSound((PlayerEntity)null, p_239797_1_, SoundEvents.BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            BeehiveBlock.dropHoneycomb(p_239797_0_, p_239797_1_);
            ((BeehiveBlock)blockstate.getBlock()).releaseBeesAndResetHoneyLevel(p_239797_0_, blockstate, p_239797_1_, (PlayerEntity)null, BeehiveTileEntity.State.BEE_RELEASED);
            return true;
         }
      }

      return false;
   }

   private static boolean tryShearLivingEntity(ServerWorld p_239798_0_, BlockPos p_239798_1_) {
      for(LivingEntity livingentity : p_239798_0_.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(p_239798_1_), EntityPredicates.NO_SPECTATORS)) {
         if (livingentity instanceof IShearable) {
            IShearable ishearable = (IShearable)livingentity;
            if (ishearable.readyForShearing()) {
               ishearable.shear(SoundCategory.BLOCKS);
               return true;
            }
         }
      }

      return false;
   }
}
