package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SQueryNBTResponsePacket implements IPacket<IClientPlayNetHandler> {
   private int transactionId;
   @Nullable
   private CompoundNBT tag;

   public SQueryNBTResponsePacket() {
   }

   public SQueryNBTResponsePacket(int p_i49757_1_, @Nullable CompoundNBT p_i49757_2_) {
      this.transactionId = p_i49757_1_;
      this.tag = p_i49757_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.transactionId = p_148837_1_.readVarInt();
      this.tag = p_148837_1_.readNbt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transactionId);
      p_148840_1_.writeNbt(this.tag);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTagQueryPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public CompoundNBT getTag() {
      return this.tag;
   }

   public boolean isSkippable() {
      return true;
   }
}
