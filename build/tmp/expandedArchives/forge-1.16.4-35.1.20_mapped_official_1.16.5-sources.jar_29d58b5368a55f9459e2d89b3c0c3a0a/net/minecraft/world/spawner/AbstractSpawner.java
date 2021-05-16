package net.minecraft.world.spawner;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractSpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private int spawnDelay = 20;
   private final List<WeightedSpawnerEntity> spawnPotentials = Lists.newArrayList();
   private WeightedSpawnerEntity nextSpawnData = new WeightedSpawnerEntity();
   private double spin;
   private double oSpin;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   @Nullable
   private Entity displayEntity;
   private int maxNearbyEntities = 6;
   private int requiredPlayerRange = 16;
   private int spawnRange = 4;

   @Nullable
   private ResourceLocation getEntityId() {
      String s = this.nextSpawnData.getTag().getString("id");

      try {
         return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
      } catch (ResourceLocationException resourcelocationexception) {
         BlockPos blockpos = this.getPos();
         LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", s, this.getLevel().dimension().location(), blockpos.getX(), blockpos.getY(), blockpos.getZ());
         return null;
      }
   }

   public void setEntityId(EntityType<?> p_200876_1_) {
      this.nextSpawnData.getTag().putString("id", Registry.ENTITY_TYPE.getKey(p_200876_1_).toString());
   }

   private boolean isNearPlayer() {
      BlockPos blockpos = this.getPos();
      return this.getLevel().hasNearbyAlivePlayer((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D, (double)this.requiredPlayerRange);
   }

   public void tick() {
      if (!this.isNearPlayer()) {
         this.oSpin = this.spin;
      } else {
         World world = this.getLevel();
         BlockPos blockpos = this.getPos();
         if (!(world instanceof ServerWorld)) {
            double d3 = (double)blockpos.getX() + world.random.nextDouble();
            double d4 = (double)blockpos.getY() + world.random.nextDouble();
            double d5 = (double)blockpos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) {
               --this.spawnDelay;
            }

            this.oSpin = this.spin;
            this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
         } else {
            if (this.spawnDelay == -1) {
               this.delay();
            }

            if (this.spawnDelay > 0) {
               --this.spawnDelay;
               return;
            }

            boolean flag = false;

            for(int i = 0; i < this.spawnCount; ++i) {
               CompoundNBT compoundnbt = this.nextSpawnData.getTag();
               Optional<EntityType<?>> optional = EntityType.by(compoundnbt);
               if (!optional.isPresent()) {
                  this.delay();
                  return;
               }

               ListNBT listnbt = compoundnbt.getList("Pos", 6);
               int j = listnbt.size();
               double d0 = j >= 1 ? listnbt.getDouble(0) : (double)blockpos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               double d1 = j >= 2 ? listnbt.getDouble(1) : (double)(blockpos.getY() + world.random.nextInt(3) - 1);
               double d2 = j >= 3 ? listnbt.getDouble(2) : (double)blockpos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               if (world.noCollision(optional.get().getAABB(d0, d1, d2))) {
                  ServerWorld serverworld = (ServerWorld)world;
                  if (EntitySpawnPlacementRegistry.checkSpawnRules(optional.get(), serverworld, SpawnReason.SPAWNER, new BlockPos(d0, d1, d2), world.getRandom())) {
                     Entity entity = EntityType.loadEntityRecursive(compoundnbt, world, (p_221408_6_) -> {
                        p_221408_6_.moveTo(d0, d1, d2, p_221408_6_.yRot, p_221408_6_.xRot);
                        return p_221408_6_;
                     });
                     if (entity == null) {
                        this.delay();
                        return;
                     }

                     int k = world.getEntitiesOfClass(entity.getClass(), (new AxisAlignedBB((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), (double)(blockpos.getX() + 1), (double)(blockpos.getY() + 1), (double)(blockpos.getZ() + 1))).inflate((double)this.spawnRange)).size();
                     if (k >= this.maxNearbyEntities) {
                        this.delay();
                        return;
                     }

                     entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
                     if (entity instanceof MobEntity) {
                        MobEntity mobentity = (MobEntity)entity;
                        if (!net.minecraftforge.event.ForgeEventFactory.canEntitySpawnSpawner(mobentity, world, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), this)) {
                           continue;
                        }

                        if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8)) {
                        if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(mobentity, world, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), this, SpawnReason.SPAWNER))
                           ((MobEntity)entity).finalizeSpawn(serverworld, world.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.SPAWNER, (ILivingEntityData)null, (CompoundNBT)null);
                        }
                     }

                     if (!serverworld.tryAddFreshEntityWithPassengers(entity)) {
                        this.delay();
                        return;
                     }

                     world.levelEvent(2004, blockpos, 0);
                     if (entity instanceof MobEntity) {
                        ((MobEntity)entity).spawnAnim();
                     }

                     flag = true;
                  }
               }
            }

            if (flag) {
               this.delay();
            }
         }

      }
   }

   private void delay() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int i = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getLevel().random.nextInt(i);
      }

      if (!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData(WeightedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
      }

      this.broadcastEvent(1);
   }

   public void load(CompoundNBT p_98270_1_) {
      this.spawnDelay = p_98270_1_.getShort("Delay");
      this.spawnPotentials.clear();
      if (p_98270_1_.contains("SpawnPotentials", 9)) {
         ListNBT listnbt = p_98270_1_.getList("SpawnPotentials", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.spawnPotentials.add(new WeightedSpawnerEntity(listnbt.getCompound(i)));
         }
      }

      if (p_98270_1_.contains("SpawnData", 10)) {
         this.setNextSpawnData(new WeightedSpawnerEntity(1, p_98270_1_.getCompound("SpawnData")));
      } else if (!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData(WeightedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
      }

      if (p_98270_1_.contains("MinSpawnDelay", 99)) {
         this.minSpawnDelay = p_98270_1_.getShort("MinSpawnDelay");
         this.maxSpawnDelay = p_98270_1_.getShort("MaxSpawnDelay");
         this.spawnCount = p_98270_1_.getShort("SpawnCount");
      }

      if (p_98270_1_.contains("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = p_98270_1_.getShort("MaxNearbyEntities");
         this.requiredPlayerRange = p_98270_1_.getShort("RequiredPlayerRange");
      }

      if (p_98270_1_.contains("SpawnRange", 99)) {
         this.spawnRange = p_98270_1_.getShort("SpawnRange");
      }

      if (this.getLevel() != null) {
         this.displayEntity = null;
      }

   }

   public CompoundNBT save(CompoundNBT p_189530_1_) {
      ResourceLocation resourcelocation = this.getEntityId();
      if (resourcelocation == null) {
         return p_189530_1_;
      } else {
         p_189530_1_.putShort("Delay", (short)this.spawnDelay);
         p_189530_1_.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
         p_189530_1_.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         p_189530_1_.putShort("SpawnCount", (short)this.spawnCount);
         p_189530_1_.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         p_189530_1_.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
         p_189530_1_.putShort("SpawnRange", (short)this.spawnRange);
         p_189530_1_.put("SpawnData", this.nextSpawnData.getTag().copy());
         ListNBT listnbt = new ListNBT();
         if (this.spawnPotentials.isEmpty()) {
            listnbt.add(this.nextSpawnData.save());
         } else {
            for(WeightedSpawnerEntity weightedspawnerentity : this.spawnPotentials) {
               listnbt.add(weightedspawnerentity.save());
            }
         }

         p_189530_1_.put("SpawnPotentials", listnbt);
         return p_189530_1_;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getOrCreateDisplayEntity() {
      if (this.displayEntity == null) {
         this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), this.getLevel(), Function.identity());
         if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8) && this.displayEntity instanceof MobEntity) {
         }
      }

      return this.displayEntity;
   }

   public boolean onEventTriggered(int p_98268_1_) {
      if (p_98268_1_ == 1 && this.getLevel().isClientSide) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {
      this.nextSpawnData = p_184993_1_;
   }

   public abstract void broadcastEvent(int p_98267_1_);

   public abstract World getLevel();

   public abstract BlockPos getPos();

   @OnlyIn(Dist.CLIENT)
   public double getSpin() {
      return this.spin;
   }

   @OnlyIn(Dist.CLIENT)
   public double getoSpin() {
      return this.oSpin;
   }

   @Nullable
   public Entity getSpawnerEntity() {
      return null;
   }
}
