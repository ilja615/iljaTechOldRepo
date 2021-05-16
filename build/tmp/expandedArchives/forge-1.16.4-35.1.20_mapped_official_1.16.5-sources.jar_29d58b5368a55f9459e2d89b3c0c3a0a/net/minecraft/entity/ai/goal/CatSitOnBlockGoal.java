package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatSitOnBlockGoal extends MoveToBlockGoal {
   private final CatEntity cat;

   public CatSitOnBlockGoal(CatEntity p_i50330_1_, double p_i50330_2_) {
      super(p_i50330_1_, p_i50330_2_, 8);
      this.cat = p_i50330_1_;
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isOrderedToSit() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   public void stop() {
      super.stop();
      this.cat.setInSittingPose(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(this.isReachedTarget());
   }

   protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
      if (!p_179488_1_.isEmptyBlock(p_179488_2_.above())) {
         return false;
      } else {
         BlockState blockstate = p_179488_1_.getBlockState(p_179488_2_);
         if (blockstate.is(Blocks.CHEST)) {
            return ChestTileEntity.getOpenCount(p_179488_1_, p_179488_2_) < 1;
         } else {
            return blockstate.is(Blocks.FURNACE) && blockstate.getValue(FurnaceBlock.LIT) ? true : blockstate.is(BlockTags.BEDS, (p_234025_0_) -> {
               return p_234025_0_.<BedPart>getOptionalValue(BedBlock.PART).map((p_234026_0_) -> {
                  return p_234026_0_ != BedPart.HEAD;
               }).orElse(true);
            });
         }
      }
   }
}
