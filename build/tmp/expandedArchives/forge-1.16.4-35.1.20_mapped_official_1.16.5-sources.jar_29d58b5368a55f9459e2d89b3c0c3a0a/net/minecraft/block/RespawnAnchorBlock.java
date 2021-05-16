package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ICollisionReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RespawnAnchorBlock extends Block {
   public static final IntegerProperty CHARGE = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;
   private static final ImmutableList<Vector3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vector3i(0, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(0, 0, 1), new Vector3i(1, 0, 0), new Vector3i(-1, 0, -1), new Vector3i(1, 0, -1), new Vector3i(-1, 0, 1), new Vector3i(1, 0, 1));
   private static final ImmutableList<Vector3i> RESPAWN_OFFSETS = (new Builder<Vector3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::above).iterator()).add(new Vector3i(0, 1, 0)).build();

   public RespawnAnchorBlock(AbstractBlock.Properties p_i241185_1_) {
      super(p_i241185_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(CHARGE, Integer.valueOf(0)));
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack itemstack = p_225533_4_.getItemInHand(p_225533_5_);
      if (p_225533_5_ == Hand.MAIN_HAND && !isRespawnFuel(itemstack) && isRespawnFuel(p_225533_4_.getItemInHand(Hand.OFF_HAND))) {
         return ActionResultType.PASS;
      } else if (isRespawnFuel(itemstack) && canBeCharged(p_225533_1_)) {
         charge(p_225533_2_, p_225533_3_, p_225533_1_);
         if (!p_225533_4_.abilities.instabuild) {
            itemstack.shrink(1);
         }

         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else if (p_225533_1_.getValue(CHARGE) == 0) {
         return ActionResultType.PASS;
      } else if (!canSetSpawn(p_225533_2_)) {
         if (!p_225533_2_.isClientSide) {
            this.explode(p_225533_1_, p_225533_2_, p_225533_3_);
         }

         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         if (!p_225533_2_.isClientSide) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_225533_4_;
            if (serverplayerentity.getRespawnDimension() != p_225533_2_.dimension() || !serverplayerentity.getRespawnPosition().equals(p_225533_3_)) {
               serverplayerentity.setRespawnPosition(p_225533_2_.dimension(), p_225533_3_, 0.0F, false, true);
               p_225533_2_.playSound((PlayerEntity)null, (double)p_225533_3_.getX() + 0.5D, (double)p_225533_3_.getY() + 0.5D, (double)p_225533_3_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
               return ActionResultType.SUCCESS;
            }
         }

         return ActionResultType.CONSUME;
      }
   }

   private static boolean isRespawnFuel(ItemStack p_235561_0_) {
      return p_235561_0_.getItem() == Items.GLOWSTONE;
   }

   private static boolean canBeCharged(BlockState p_235568_0_) {
      return p_235568_0_.getValue(CHARGE) < 4;
   }

   private static boolean isWaterThatWouldFlow(BlockPos p_235566_0_, World p_235566_1_) {
      FluidState fluidstate = p_235566_1_.getFluidState(p_235566_0_);
      if (!fluidstate.is(FluidTags.WATER)) {
         return false;
      } else if (fluidstate.isSource()) {
         return true;
      } else {
         float f = (float)fluidstate.getAmount();
         if (f < 2.0F) {
            return false;
         } else {
            FluidState fluidstate1 = p_235566_1_.getFluidState(p_235566_0_.below());
            return !fluidstate1.is(FluidTags.WATER);
         }
      }
   }

   private void explode(BlockState p_235567_1_, World p_235567_2_, final BlockPos p_235567_3_) {
      p_235567_2_.removeBlock(p_235567_3_, false);
      boolean flag = Direction.Plane.HORIZONTAL.stream().map(p_235567_3_::relative).anyMatch((p_235563_1_) -> {
         return isWaterThatWouldFlow(p_235563_1_, p_235567_2_);
      });
      final boolean flag1 = flag || p_235567_2_.getFluidState(p_235567_3_.above()).is(FluidTags.WATER);
      ExplosionContext explosioncontext = new ExplosionContext() {
         public Optional<Float> getBlockExplosionResistance(Explosion p_230312_1_, IBlockReader p_230312_2_, BlockPos p_230312_3_, BlockState p_230312_4_, FluidState p_230312_5_) {
            return p_230312_3_.equals(p_235567_3_) && flag1 ? Optional.of(Blocks.WATER.getExplosionResistance()) : super.getBlockExplosionResistance(p_230312_1_, p_230312_2_, p_230312_3_, p_230312_4_, p_230312_5_);
         }
      };
      p_235567_2_.explode((Entity)null, DamageSource.badRespawnPointExplosion(), explosioncontext, (double)p_235567_3_.getX() + 0.5D, (double)p_235567_3_.getY() + 0.5D, (double)p_235567_3_.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
   }

   public static boolean canSetSpawn(World p_235562_0_) {
      return p_235562_0_.dimensionType().respawnAnchorWorks();
   }

   public static void charge(World p_235564_0_, BlockPos p_235564_1_, BlockState p_235564_2_) {
      p_235564_0_.setBlock(p_235564_1_, p_235564_2_.setValue(CHARGE, Integer.valueOf(p_235564_2_.getValue(CHARGE) + 1)), 3);
      p_235564_0_.playSound((PlayerEntity)null, (double)p_235564_1_.getX() + 0.5D, (double)p_235564_1_.getY() + 0.5D, (double)p_235564_1_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.getValue(CHARGE) != 0) {
         if (p_180655_4_.nextInt(100) == 0) {
            p_180655_2_.playSound((PlayerEntity)null, (double)p_180655_3_.getX() + 0.5D, (double)p_180655_3_.getY() + 0.5D, (double)p_180655_3_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         double d0 = (double)p_180655_3_.getX() + 0.5D + (0.5D - p_180655_4_.nextDouble());
         double d1 = (double)p_180655_3_.getY() + 1.0D;
         double d2 = (double)p_180655_3_.getZ() + 0.5D + (0.5D - p_180655_4_.nextDouble());
         double d3 = (double)p_180655_4_.nextFloat() * 0.04D;
         p_180655_2_.addParticle(ParticleTypes.REVERSE_PORTAL, d0, d1, d2, 0.0D, d3, 0.0D);
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(CHARGE);
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public static int getScaledChargeLevel(BlockState p_235565_0_, int p_235565_1_) {
      return MathHelper.floor((float)(p_235565_0_.getValue(CHARGE) - 0) / 4.0F * (float)p_235565_1_);
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return getScaledChargeLevel(p_180641_1_, 15);
   }

   public static Optional<Vector3d> findStandUpPosition(EntityType<?> p_235560_0_, ICollisionReader p_235560_1_, BlockPos p_235560_2_) {
      Optional<Vector3d> optional = findStandUpPosition(p_235560_0_, p_235560_1_, p_235560_2_, true);
      return optional.isPresent() ? optional : findStandUpPosition(p_235560_0_, p_235560_1_, p_235560_2_, false);
   }

   private static Optional<Vector3d> findStandUpPosition(EntityType<?> p_242678_0_, ICollisionReader p_242678_1_, BlockPos p_242678_2_, boolean p_242678_3_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Vector3i vector3i : RESPAWN_OFFSETS) {
         blockpos$mutable.set(p_242678_2_).move(vector3i);
         Vector3d vector3d = TransportationHelper.findSafeDismountLocation(p_242678_0_, p_242678_1_, blockpos$mutable, p_242678_3_);
         if (vector3d != null) {
            return Optional.of(vector3d);
         }
      }

      return Optional.empty();
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
