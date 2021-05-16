package net.minecraft.block;

import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TargetBlock extends Block {
   private static final IntegerProperty OUTPUT_POWER = BlockStateProperties.POWER;

   public TargetBlock(AbstractBlock.Properties p_i241188_1_) {
      super(p_i241188_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(OUTPUT_POWER, Integer.valueOf(0)));
   }

   public void onProjectileHit(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, ProjectileEntity p_220066_4_) {
      int i = updateRedstoneOutput(p_220066_1_, p_220066_2_, p_220066_3_, p_220066_4_);
      Entity entity = p_220066_4_.getOwner();
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
         serverplayerentity.awardStat(Stats.TARGET_HIT);
         CriteriaTriggers.TARGET_BLOCK_HIT.trigger(serverplayerentity, p_220066_4_, p_220066_3_.getLocation(), i);
      }

   }

   private static int updateRedstoneOutput(IWorld p_235605_0_, BlockState p_235605_1_, BlockRayTraceResult p_235605_2_, Entity p_235605_3_) {
      int i = getRedstoneStrength(p_235605_2_, p_235605_2_.getLocation());
      int j = p_235605_3_ instanceof AbstractArrowEntity ? 20 : 8;
      if (!p_235605_0_.getBlockTicks().hasScheduledTick(p_235605_2_.getBlockPos(), p_235605_1_.getBlock())) {
         setOutputPower(p_235605_0_, p_235605_1_, i, p_235605_2_.getBlockPos(), j);
      }

      return i;
   }

   private static int getRedstoneStrength(BlockRayTraceResult p_235606_0_, Vector3d p_235606_1_) {
      Direction direction = p_235606_0_.getDirection();
      double d0 = Math.abs(MathHelper.frac(p_235606_1_.x) - 0.5D);
      double d1 = Math.abs(MathHelper.frac(p_235606_1_.y) - 0.5D);
      double d2 = Math.abs(MathHelper.frac(p_235606_1_.z) - 0.5D);
      Direction.Axis direction$axis = direction.getAxis();
      double d3;
      if (direction$axis == Direction.Axis.Y) {
         d3 = Math.max(d0, d2);
      } else if (direction$axis == Direction.Axis.Z) {
         d3 = Math.max(d0, d1);
      } else {
         d3 = Math.max(d1, d2);
      }

      return Math.max(1, MathHelper.ceil(15.0D * MathHelper.clamp((0.5D - d3) / 0.5D, 0.0D, 1.0D)));
   }

   private static void setOutputPower(IWorld p_235604_0_, BlockState p_235604_1_, int p_235604_2_, BlockPos p_235604_3_, int p_235604_4_) {
      p_235604_0_.setBlock(p_235604_3_, p_235604_1_.setValue(OUTPUT_POWER, Integer.valueOf(p_235604_2_)), 3);
      p_235604_0_.getBlockTicks().scheduleTick(p_235604_3_, p_235604_1_.getBlock(), p_235604_4_);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_1_.getValue(OUTPUT_POWER) != 0) {
         p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(OUTPUT_POWER, Integer.valueOf(0)), 3);
      }

   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(OUTPUT_POWER);
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(OUTPUT_POWER);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_2_.isClientSide() && !p_220082_1_.is(p_220082_4_.getBlock())) {
         if (p_220082_1_.getValue(OUTPUT_POWER) > 0 && !p_220082_2_.getBlockTicks().hasScheduledTick(p_220082_3_, this)) {
            p_220082_2_.setBlock(p_220082_3_, p_220082_1_.setValue(OUTPUT_POWER, Integer.valueOf(0)), 18);
         }

      }
   }
}
