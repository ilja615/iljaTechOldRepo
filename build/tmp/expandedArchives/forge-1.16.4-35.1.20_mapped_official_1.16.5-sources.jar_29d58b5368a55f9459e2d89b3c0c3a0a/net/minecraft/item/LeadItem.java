package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LeadItem extends Item {
   public LeadItem(Item.Properties p_i48484_1_) {
      super(p_i48484_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      Block block = world.getBlockState(blockpos).getBlock();
      if (block.is(BlockTags.FENCES)) {
         PlayerEntity playerentity = p_195939_1_.getPlayer();
         if (!world.isClientSide && playerentity != null) {
            bindPlayerMobs(playerentity, world, blockpos);
         }

         return ActionResultType.sidedSuccess(world.isClientSide);
      } else {
         return ActionResultType.PASS;
      }
   }

   public static ActionResultType bindPlayerMobs(PlayerEntity p_226641_0_, World p_226641_1_, BlockPos p_226641_2_) {
      LeashKnotEntity leashknotentity = null;
      boolean flag = false;
      double d0 = 7.0D;
      int i = p_226641_2_.getX();
      int j = p_226641_2_.getY();
      int k = p_226641_2_.getZ();

      for(MobEntity mobentity : p_226641_1_.getEntitiesOfClass(MobEntity.class, new AxisAlignedBB((double)i - 7.0D, (double)j - 7.0D, (double)k - 7.0D, (double)i + 7.0D, (double)j + 7.0D, (double)k + 7.0D))) {
         if (mobentity.getLeashHolder() == p_226641_0_) {
            if (leashknotentity == null) {
               leashknotentity = LeashKnotEntity.getOrCreateKnot(p_226641_1_, p_226641_2_);
            }

            mobentity.setLeashedTo(leashknotentity, true);
            flag = true;
         }
      }

      return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
   }
}
