package net.minecraft.entity.item;

import java.util.Map.Entry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExperienceOrbEntity extends Entity {
   public int tickCount;
   public int age;
   public int throwTime;
   private int health = 5;
   public int value;
   private PlayerEntity followingPlayer;
   private int followingTime;

   public ExperienceOrbEntity(World p_i1585_1_, double p_i1585_2_, double p_i1585_4_, double p_i1585_6_, int p_i1585_8_) {
      this(EntityType.EXPERIENCE_ORB, p_i1585_1_);
      this.setPos(p_i1585_2_, p_i1585_4_, p_i1585_6_);
      this.yRot = (float)(this.random.nextDouble() * 360.0D);
      this.setDeltaMovement((this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D);
      this.value = p_i1585_8_;
   }

   public ExperienceOrbEntity(EntityType<? extends ExperienceOrbEntity> p_i50382_1_, World p_i50382_2_) {
      super(p_i50382_1_, p_i50382_2_);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
   }

   public void tick() {
      super.tick();
      if (this.throwTime > 0) {
         --this.throwTime;
      }

      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      if (this.isEyeInFluid(FluidTags.WATER)) {
         this.setUnderwaterMovement();
      } else if (!this.isNoGravity()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
      }

      if (this.level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
         this.setDeltaMovement((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), (double)0.2F, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
         this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
      }

      if (!this.level.noCollision(this.getBoundingBox())) {
         this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
      }

      double d0 = 8.0D;
      if (this.followingTime < this.tickCount - 20 + this.getId() % 100) {
         if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 64.0D) {
            this.followingPlayer = this.level.getNearestPlayer(this, 8.0D);
         }

         this.followingTime = this.tickCount;
      }

      if (this.followingPlayer != null && this.followingPlayer.isSpectator()) {
         this.followingPlayer = null;
      }

      if (this.followingPlayer != null) {
         Vector3d vector3d = new Vector3d(this.followingPlayer.getX() - this.getX(), this.followingPlayer.getY() + (double)this.followingPlayer.getEyeHeight() / 2.0D - this.getY(), this.followingPlayer.getZ() - this.getZ());
         double d1 = vector3d.lengthSqr();
         if (d1 < 64.0D) {
            double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
            this.setDeltaMovement(this.getDeltaMovement().add(vector3d.normalize().scale(d2 * d2 * 0.1D)));
         }
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      float f = 0.98F;
      if (this.onGround) {
         BlockPos pos =new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
         f = this.level.getBlockState(pos).getSlipperiness(this.level, pos, this) * 0.98F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.98D, (double)f));
      if (this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
      }

      ++this.tickCount;
      ++this.age;
      if (this.age >= 6000) {
         this.remove();
      }

   }

   private void setUnderwaterMovement() {
      Vector3d vector3d = this.getDeltaMovement();
      this.setDeltaMovement(vector3d.x * (double)0.99F, Math.min(vector3d.y + (double)5.0E-4F, (double)0.06F), vector3d.z * (double)0.99F);
   }

   protected void doWaterSplashEffect() {
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.level.isClientSide || this.removed) return false; //Forge: Fixes MC-53850
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - p_70097_2_);
         if (this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("Health", (short)this.health);
      p_213281_1_.putShort("Age", (short)this.age);
      p_213281_1_.putShort("Value", (short)this.value);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.health = p_70037_1_.getShort("Health");
      this.age = p_70037_1_.getShort("Age");
      this.value = p_70037_1_.getShort("Value");
   }

   public void playerTouch(PlayerEntity p_70100_1_) {
      if (!this.level.isClientSide) {
         if (this.throwTime == 0 && p_70100_1_.takeXpDelay == 0) {
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp(p_70100_1_, this))) return;
            p_70100_1_.takeXpDelay = 2;
            p_70100_1_.take(this, 1);
            Entry<EquipmentSlotType, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, p_70100_1_, ItemStack::isDamaged);
            if (entry != null) {
               ItemStack itemstack = entry.getValue();
               if (!itemstack.isEmpty() && itemstack.isDamaged()) {
                  int i = Math.min((int)(this.value * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
                  this.value -= this.durabilityToXp(i);
                  itemstack.setDamageValue(itemstack.getDamageValue() - i);
               }
            }

            if (this.value > 0) {
               p_70100_1_.giveExperiencePoints(this.value);
            }

            this.remove();
         }

      }
   }

   private int durabilityToXp(int p_184515_1_) {
      return p_184515_1_ / 2;
   }

   private int xpToDurability(int p_184514_1_) {
      return p_184514_1_ * 2;
   }

   public int getValue() {
      return this.value;
   }

   @OnlyIn(Dist.CLIENT)
   public int getIcon() {
      if (this.value >= 2477) {
         return 10;
      } else if (this.value >= 1237) {
         return 9;
      } else if (this.value >= 617) {
         return 8;
      } else if (this.value >= 307) {
         return 7;
      } else if (this.value >= 149) {
         return 6;
      } else if (this.value >= 73) {
         return 5;
      } else if (this.value >= 37) {
         return 4;
      } else if (this.value >= 17) {
         return 3;
      } else if (this.value >= 7) {
         return 2;
      } else {
         return this.value >= 3 ? 1 : 0;
      }
   }

   public static int getExperienceValue(int p_70527_0_) {
      if (p_70527_0_ >= 2477) {
         return 2477;
      } else if (p_70527_0_ >= 1237) {
         return 1237;
      } else if (p_70527_0_ >= 617) {
         return 617;
      } else if (p_70527_0_ >= 307) {
         return 307;
      } else if (p_70527_0_ >= 149) {
         return 149;
      } else if (p_70527_0_ >= 73) {
         return 73;
      } else if (p_70527_0_ >= 37) {
         return 37;
      } else if (p_70527_0_ >= 17) {
         return 17;
      } else if (p_70527_0_ >= 7) {
         return 7;
      } else {
         return p_70527_0_ >= 3 ? 3 : 1;
      }
   }

   public boolean isAttackable() {
      return false;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnExperienceOrbPacket(this);
   }
}
