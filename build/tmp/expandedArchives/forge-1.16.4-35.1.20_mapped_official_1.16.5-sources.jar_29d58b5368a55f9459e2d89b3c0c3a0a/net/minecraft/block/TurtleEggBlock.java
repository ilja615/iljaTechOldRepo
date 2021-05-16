package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TurtleEggBlock extends Block {
   private static final VoxelShape ONE_EGG_AABB = Block.box(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

   public TurtleEggBlock(AbstractBlock.Properties p_i48778_1_) {
      super(p_i48778_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, Integer.valueOf(0)).setValue(EGGS, Integer.valueOf(1)));
   }

   public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
      this.destroyEgg(p_176199_1_, p_176199_2_, p_176199_3_, 100);
      super.stepOn(p_176199_1_, p_176199_2_, p_176199_3_);
   }

   public void fallOn(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      if (!(p_180658_3_ instanceof ZombieEntity)) {
         this.destroyEgg(p_180658_1_, p_180658_2_, p_180658_3_, 3);
      }

      super.fallOn(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_);
   }

   private void destroyEgg(World p_203167_1_, BlockPos p_203167_2_, Entity p_203167_3_, int p_203167_4_) {
      if (this.canDestroyEgg(p_203167_1_, p_203167_3_)) {
         if (!p_203167_1_.isClientSide && p_203167_1_.random.nextInt(p_203167_4_) == 0) {
            BlockState blockstate = p_203167_1_.getBlockState(p_203167_2_);
            if (blockstate.is(Blocks.TURTLE_EGG)) {
               this.decreaseEggs(p_203167_1_, p_203167_2_, blockstate);
            }
         }

      }
   }

   private void decreaseEggs(World p_203166_1_, BlockPos p_203166_2_, BlockState p_203166_3_) {
      p_203166_1_.playSound((PlayerEntity)null, p_203166_2_, SoundEvents.TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_203166_1_.random.nextFloat() * 0.2F);
      int i = p_203166_3_.getValue(EGGS);
      if (i <= 1) {
         p_203166_1_.destroyBlock(p_203166_2_, false);
      } else {
         p_203166_1_.setBlock(p_203166_2_, p_203166_3_.setValue(EGGS, Integer.valueOf(i - 1)), 2);
         p_203166_1_.levelEvent(2001, p_203166_2_, Block.getId(p_203166_3_));
      }

   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (this.shouldUpdateHatchLevel(p_225542_2_) && onSand(p_225542_2_, p_225542_3_)) {
         int i = p_225542_1_.getValue(HATCH);
         if (i < 2) {
            p_225542_2_.playSound((PlayerEntity)null, p_225542_3_, SoundEvents.TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_225542_4_.nextFloat() * 0.2F);
            p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(HATCH, Integer.valueOf(i + 1)), 2);
         } else {
            p_225542_2_.playSound((PlayerEntity)null, p_225542_3_, SoundEvents.TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + p_225542_4_.nextFloat() * 0.2F);
            p_225542_2_.removeBlock(p_225542_3_, false);

            for(int j = 0; j < p_225542_1_.getValue(EGGS); ++j) {
               p_225542_2_.levelEvent(2001, p_225542_3_, Block.getId(p_225542_1_));
               TurtleEntity turtleentity = EntityType.TURTLE.create(p_225542_2_);
               turtleentity.setAge(-24000);
               turtleentity.setHomePos(p_225542_3_);
               turtleentity.moveTo((double)p_225542_3_.getX() + 0.3D + (double)j * 0.2D, (double)p_225542_3_.getY(), (double)p_225542_3_.getZ() + 0.3D, 0.0F, 0.0F);
               p_225542_2_.addFreshEntity(turtleentity);
            }
         }
      }

   }

   public static boolean onSand(IBlockReader p_203168_0_, BlockPos p_203168_1_) {
      return isSand(p_203168_0_, p_203168_1_.below());
   }

   public static boolean isSand(IBlockReader p_241473_0_, BlockPos p_241473_1_) {
      return p_241473_0_.getBlockState(p_241473_1_).is(BlockTags.SAND);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (onSand(p_220082_2_, p_220082_3_) && !p_220082_2_.isClientSide) {
         p_220082_2_.levelEvent(2005, p_220082_3_, 0);
      }

   }

   private boolean shouldUpdateHatchLevel(World p_203169_1_) {
      float f = p_203169_1_.getTimeOfDay(1.0F);
      if ((double)f < 0.69D && (double)f > 0.65D) {
         return true;
      } else {
         return p_203169_1_.random.nextInt(500) == 0;
      }
   }

   public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.playerDestroy(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      this.decreaseEggs(p_180657_1_, p_180657_3_, p_180657_4_);
   }

   public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_2_.getItemInHand().getItem() == this.asItem() && p_196253_1_.getValue(EGGS) < 4 ? true : super.canBeReplaced(p_196253_1_, p_196253_2_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos());
      return blockstate.is(this) ? blockstate.setValue(EGGS, Integer.valueOf(Math.min(4, blockstate.getValue(EGGS) + 1))) : super.getStateForPlacement(p_196258_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return p_220053_1_.getValue(EGGS) > 1 ? MULTIPLE_EGGS_AABB : ONE_EGG_AABB;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HATCH, EGGS);
   }

   private boolean canDestroyEgg(World p_212570_1_, Entity p_212570_2_) {
      if (!(p_212570_2_ instanceof TurtleEntity) && !(p_212570_2_ instanceof BatEntity)) {
         if (!(p_212570_2_ instanceof LivingEntity)) {
            return false;
         } else {
            return p_212570_2_ instanceof PlayerEntity || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(p_212570_1_, p_212570_2_);
         }
      } else {
         return false;
      }
   }
}
