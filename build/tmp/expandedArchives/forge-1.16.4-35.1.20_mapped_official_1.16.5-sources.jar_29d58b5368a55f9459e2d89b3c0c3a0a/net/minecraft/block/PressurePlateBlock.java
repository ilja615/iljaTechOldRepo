package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   public PressurePlateBlock(PressurePlateBlock.Sensitivity p_i48348_1_, AbstractBlock.Properties p_i48348_2_) {
      super(p_i48348_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
      this.sensitivity = p_i48348_1_;
   }

   protected int getSignalForState(BlockState p_176576_1_) {
      return p_176576_1_.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(BlockState p_176575_1_, int p_176575_2_) {
      return p_176575_1_.setValue(POWERED, Boolean.valueOf(p_176575_2_ > 0));
   }

   protected void playOnSound(IWorld p_185507_1_, BlockPos p_185507_2_) {
      if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
         p_185507_1_.playSound((PlayerEntity)null, p_185507_2_, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
      } else {
         p_185507_1_.playSound((PlayerEntity)null, p_185507_2_, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
      }

   }

   protected void playOffSound(IWorld p_185508_1_, BlockPos p_185508_2_) {
      if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
         p_185508_1_.playSound((PlayerEntity)null, p_185508_2_, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
      } else {
         p_185508_1_.playSound((PlayerEntity)null, p_185508_2_, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
      }

   }

   protected int getSignalStrength(World p_180669_1_, BlockPos p_180669_2_) {
      AxisAlignedBB axisalignedbb = TOUCH_AABB.move(p_180669_2_);
      List<? extends Entity> list;
      switch(this.sensitivity) {
      case EVERYTHING:
         list = p_180669_1_.getEntities((Entity)null, axisalignedbb);
         break;
      case MOBS:
         list = p_180669_1_.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
         break;
      default:
         return 0;
      }

      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.isIgnoringBlockTriggers()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(POWERED);
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
