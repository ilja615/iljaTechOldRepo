package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEntityActionPacket implements IPacket<IServerPlayNetHandler> {
   private int id;
   private CEntityActionPacket.Action action;
   private int data;

   public CEntityActionPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEntityActionPacket(Entity p_i46869_1_, CEntityActionPacket.Action p_i46869_2_) {
      this(p_i46869_1_, p_i46869_2_, 0);
   }

   @OnlyIn(Dist.CLIENT)
   public CEntityActionPacket(Entity p_i46870_1_, CEntityActionPacket.Action p_i46870_2_, int p_i46870_3_) {
      this.id = p_i46870_1_.getId();
      this.action = p_i46870_2_;
      this.data = p_i46870_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.action = p_148837_1_.readEnum(CEntityActionPacket.Action.class);
      this.data = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeEnum(this.action);
      p_148840_1_.writeVarInt(this.data);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerCommand(this);
   }

   public CEntityActionPacket.Action getAction() {
      return this.action;
   }

   public int getData() {
      return this.data;
   }

   public static enum Action {
      PRESS_SHIFT_KEY,
      RELEASE_SHIFT_KEY,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;
   }
}
