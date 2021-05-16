package net.minecraft.util;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CachedBlockInfo {
   private final IWorldReader level;
   private final BlockPos pos;
   private final boolean loadChunks;
   private BlockState state;
   private TileEntity entity;
   private boolean cachedEntity;

   public CachedBlockInfo(IWorldReader p_i48968_1_, BlockPos p_i48968_2_, boolean p_i48968_3_) {
      this.level = p_i48968_1_;
      this.pos = p_i48968_2_.immutable();
      this.loadChunks = p_i48968_3_;
   }

   public BlockState getState() {
      if (this.state == null && (this.loadChunks || this.level.hasChunkAt(this.pos))) {
         this.state = this.level.getBlockState(this.pos);
      }

      return this.state;
   }

   @Nullable
   public TileEntity getEntity() {
      if (this.entity == null && !this.cachedEntity) {
         this.entity = this.level.getBlockEntity(this.pos);
         this.cachedEntity = true;
      }

      return this.entity;
   }

   public IWorldReader getLevel() {
      return this.level;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static Predicate<CachedBlockInfo> hasState(Predicate<BlockState> p_177510_0_) {
      return (p_201002_1_) -> {
         return p_201002_1_ != null && p_177510_0_.test(p_201002_1_.getState());
      };
   }
}
