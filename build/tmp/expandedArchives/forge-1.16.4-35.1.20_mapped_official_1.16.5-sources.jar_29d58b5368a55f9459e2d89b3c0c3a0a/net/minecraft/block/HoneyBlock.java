package net.minecraft.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HoneyBlock extends BreakableBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

   public HoneyBlock(AbstractBlock.Properties p_i225762_1_) {
      super(p_i225762_1_);
   }

   private static boolean doesEntityDoHoneyBlockSlideEffects(Entity p_226937_0_) {
      return p_226937_0_ instanceof LivingEntity || p_226937_0_ instanceof AbstractMinecartEntity || p_226937_0_ instanceof TNTEntity || p_226937_0_ instanceof BoatEntity;
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return SHAPE;
   }

   public void fallOn(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      p_180658_3_.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
      if (!p_180658_1_.isClientSide) {
         p_180658_1_.broadcastEntityEvent(p_180658_3_, (byte)54);
      }

      if (p_180658_3_.causeFallDamage(p_180658_4_, 0.2F)) {
         p_180658_3_.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
      }

   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (this.isSlidingDown(p_196262_3_, p_196262_4_)) {
         this.maybeDoSlideAchievement(p_196262_4_, p_196262_3_);
         this.doSlideMovement(p_196262_4_);
         this.maybeDoSlideEffects(p_196262_2_, p_196262_4_);
      }

      super.entityInside(p_196262_1_, p_196262_2_, p_196262_3_, p_196262_4_);
   }

   private boolean isSlidingDown(BlockPos p_226935_1_, Entity p_226935_2_) {
      if (p_226935_2_.isOnGround()) {
         return false;
      } else if (p_226935_2_.getY() > (double)p_226935_1_.getY() + 0.9375D - 1.0E-7D) {
         return false;
      } else if (p_226935_2_.getDeltaMovement().y >= -0.08D) {
         return false;
      } else {
         double d0 = Math.abs((double)p_226935_1_.getX() + 0.5D - p_226935_2_.getX());
         double d1 = Math.abs((double)p_226935_1_.getZ() + 0.5D - p_226935_2_.getZ());
         double d2 = 0.4375D + (double)(p_226935_2_.getBbWidth() / 2.0F);
         return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
      }
   }

   private void maybeDoSlideAchievement(Entity p_226933_1_, BlockPos p_226933_2_) {
      if (p_226933_1_ instanceof ServerPlayerEntity && p_226933_1_.level.getGameTime() % 20L == 0L) {
         CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger((ServerPlayerEntity)p_226933_1_, p_226933_1_.level.getBlockState(p_226933_2_));
      }

   }

   private void doSlideMovement(Entity p_226938_1_) {
      Vector3d vector3d = p_226938_1_.getDeltaMovement();
      if (vector3d.y < -0.13D) {
         double d0 = -0.05D / vector3d.y;
         p_226938_1_.setDeltaMovement(new Vector3d(vector3d.x * d0, -0.05D, vector3d.z * d0));
      } else {
         p_226938_1_.setDeltaMovement(new Vector3d(vector3d.x, -0.05D, vector3d.z));
      }

      p_226938_1_.fallDistance = 0.0F;
   }

   private void maybeDoSlideEffects(World p_226934_1_, Entity p_226934_2_) {
      if (doesEntityDoHoneyBlockSlideEffects(p_226934_2_)) {
         if (p_226934_1_.random.nextInt(5) == 0) {
            p_226934_2_.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
         }

         if (!p_226934_1_.isClientSide && p_226934_1_.random.nextInt(5) == 0) {
            p_226934_1_.broadcastEntityEvent(p_226934_2_, (byte)53);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static void showSlideParticles(Entity p_226931_0_) {
      showParticles(p_226931_0_, 5);
   }

   @OnlyIn(Dist.CLIENT)
   public static void showJumpParticles(Entity p_226936_0_) {
      showParticles(p_226936_0_, 10);
   }

   @OnlyIn(Dist.CLIENT)
   private static void showParticles(Entity p_226932_0_, int p_226932_1_) {
      if (p_226932_0_.level.isClientSide) {
         BlockState blockstate = Blocks.HONEY_BLOCK.defaultBlockState();

         for(int i = 0; i < p_226932_1_; ++i) {
            p_226932_0_.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate), p_226932_0_.getX(), p_226932_0_.getY(), p_226932_0_.getZ(), 0.0D, 0.0D, 0.0D);
         }

      }
   }
}
