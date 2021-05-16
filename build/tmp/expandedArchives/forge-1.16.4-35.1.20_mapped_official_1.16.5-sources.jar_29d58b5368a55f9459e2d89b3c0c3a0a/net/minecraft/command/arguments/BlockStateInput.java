package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockStateInput implements Predicate<CachedBlockInfo> {
   private final BlockState state;
   private final Set<Property<?>> properties;
   @Nullable
   private final CompoundNBT tag;

   public BlockStateInput(BlockState p_i47967_1_, Set<Property<?>> p_i47967_2_, @Nullable CompoundNBT p_i47967_3_) {
      this.state = p_i47967_1_;
      this.properties = p_i47967_2_;
      this.tag = p_i47967_3_;
   }

   public BlockState getState() {
      return this.state;
   }

   public boolean test(CachedBlockInfo p_test_1_) {
      BlockState blockstate = p_test_1_.getState();
      if (!blockstate.is(this.state.getBlock())) {
         return false;
      } else {
         for(Property<?> property : this.properties) {
            if (blockstate.getValue(property) != this.state.getValue(property)) {
               return false;
            }
         }

         if (this.tag == null) {
            return true;
         } else {
            TileEntity tileentity = p_test_1_.getEntity();
            return tileentity != null && NBTUtil.compareNbt(this.tag, tileentity.save(new CompoundNBT()), true);
         }
      }
   }

   public boolean place(ServerWorld p_197230_1_, BlockPos p_197230_2_, int p_197230_3_) {
      BlockState blockstate = Block.updateFromNeighbourShapes(this.state, p_197230_1_, p_197230_2_);
      if (blockstate.isAir()) {
         blockstate = this.state;
      }

      if (!p_197230_1_.setBlock(p_197230_2_, blockstate, p_197230_3_)) {
         return false;
      } else {
         if (this.tag != null) {
            TileEntity tileentity = p_197230_1_.getBlockEntity(p_197230_2_);
            if (tileentity != null) {
               CompoundNBT compoundnbt = this.tag.copy();
               compoundnbt.putInt("x", p_197230_2_.getX());
               compoundnbt.putInt("y", p_197230_2_.getY());
               compoundnbt.putInt("z", p_197230_2_.getZ());
               tileentity.load(blockstate, compoundnbt);
            }
         }

         return true;
      }
   }
}
