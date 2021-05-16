package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSetSlotPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private int slot;
   private ItemStack itemStack = ItemStack.EMPTY;

   public SSetSlotPacket() {
   }

   public SSetSlotPacket(int p_i46951_1_, int p_i46951_2_, ItemStack p_i46951_3_) {
      this.containerId = p_i46951_1_;
      this.slot = p_i46951_2_;
      this.itemStack = p_i46951_3_.copy();
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerSetSlot(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readByte();
      this.slot = p_148837_1_.readShort();
      this.itemStack = p_148837_1_.readItem();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeShort(this.slot);
      p_148840_1_.writeItem(this.itemStack);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlot() {
      return this.slot;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      return this.itemStack;
   }
}
