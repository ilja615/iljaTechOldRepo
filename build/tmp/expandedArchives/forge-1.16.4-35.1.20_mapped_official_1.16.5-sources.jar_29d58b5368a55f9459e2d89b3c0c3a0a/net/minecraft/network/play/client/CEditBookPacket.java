package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEditBookPacket implements IPacket<IServerPlayNetHandler> {
   private ItemStack book;
   private boolean signing;
   private int slot;

   public CEditBookPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEditBookPacket(ItemStack p_i244520_1_, boolean p_i244520_2_, int p_i244520_3_) {
      this.book = p_i244520_1_.copy();
      this.signing = p_i244520_2_;
      this.slot = p_i244520_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.book = p_148837_1_.readItem();
      this.signing = p_148837_1_.readBoolean();
      this.slot = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeItem(this.book);
      p_148840_1_.writeBoolean(this.signing);
      p_148840_1_.writeVarInt(this.slot);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleEditBook(this);
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean isSigning() {
      return this.signing;
   }

   public int getSlot() {
      return this.slot;
   }
}
