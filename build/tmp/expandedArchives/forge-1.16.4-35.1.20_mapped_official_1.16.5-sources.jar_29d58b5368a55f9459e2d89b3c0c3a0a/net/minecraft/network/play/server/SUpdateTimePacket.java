package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateTimePacket implements IPacket<IClientPlayNetHandler> {
   private long gameTime;
   private long dayTime;

   public SUpdateTimePacket() {
   }

   public SUpdateTimePacket(long p_i46902_1_, long p_i46902_3_, boolean p_i46902_5_) {
      this.gameTime = p_i46902_1_;
      this.dayTime = p_i46902_3_;
      if (!p_i46902_5_) {
         this.dayTime = -this.dayTime;
         if (this.dayTime == 0L) {
            this.dayTime = -1L;
         }
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.gameTime = p_148837_1_.readLong();
      this.dayTime = p_148837_1_.readLong();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.gameTime);
      p_148840_1_.writeLong(this.dayTime);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetTime(this);
   }

   @OnlyIn(Dist.CLIENT)
   public long getGameTime() {
      return this.gameTime;
   }

   @OnlyIn(Dist.CLIENT)
   public long getDayTime() {
      return this.dayTime;
   }
}
