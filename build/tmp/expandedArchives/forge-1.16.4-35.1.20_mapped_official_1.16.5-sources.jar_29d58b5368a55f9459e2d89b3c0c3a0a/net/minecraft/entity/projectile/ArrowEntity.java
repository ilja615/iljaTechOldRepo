package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArrowEntity extends AbstractArrowEntity {
   private static final DataParameter<Integer> ID_EFFECT_COLOR = EntityDataManager.defineId(ArrowEntity.class, DataSerializers.INT);
   private Potion potion = Potions.EMPTY;
   private final Set<EffectInstance> effects = Sets.newHashSet();
   private boolean fixedColor;

   public ArrowEntity(EntityType<? extends ArrowEntity> p_i50172_1_, World p_i50172_2_) {
      super(p_i50172_1_, p_i50172_2_);
   }

   public ArrowEntity(World p_i46757_1_, double p_i46757_2_, double p_i46757_4_, double p_i46757_6_) {
      super(EntityType.ARROW, p_i46757_2_, p_i46757_4_, p_i46757_6_, p_i46757_1_);
   }

   public ArrowEntity(World p_i46758_1_, LivingEntity p_i46758_2_) {
      super(EntityType.ARROW, p_i46758_2_, p_i46758_1_);
   }

   public void setEffectsFromItem(ItemStack p_184555_1_) {
      if (p_184555_1_.getItem() == Items.TIPPED_ARROW) {
         this.potion = PotionUtils.getPotion(p_184555_1_);
         Collection<EffectInstance> collection = PotionUtils.getCustomEffects(p_184555_1_);
         if (!collection.isEmpty()) {
            for(EffectInstance effectinstance : collection) {
               this.effects.add(new EffectInstance(effectinstance));
            }
         }

         int i = getCustomColor(p_184555_1_);
         if (i == -1) {
            this.updateColor();
         } else {
            this.setFixedColor(i);
         }
      } else if (p_184555_1_.getItem() == Items.ARROW) {
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, -1);
      }

   }

   public static int getCustomColor(ItemStack p_191508_0_) {
      CompoundNBT compoundnbt = p_191508_0_.getTag();
      return compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99) ? compoundnbt.getInt("CustomPotionColor") : -1;
   }

   private void updateColor() {
      this.fixedColor = false;
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.entityData.set(ID_EFFECT_COLOR, -1);
      } else {
         this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(EffectInstance p_184558_1_) {
      this.effects.add(p_184558_1_);
      this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_EFFECT_COLOR, -1);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         if (this.inGround) {
            if (this.inGroundTime % 5 == 0) {
               this.makeParticle(1);
            }
         } else {
            this.makeParticle(2);
         }
      } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
         this.level.broadcastEntityEvent(this, (byte)0);
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, -1);
      }

   }

   private void makeParticle(int p_184556_1_) {
      int i = this.getColor();
      if (i != -1 && p_184556_1_ > 0) {
         double d0 = (double)(i >> 16 & 255) / 255.0D;
         double d1 = (double)(i >> 8 & 255) / 255.0D;
         double d2 = (double)(i >> 0 & 255) / 255.0D;

         for(int j = 0; j < p_184556_1_; ++j) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
         }

      }
   }

   public int getColor() {
      return this.entityData.get(ID_EFFECT_COLOR);
   }

   private void setFixedColor(int p_191507_1_) {
      this.fixedColor = true;
      this.entityData.set(ID_EFFECT_COLOR, p_191507_1_);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (this.potion != Potions.EMPTY && this.potion != null) {
         p_213281_1_.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (this.fixedColor) {
         p_213281_1_.putInt("Color", this.getColor());
      }

      if (!this.effects.isEmpty()) {
         ListNBT listnbt = new ListNBT();

         for(EffectInstance effectinstance : this.effects) {
            listnbt.add(effectinstance.save(new CompoundNBT()));
         }

         p_213281_1_.put("CustomPotionEffects", listnbt);
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("Potion", 8)) {
         this.potion = PotionUtils.getPotion(p_70037_1_);
      }

      for(EffectInstance effectinstance : PotionUtils.getCustomEffects(p_70037_1_)) {
         this.addEffect(effectinstance);
      }

      if (p_70037_1_.contains("Color", 99)) {
         this.setFixedColor(p_70037_1_.getInt("Color"));
      } else {
         this.updateColor();
      }

   }

   protected void doPostHurtEffects(LivingEntity p_184548_1_) {
      super.doPostHurtEffects(p_184548_1_);

      for(EffectInstance effectinstance : this.potion.getEffects()) {
         p_184548_1_.addEffect(new EffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
      }

      if (!this.effects.isEmpty()) {
         for(EffectInstance effectinstance1 : this.effects) {
            p_184548_1_.addEffect(effectinstance1);
         }
      }

   }

   protected ItemStack getPickupItem() {
      if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
         return new ItemStack(Items.ARROW);
      } else {
         ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
         PotionUtils.setPotion(itemstack, this.potion);
         PotionUtils.setCustomEffects(itemstack, this.effects);
         if (this.fixedColor) {
            itemstack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
         }

         return itemstack;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 0) {
         int i = this.getColor();
         if (i != -1) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for(int j = 0; j < 20; ++j) {
               this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
            }
         }
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }
}
