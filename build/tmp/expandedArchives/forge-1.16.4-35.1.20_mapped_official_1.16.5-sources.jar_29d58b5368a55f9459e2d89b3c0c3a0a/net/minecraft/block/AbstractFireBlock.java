package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractFireBlock extends Block {
   private final float fireDamage;
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

   public AbstractFireBlock(AbstractBlock.Properties p_i241173_1_, float p_i241173_2_) {
      super(p_i241173_1_);
      this.fireDamage = p_i241173_2_;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return getState(p_196258_1_.getLevel(), p_196258_1_.getClickedPos());
   }

   public static BlockState getState(IBlockReader p_235326_0_, BlockPos p_235326_1_) {
      BlockPos blockpos = p_235326_1_.below();
      BlockState blockstate = p_235326_0_.getBlockState(blockpos);
      return SoulFireBlock.canSurviveOnBlock(blockstate.getBlock()) ? Blocks.SOUL_FIRE.defaultBlockState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(p_235326_0_, p_235326_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return DOWN_AABB;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(24) == 0) {
         p_180655_2_.playLocalSound((double)p_180655_3_.getX() + 0.5D, (double)p_180655_3_.getY() + 0.5D, (double)p_180655_3_.getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + p_180655_4_.nextFloat(), p_180655_4_.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos blockpos = p_180655_3_.below();
      BlockState blockstate = p_180655_2_.getBlockState(blockpos);
      if (!this.canBurn(blockstate) && !blockstate.isFaceSturdy(p_180655_2_, blockpos, Direction.UP)) {
         if (this.canBurn(p_180655_2_.getBlockState(p_180655_3_.west()))) {
            for(int j = 0; j < 2; ++j) {
               double d3 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble() * (double)0.1F;
               double d8 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d13 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_180655_2_.getBlockState(p_180655_3_.east()))) {
            for(int k = 0; k < 2; ++k) {
               double d4 = (double)(p_180655_3_.getX() + 1) - p_180655_4_.nextDouble() * (double)0.1F;
               double d9 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d14 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_180655_2_.getBlockState(p_180655_3_.north()))) {
            for(int l = 0; l < 2; ++l) {
               double d5 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               double d10 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d15 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble() * (double)0.1F;
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_180655_2_.getBlockState(p_180655_3_.south()))) {
            for(int i1 = 0; i1 < 2; ++i1) {
               double d6 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               double d11 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               double d16 = (double)(p_180655_3_.getZ() + 1) - p_180655_4_.nextDouble() * (double)0.1F;
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_180655_2_.getBlockState(p_180655_3_.above()))) {
            for(int j1 = 0; j1 < 2; ++j1) {
               double d7 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               double d12 = (double)(p_180655_3_.getY() + 1) - p_180655_4_.nextDouble() * (double)0.1F;
               double d17 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(int i = 0; i < 3; ++i) {
            double d0 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
            double d1 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
            p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected abstract boolean canBurn(BlockState p_196446_1_);

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_4_.fireImmune()) {
         p_196262_4_.setRemainingFireTicks(p_196262_4_.getRemainingFireTicks() + 1);
         if (p_196262_4_.getRemainingFireTicks() == 0) {
            p_196262_4_.setSecondsOnFire(8);
         }

         p_196262_4_.hurt(DamageSource.IN_FIRE, this.fireDamage);
      }

      super.entityInside(p_196262_1_, p_196262_2_, p_196262_3_, p_196262_4_);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock())) {
         if (inPortalDimension(p_220082_2_)) {
            Optional<PortalSize> optional = PortalSize.findEmptyPortalShape(p_220082_2_, p_220082_3_, Direction.Axis.X);
            optional =  net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(p_220082_2_, p_220082_3_, optional);
            if (optional.isPresent()) {
               optional.get().createPortalBlocks();
               return;
            }
         }

         if (!p_220082_1_.canSurvive(p_220082_2_, p_220082_3_)) {
            p_220082_2_.removeBlock(p_220082_3_, false);
         }

      }
   }

   private static boolean inPortalDimension(World p_242649_0_) {
      return p_242649_0_.dimension() == World.OVERWORLD || p_242649_0_.dimension() == World.NETHER;
   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isClientSide()) {
         p_176208_1_.levelEvent((PlayerEntity)null, 1009, p_176208_2_, 0);
      }

   }

   public static boolean canBePlacedAt(World p_241465_0_, BlockPos p_241465_1_, Direction p_241465_2_) {
      BlockState blockstate = p_241465_0_.getBlockState(p_241465_1_);
      if (!blockstate.isAir()) {
         return false;
      } else {
         return getState(p_241465_0_, p_241465_1_).canSurvive(p_241465_0_, p_241465_1_) || isPortal(p_241465_0_, p_241465_1_, p_241465_2_);
      }
   }

   private static boolean isPortal(World p_241466_0_, BlockPos p_241466_1_, Direction p_241466_2_) {
      if (!inPortalDimension(p_241466_0_)) {
         return false;
      } else {
         BlockPos.Mutable blockpos$mutable = p_241466_1_.mutable();
         boolean flag = false;

         for(Direction direction : Direction.values()) {
            if (p_241466_0_.getBlockState(blockpos$mutable.set(p_241466_1_).move(direction)).is(Blocks.OBSIDIAN)) {
               flag = true;
               break;
            }
         }

         return flag && PortalSize.findEmptyPortalShape(p_241466_0_, p_241466_1_, p_241466_2_.getCounterClockWise().getAxis()).isPresent();
      }
   }
}
