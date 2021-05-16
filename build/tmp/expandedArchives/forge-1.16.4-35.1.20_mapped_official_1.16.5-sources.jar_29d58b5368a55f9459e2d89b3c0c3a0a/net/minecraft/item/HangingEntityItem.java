package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HangingEntityItem extends Item {
   private final EntityType<? extends HangingEntity> type;

   public HangingEntityItem(EntityType<? extends HangingEntity> p_i50043_1_, Item.Properties p_i50043_2_) {
      super(p_i50043_2_);
      this.type = p_i50043_1_;
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      BlockPos blockpos = p_195939_1_.getClickedPos();
      Direction direction = p_195939_1_.getClickedFace();
      BlockPos blockpos1 = blockpos.relative(direction);
      PlayerEntity playerentity = p_195939_1_.getPlayer();
      ItemStack itemstack = p_195939_1_.getItemInHand();
      if (playerentity != null && !this.mayPlace(playerentity, direction, itemstack, blockpos1)) {
         return ActionResultType.FAIL;
      } else {
         World world = p_195939_1_.getLevel();
         HangingEntity hangingentity;
         if (this.type == EntityType.PAINTING) {
            hangingentity = new PaintingEntity(world, blockpos1, direction);
         } else {
            if (this.type != EntityType.ITEM_FRAME) {
               return ActionResultType.sidedSuccess(world.isClientSide);
            }

            hangingentity = new ItemFrameEntity(world, blockpos1, direction);
         }

         CompoundNBT compoundnbt = itemstack.getTag();
         if (compoundnbt != null) {
            EntityType.updateCustomEntityTag(world, playerentity, hangingentity, compoundnbt);
         }

         if (hangingentity.survives()) {
            if (!world.isClientSide) {
               hangingentity.playPlacementSound();
               world.addFreshEntity(hangingentity);
            }

            itemstack.shrink(1);
            return ActionResultType.sidedSuccess(world.isClientSide);
         } else {
            return ActionResultType.CONSUME;
         }
      }
   }

   protected boolean mayPlace(PlayerEntity p_200127_1_, Direction p_200127_2_, ItemStack p_200127_3_, BlockPos p_200127_4_) {
      return !p_200127_2_.getAxis().isVertical() && p_200127_1_.mayUseItemAt(p_200127_4_, p_200127_2_, p_200127_3_);
   }
}
