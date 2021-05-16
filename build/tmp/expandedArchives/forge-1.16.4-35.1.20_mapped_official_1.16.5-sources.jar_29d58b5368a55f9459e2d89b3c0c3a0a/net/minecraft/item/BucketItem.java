package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BucketItem extends Item {
   private final Fluid content;

   // Forge: Use the other constructor that takes a Supplier
   @Deprecated
   public BucketItem(Fluid p_i49025_1_, Item.Properties p_i49025_2_) {
      super(p_i49025_2_);
      this.content = p_i49025_1_;
      this.fluidSupplier = p_i49025_1_.delegate;
   }

   /**
    * @param supplier A fluid supplier such as {@link net.minecraftforge.fml.RegistryObject<Fluid>}
    */
   public BucketItem(java.util.function.Supplier<? extends Fluid> supplier, Item.Properties builder) {
      super(builder);
      this.content = null;
      this.fluidSupplier = supplier;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      RayTraceResult raytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, this.content == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
      ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(p_77659_2_, p_77659_1_, itemstack, raytraceresult);
      if (ret != null) return ret;
      if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
         return ActionResult.pass(itemstack);
      } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
         return ActionResult.pass(itemstack);
      } else {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
         BlockPos blockpos = blockraytraceresult.getBlockPos();
         Direction direction = blockraytraceresult.getDirection();
         BlockPos blockpos1 = blockpos.relative(direction);
         if (p_77659_1_.mayInteract(p_77659_2_, blockpos) && p_77659_2_.mayUseItemAt(blockpos1, direction, itemstack)) {
            if (this.content == Fluids.EMPTY) {
               BlockState blockstate1 = p_77659_1_.getBlockState(blockpos);
               if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                  Fluid fluid = ((IBucketPickupHandler)blockstate1.getBlock()).takeLiquid(p_77659_1_, blockpos, blockstate1);
                  if (fluid != Fluids.EMPTY) {
                     p_77659_2_.awardStat(Stats.ITEM_USED.get(this));

                     SoundEvent soundevent = this.content.getAttributes().getFillSound();
                     if (soundevent == null) soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                     p_77659_2_.playSound(soundevent, 1.0F, 1.0F);
                     ItemStack itemstack1 = DrinkHelper.createFilledResult(itemstack, p_77659_2_, new ItemStack(fluid.getBucket()));
                     if (!p_77659_1_.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)p_77659_2_, new ItemStack(fluid.getBucket()));
                     }

                     return ActionResult.sidedSuccess(itemstack1, p_77659_1_.isClientSide());
                  }
               }

               return ActionResult.fail(itemstack);
            } else {
               BlockState blockstate = p_77659_1_.getBlockState(blockpos);
               BlockPos blockpos2 = canBlockContainFluid(p_77659_1_, blockpos, blockstate) ? blockpos : blockpos1;
               if (this.emptyBucket(p_77659_2_, p_77659_1_, blockpos2, blockraytraceresult)) {
                  this.checkExtraContent(p_77659_1_, itemstack, blockpos2);
                  if (p_77659_2_ instanceof ServerPlayerEntity) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)p_77659_2_, blockpos2, itemstack);
                  }

                  p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
                  return ActionResult.sidedSuccess(this.getEmptySuccessItem(itemstack, p_77659_2_), p_77659_1_.isClientSide());
               } else {
                  return ActionResult.fail(itemstack);
               }
            }
         } else {
            return ActionResult.fail(itemstack);
         }
      }
   }

   protected ItemStack getEmptySuccessItem(ItemStack p_203790_1_, PlayerEntity p_203790_2_) {
      return !p_203790_2_.abilities.instabuild ? new ItemStack(Items.BUCKET) : p_203790_1_;
   }

   public void checkExtraContent(World p_203792_1_, ItemStack p_203792_2_, BlockPos p_203792_3_) {
   }

   public boolean emptyBucket(@Nullable PlayerEntity p_180616_1_, World p_180616_2_, BlockPos p_180616_3_, @Nullable BlockRayTraceResult p_180616_4_) {
      if (!(this.content instanceof FlowingFluid)) {
         return false;
      } else {
         BlockState blockstate = p_180616_2_.getBlockState(p_180616_3_);
         Block block = blockstate.getBlock();
         Material material = blockstate.getMaterial();
         boolean flag = blockstate.canBeReplaced(this.content);
         boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(p_180616_2_, p_180616_3_, blockstate, this.content);
         if (!flag1) {
            return p_180616_4_ != null && this.emptyBucket(p_180616_1_, p_180616_2_, p_180616_4_.getBlockPos().relative(p_180616_4_.getDirection()), (BlockRayTraceResult)null);
         } else if (p_180616_2_.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int i = p_180616_3_.getX();
            int j = p_180616_3_.getY();
            int k = p_180616_3_.getZ();
            p_180616_2_.playSound(p_180616_1_, p_180616_3_, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_180616_2_.random.nextFloat() - p_180616_2_.random.nextFloat()) * 0.8F);

            for(int l = 0; l < 8; ++l) {
               p_180616_2_.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
            }

            return true;
         } else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(p_180616_2_,p_180616_3_,blockstate,content)) {
            ((ILiquidContainer)block).placeLiquid(p_180616_2_, p_180616_3_, blockstate, ((FlowingFluid)this.content).getSource(false));
            this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
            return true;
         } else {
            if (!p_180616_2_.isClientSide && flag && !material.isLiquid()) {
               p_180616_2_.destroyBlock(p_180616_3_, true);
            }

            if (!p_180616_2_.setBlock(p_180616_3_, this.content.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
               return false;
            } else {
               this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
               return true;
            }
         }
      }
   }

   protected void playEmptySound(@Nullable PlayerEntity p_203791_1_, IWorld p_203791_2_, BlockPos p_203791_3_) {
      SoundEvent soundevent = this.content.getAttributes().getEmptySound();
      if(soundevent == null) soundevent = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
      p_203791_2_.playSound(p_203791_1_, p_203791_3_, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   @Override
   public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
      if (this.getClass() == BucketItem.class)
         return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
      else
         return super.initCapabilities(stack, nbt);
   }

   private final java.util.function.Supplier<? extends Fluid> fluidSupplier;
   public Fluid getFluid() { return fluidSupplier.get(); }

   private boolean canBlockContainFluid(World worldIn, BlockPos posIn, BlockState blockstate)
   {
      return blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, this.content);
   }
}
