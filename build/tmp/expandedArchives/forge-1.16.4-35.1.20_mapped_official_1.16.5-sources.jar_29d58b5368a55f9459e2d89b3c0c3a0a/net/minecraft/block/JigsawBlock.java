package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;
import net.minecraft.world.gen.feature.template.Template;

public class JigsawBlock extends Block implements ITileEntityProvider {
   public static final EnumProperty<JigsawOrientation> ORIENTATION = BlockStateProperties.ORIENTATION;

   public JigsawBlock(AbstractBlock.Properties p_i49981_1_) {
      super(p_i49981_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, JigsawOrientation.NORTH_UP));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(ORIENTATION);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(ORIENTATION, p_185499_2_.rotation().rotate(p_185499_1_.getValue(ORIENTATION)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.setValue(ORIENTATION, p_185471_2_.rotation().rotate(p_185471_1_.getValue(ORIENTATION)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction direction = p_196258_1_.getClickedFace();
      Direction direction1;
      if (direction.getAxis() == Direction.Axis.Y) {
         direction1 = p_196258_1_.getHorizontalDirection().getOpposite();
      } else {
         direction1 = Direction.UP;
      }

      return this.defaultBlockState().setValue(ORIENTATION, JigsawOrientation.fromFrontAndTop(direction, direction1));
   }

   @Nullable
   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new JigsawTileEntity();
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
      if (tileentity instanceof JigsawTileEntity && p_225533_4_.canUseGameMasterBlocks()) {
         p_225533_4_.openJigsawBlock((JigsawTileEntity)tileentity);
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         return ActionResultType.PASS;
      }
   }

   public static boolean canAttach(Template.BlockInfo p_220171_0_, Template.BlockInfo p_220171_1_) {
      Direction direction = getFrontFacing(p_220171_0_.state);
      Direction direction1 = getFrontFacing(p_220171_1_.state);
      Direction direction2 = getTopFacing(p_220171_0_.state);
      Direction direction3 = getTopFacing(p_220171_1_.state);
      JigsawTileEntity.OrientationType jigsawtileentity$orientationtype = JigsawTileEntity.OrientationType.byName(p_220171_0_.nbt.getString("joint")).orElseGet(() -> {
         return direction.getAxis().isHorizontal() ? JigsawTileEntity.OrientationType.ALIGNED : JigsawTileEntity.OrientationType.ROLLABLE;
      });
      boolean flag = jigsawtileentity$orientationtype == JigsawTileEntity.OrientationType.ROLLABLE;
      return direction == direction1.getOpposite() && (flag || direction2 == direction3) && p_220171_0_.nbt.getString("target").equals(p_220171_1_.nbt.getString("name"));
   }

   public static Direction getFrontFacing(BlockState p_235508_0_) {
      return p_235508_0_.getValue(ORIENTATION).front();
   }

   public static Direction getTopFacing(BlockState p_235509_0_) {
      return p_235509_0_.getValue(ORIENTATION).top();
   }
}
