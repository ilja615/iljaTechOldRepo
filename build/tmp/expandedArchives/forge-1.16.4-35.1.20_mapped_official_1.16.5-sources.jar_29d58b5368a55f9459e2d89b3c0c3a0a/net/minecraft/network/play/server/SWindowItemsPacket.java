package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SWindowItemsPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private List<ItemStack> items;

   public SWindowItemsPacket() {
   }

   public SWindowItemsPacket(int p_i47317_1_, NonNullList<ItemStack> p_i47317_2_) {
      this.containerId = p_i47317_1_;
      this.items = NonNullList.withSize(p_i47317_2_.size(), ItemStack.EMPTY);

      for(int i = 0; i < this.items.size(); ++i) {
         this.items.set(i, p_i47317_2_.get(i).copy());
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readUnsignedByte();
      int i = p_148837_1_.readShort();
      this.items = NonNullList.withSize(i, ItemStack.EMPTY);

      for(int j = 0; j < i; ++j) {
         this.items.set(j, p_148837_1_.readItem());
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeShort(this.items.size());

      for(ItemStack itemstack : this.items) {
         p_148840_1_.writeItem(itemstack);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerContent(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @OnlyIn(Dist.CLIENT)
   public List<ItemStack> getItems() {
      return this.items;
   }
}
