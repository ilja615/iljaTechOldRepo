package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCooldownPacket implements IPacket<IClientPlayNetHandler> {
   private Item item;
   private int duration;

   public SCooldownPacket() {
   }

   public SCooldownPacket(Item p_i46950_1_, int p_i46950_2_) {
      this.item = p_i46950_1_;
      this.duration = p_i46950_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.item = Item.byId(p_148837_1_.readVarInt());
      this.duration = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(Item.getId(this.item));
      p_148840_1_.writeVarInt(this.duration);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleItemCooldown(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Item getItem() {
      return this.item;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDuration() {
      return this.duration;
   }
}
