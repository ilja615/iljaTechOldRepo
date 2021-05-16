package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
   public WritableBookItem(Item.Properties p_i48455_1_) {
      super(p_i48455_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (blockstate.is(Blocks.LECTERN)) {
         return LecternBlock.tryPlaceBook(world, blockpos, blockstate, p_195939_1_.getItemInHand()) ? ActionResultType.sidedSuccess(world.isClientSide) : ActionResultType.PASS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      p_77659_2_.openItemGui(itemstack, p_77659_3_);
      p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
      return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
   }

   public static boolean makeSureTagIsValid(@Nullable CompoundNBT p_150930_0_) {
      if (p_150930_0_ == null) {
         return false;
      } else if (!p_150930_0_.contains("pages", 9)) {
         return false;
      } else {
         ListNBT listnbt = p_150930_0_.getList("pages", 8);

         for(int i = 0; i < listnbt.size(); ++i) {
            String s = listnbt.getString(i);
            if (s.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
