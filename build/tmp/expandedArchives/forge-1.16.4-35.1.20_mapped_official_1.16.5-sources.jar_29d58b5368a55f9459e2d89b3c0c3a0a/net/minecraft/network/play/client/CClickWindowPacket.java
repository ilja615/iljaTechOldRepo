package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CClickWindowPacket implements IPacket<IServerPlayNetHandler> {
   private int containerId;
   private int slotNum;
   private int buttonNum;
   private short uid;
   private ItemStack itemStack = ItemStack.EMPTY;
   private ClickType clickType;

   public CClickWindowPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CClickWindowPacket(int p_i46882_1_, int p_i46882_2_, int p_i46882_3_, ClickType p_i46882_4_, ItemStack p_i46882_5_, short p_i46882_6_) {
      this.containerId = p_i46882_1_;
      this.slotNum = p_i46882_2_;
      this.buttonNum = p_i46882_3_;
      this.itemStack = p_i46882_5_.copy();
      this.uid = p_i46882_6_;
      this.clickType = p_i46882_4_;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerClick(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readByte();
      this.slotNum = p_148837_1_.readShort();
      this.buttonNum = p_148837_1_.readByte();
      this.uid = p_148837_1_.readShort();
      this.clickType = p_148837_1_.readEnum(ClickType.class);
      this.itemStack = p_148837_1_.readItem();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeShort(this.slotNum);
      p_148840_1_.writeByte(this.buttonNum);
      p_148840_1_.writeShort(this.uid);
      p_148840_1_.writeEnum(this.clickType);
      p_148840_1_.writeItemStack(this.itemStack, false); //Forge: Include full tag for C->S
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public int getButtonNum() {
      return this.buttonNum;
   }

   public short getUid() {
      return this.uid;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }

   public ClickType getClickType() {
      return this.clickType;
   }
}
