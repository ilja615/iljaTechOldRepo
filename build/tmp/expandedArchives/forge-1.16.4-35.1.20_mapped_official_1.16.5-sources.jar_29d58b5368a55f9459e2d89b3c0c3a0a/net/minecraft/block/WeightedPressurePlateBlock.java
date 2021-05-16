package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   private final int maxWeight;

   public WeightedPressurePlateBlock(int p_i48295_1_, AbstractBlock.Properties p_i48295_2_) {
      super(p_i48295_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWER, Integer.valueOf(0)));
      this.maxWeight = p_i48295_1_;
   }

   protected int getSignalStrength(World p_180669_1_, BlockPos p_180669_2_) {
      int i = Math.min(p_180669_1_.getEntitiesOfClass(Entity.class, TOUCH_AABB.move(p_180669_2_)).size(), this.maxWeight);
      if (i > 0) {
         float f = (float)Math.min(this.maxWeight, i) / (float)this.maxWeight;
         return MathHelper.ceil(f * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playOnSound(IWorld p_185507_1_, BlockPos p_185507_2_) {
      p_185507_1_.playSound((PlayerEntity)null, p_185507_2_, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void playOffSound(IWorld p_185508_1_, BlockPos p_185508_2_) {
      p_185508_1_.playSound((PlayerEntity)null, p_185508_2_, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
   }

   protected int getSignalForState(BlockState p_176576_1_) {
      return p_176576_1_.getValue(POWER);
   }

   protected BlockState setSignalForState(BlockState p_176575_1_, int p_176575_2_) {
      return p_176575_1_.setValue(POWER, Integer.valueOf(p_176575_2_));
   }

   protected int getPressedTime() {
      return 10;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(POWER);
   }
}
