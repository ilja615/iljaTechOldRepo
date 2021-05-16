package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;

public class EnderDragonPartEntity extends Entity {
   public final EnderDragonEntity parentMob;
   public final String name;
   private final EntitySize size;

   public EnderDragonPartEntity(EnderDragonEntity p_i50232_1_, String p_i50232_2_, float p_i50232_3_, float p_i50232_4_) {
      super(p_i50232_1_.getType(), p_i50232_1_.level);
      this.size = EntitySize.scalable(p_i50232_3_, p_i50232_4_);
      this.refreshDimensions();
      this.parentMob = p_i50232_1_;
      this.name = p_i50232_2_;
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : this.parentMob.hurt(this, p_70097_1_, p_70097_2_);
   }

   public boolean is(Entity p_70028_1_) {
      return this == p_70028_1_ || this.parentMob == p_70028_1_;
   }

   public IPacket<?> getAddEntityPacket() {
      throw new UnsupportedOperationException();
   }

   public EntitySize getDimensions(Pose p_213305_1_) {
      return this.size;
   }
}
