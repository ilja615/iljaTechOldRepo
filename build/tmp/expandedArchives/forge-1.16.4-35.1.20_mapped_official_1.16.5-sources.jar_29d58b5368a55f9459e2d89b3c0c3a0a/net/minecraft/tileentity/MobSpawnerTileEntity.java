package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;

public class MobSpawnerTileEntity extends TileEntity implements ITickableTileEntity {
   private final AbstractSpawner spawner = new AbstractSpawner() {
      public void broadcastEvent(int p_98267_1_) {
         MobSpawnerTileEntity.this.level.blockEvent(MobSpawnerTileEntity.this.worldPosition, Blocks.SPAWNER, p_98267_1_, 0);
      }

      public World getLevel() {
         return MobSpawnerTileEntity.this.level;
      }

      public BlockPos getPos() {
         return MobSpawnerTileEntity.this.worldPosition;
      }

      public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {
         super.setNextSpawnData(p_184993_1_);
         if (this.getLevel() != null) {
            BlockState blockstate = this.getLevel().getBlockState(this.getPos());
            this.getLevel().sendBlockUpdated(MobSpawnerTileEntity.this.worldPosition, blockstate, blockstate, 4);
         }

      }
   };

   public MobSpawnerTileEntity() {
      super(TileEntityType.MOB_SPAWNER);
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.spawner.load(p_230337_2_);
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      this.spawner.save(p_189515_1_);
      return p_189515_1_;
   }

   public void tick() {
      this.spawner.tick();
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 1, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      CompoundNBT compoundnbt = this.save(new CompoundNBT());
      compoundnbt.remove("SpawnPotentials");
      return compoundnbt;
   }

   public boolean triggerEvent(int p_145842_1_, int p_145842_2_) {
      return this.spawner.onEventTriggered(p_145842_1_) ? true : super.triggerEvent(p_145842_1_, p_145842_2_);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public AbstractSpawner getSpawner() {
      return this.spawner;
   }
}
