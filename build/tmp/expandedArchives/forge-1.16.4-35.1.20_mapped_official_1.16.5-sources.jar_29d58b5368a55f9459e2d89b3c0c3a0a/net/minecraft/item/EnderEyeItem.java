package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class EnderEyeItem extends Item {
   public EnderEyeItem(Item.Properties p_i48502_1_) {
      super(p_i48502_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (blockstate.is(Blocks.END_PORTAL_FRAME) && !blockstate.getValue(EndPortalFrameBlock.HAS_EYE)) {
         if (world.isClientSide) {
            return ActionResultType.SUCCESS;
         } else {
            BlockState blockstate1 = blockstate.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(true));
            Block.pushEntitiesUp(blockstate, blockstate1, world, blockpos);
            world.setBlock(blockpos, blockstate1, 2);
            world.updateNeighbourForOutputSignal(blockpos, Blocks.END_PORTAL_FRAME);
            p_195939_1_.getItemInHand().shrink(1);
            world.levelEvent(1503, blockpos, 0);
            BlockPattern.PatternHelper blockpattern$patternhelper = EndPortalFrameBlock.getOrCreatePortalShape().find(world, blockpos);
            if (blockpattern$patternhelper != null) {
               BlockPos blockpos1 = blockpattern$patternhelper.getFrontTopLeft().offset(-3, 0, -3);

               for(int i = 0; i < 3; ++i) {
                  for(int j = 0; j < 3; ++j) {
                     world.setBlock(blockpos1.offset(i, 0, j), Blocks.END_PORTAL.defaultBlockState(), 2);
                  }
               }

               world.globalLevelEvent(1038, blockpos1.offset(1, 0, 1), 0);
            }

            return ActionResultType.CONSUME;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      RayTraceResult raytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.NONE);
      if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && p_77659_1_.getBlockState(((BlockRayTraceResult)raytraceresult).getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
         return ActionResult.pass(itemstack);
      } else {
         p_77659_2_.startUsingItem(p_77659_3_);
         if (p_77659_1_ instanceof ServerWorld) {
            BlockPos blockpos = ((ServerWorld)p_77659_1_).getChunkSource().getGenerator().findNearestMapFeature((ServerWorld)p_77659_1_, Structure.STRONGHOLD, p_77659_2_.blockPosition(), 100, false);
            if (blockpos != null) {
               EyeOfEnderEntity eyeofenderentity = new EyeOfEnderEntity(p_77659_1_, p_77659_2_.getX(), p_77659_2_.getY(0.5D), p_77659_2_.getZ());
               eyeofenderentity.setItem(itemstack);
               eyeofenderentity.signalTo(blockpos);
               p_77659_1_.addFreshEntity(eyeofenderentity);
               if (p_77659_2_ instanceof ServerPlayerEntity) {
                  CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayerEntity)p_77659_2_, blockpos);
               }

               p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.getX(), p_77659_2_.getY(), p_77659_2_.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
               p_77659_1_.levelEvent((PlayerEntity)null, 1003, p_77659_2_.blockPosition(), 0);
               if (!p_77659_2_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
               p_77659_2_.swing(p_77659_3_, true);
               return ActionResult.success(itemstack);
            }
         }

         return ActionResult.consume(itemstack);
      }
   }
}
