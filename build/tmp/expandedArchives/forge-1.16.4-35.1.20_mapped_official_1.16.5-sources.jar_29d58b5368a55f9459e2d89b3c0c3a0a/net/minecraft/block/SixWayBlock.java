package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SixWayBlock extends Block {
   private static final Direction[] DIRECTIONS = Direction.values();
   public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
   public static final BooleanProperty EAST = BlockStateProperties.EAST;
   public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
   public static final BooleanProperty WEST = BlockStateProperties.WEST;
   public static final BooleanProperty UP = BlockStateProperties.UP;
   public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
      p_203421_0_.put(Direction.NORTH, NORTH);
      p_203421_0_.put(Direction.EAST, EAST);
      p_203421_0_.put(Direction.SOUTH, SOUTH);
      p_203421_0_.put(Direction.WEST, WEST);
      p_203421_0_.put(Direction.UP, UP);
      p_203421_0_.put(Direction.DOWN, DOWN);
   });
   protected final VoxelShape[] shapeByIndex;

   public SixWayBlock(float p_i48355_1_, AbstractBlock.Properties p_i48355_2_) {
      super(p_i48355_2_);
      this.shapeByIndex = this.makeShapes(p_i48355_1_);
   }

   private VoxelShape[] makeShapes(float p_196487_1_) {
      float f = 0.5F - p_196487_1_;
      float f1 = 0.5F + p_196487_1_;
      VoxelShape voxelshape = Block.box((double)(f * 16.0F), (double)(f * 16.0F), (double)(f * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F));
      VoxelShape[] avoxelshape = new VoxelShape[DIRECTIONS.length];

      for(int i = 0; i < DIRECTIONS.length; ++i) {
         Direction direction = DIRECTIONS[i];
         avoxelshape[i] = VoxelShapes.box(0.5D + Math.min((double)(-p_196487_1_), (double)direction.getStepX() * 0.5D), 0.5D + Math.min((double)(-p_196487_1_), (double)direction.getStepY() * 0.5D), 0.5D + Math.min((double)(-p_196487_1_), (double)direction.getStepZ() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)direction.getStepX() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)direction.getStepY() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)direction.getStepZ() * 0.5D));
      }

      VoxelShape[] avoxelshape1 = new VoxelShape[64];

      for(int k = 0; k < 64; ++k) {
         VoxelShape voxelshape1 = voxelshape;

         for(int j = 0; j < DIRECTIONS.length; ++j) {
            if ((k & 1 << j) != 0) {
               voxelshape1 = VoxelShapes.or(voxelshape1, avoxelshape[j]);
            }
         }

         avoxelshape1[k] = voxelshape1;
      }

      return avoxelshape1;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return false;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.shapeByIndex[this.getAABBIndex(p_220053_1_)];
   }

   protected int getAABBIndex(BlockState p_196486_1_) {
      int i = 0;

      for(int j = 0; j < DIRECTIONS.length; ++j) {
         if (p_196486_1_.getValue(PROPERTY_BY_DIRECTION.get(DIRECTIONS[j]))) {
            i |= 1 << j;
         }
      }

      return i;
   }
}
