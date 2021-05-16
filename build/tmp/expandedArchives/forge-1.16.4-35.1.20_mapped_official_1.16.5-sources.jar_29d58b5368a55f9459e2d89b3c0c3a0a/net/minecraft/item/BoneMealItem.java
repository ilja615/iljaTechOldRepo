package net.minecraft.item;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BoneMealItem extends Item {
   public BoneMealItem(Item.Properties p_i50055_1_) {
      super(p_i50055_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockPos blockpos1 = blockpos.relative(p_195939_1_.getClickedFace());
      if (applyBonemeal(p_195939_1_.getItemInHand(), world, blockpos, p_195939_1_.getPlayer())) {
         if (!world.isClientSide) {
            world.levelEvent(2005, blockpos, 0);
         }

         return ActionResultType.sidedSuccess(world.isClientSide);
      } else {
         BlockState blockstate = world.getBlockState(blockpos);
         boolean flag = blockstate.isFaceSturdy(world, blockpos, p_195939_1_.getClickedFace());
         if (flag && growWaterPlant(p_195939_1_.getItemInHand(), world, blockpos1, p_195939_1_.getClickedFace())) {
            if (!world.isClientSide) {
               world.levelEvent(2005, blockpos1, 0);
            }

            return ActionResultType.sidedSuccess(world.isClientSide);
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   @Deprecated //Forge: Use Player/Hand version
   public static boolean growCrop(ItemStack p_195966_0_, World p_195966_1_, BlockPos p_195966_2_) {
      if (p_195966_1_ instanceof net.minecraft.world.server.ServerWorld)
         return applyBonemeal(p_195966_0_, p_195966_1_, p_195966_2_, net.minecraftforge.common.util.FakePlayerFactory.getMinecraft((net.minecraft.world.server.ServerWorld)p_195966_1_));
      return false;
   }

   public static boolean applyBonemeal(ItemStack p_195966_0_, World p_195966_1_, BlockPos p_195966_2_, net.minecraft.entity.player.PlayerEntity player) {
      BlockState blockstate = p_195966_1_.getBlockState(p_195966_2_);
      int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, p_195966_1_, p_195966_2_, blockstate, p_195966_0_);
      if (hook != 0) return hook > 0;
      if (blockstate.getBlock() instanceof IGrowable) {
         IGrowable igrowable = (IGrowable)blockstate.getBlock();
         if (igrowable.isValidBonemealTarget(p_195966_1_, p_195966_2_, blockstate, p_195966_1_.isClientSide)) {
            if (p_195966_1_ instanceof ServerWorld) {
               if (igrowable.isBonemealSuccess(p_195966_1_, p_195966_1_.random, p_195966_2_, blockstate)) {
                  igrowable.performBonemeal((ServerWorld)p_195966_1_, p_195966_1_.random, p_195966_2_, blockstate);
               }

               p_195966_0_.shrink(1);
            }

            return true;
         }
      }

      return false;
   }

   public static boolean growWaterPlant(ItemStack p_203173_0_, World p_203173_1_, BlockPos p_203173_2_, @Nullable Direction p_203173_3_) {
      if (p_203173_1_.getBlockState(p_203173_2_).is(Blocks.WATER) && p_203173_1_.getFluidState(p_203173_2_).getAmount() == 8) {
         if (!(p_203173_1_ instanceof ServerWorld)) {
            return true;
         } else {
            label80:
            for(int i = 0; i < 128; ++i) {
               BlockPos blockpos = p_203173_2_;
               BlockState blockstate = Blocks.SEAGRASS.defaultBlockState();

               for(int j = 0; j < i / 16; ++j) {
                  blockpos = blockpos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                  if (p_203173_1_.getBlockState(blockpos).isCollisionShapeFullBlock(p_203173_1_, blockpos)) {
                     continue label80;
                  }
               }

               Optional<RegistryKey<Biome>> optional = p_203173_1_.getBiomeName(blockpos);
               if (Objects.equals(optional, Optional.of(Biomes.WARM_OCEAN)) || Objects.equals(optional, Optional.of(Biomes.DEEP_WARM_OCEAN))) {
                  if (i == 0 && p_203173_3_ != null && p_203173_3_.getAxis().isHorizontal()) {
                     blockstate = BlockTags.WALL_CORALS.getRandomElement(p_203173_1_.random).defaultBlockState().setValue(DeadCoralWallFanBlock.FACING, p_203173_3_);
                  } else if (random.nextInt(4) == 0) {
                     blockstate = BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random).defaultBlockState();
                  }
               }

               if (blockstate.getBlock().is(BlockTags.WALL_CORALS)) {
                  for(int k = 0; !blockstate.canSurvive(p_203173_1_, blockpos) && k < 4; ++k) {
                     blockstate = blockstate.setValue(DeadCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                  }
               }

               if (blockstate.canSurvive(p_203173_1_, blockpos)) {
                  BlockState blockstate1 = p_203173_1_.getBlockState(blockpos);
                  if (blockstate1.is(Blocks.WATER) && p_203173_1_.getFluidState(blockpos).getAmount() == 8) {
                     p_203173_1_.setBlock(blockpos, blockstate, 3);
                  } else if (blockstate1.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                     ((IGrowable)Blocks.SEAGRASS).performBonemeal((ServerWorld)p_203173_1_, random, blockpos, blockstate1);
                  }
               }
            }

            p_203173_0_.shrink(1);
            return true;
         }
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addGrowthParticles(IWorld p_195965_0_, BlockPos p_195965_1_, int p_195965_2_) {
      if (p_195965_2_ == 0) {
         p_195965_2_ = 15;
      }

      BlockState blockstate = p_195965_0_.getBlockState(p_195965_1_);
      if (!blockstate.isAir(p_195965_0_, p_195965_1_)) {
         double d0 = 0.5D;
         double d1;
         if (blockstate.is(Blocks.WATER)) {
            p_195965_2_ *= 3;
            d1 = 1.0D;
            d0 = 3.0D;
         } else if (blockstate.isSolidRender(p_195965_0_, p_195965_1_)) {
            p_195965_1_ = p_195965_1_.above();
            p_195965_2_ *= 3;
            d0 = 3.0D;
            d1 = 1.0D;
         } else {
            d1 = blockstate.getShape(p_195965_0_, p_195965_1_).max(Direction.Axis.Y);
         }

         p_195965_0_.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)p_195965_1_.getX() + 0.5D, (double)p_195965_1_.getY() + 0.5D, (double)p_195965_1_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);

         for(int i = 0; i < p_195965_2_; ++i) {
            double d2 = random.nextGaussian() * 0.02D;
            double d3 = random.nextGaussian() * 0.02D;
            double d4 = random.nextGaussian() * 0.02D;
            double d5 = 0.5D - d0;
            double d6 = (double)p_195965_1_.getX() + d5 + random.nextDouble() * d0 * 2.0D;
            double d7 = (double)p_195965_1_.getY() + random.nextDouble() * d1;
            double d8 = (double)p_195965_1_.getZ() + d5 + random.nextDouble() * d0 * 2.0D;
            if (!p_195965_0_.getBlockState((new BlockPos(d6, d7, d8)).below()).isAir()) {
               p_195965_0_.addParticle(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
            }
         }

      }
   }
}
