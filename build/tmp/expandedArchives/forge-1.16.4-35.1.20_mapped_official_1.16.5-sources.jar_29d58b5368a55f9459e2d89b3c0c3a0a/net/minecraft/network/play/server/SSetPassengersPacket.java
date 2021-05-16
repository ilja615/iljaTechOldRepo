package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSetPassengersPacket implements IPacket<IClientPlayNetHandler> {
   private int vehicle;
   private int[] passengers;

   public SSetPassengersPacket() {
   }

   public SSetPassengersPacket(Entity p_i46909_1_) {
      this.vehicle = p_i46909_1_.getId();
      List<Entity> list = p_i46909_1_.getPassengers();
      this.passengers = new int[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         this.passengers[i] = list.get(i).getId();
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.vehicle = p_148837_1_.readVarInt();
      this.passengers = p_148837_1_.readVarIntArray();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.vehicle);
      p_148840_1_.writeVarIntArray(this.passengers);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetEntityPassengersPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int[] getPassengers() {
      return this.passengers;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVehicle() {
      return this.vehicle;
   }
}
