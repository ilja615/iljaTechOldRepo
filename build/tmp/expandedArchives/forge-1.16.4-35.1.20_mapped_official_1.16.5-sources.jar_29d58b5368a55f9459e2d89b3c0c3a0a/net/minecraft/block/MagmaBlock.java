package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MagmaBlock extends Block {
   public MagmaBlock(AbstractBlock.Properties p_i48366_1_) {
      super(p_i48366_1_);
   }

   public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      if (!p_176199_3_.fireImmune() && p_176199_3_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)p_176199_3_)) {
         p_176199_3_.hurt(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.stepOn(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      BubbleColumnBlock.growColumn(p_225534_2_, p_225534_3_.above(), true);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.UP && p_196271_3_.is(Blocks.WATER)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 20);
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      BlockPos blockpos = p_225542_3_.above();
      if (p_225542_2_.getFluidState(p_225542_3_).is(FluidTags.WATER)) {
         p_225542_2_.playSound((PlayerEntity)null, p_225542_3_, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_225542_2_.random.nextFloat() - p_225542_2_.random.nextFloat()) * 0.8F);
         p_225542_2_.sendParticles(ParticleTypes.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
      }

   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      p_220082_2_.getBlockTicks().scheduleTick(p_220082_3_, this, 20);
   }
}
