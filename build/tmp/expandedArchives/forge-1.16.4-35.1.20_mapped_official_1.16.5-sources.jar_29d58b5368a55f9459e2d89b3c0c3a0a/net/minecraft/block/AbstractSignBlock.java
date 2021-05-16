package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractSignBlock extends ContainerBlock implements IWaterLoggable {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   private final WoodType type;

   protected AbstractSignBlock(AbstractBlock.Properties p_i225763_1_, WoodType p_i225763_2_) {
      super(p_i225763_1_);
      this.type = p_i225763_2_;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getValue(WATERLOGGED)) {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new SignTileEntity();
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack itemstack = p_225533_4_.getItemInHand(p_225533_5_);
      boolean flag = itemstack.getItem() instanceof DyeItem && p_225533_4_.abilities.mayBuild;
      if (p_225533_2_.isClientSide) {
         return flag ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
      } else {
         TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
         if (tileentity instanceof SignTileEntity) {
            SignTileEntity signtileentity = (SignTileEntity)tileentity;
            if (flag) {
               boolean flag1 = signtileentity.setColor(((DyeItem)itemstack.getItem()).getDyeColor());
               if (flag1 && !p_225533_4_.isCreative()) {
                  itemstack.shrink(1);
               }
            }

            return signtileentity.executeClickCommands(p_225533_4_) ? ActionResultType.SUCCESS : ActionResultType.PASS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public WoodType type() {
      return this.type;
   }
}
