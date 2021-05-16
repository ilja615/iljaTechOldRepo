package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class JukeboxBlock extends ContainerBlock {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   public JukeboxBlock(AbstractBlock.Properties p_i48372_1_) {
      super(p_i48372_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, Boolean.valueOf(false)));
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      super.setPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      CompoundNBT compoundnbt = p_180633_5_.getOrCreateTag();
      if (compoundnbt.contains("BlockEntityTag")) {
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockEntityTag");
         if (compoundnbt1.contains("RecordItem")) {
            p_180633_1_.setBlock(p_180633_2_, p_180633_3_.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
         }
      }

   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_1_.getValue(HAS_RECORD)) {
         this.dropRecording(p_225533_2_, p_225533_3_);
         p_225533_1_ = p_225533_1_.setValue(HAS_RECORD, Boolean.valueOf(false));
         p_225533_2_.setBlock(p_225533_3_, p_225533_1_, 2);
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         return ActionResultType.PASS;
      }
   }

   public void setRecord(IWorld p_176431_1_, BlockPos p_176431_2_, BlockState p_176431_3_, ItemStack p_176431_4_) {
      TileEntity tileentity = p_176431_1_.getBlockEntity(p_176431_2_);
      if (tileentity instanceof JukeboxTileEntity) {
         ((JukeboxTileEntity)tileentity).setRecord(p_176431_4_.copy());
         p_176431_1_.setBlock(p_176431_2_, p_176431_3_.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   private void dropRecording(World p_203419_1_, BlockPos p_203419_2_) {
      if (!p_203419_1_.isClientSide) {
         TileEntity tileentity = p_203419_1_.getBlockEntity(p_203419_2_);
         if (tileentity instanceof JukeboxTileEntity) {
            JukeboxTileEntity jukeboxtileentity = (JukeboxTileEntity)tileentity;
            ItemStack itemstack = jukeboxtileentity.getRecord();
            if (!itemstack.isEmpty()) {
               p_203419_1_.levelEvent(1010, p_203419_2_, 0);
               jukeboxtileentity.clearContent();
               float f = 0.7F;
               double d0 = (double)(p_203419_1_.random.nextFloat() * 0.7F) + (double)0.15F;
               double d1 = (double)(p_203419_1_.random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
               double d2 = (double)(p_203419_1_.random.nextFloat() * 0.7F) + (double)0.15F;
               ItemStack itemstack1 = itemstack.copy();
               ItemEntity itementity = new ItemEntity(p_203419_1_, (double)p_203419_2_.getX() + d0, (double)p_203419_2_.getY() + d1, (double)p_203419_2_.getZ() + d2, itemstack1);
               itementity.setDefaultPickUpDelay();
               p_203419_1_.addFreshEntity(itementity);
            }
         }
      }
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         this.dropRecording(p_196243_2_, p_196243_3_);
         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new JukeboxTileEntity();
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      TileEntity tileentity = p_180641_2_.getBlockEntity(p_180641_3_);
      if (tileentity instanceof JukeboxTileEntity) {
         Item item = ((JukeboxTileEntity)tileentity).getRecord().getItem();
         if (item instanceof MusicDiscItem) {
            return ((MusicDiscItem)item).getAnalogOutput();
         }
      }

      return 0;
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HAS_RECORD);
   }
}
