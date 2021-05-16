package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloudEntity extends Entity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DataParameter<Float> DATA_RADIUS = EntityDataManager.defineId(AreaEffectCloudEntity.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(AreaEffectCloudEntity.class, DataSerializers.INT);
   private static final DataParameter<Boolean> DATA_WAITING = EntityDataManager.defineId(AreaEffectCloudEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<IParticleData> DATA_PARTICLE = EntityDataManager.defineId(AreaEffectCloudEntity.class, DataSerializers.PARTICLE);
   private Potion potion = Potions.EMPTY;
   private final List<EffectInstance> effects = Lists.newArrayList();
   private final Map<Entity, Integer> victims = Maps.newHashMap();
   private int duration = 600;
   private int waitTime = 20;
   private int reapplicationDelay = 20;
   private boolean fixedColor;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   private LivingEntity owner;
   private UUID ownerUUID;

   public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> p_i50389_1_, World p_i50389_2_) {
      super(p_i50389_1_, p_i50389_2_);
      this.noPhysics = true;
      this.setRadius(3.0F);
   }

   public AreaEffectCloudEntity(World p_i46810_1_, double p_i46810_2_, double p_i46810_4_, double p_i46810_6_) {
      this(EntityType.AREA_EFFECT_CLOUD, p_i46810_1_);
      this.setPos(p_i46810_2_, p_i46810_4_, p_i46810_6_);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_COLOR, 0);
      this.getEntityData().define(DATA_RADIUS, 0.5F);
      this.getEntityData().define(DATA_WAITING, false);
      this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
   }

   public void setRadius(float p_184483_1_) {
      if (!this.level.isClientSide) {
         this.getEntityData().set(DATA_RADIUS, p_184483_1_);
      }

   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   public float getRadius() {
      return this.getEntityData().get(DATA_RADIUS);
   }

   public void setPotion(Potion p_184484_1_) {
      this.potion = p_184484_1_;
      if (!this.fixedColor) {
         this.updateColor();
      }

   }

   private void updateColor() {
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.getEntityData().set(DATA_COLOR, 0);
      } else {
         this.getEntityData().set(DATA_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(EffectInstance p_184496_1_) {
      this.effects.add(p_184496_1_);
      if (!this.fixedColor) {
         this.updateColor();
      }

   }

   public int getColor() {
      return this.getEntityData().get(DATA_COLOR);
   }

   public void setFixedColor(int p_184482_1_) {
      this.fixedColor = true;
      this.getEntityData().set(DATA_COLOR, p_184482_1_);
   }

   public IParticleData getParticle() {
      return this.getEntityData().get(DATA_PARTICLE);
   }

   public void setParticle(IParticleData p_195059_1_) {
      this.getEntityData().set(DATA_PARTICLE, p_195059_1_);
   }

   protected void setWaiting(boolean p_184488_1_) {
      this.getEntityData().set(DATA_WAITING, p_184488_1_);
   }

   public boolean isWaiting() {
      return this.getEntityData().get(DATA_WAITING);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int p_184486_1_) {
      this.duration = p_184486_1_;
   }

   public void tick() {
      super.tick();
      boolean flag = this.isWaiting();
      float f = this.getRadius();
      if (this.level.isClientSide) {
         IParticleData iparticledata = this.getParticle();
         if (flag) {
            if (this.random.nextBoolean()) {
               for(int i = 0; i < 2; ++i) {
                  float f1 = this.random.nextFloat() * ((float)Math.PI * 2F);
                  float f2 = MathHelper.sqrt(this.random.nextFloat()) * 0.2F;
                  float f3 = MathHelper.cos(f1) * f2;
                  float f4 = MathHelper.sin(f1) * f2;
                  if (iparticledata.getType() == ParticleTypes.ENTITY_EFFECT) {
                     int j = this.random.nextBoolean() ? 16777215 : this.getColor();
                     int k = j >> 16 & 255;
                     int l = j >> 8 & 255;
                     int i1 = j & 255;
                     this.level.addAlwaysVisibleParticle(iparticledata, this.getX() + (double)f3, this.getY(), this.getZ() + (double)f4, (double)((float)k / 255.0F), (double)((float)l / 255.0F), (double)((float)i1 / 255.0F));
                  } else {
                     this.level.addAlwaysVisibleParticle(iparticledata, this.getX() + (double)f3, this.getY(), this.getZ() + (double)f4, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         } else {
            float f5 = (float)Math.PI * f * f;

            for(int k1 = 0; (float)k1 < f5; ++k1) {
               float f6 = this.random.nextFloat() * ((float)Math.PI * 2F);
               float f7 = MathHelper.sqrt(this.random.nextFloat()) * f;
               float f8 = MathHelper.cos(f6) * f7;
               float f9 = MathHelper.sin(f6) * f7;
               if (iparticledata.getType() == ParticleTypes.ENTITY_EFFECT) {
                  int l1 = this.getColor();
                  int i2 = l1 >> 16 & 255;
                  int j2 = l1 >> 8 & 255;
                  int j1 = l1 & 255;
                  this.level.addAlwaysVisibleParticle(iparticledata, this.getX() + (double)f8, this.getY(), this.getZ() + (double)f9, (double)((float)i2 / 255.0F), (double)((float)j2 / 255.0F), (double)((float)j1 / 255.0F));
               } else {
                  this.level.addAlwaysVisibleParticle(iparticledata, this.getX() + (double)f8, this.getY(), this.getZ() + (double)f9, (0.5D - this.random.nextDouble()) * 0.15D, (double)0.01F, (0.5D - this.random.nextDouble()) * 0.15D);
               }
            }
         }
      } else {
         if (this.tickCount >= this.waitTime + this.duration) {
            this.remove();
            return;
         }

         boolean flag1 = this.tickCount < this.waitTime;
         if (flag != flag1) {
            this.setWaiting(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            f += this.radiusPerTick;
            if (f < 0.5F) {
               this.remove();
               return;
            }

            this.setRadius(f);
         }

         if (this.tickCount % 5 == 0) {
            Iterator<Entry<Entity, Integer>> iterator = this.victims.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<Entity, Integer> entry = iterator.next();
               if (this.tickCount >= entry.getValue()) {
                  iterator.remove();
               }
            }

            List<EffectInstance> list = Lists.newArrayList();

            for(EffectInstance effectinstance1 : this.potion.getEffects()) {
               list.add(new EffectInstance(effectinstance1.getEffect(), effectinstance1.getDuration() / 4, effectinstance1.getAmplifier(), effectinstance1.isAmbient(), effectinstance1.isVisible()));
            }

            list.addAll(this.effects);
            if (list.isEmpty()) {
               this.victims.clear();
            } else {
               List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
               if (!list1.isEmpty()) {
                  for(LivingEntity livingentity : list1) {
                     if (!this.victims.containsKey(livingentity) && livingentity.isAffectedByPotions()) {
                        double d0 = livingentity.getX() - this.getX();
                        double d1 = livingentity.getZ() - this.getZ();
                        double d2 = d0 * d0 + d1 * d1;
                        if (d2 <= (double)(f * f)) {
                           this.victims.put(livingentity, this.tickCount + this.reapplicationDelay);

                           for(EffectInstance effectinstance : list) {
                              if (effectinstance.getEffect().isInstantenous()) {
                                 effectinstance.getEffect().applyInstantenousEffect(this, this.getOwner(), livingentity, effectinstance.getAmplifier(), 0.5D);
                              } else {
                                 livingentity.addEffect(new EffectInstance(effectinstance));
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              f += this.radiusOnUse;
                              if (f < 0.5F) {
                                 this.remove();
                                 return;
                              }

                              this.setRadius(f);
                           }

                           if (this.durationOnUse != 0) {
                              this.duration += this.durationOnUse;
                              if (this.duration <= 0) {
                                 this.remove();
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void setRadiusOnUse(float p_184495_1_) {
      this.radiusOnUse = p_184495_1_;
   }

   public void setRadiusPerTick(float p_184487_1_) {
      this.radiusPerTick = p_184487_1_;
   }

   public void setWaitTime(int p_184485_1_) {
      this.waitTime = p_184485_1_;
   }

   public void setOwner(@Nullable LivingEntity p_184481_1_) {
      this.owner = p_184481_1_;
      this.ownerUUID = p_184481_1_ == null ? null : p_184481_1_.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerWorld) {
         Entity entity = ((ServerWorld)this.level).getEntity(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.tickCount = p_70037_1_.getInt("Age");
      this.duration = p_70037_1_.getInt("Duration");
      this.waitTime = p_70037_1_.getInt("WaitTime");
      this.reapplicationDelay = p_70037_1_.getInt("ReapplicationDelay");
      this.durationOnUse = p_70037_1_.getInt("DurationOnUse");
      this.radiusOnUse = p_70037_1_.getFloat("RadiusOnUse");
      this.radiusPerTick = p_70037_1_.getFloat("RadiusPerTick");
      this.setRadius(p_70037_1_.getFloat("Radius"));
      if (p_70037_1_.hasUUID("Owner")) {
         this.ownerUUID = p_70037_1_.getUUID("Owner");
      }

      if (p_70037_1_.contains("Particle", 8)) {
         try {
            this.setParticle(ParticleArgument.readParticle(new StringReader(p_70037_1_.getString("Particle"))));
         } catch (CommandSyntaxException commandsyntaxexception) {
            LOGGER.warn("Couldn't load custom particle {}", p_70037_1_.getString("Particle"), commandsyntaxexception);
         }
      }

      if (p_70037_1_.contains("Color", 99)) {
         this.setFixedColor(p_70037_1_.getInt("Color"));
      }

      if (p_70037_1_.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotion(p_70037_1_));
      }

      if (p_70037_1_.contains("Effects", 9)) {
         ListNBT listnbt = p_70037_1_.getList("Effects", 10);
         this.effects.clear();

         for(int i = 0; i < listnbt.size(); ++i) {
            EffectInstance effectinstance = EffectInstance.load(listnbt.getCompound(i));
            if (effectinstance != null) {
               this.addEffect(effectinstance);
            }
         }
      }

   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.putInt("Age", this.tickCount);
      p_213281_1_.putInt("Duration", this.duration);
      p_213281_1_.putInt("WaitTime", this.waitTime);
      p_213281_1_.putInt("ReapplicationDelay", this.reapplicationDelay);
      p_213281_1_.putInt("DurationOnUse", this.durationOnUse);
      p_213281_1_.putFloat("RadiusOnUse", this.radiusOnUse);
      p_213281_1_.putFloat("RadiusPerTick", this.radiusPerTick);
      p_213281_1_.putFloat("Radius", this.getRadius());
      p_213281_1_.putString("Particle", this.getParticle().writeToString());
      if (this.ownerUUID != null) {
         p_213281_1_.putUUID("Owner", this.ownerUUID);
      }

      if (this.fixedColor) {
         p_213281_1_.putInt("Color", this.getColor());
      }

      if (this.potion != Potions.EMPTY && this.potion != null) {
         p_213281_1_.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         ListNBT listnbt = new ListNBT();

         for(EffectInstance effectinstance : this.effects) {
            listnbt.add(effectinstance.save(new CompoundNBT()));
         }

         p_213281_1_.put("Effects", listnbt);
      }

   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_RADIUS.equals(p_184206_1_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }

   public EntitySize getDimensions(Pose p_213305_1_) {
      return EntitySize.scalable(this.getRadius() * 2.0F, 0.5F);
   }
}
