package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class SpectralArrowEntity extends AbstractArrowEntity {
   private int duration = 200;

   public SpectralArrowEntity(EntityType<? extends SpectralArrowEntity> p_i50158_1_, World p_i50158_2_) {
      super(p_i50158_1_, p_i50158_2_);
   }

   public SpectralArrowEntity(World p_i46768_1_, LivingEntity p_i46768_2_) {
      super(EntityType.SPECTRAL_ARROW, p_i46768_2_, p_i46768_1_);
   }

   public SpectralArrowEntity(World p_i46769_1_, double p_i46769_2_, double p_i46769_4_, double p_i46769_6_) {
      super(EntityType.SPECTRAL_ARROW, p_i46769_2_, p_i46769_4_, p_i46769_6_, p_i46769_1_);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide && !this.inGround) {
         this.level.addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected ItemStack getPickupItem() {
      return new ItemStack(Items.SPECTRAL_ARROW);
   }

   protected void doPostHurtEffects(LivingEntity p_184548_1_) {
      super.doPostHurtEffects(p_184548_1_);
      EffectInstance effectinstance = new EffectInstance(Effects.GLOWING, this.duration, 0);
      p_184548_1_.addEffect(effectinstance);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("Duration")) {
         this.duration = p_70037_1_.getInt("Duration");
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Duration", this.duration);
   }
}
