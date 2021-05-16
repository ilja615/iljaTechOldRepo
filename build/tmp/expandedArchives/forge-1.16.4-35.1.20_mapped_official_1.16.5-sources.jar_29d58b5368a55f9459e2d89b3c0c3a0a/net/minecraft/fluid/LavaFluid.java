package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class LavaFluid extends FlowingFluid {
   public Fluid getFlowing() {
      return Fluids.FLOWING_LAVA;
   }

   public Fluid getSource() {
      return Fluids.LAVA;
   }

   public Item getBucket() {
      return Items.LAVA_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World p_204522_1_, BlockPos p_204522_2_, FluidState p_204522_3_, Random p_204522_4_) {
      BlockPos blockpos = p_204522_2_.above();
      if (p_204522_1_.getBlockState(blockpos).isAir() && !p_204522_1_.getBlockState(blockpos).isSolidRender(p_204522_1_, blockpos)) {
         if (p_204522_4_.nextInt(100) == 0) {
            double d0 = (double)p_204522_2_.getX() + p_204522_4_.nextDouble();
            double d1 = (double)p_204522_2_.getY() + 1.0D;
            double d2 = (double)p_204522_2_.getZ() + p_204522_4_.nextDouble();
            p_204522_1_.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            p_204522_1_.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundCategory.BLOCKS, 0.2F + p_204522_4_.nextFloat() * 0.2F, 0.9F + p_204522_4_.nextFloat() * 0.15F, false);
         }

         if (p_204522_4_.nextInt(200) == 0) {
            p_204522_1_.playLocalSound((double)p_204522_2_.getX(), (double)p_204522_2_.getY(), (double)p_204522_2_.getZ(), SoundEvents.LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_204522_4_.nextFloat() * 0.2F, 0.9F + p_204522_4_.nextFloat() * 0.15F, false);
         }
      }

   }

   public void randomTick(World p_207186_1_, BlockPos p_207186_2_, FluidState p_207186_3_, Random p_207186_4_) {
      if (p_207186_1_.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         int i = p_207186_4_.nextInt(3);
         if (i > 0) {
            BlockPos blockpos = p_207186_2_;

            for(int j = 0; j < i; ++j) {
               blockpos = blockpos.offset(p_207186_4_.nextInt(3) - 1, 1, p_207186_4_.nextInt(3) - 1);
               if (!p_207186_1_.isLoaded(blockpos)) {
                  return;
               }

               BlockState blockstate = p_207186_1_.getBlockState(blockpos);
               if (blockstate.isAir()) {
                  if (this.hasFlammableNeighbours(p_207186_1_, blockpos)) {
                     p_207186_1_.setBlockAndUpdate(blockpos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_207186_1_, blockpos, p_207186_2_, Blocks.FIRE.defaultBlockState()));
                     return;
                  }
               } else if (blockstate.getMaterial().blocksMotion()) {
                  return;
               }
            }
         } else {
            for(int k = 0; k < 3; ++k) {
               BlockPos blockpos1 = p_207186_2_.offset(p_207186_4_.nextInt(3) - 1, 0, p_207186_4_.nextInt(3) - 1);
               if (!p_207186_1_.isLoaded(blockpos1)) {
                  return;
               }

               if (p_207186_1_.isEmptyBlock(blockpos1.above()) && this.isFlammable(p_207186_1_, blockpos1)) {
                  p_207186_1_.setBlockAndUpdate(blockpos1.above(), net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_207186_1_, blockpos1.above(), p_207186_2_, Blocks.FIRE.defaultBlockState()));
               }
            }
         }

      }
   }

   private boolean hasFlammableNeighbours(IWorldReader p_176369_1_, BlockPos p_176369_2_) {
      for(Direction direction : Direction.values()) {
         if (this.isFlammable(p_176369_1_, p_176369_2_.relative(direction))) {
            return true;
         }
      }

      return false;
   }

   private boolean isFlammable(IWorldReader p_176368_1_, BlockPos p_176368_2_) {
      return p_176368_2_.getY() >= 0 && p_176368_2_.getY() < 256 && !p_176368_1_.hasChunkAt(p_176368_2_) ? false : p_176368_1_.getBlockState(p_176368_2_).getMaterial().isFlammable();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticle() {
      return ParticleTypes.DRIPPING_LAVA;
   }

   protected void beforeDestroyingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, BlockState p_205580_3_) {
      this.fizz(p_205580_1_, p_205580_2_);
   }

   public int getSlopeFindDistance(IWorldReader p_185698_1_) {
      return p_185698_1_.dimensionType().ultraWarm() ? 4 : 2;
   }

   public BlockState createLegacyBlock(FluidState p_204527_1_) {
      return Blocks.LAVA.defaultBlockState().setValue(FlowingFluidBlock.LEVEL, Integer.valueOf(getLegacyLevel(p_204527_1_)));
   }

   public boolean isSame(Fluid p_207187_1_) {
      return p_207187_1_ == Fluids.LAVA || p_207187_1_ == Fluids.FLOWING_LAVA;
   }

   public int getDropOff(IWorldReader p_204528_1_) {
      return p_204528_1_.dimensionType().ultraWarm() ? 1 : 2;
   }

   public boolean canBeReplacedWith(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
      return p_215665_1_.getHeight(p_215665_2_, p_215665_3_) >= 0.44444445F && p_215665_4_.is(FluidTags.WATER);
   }

   public int getTickDelay(IWorldReader p_205569_1_) {
      return p_205569_1_.dimensionType().ultraWarm() ? 10 : 30;
   }

   public int getSpreadDelay(World p_215667_1_, BlockPos p_215667_2_, FluidState p_215667_3_, FluidState p_215667_4_) {
      int i = this.getTickDelay(p_215667_1_);
      if (!p_215667_3_.isEmpty() && !p_215667_4_.isEmpty() && !p_215667_3_.getValue(FALLING) && !p_215667_4_.getValue(FALLING) && p_215667_4_.getHeight(p_215667_1_, p_215667_2_) > p_215667_3_.getHeight(p_215667_1_, p_215667_2_) && p_215667_1_.getRandom().nextInt(4) != 0) {
         i *= 4;
      }

      return i;
   }

   private void fizz(IWorld p_205581_1_, BlockPos p_205581_2_) {
      p_205581_1_.levelEvent(1501, p_205581_2_, 0);
   }

   protected boolean canConvertToSource() {
      return false;
   }

   protected void spreadTo(IWorld p_205574_1_, BlockPos p_205574_2_, BlockState p_205574_3_, Direction p_205574_4_, FluidState p_205574_5_) {
      if (p_205574_4_ == Direction.DOWN) {
         FluidState fluidstate = p_205574_1_.getFluidState(p_205574_2_);
         if (this.is(FluidTags.LAVA) && fluidstate.is(FluidTags.WATER)) {
            if (p_205574_3_.getBlock() instanceof FlowingFluidBlock) {
               p_205574_1_.setBlock(p_205574_2_, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_205574_1_, p_205574_2_, p_205574_2_, Blocks.STONE.defaultBlockState()), 3);
            }

            this.fizz(p_205574_1_, p_205574_2_);
            return;
         }
      }

      super.spreadTo(p_205574_1_, p_205574_2_, p_205574_3_, p_205574_4_, p_205574_5_);
   }

   protected boolean isRandomlyTicking() {
      return true;
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends LavaFluid {
      protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> p_207184_1_) {
         super.createFluidStateDefinition(p_207184_1_);
         p_207184_1_.add(LEVEL);
      }

      public int getAmount(FluidState p_207192_1_) {
         return p_207192_1_.getValue(LEVEL);
      }

      public boolean isSource(FluidState p_207193_1_) {
         return false;
      }
   }

   public static class Source extends LavaFluid {
      public int getAmount(FluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(FluidState p_207193_1_) {
         return true;
      }
   }
}
