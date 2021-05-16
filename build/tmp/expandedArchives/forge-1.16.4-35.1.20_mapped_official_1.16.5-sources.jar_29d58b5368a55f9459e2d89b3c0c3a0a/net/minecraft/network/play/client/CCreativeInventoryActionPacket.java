package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCreativeInventoryActionPacket implements IPacket<IServerPlayNetHandler> {
   private int slotNum;
   private ItemStack itemStack = ItemStack.EMPTY;

   public CCreativeInventoryActionPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCreativeInventoryActionPacket(int p_i46862_1_, ItemStack p_i46862_2_) {
      this.slotNum = p_i46862_1_;
      this.itemStack = p_i46862_2_.copy();
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetCreativeModeSlot(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.slotNum = p_148837_1_.readShort();
      this.itemStack = p_148837_1_.readItem();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeShort(this.slotNum);
      p_148840_1_.writeItemStack(this.itemStack, false); //Forge: Include full tag for C->S
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
