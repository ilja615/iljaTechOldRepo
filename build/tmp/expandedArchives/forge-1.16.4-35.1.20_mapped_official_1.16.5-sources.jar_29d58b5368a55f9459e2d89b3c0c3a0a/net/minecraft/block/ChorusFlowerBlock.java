package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ChorusFlowerBlock extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
   private final ChorusPlantBlock plant;

   public ChorusFlowerBlock(ChorusPlantBlock p_i48429_1_, AbstractBlock.Properties p_i48429_2_) {
      super(p_i48429_2_);
      this.plant = p_i48429_1_;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      }

   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return p_149653_1_.getValue(AGE) < 5;
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      BlockPos blockpos = p_225542_3_.above();
      if (p_225542_2_.isEmptyBlock(blockpos) && blockpos.getY() < 256) {
         int i = p_225542_1_.getValue(AGE);
         if (i < 5 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225542_2_, blockpos, p_225542_1_, true)) {
            boolean flag = false;
            boolean flag1 = false;
            BlockState blockstate = p_225542_2_.getBlockState(p_225542_3_.below());
            Block block = blockstate.getBlock();
            if (block == Blocks.END_STONE) {
               flag = true;
            } else if (block == this.plant) {
               int j = 1;

               for(int k = 0; k < 4; ++k) {
                  Block block1 = p_225542_2_.getBlockState(p_225542_3_.below(j + 1)).getBlock();
                  if (block1 != this.plant) {
                     if (block1 == Blocks.END_STONE) {
                        flag1 = true;
                     }
                     break;
                  }

                  ++j;
               }

               if (j < 2 || j <= p_225542_4_.nextInt(flag1 ? 5 : 4)) {
                  flag = true;
               }
            } else if (blockstate.isAir(p_225542_2_, p_225542_3_.below())) {
               flag = true;
            }

            if (flag && allNeighborsEmpty(p_225542_2_, blockpos, (Direction)null) && p_225542_2_.isEmptyBlock(p_225542_3_.above(2))) {
               p_225542_2_.setBlock(p_225542_3_, this.plant.getStateForPlacement(p_225542_2_, p_225542_3_), 2);
               this.placeGrownFlower(p_225542_2_, blockpos, i);
            } else if (i < 4) {
               int l = p_225542_4_.nextInt(4);
               if (flag1) {
                  ++l;
               }

               boolean flag2 = false;

               for(int i1 = 0; i1 < l; ++i1) {
                  Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_225542_4_);
                  BlockPos blockpos1 = p_225542_3_.relative(direction);
                  if (p_225542_2_.isEmptyBlock(blockpos1) && p_225542_2_.isEmptyBlock(blockpos1.below()) && allNeighborsEmpty(p_225542_2_, blockpos1, direction.getOpposite())) {
                     this.placeGrownFlower(p_225542_2_, blockpos1, i + 1);
                     flag2 = true;
                  }
               }

               if (flag2) {
                  p_225542_2_.setBlock(p_225542_3_, this.plant.getStateForPlacement(p_225542_2_, p_225542_3_), 2);
               } else {
                  this.placeDeadFlower(p_225542_2_, p_225542_3_);
               }
            } else {
               this.placeDeadFlower(p_225542_2_, p_225542_3_);
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_225542_2_, p_225542_3_, p_225542_1_);
         }
      }
   }

   private void placeGrownFlower(World p_185602_1_, BlockPos p_185602_2_, int p_185602_3_) {
      p_185602_1_.setBlock(p_185602_2_, this.defaultBlockState().setValue(AGE, Integer.valueOf(p_185602_3_)), 2);
      p_185602_1_.levelEvent(1033, p_185602_2_, 0);
   }

   private void placeDeadFlower(World p_185605_1_, BlockPos p_185605_2_) {
      p_185605_1_.setBlock(p_185605_2_, this.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      p_185605_1_.levelEvent(1034, p_185605_2_, 0);
   }

   private static boolean allNeighborsEmpty(IWorldReader p_185604_0_, BlockPos p_185604_1_, @Nullable Direction p_185604_2_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (direction != p_185604_2_ && !p_185604_0_.isEmptyBlock(p_185604_1_.relative(direction))) {
            return false;
         }
      }

      return true;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ != Direction.UP && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.below());
      if (blockstate.getBlock() != this.plant && !blockstate.is(Blocks.END_STONE)) {
         if (!blockstate.isAir(p_196260_2_, p_196260_3_.below())) {
            return false;
         } else {
            boolean flag = false;

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               BlockState blockstate1 = p_196260_2_.getBlockState(p_196260_3_.relative(direction));
               if (blockstate1.is(this.plant)) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else if (!blockstate1.isAir(p_196260_2_, p_196260_3_.relative(direction))) {
                  return false;
               }
            }

            return flag;
         }
      } else {
         return true;
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public static void generatePlant(IWorld p_185603_0_, BlockPos p_185603_1_, Random p_185603_2_, int p_185603_3_) {
      p_185603_0_.setBlock(p_185603_1_, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(p_185603_0_, p_185603_1_), 2);
      growTreeRecursive(p_185603_0_, p_185603_1_, p_185603_2_, p_185603_1_, p_185603_3_, 0);
   }

   private static void growTreeRecursive(IWorld p_185601_0_, BlockPos p_185601_1_, Random p_185601_2_, BlockPos p_185601_3_, int p_185601_4_, int p_185601_5_) {
      ChorusPlantBlock chorusplantblock = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
      int i = p_185601_2_.nextInt(4) + 1;
      if (p_185601_5_ == 0) {
         ++i;
      }

      for(int j = 0; j < i; ++j) {
         BlockPos blockpos = p_185601_1_.above(j + 1);
         if (!allNeighborsEmpty(p_185601_0_, blockpos, (Direction)null)) {
            return;
         }

         p_185601_0_.setBlock(blockpos, chorusplantblock.getStateForPlacement(p_185601_0_, blockpos), 2);
         p_185601_0_.setBlock(blockpos.below(), chorusplantblock.getStateForPlacement(p_185601_0_, blockpos.below()), 2);
      }

      boolean flag = false;
      if (p_185601_5_ < 4) {
         int l = p_185601_2_.nextInt(4);
         if (p_185601_5_ == 0) {
            ++l;
         }

         for(int k = 0; k < l; ++k) {
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_185601_2_);
            BlockPos blockpos1 = p_185601_1_.above(i).relative(direction);
            if (Math.abs(blockpos1.getX() - p_185601_3_.getX()) < p_185601_4_ && Math.abs(blockpos1.getZ() - p_185601_3_.getZ()) < p_185601_4_ && p_185601_0_.isEmptyBlock(blockpos1) && p_185601_0_.isEmptyBlock(blockpos1.below()) && allNeighborsEmpty(p_185601_0_, blockpos1, direction.getOpposite())) {
               flag = true;
               p_185601_0_.setBlock(blockpos1, chorusplantblock.getStateForPlacement(p_185601_0_, blockpos1), 2);
               p_185601_0_.setBlock(blockpos1.relative(direction.getOpposite()), chorusplantblock.getStateForPlacement(p_185601_0_, blockpos1.relative(direction.getOpposite())), 2);
               growTreeRecursive(p_185601_0_, blockpos1, p_185601_2_, p_185601_3_, p_185601_4_, p_185601_5_ + 1);
            }
         }
      }

      if (!flag) {
         p_185601_0_.setBlock(p_185601_1_.above(i), Blocks.CHORUS_FLOWER.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      }

   }

   public void onProjectileHit(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, ProjectileEntity p_220066_4_) {
      if (p_220066_4_.getType().is(EntityTypeTags.IMPACT_PROJECTILES)) {
         BlockPos blockpos = p_220066_3_.getBlockPos();
         p_220066_1_.destroyBlock(blockpos, true, p_220066_4_);
      }

   }
}
