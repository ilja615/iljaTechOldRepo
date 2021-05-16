package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCollectItemPacket implements IPacket<IClientPlayNetHandler> {
   private int itemId;
   private int playerId;
   private int amount;

   public SCollectItemPacket() {
   }

   public SCollectItemPacket(int p_i47316_1_, int p_i47316_2_, int p_i47316_3_) {
      this.itemId = p_i47316_1_;
      this.playerId = p_i47316_2_;
      this.amount = p_i47316_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.itemId = p_148837_1_.readVarInt();
      this.playerId = p_148837_1_.readVarInt();
      this.amount = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.itemId);
      p_148840_1_.writeVarInt(this.playerId);
      p_148840_1_.writeVarInt(this.amount);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTakeItemEntity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getItemId() {
      return this.itemId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getPlayerId() {
      return this.playerId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAmount() {
      return this.amount;
   }
}
