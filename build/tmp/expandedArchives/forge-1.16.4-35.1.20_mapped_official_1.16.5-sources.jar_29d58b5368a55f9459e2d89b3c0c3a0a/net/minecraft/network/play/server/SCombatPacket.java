package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class SCombatPacket implements IPacket<IClientPlayNetHandler> {
   public SCombatPacket.Event event;
   public int playerId;
   public int killerId;
   public int duration;
   public ITextComponent message;

   public SCombatPacket() {
   }

   public SCombatPacket(CombatTracker p_i46931_1_, SCombatPacket.Event p_i46931_2_) {
      this(p_i46931_1_, p_i46931_2_, StringTextComponent.EMPTY);
   }

   public SCombatPacket(CombatTracker p_i49825_1_, SCombatPacket.Event p_i49825_2_, ITextComponent p_i49825_3_) {
      this.event = p_i49825_2_;
      LivingEntity livingentity = p_i49825_1_.getKiller();
      switch(p_i49825_2_) {
      case END_COMBAT:
         this.duration = p_i49825_1_.getCombatDuration();
         this.killerId = livingentity == null ? -1 : livingentity.getId();
         break;
      case ENTITY_DIED:
         this.playerId = p_i49825_1_.getMob().getId();
         this.killerId = livingentity == null ? -1 : livingentity.getId();
         this.message = p_i49825_3_;
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.event = p_148837_1_.readEnum(SCombatPacket.Event.class);
      if (this.event == SCombatPacket.Event.END_COMBAT) {
         this.duration = p_148837_1_.readVarInt();
         this.killerId = p_148837_1_.readInt();
      } else if (this.event == SCombatPacket.Event.ENTITY_DIED) {
         this.playerId = p_148837_1_.readVarInt();
         this.killerId = p_148837_1_.readInt();
         this.message = p_148837_1_.readComponent();
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.event);
      if (this.event == SCombatPacket.Event.END_COMBAT) {
         p_148840_1_.writeVarInt(this.duration);
         p_148840_1_.writeInt(this.killerId);
      } else if (this.event == SCombatPacket.Event.ENTITY_DIED) {
         p_148840_1_.writeVarInt(this.playerId);
         p_148840_1_.writeInt(this.killerId);
         p_148840_1_.writeComponent(this.message);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerCombat(this);
   }

   public boolean isSkippable() {
      return this.event == SCombatPacket.Event.ENTITY_DIED;
   }

   public static enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;
   }
}
