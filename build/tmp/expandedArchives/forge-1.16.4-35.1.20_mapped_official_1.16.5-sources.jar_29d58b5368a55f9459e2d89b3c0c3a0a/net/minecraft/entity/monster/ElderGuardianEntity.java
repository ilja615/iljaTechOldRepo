package net.minecraft.entity.monster;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ElderGuardianEntity extends GuardianEntity {
   public static final float ELDER_SIZE_SCALE = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();

   public ElderGuardianEntity(EntityType<? extends ElderGuardianEntity> p_i50211_1_, World p_i50211_2_) {
      super(p_i50211_1_, p_i50211_2_);
      this.setPersistenceRequired();
      if (this.randomStrollGoal != null) {
         this.randomStrollGoal.setInterval(400);
      }

   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return GuardianEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.MAX_HEALTH, 80.0D);
   }

   public int getAttackDuration() {
      return 60;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_AMBIENT : SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_HURT : SoundEvents.ELDER_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_DEATH : SoundEvents.ELDER_GUARDIAN_DEATH_LAND;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ELDER_GUARDIAN_FLOP;
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      int i = 1200;
      if ((this.tickCount + this.getId()) % 1200 == 0) {
         Effect effect = Effects.DIG_SLOWDOWN;
         List<ServerPlayerEntity> list = ((ServerWorld)this.level).getPlayers((p_210138_1_) -> {
            return this.distanceToSqr(p_210138_1_) < 2500.0D && p_210138_1_.gameMode.isSurvival();
         });
         int j = 2;
         int k = 6000;
         int l = 1200;

         for(ServerPlayerEntity serverplayerentity : list) {
            if (!serverplayerentity.hasEffect(effect) || serverplayerentity.getEffect(effect).getAmplifier() < 2 || serverplayerentity.getEffect(effect).getDuration() < 1200) {
               serverplayerentity.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.GUARDIAN_ELDER_EFFECT, this.isSilent() ? 0.0F : 1.0F));
               serverplayerentity.addEffect(new EffectInstance(effect, 6000, 2));
            }
         }
      }

      if (!this.hasRestriction()) {
         this.restrictTo(this.blockPosition(), 16);
      }

   }
}
