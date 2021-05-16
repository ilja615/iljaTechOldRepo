package net.minecraft.entity.item;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity extends Entity {
   protected static final Predicate<Entity> HANGING_ENTITY = (p_210144_0_) -> {
      return p_210144_0_ instanceof HangingEntity;
   };
   private int checkInterval;
   protected BlockPos pos;
   protected Direction direction = Direction.SOUTH;

   protected HangingEntity(EntityType<? extends HangingEntity> p_i48561_1_, World p_i48561_2_) {
      super(p_i48561_1_, p_i48561_2_);
   }

   protected HangingEntity(EntityType<? extends HangingEntity> p_i48562_1_, World p_i48562_2_, BlockPos p_i48562_3_) {
      this(p_i48562_1_, p_i48562_2_);
      this.pos = p_i48562_3_;
   }

   protected void defineSynchedData() {
   }

   protected void setDirection(Direction p_174859_1_) {
      Validate.notNull(p_174859_1_);
      Validate.isTrue(p_174859_1_.getAxis().isHorizontal());
      this.direction = p_174859_1_;
      this.yRot = (float)(this.direction.get2DDataValue() * 90);
      this.yRotO = this.yRot;
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double d0 = (double)this.pos.getX() + 0.5D;
         double d1 = (double)this.pos.getY() + 0.5D;
         double d2 = (double)this.pos.getZ() + 0.5D;
         double d3 = 0.46875D;
         double d4 = this.offs(this.getWidth());
         double d5 = this.offs(this.getHeight());
         d0 = d0 - (double)this.direction.getStepX() * 0.46875D;
         d2 = d2 - (double)this.direction.getStepZ() * 0.46875D;
         d1 = d1 + d5;
         Direction direction = this.direction.getCounterClockWise();
         d0 = d0 + d4 * (double)direction.getStepX();
         d2 = d2 + d4 * (double)direction.getStepZ();
         this.setPosRaw(d0, d1, d2);
         double d6 = (double)this.getWidth();
         double d7 = (double)this.getHeight();
         double d8 = (double)this.getWidth();
         if (this.direction.getAxis() == Direction.Axis.Z) {
            d8 = 1.0D;
         } else {
            d6 = 1.0D;
         }

         d6 = d6 / 32.0D;
         d7 = d7 / 32.0D;
         d8 = d8 / 32.0D;
         this.setBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
      }
   }

   private double offs(int p_190202_1_) {
      return p_190202_1_ % 32 == 0 ? 0.5D : 0.0D;
   }

   public void tick() {
      if (!this.level.isClientSide) {
         if (this.getY() < -64.0D) {
            this.outOfWorld();
         }

         if (this.checkInterval++ == 100) {
            this.checkInterval = 0;
            if (!this.removed && !this.survives()) {
               this.remove();
               this.dropItem((Entity)null);
            }
         }
      }

   }

   public boolean survives() {
      if (!this.level.noCollision(this)) {
         return false;
      } else {
         int i = Math.max(1, this.getWidth() / 16);
         int j = Math.max(1, this.getHeight() / 16);
         BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
         Direction direction = this.direction.getCounterClockWise();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = 0; k < i; ++k) {
            for(int l = 0; l < j; ++l) {
               int i1 = (i - 1) / -2;
               int j1 = (j - 1) / -2;
               blockpos$mutable.set(blockpos).move(direction, k + i1).move(Direction.UP, l + j1);
               BlockState blockstate = this.level.getBlockState(blockpos$mutable);
               if (net.minecraft.block.Block.canSupportCenter(this.level, blockpos$mutable, this.direction))
                  continue;
               if (!blockstate.getMaterial().isSolid() && !RedstoneDiodeBlock.isDiode(blockstate)) {
                  return false;
               }
            }
         }

         return this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
      }
   }

   public boolean isPickable() {
      return true;
   }

   public boolean skipAttackInteraction(Entity p_85031_1_) {
      if (p_85031_1_ instanceof PlayerEntity) {
         PlayerEntity playerentity = (PlayerEntity)p_85031_1_;
         return !this.level.mayInteract(playerentity, this.pos) ? true : this.hurt(DamageSource.playerAttack(playerentity), 0.0F);
      } else {
         return false;
      }
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         if (!this.removed && !this.level.isClientSide) {
            this.remove();
            this.markHurt();
            this.dropItem(p_70097_1_.getEntity());
         }

         return true;
      }
   }

   public void move(MoverType p_213315_1_, Vector3d p_213315_2_) {
      if (!this.level.isClientSide && !this.removed && p_213315_2_.lengthSqr() > 0.0D) {
         this.remove();
         this.dropItem((Entity)null);
      }

   }

   public void push(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
      if (!this.level.isClientSide && !this.removed && p_70024_1_ * p_70024_1_ + p_70024_3_ * p_70024_3_ + p_70024_5_ * p_70024_5_ > 0.0D) {
         this.remove();
         this.dropItem((Entity)null);
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      BlockPos blockpos = this.getPos();
      p_213281_1_.putInt("TileX", blockpos.getX());
      p_213281_1_.putInt("TileY", blockpos.getY());
      p_213281_1_.putInt("TileZ", blockpos.getZ());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.pos = new BlockPos(p_70037_1_.getInt("TileX"), p_70037_1_.getInt("TileY"), p_70037_1_.getInt("TileZ"));
   }

   public abstract int getWidth();

   public abstract int getHeight();

   public abstract void dropItem(@Nullable Entity p_110128_1_);

   public abstract void playPlacementSound();

   public ItemEntity spawnAtLocation(ItemStack p_70099_1_, float p_70099_2_) {
      ItemEntity itementity = new ItemEntity(this.level, this.getX() + (double)((float)this.direction.getStepX() * 0.15F), this.getY() + (double)p_70099_2_, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15F), p_70099_1_);
      itementity.setDefaultPickUpDelay();
      this.level.addFreshEntity(itementity);
      return itementity;
   }

   protected boolean repositionEntityAfterLoad() {
      return false;
   }

   public void setPos(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      this.pos = new BlockPos(p_70107_1_, p_70107_3_, p_70107_5_);
      this.recalculateBoundingBox();
      this.hasImpulse = true;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public float rotate(Rotation p_184229_1_) {
      if (this.direction.getAxis() != Direction.Axis.Y) {
         switch(p_184229_1_) {
         case CLOCKWISE_180:
            this.direction = this.direction.getOpposite();
            break;
         case COUNTERCLOCKWISE_90:
            this.direction = this.direction.getCounterClockWise();
            break;
         case CLOCKWISE_90:
            this.direction = this.direction.getClockWise();
         }
      }

      float f = MathHelper.wrapDegrees(this.yRot);
      switch(p_184229_1_) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 90.0F;
      case CLOCKWISE_90:
         return f + 270.0F;
      default:
         return f;
      }
   }

   public float mirror(Mirror p_184217_1_) {
      return this.rotate(p_184217_1_.getRotation(this.direction));
   }

   public void thunderHit(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
   }

   public void refreshDimensions() {
   }
}
