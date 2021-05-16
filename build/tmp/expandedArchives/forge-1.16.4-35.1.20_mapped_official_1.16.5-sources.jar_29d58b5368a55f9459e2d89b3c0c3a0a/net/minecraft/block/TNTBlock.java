package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTBlock extends Block {
   public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

   public TNTBlock(AbstractBlock.Properties p_i48309_1_) {
      super(p_i48309_1_);
      this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, Boolean.valueOf(false)));
   }

   public void catchFire(BlockState state, World world, BlockPos pos, @Nullable net.minecraft.util.Direction face, @Nullable LivingEntity igniter) {
      explode(world, pos, igniter);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock())) {
         if (p_220082_2_.hasNeighborSignal(p_220082_3_)) {
            catchFire(p_220082_1_, p_220082_2_, p_220082_3_, null, null);
            p_220082_2_.removeBlock(p_220082_3_, false);
         }

      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_2_.hasNeighborSignal(p_220069_3_)) {
         catchFire(p_220069_1_, p_220069_2_, p_220069_3_, null, null);
         p_220069_2_.removeBlock(p_220069_3_, false);
      }

   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isClientSide() && !p_176208_4_.isCreative() && p_176208_3_.getValue(UNSTABLE)) {
         catchFire(p_176208_3_, p_176208_1_, p_176208_2_, null, null);
      }

      super.playerWillDestroy(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public void wasExploded(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
      if (!p_180652_1_.isClientSide) {
         TNTEntity tntentity = new TNTEntity(p_180652_1_, (double)p_180652_2_.getX() + 0.5D, (double)p_180652_2_.getY(), (double)p_180652_2_.getZ() + 0.5D, p_180652_3_.getSourceMob());
         tntentity.setFuse((short)(p_180652_1_.random.nextInt(tntentity.getLife() / 4) + tntentity.getLife() / 8));
         p_180652_1_.addFreshEntity(tntentity);
      }
   }

   @Deprecated //Forge: Prefer using IForgeBlock#catchFire
   public static void explode(World p_196534_0_, BlockPos p_196534_1_) {
      explode(p_196534_0_, p_196534_1_, (LivingEntity)null);
   }

   @Deprecated //Forge: Prefer using IForgeBlock#catchFire
   private static void explode(World p_196535_0_, BlockPos p_196535_1_, @Nullable LivingEntity p_196535_2_) {
      if (!p_196535_0_.isClientSide) {
         TNTEntity tntentity = new TNTEntity(p_196535_0_, (double)p_196535_1_.getX() + 0.5D, (double)p_196535_1_.getY(), (double)p_196535_1_.getZ() + 0.5D, p_196535_2_);
         p_196535_0_.addFreshEntity(tntentity);
         p_196535_0_.playSound((PlayerEntity)null, tntentity.getX(), tntentity.getY(), tntentity.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack itemstack = p_225533_4_.getItemInHand(p_225533_5_);
      Item item = itemstack.getItem();
      if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
         return super.use(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_4_, p_225533_5_, p_225533_6_);
      } else {
         catchFire(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_6_.getDirection(), p_225533_4_);
         p_225533_2_.setBlock(p_225533_3_, Blocks.AIR.defaultBlockState(), 11);
         if (!p_225533_4_.isCreative()) {
            if (item == Items.FLINT_AND_STEEL) {
               itemstack.hurtAndBreak(1, p_225533_4_, (p_220287_1_) -> {
                  p_220287_1_.broadcastBreakEvent(p_225533_5_);
               });
            } else {
               itemstack.shrink(1);
            }
         }

         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      }
   }

   public void onProjectileHit(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, ProjectileEntity p_220066_4_) {
      if (!p_220066_1_.isClientSide) {
         Entity entity = p_220066_4_.getOwner();
         if (p_220066_4_.isOnFire()) {
            BlockPos blockpos = p_220066_3_.getBlockPos();
            catchFire(p_220066_2_, p_220066_1_, blockpos, null, entity instanceof LivingEntity ? (LivingEntity)entity : null);
            p_220066_1_.removeBlock(blockpos, false);
         }
      }

   }

   public boolean dropFromExplosion(Explosion p_149659_1_) {
      return false;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UNSTABLE);
   }
}
