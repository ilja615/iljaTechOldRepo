package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
   private final AbstractSpawner spawner = new AbstractSpawner() {
      public void broadcastEvent(int p_98267_1_) {
         SpawnerMinecartEntity.this.level.broadcastEntityEvent(SpawnerMinecartEntity.this, (byte)p_98267_1_);
      }

      public World getLevel() {
         return SpawnerMinecartEntity.this.level;
      }

      public BlockPos getPos() {
         return SpawnerMinecartEntity.this.blockPosition();
      }

      @Override
      @javax.annotation.Nullable
      public net.minecraft.entity.Entity getSpawnerEntity() {
         return SpawnerMinecartEntity.this;
      }
   };

   public SpawnerMinecartEntity(EntityType<? extends SpawnerMinecartEntity> p_i50114_1_, World p_i50114_2_) {
      super(p_i50114_1_, p_i50114_2_);
   }

   public SpawnerMinecartEntity(World p_i46753_1_, double p_i46753_2_, double p_i46753_4_, double p_i46753_6_) {
      super(EntityType.SPAWNER_MINECART, p_i46753_1_, p_i46753_2_, p_i46753_4_, p_i46753_6_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.SPAWNER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.SPAWNER.defaultBlockState();
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.spawner.load(p_70037_1_);
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      this.spawner.save(p_213281_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      this.spawner.onEventTriggered(p_70103_1_);
   }

   public void tick() {
      super.tick();
      this.spawner.tick();
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }
}
