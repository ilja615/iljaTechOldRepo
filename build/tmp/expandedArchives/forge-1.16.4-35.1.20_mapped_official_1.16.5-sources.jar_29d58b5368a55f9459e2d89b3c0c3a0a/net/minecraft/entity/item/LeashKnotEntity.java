package net.minecraft.entity.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeashKnotEntity extends HangingEntity {
   public LeashKnotEntity(EntityType<? extends LeashKnotEntity> p_i50223_1_, World p_i50223_2_) {
      super(p_i50223_1_, p_i50223_2_);
   }

   public LeashKnotEntity(World p_i45851_1_, BlockPos p_i45851_2_) {
      super(EntityType.LEASH_KNOT, p_i45851_1_, p_i45851_2_);
      this.setPos((double)p_i45851_2_.getX() + 0.5D, (double)p_i45851_2_.getY() + 0.5D, (double)p_i45851_2_.getZ() + 0.5D);
      float f = 0.125F;
      float f1 = 0.1875F;
      float f2 = 0.25F;
      this.setBoundingBox(new AxisAlignedBB(this.getX() - 0.1875D, this.getY() - 0.25D + 0.125D, this.getZ() - 0.1875D, this.getX() + 0.1875D, this.getY() + 0.25D + 0.125D, this.getZ() + 0.1875D));
      this.forcedLoading = true;
   }

   public void setPos(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      super.setPos((double)MathHelper.floor(p_70107_1_) + 0.5D, (double)MathHelper.floor(p_70107_3_) + 0.5D, (double)MathHelper.floor(p_70107_5_) + 0.5D);
   }

   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);
      if (this.isAddedToWorld() && this.level instanceof net.minecraft.world.server.ServerWorld) ((net.minecraft.world.server.ServerWorld)this.level).updateChunkPos(this); // Forge - Process chunk registration after moving.
   }

   public void setDirection(Direction p_174859_1_) {
   }

   public int getWidth() {
      return 9;
   }

   public int getHeight() {
      return 9;
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return -0.0625F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      return p_70112_1_ < 1024.0D;
   }

   public void dropItem(@Nullable Entity p_110128_1_) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
   }

   public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (this.level.isClientSide) {
         return ActionResultType.SUCCESS;
      } else {
         boolean flag = false;
         double d0 = 7.0D;
         List<MobEntity> list = this.level.getEntitiesOfClass(MobEntity.class, new AxisAlignedBB(this.getX() - 7.0D, this.getY() - 7.0D, this.getZ() - 7.0D, this.getX() + 7.0D, this.getY() + 7.0D, this.getZ() + 7.0D));

         for(MobEntity mobentity : list) {
            if (mobentity.getLeashHolder() == p_184230_1_) {
               mobentity.setLeashedTo(this, true);
               flag = true;
            }
         }

         if (!flag) {
            this.remove();
            if (p_184230_1_.abilities.instabuild) {
               for(MobEntity mobentity1 : list) {
                  if (mobentity1.isLeashed() && mobentity1.getLeashHolder() == this) {
                     mobentity1.dropLeash(true, false);
                  }
               }
            }
         }

         return ActionResultType.CONSUME;
      }
   }

   public boolean survives() {
      return this.level.getBlockState(this.pos).getBlock().is(BlockTags.FENCES);
   }

   public static LeashKnotEntity getOrCreateKnot(World p_213855_0_, BlockPos p_213855_1_) {
      int i = p_213855_1_.getX();
      int j = p_213855_1_.getY();
      int k = p_213855_1_.getZ();

      for(LeashKnotEntity leashknotentity : p_213855_0_.getEntitiesOfClass(LeashKnotEntity.class, new AxisAlignedBB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
         if (leashknotentity.getPos().equals(p_213855_1_)) {
            return leashknotentity;
         }
      }

      LeashKnotEntity leashknotentity1 = new LeashKnotEntity(p_213855_0_, p_213855_1_);
      p_213855_0_.addFreshEntity(leashknotentity1);
      leashknotentity1.playPlacementSound();
      return leashknotentity1;
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this, this.getType(), 0, this.getPos());
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getRopeHoldPosition(float p_241843_1_) {
      return this.getPosition(p_241843_1_).add(0.0D, 0.2D, 0.0D);
   }
}
