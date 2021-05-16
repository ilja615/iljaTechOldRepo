package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUseEntityPacket implements IPacket<IServerPlayNetHandler> {
   private int entityId;
   private CUseEntityPacket.Action action;
   private Vector3d location;
   private Hand hand;
   private boolean usingSecondaryAction;

   public CUseEntityPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUseEntityPacket(Entity p_i46877_1_, boolean p_i46877_2_) {
      this.entityId = p_i46877_1_.getId();
      this.action = CUseEntityPacket.Action.ATTACK;
      this.usingSecondaryAction = p_i46877_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public CUseEntityPacket(Entity p_i46878_1_, Hand p_i46878_2_, boolean p_i46878_3_) {
      this.entityId = p_i46878_1_.getId();
      this.action = CUseEntityPacket.Action.INTERACT;
      this.hand = p_i46878_2_;
      this.usingSecondaryAction = p_i46878_3_;
   }

   @OnlyIn(Dist.CLIENT)
   public CUseEntityPacket(Entity p_i47098_1_, Hand p_i47098_2_, Vector3d p_i47098_3_, boolean p_i47098_4_) {
      this.entityId = p_i47098_1_.getId();
      this.action = CUseEntityPacket.Action.INTERACT_AT;
      this.hand = p_i47098_2_;
      this.location = p_i47098_3_;
      this.usingSecondaryAction = p_i47098_4_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.action = p_148837_1_.readEnum(CUseEntityPacket.Action.class);
      if (this.action == CUseEntityPacket.Action.INTERACT_AT) {
         this.location = new Vector3d((double)p_148837_1_.readFloat(), (double)p_148837_1_.readFloat(), (double)p_148837_1_.readFloat());
      }

      if (this.action == CUseEntityPacket.Action.INTERACT || this.action == CUseEntityPacket.Action.INTERACT_AT) {
         this.hand = p_148837_1_.readEnum(Hand.class);
      }

      this.usingSecondaryAction = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeEnum(this.action);
      if (this.action == CUseEntityPacket.Action.INTERACT_AT) {
         p_148840_1_.writeFloat((float)this.location.x);
         p_148840_1_.writeFloat((float)this.location.y);
         p_148840_1_.writeFloat((float)this.location.z);
      }

      if (this.action == CUseEntityPacket.Action.INTERACT || this.action == CUseEntityPacket.Action.INTERACT_AT) {
         p_148840_1_.writeEnum(this.hand);
      }

      p_148840_1_.writeBoolean(this.usingSecondaryAction);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleInteract(this);
   }

   @Nullable
   public Entity getTarget(World p_149564_1_) {
      return p_149564_1_.getEntity(this.entityId);
   }

   public CUseEntityPacket.Action getAction() {
      return this.action;
   }

   @Nullable
   public Hand getHand() {
      return this.hand;
   }

   public Vector3d getLocation() {
      return this.location;
   }

   public boolean isUsingSecondaryAction() {
      return this.usingSecondaryAction;
   }

   public static enum Action {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }
}
