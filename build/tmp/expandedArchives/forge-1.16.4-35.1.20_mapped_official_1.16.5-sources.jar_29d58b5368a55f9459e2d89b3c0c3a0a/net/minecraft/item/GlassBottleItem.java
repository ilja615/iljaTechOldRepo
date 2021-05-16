package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
   public GlassBottleItem(Item.Properties p_i48523_1_) {
      super(p_i48523_1_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      List<AreaEffectCloudEntity> list = p_77659_1_.getEntitiesOfClass(AreaEffectCloudEntity.class, p_77659_2_.getBoundingBox().inflate(2.0D), (p_210311_0_) -> {
         return p_210311_0_ != null && p_210311_0_.isAlive() && p_210311_0_.getOwner() instanceof EnderDragonEntity;
      });
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      if (!list.isEmpty()) {
         AreaEffectCloudEntity areaeffectcloudentity = list.get(0);
         areaeffectcloudentity.setRadius(areaeffectcloudentity.getRadius() - 0.5F);
         p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.getX(), p_77659_2_.getY(), p_77659_2_.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         return ActionResult.sidedSuccess(this.turnBottleIntoItem(itemstack, p_77659_2_, new ItemStack(Items.DRAGON_BREATH)), p_77659_1_.isClientSide());
      } else {
         RayTraceResult raytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.SOURCE_ONLY);
         if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.pass(itemstack);
         } else {
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
               BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getBlockPos();
               if (!p_77659_1_.mayInteract(p_77659_2_, blockpos)) {
                  return ActionResult.pass(itemstack);
               }

               if (p_77659_1_.getFluidState(blockpos).is(FluidTags.WATER)) {
                  p_77659_1_.playSound(p_77659_2_, p_77659_2_.getX(), p_77659_2_.getY(), p_77659_2_.getZ(), SoundEvents.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                  return ActionResult.sidedSuccess(this.turnBottleIntoItem(itemstack, p_77659_2_, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), p_77659_1_.isClientSide());
               }
            }

            return ActionResult.pass(itemstack);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack p_185061_1_, PlayerEntity p_185061_2_, ItemStack p_185061_3_) {
      p_185061_2_.awardStat(Stats.ITEM_USED.get(this));
      return DrinkHelper.createFilledResult(p_185061_1_, p_185061_2_, p_185061_3_);
   }
}
