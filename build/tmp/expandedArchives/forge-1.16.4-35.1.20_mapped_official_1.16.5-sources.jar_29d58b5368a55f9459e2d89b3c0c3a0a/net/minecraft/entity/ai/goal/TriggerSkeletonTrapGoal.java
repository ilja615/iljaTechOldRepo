package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.server.ServerWorld;

public class TriggerSkeletonTrapGoal extends Goal {
   private final SkeletonHorseEntity horse;

   public TriggerSkeletonTrapGoal(SkeletonHorseEntity p_i46797_1_) {
      this.horse = p_i46797_1_;
   }

   public boolean canUse() {
      return this.horse.level.hasNearbyAlivePlayer(this.horse.getX(), this.horse.getY(), this.horse.getZ(), 10.0D);
   }

   public void tick() {
      ServerWorld serverworld = (ServerWorld)this.horse.level;
      DifficultyInstance difficultyinstance = serverworld.getCurrentDifficultyAt(this.horse.blockPosition());
      this.horse.setTrap(false);
      this.horse.setTamed(true);
      this.horse.setAge(0);
      LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(serverworld);
      lightningboltentity.moveTo(this.horse.getX(), this.horse.getY(), this.horse.getZ());
      lightningboltentity.setVisualOnly(true);
      serverworld.addFreshEntity(lightningboltentity);
      SkeletonEntity skeletonentity = this.createSkeleton(difficultyinstance, this.horse);
      skeletonentity.startRiding(this.horse);
      serverworld.addFreshEntityWithPassengers(skeletonentity);

      for(int i = 0; i < 3; ++i) {
         AbstractHorseEntity abstracthorseentity = this.createHorse(difficultyinstance);
         SkeletonEntity skeletonentity1 = this.createSkeleton(difficultyinstance, abstracthorseentity);
         skeletonentity1.startRiding(abstracthorseentity);
         abstracthorseentity.push(this.horse.getRandom().nextGaussian() * 0.5D, 0.0D, this.horse.getRandom().nextGaussian() * 0.5D);
         serverworld.addFreshEntityWithPassengers(abstracthorseentity);
      }

   }

   private AbstractHorseEntity createHorse(DifficultyInstance p_188515_1_) {
      SkeletonHorseEntity skeletonhorseentity = EntityType.SKELETON_HORSE.create(this.horse.level);
      skeletonhorseentity.finalizeSpawn((ServerWorld)this.horse.level, p_188515_1_, SpawnReason.TRIGGERED, (ILivingEntityData)null, (CompoundNBT)null);
      skeletonhorseentity.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
      skeletonhorseentity.invulnerableTime = 60;
      skeletonhorseentity.setPersistenceRequired();
      skeletonhorseentity.setTamed(true);
      skeletonhorseentity.setAge(0);
      return skeletonhorseentity;
   }

   private SkeletonEntity createSkeleton(DifficultyInstance p_188514_1_, AbstractHorseEntity p_188514_2_) {
      SkeletonEntity skeletonentity = EntityType.SKELETON.create(p_188514_2_.level);
      skeletonentity.finalizeSpawn((ServerWorld)p_188514_2_.level, p_188514_1_, SpawnReason.TRIGGERED, (ILivingEntityData)null, (CompoundNBT)null);
      skeletonentity.setPos(p_188514_2_.getX(), p_188514_2_.getY(), p_188514_2_.getZ());
      skeletonentity.invulnerableTime = 60;
      skeletonentity.setPersistenceRequired();
      if (skeletonentity.getItemBySlot(EquipmentSlotType.HEAD).isEmpty()) {
         skeletonentity.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Items.IRON_HELMET));
      }

      skeletonentity.setItemSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.enchantItem(skeletonentity.getRandom(), this.disenchant(skeletonentity.getMainHandItem()), (int)(5.0F + p_188514_1_.getSpecialMultiplier() * (float)skeletonentity.getRandom().nextInt(18)), false));
      skeletonentity.setItemSlot(EquipmentSlotType.HEAD, EnchantmentHelper.enchantItem(skeletonentity.getRandom(), this.disenchant(skeletonentity.getItemBySlot(EquipmentSlotType.HEAD)), (int)(5.0F + p_188514_1_.getSpecialMultiplier() * (float)skeletonentity.getRandom().nextInt(18)), false));
      return skeletonentity;
   }

   private ItemStack disenchant(ItemStack p_242327_1_) {
      p_242327_1_.removeTagKey("Enchantments");
      return p_242327_1_;
   }
}
