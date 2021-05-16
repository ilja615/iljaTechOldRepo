package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackedEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerWorld level;
   private final Entity entity;
   private final int updateInterval;
   private final boolean trackDelta;
   private final Consumer<IPacket<?>> broadcast;
   private long xp;
   private long yp;
   private long zp;
   private int yRotp;
   private int xRotp;
   private int yHeadRotp;
   private Vector3d ap = Vector3d.ZERO;
   private int tickCount;
   private int teleportDelay;
   private List<Entity> lastPassengers = Collections.emptyList();
   private boolean wasRiding;
   private boolean wasOnGround;

   public TrackedEntity(ServerWorld p_i50704_1_, Entity p_i50704_2_, int p_i50704_3_, boolean p_i50704_4_, Consumer<IPacket<?>> p_i50704_5_) {
      this.level = p_i50704_1_;
      this.broadcast = p_i50704_5_;
      this.entity = p_i50704_2_;
      this.updateInterval = p_i50704_3_;
      this.trackDelta = p_i50704_4_;
      this.updateSentPos();
      this.yRotp = MathHelper.floor(p_i50704_2_.yRot * 256.0F / 360.0F);
      this.xRotp = MathHelper.floor(p_i50704_2_.xRot * 256.0F / 360.0F);
      this.yHeadRotp = MathHelper.floor(p_i50704_2_.getYHeadRot() * 256.0F / 360.0F);
      this.wasOnGround = p_i50704_2_.isOnGround();
   }

   public void sendChanges() {
      List<Entity> list = this.entity.getPassengers();
      if (!list.equals(this.lastPassengers)) {
         this.lastPassengers = list;
         this.broadcast.accept(new SSetPassengersPacket(this.entity));
      }

      if (this.entity instanceof ItemFrameEntity && this.tickCount % 10 == 0) {
         ItemFrameEntity itemframeentity = (ItemFrameEntity)this.entity;
         ItemStack itemstack = itemframeentity.getItem();
         MapData mapdata = FilledMapItem.getOrCreateSavedData(itemstack, this.level);
         if (mapdata != null) {
            for(ServerPlayerEntity serverplayerentity : this.level.players()) {
               mapdata.tickCarriedBy(serverplayerentity, itemstack);
               IPacket<?> ipacket = ((FilledMapItem)itemstack.getItem()).getUpdatePacket(itemstack, this.level, serverplayerentity);
               if (ipacket != null) {
                  serverplayerentity.connection.send(ipacket);
               }
            }
         }

         this.sendDirtyEntityData();
      }

      if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
         if (this.entity.isPassenger()) {
            int i1 = MathHelper.floor(this.entity.yRot * 256.0F / 360.0F);
            int l1 = MathHelper.floor(this.entity.xRot * 256.0F / 360.0F);
            boolean flag2 = Math.abs(i1 - this.yRotp) >= 1 || Math.abs(l1 - this.xRotp) >= 1;
            if (flag2) {
               this.broadcast.accept(new SEntityPacket.LookPacket(this.entity.getId(), (byte)i1, (byte)l1, this.entity.isOnGround()));
               this.yRotp = i1;
               this.xRotp = l1;
            }

            this.updateSentPos();
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            ++this.teleportDelay;
            int l = MathHelper.floor(this.entity.yRot * 256.0F / 360.0F);
            int k1 = MathHelper.floor(this.entity.xRot * 256.0F / 360.0F);
            Vector3d vector3d = this.entity.position().subtract(SEntityPacket.packetToEntity(this.xp, this.yp, this.zp));
            boolean flag3 = vector3d.lengthSqr() >= (double)7.6293945E-6F;
            IPacket<?> ipacket1 = null;
            boolean flag4 = flag3 || this.tickCount % 60 == 0;
            boolean flag = Math.abs(l - this.yRotp) >= 1 || Math.abs(k1 - this.xRotp) >= 1;
            if (this.tickCount > 0 || this.entity instanceof AbstractArrowEntity) {
               long i = SEntityPacket.entityToPacket(vector3d.x);
               long j = SEntityPacket.entityToPacket(vector3d.y);
               long k = SEntityPacket.entityToPacket(vector3d.z);
               boolean flag1 = i < -32768L || i > 32767L || j < -32768L || j > 32767L || k < -32768L || k > 32767L;
               if (!flag1 && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.isOnGround()) {
                  if ((!flag4 || !flag) && !(this.entity instanceof AbstractArrowEntity)) {
                     if (flag4) {
                        ipacket1 = new SEntityPacket.RelativeMovePacket(this.entity.getId(), (short)((int)i), (short)((int)j), (short)((int)k), this.entity.isOnGround());
                     } else if (flag) {
                        ipacket1 = new SEntityPacket.LookPacket(this.entity.getId(), (byte)l, (byte)k1, this.entity.isOnGround());
                     }
                  } else {
                     ipacket1 = new SEntityPacket.MovePacket(this.entity.getId(), (short)((int)i), (short)((int)j), (short)((int)k), (byte)l, (byte)k1, this.entity.isOnGround());
                  }
               } else {
                  this.wasOnGround = this.entity.isOnGround();
                  this.teleportDelay = 0;
                  ipacket1 = new SEntityTeleportPacket(this.entity);
               }
            }

            if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.tickCount > 0) {
               Vector3d vector3d1 = this.entity.getDeltaMovement();
               double d0 = vector3d1.distanceToSqr(this.ap);
               if (d0 > 1.0E-7D || d0 > 0.0D && vector3d1.lengthSqr() == 0.0D) {
                  this.ap = vector3d1;
                  this.broadcast.accept(new SEntityVelocityPacket(this.entity.getId(), this.ap));
               }
            }

            if (ipacket1 != null) {
               this.broadcast.accept(ipacket1);
            }

            this.sendDirtyEntityData();
            if (flag4) {
               this.updateSentPos();
            }

            if (flag) {
               this.yRotp = l;
               this.xRotp = k1;
            }

            this.wasRiding = false;
         }

         int j1 = MathHelper.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
         if (Math.abs(j1 - this.yHeadRotp) >= 1) {
            this.broadcast.accept(new SEntityHeadLookPacket(this.entity, (byte)j1));
            this.yHeadRotp = j1;
         }

         this.entity.hasImpulse = false;
      }

      ++this.tickCount;
      if (this.entity.hurtMarked) {
         this.broadcastAndSend(new SEntityVelocityPacket(this.entity));
         this.entity.hurtMarked = false;
      }

   }

   public void removePairing(ServerPlayerEntity p_219454_1_) {
      this.entity.stopSeenByPlayer(p_219454_1_);
      p_219454_1_.sendRemoveEntity(this.entity);
      net.minecraftforge.event.ForgeEventFactory.onStopEntityTracking(this.entity, p_219454_1_);
   }

   public void addPairing(ServerPlayerEntity p_219455_1_) {
      this.sendPairingData(p_219455_1_.connection::send);
      this.entity.startSeenByPlayer(p_219455_1_);
      p_219455_1_.cancelRemoveEntity(this.entity);
      net.minecraftforge.event.ForgeEventFactory.onStartEntityTracking(this.entity, p_219455_1_);
   }

   public void sendPairingData(Consumer<IPacket<?>> p_219452_1_) {
      if (this.entity.removed) {
         LOGGER.warn("Fetching packet for removed entity " + this.entity);
      }

      IPacket<?> ipacket = this.entity.getAddEntityPacket();
      this.yHeadRotp = MathHelper.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      p_219452_1_.accept(ipacket);
      if (!this.entity.getEntityData().isEmpty()) {
         p_219452_1_.accept(new SEntityMetadataPacket(this.entity.getId(), this.entity.getEntityData(), true));
      }

      boolean flag = this.trackDelta;
      if (this.entity instanceof LivingEntity) {
         Collection<ModifiableAttributeInstance> collection = ((LivingEntity)this.entity).getAttributes().getSyncableAttributes();
         if (!collection.isEmpty()) {
            p_219452_1_.accept(new SEntityPropertiesPacket(this.entity.getId(), collection));
         }

         if (((LivingEntity)this.entity).isFallFlying()) {
            flag = true;
         }
      }

      this.ap = this.entity.getDeltaMovement();
      if (flag && !(ipacket instanceof SSpawnMobPacket)) {
         p_219452_1_.accept(new SEntityVelocityPacket(this.entity.getId(), this.ap));
      }

      if (this.entity instanceof LivingEntity) {
         List<Pair<EquipmentSlotType, ItemStack>> list = Lists.newArrayList();

         for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            ItemStack itemstack = ((LivingEntity)this.entity).getItemBySlot(equipmentslottype);
            if (!itemstack.isEmpty()) {
               list.add(Pair.of(equipmentslottype, itemstack.copy()));
            }
         }

         if (!list.isEmpty()) {
            p_219452_1_.accept(new SEntityEquipmentPacket(this.entity.getId(), list));
         }
      }

      if (this.entity instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)this.entity;

         for(EffectInstance effectinstance : livingentity.getActiveEffects()) {
            p_219452_1_.accept(new SPlayEntityEffectPacket(this.entity.getId(), effectinstance));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         p_219452_1_.accept(new SSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         p_219452_1_.accept(new SSetPassengersPacket(this.entity.getVehicle()));
      }

      if (this.entity instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)this.entity;
         if (mobentity.isLeashed()) {
            p_219452_1_.accept(new SMountEntityPacket(mobentity, mobentity.getLeashHolder()));
         }
      }

   }

   private void sendDirtyEntityData() {
      EntityDataManager entitydatamanager = this.entity.getEntityData();
      if (entitydatamanager.isDirty()) {
         this.broadcastAndSend(new SEntityMetadataPacket(this.entity.getId(), entitydatamanager, false));
      }

      if (this.entity instanceof LivingEntity) {
         Set<ModifiableAttributeInstance> set = ((LivingEntity)this.entity).getAttributes().getDirtyAttributes();
         if (!set.isEmpty()) {
            this.broadcastAndSend(new SEntityPropertiesPacket(this.entity.getId(), set));
         }

         set.clear();
      }

   }

   private void updateSentPos() {
      this.xp = SEntityPacket.entityToPacket(this.entity.getX());
      this.yp = SEntityPacket.entityToPacket(this.entity.getY());
      this.zp = SEntityPacket.entityToPacket(this.entity.getZ());
   }

   public Vector3d sentPos() {
      return SEntityPacket.packetToEntity(this.xp, this.yp, this.zp);
   }

   private void broadcastAndSend(IPacket<?> p_219451_1_) {
      this.broadcast.accept(p_219451_1_);
      if (this.entity instanceof ServerPlayerEntity) {
         ((ServerPlayerEntity)this.entity).connection.send(p_219451_1_);
      }

   }
}
