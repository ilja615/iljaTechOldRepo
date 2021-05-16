package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CInputPacket implements IPacket<IServerPlayNetHandler> {
   private float xxa;
   private float zza;
   private boolean isJumping;
   private boolean isShiftKeyDown;

   public CInputPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CInputPacket(float p_i46868_1_, float p_i46868_2_, boolean p_i46868_3_, boolean p_i46868_4_) {
      this.xxa = p_i46868_1_;
      this.zza = p_i46868_2_;
      this.isJumping = p_i46868_3_;
      this.isShiftKeyDown = p_i46868_4_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.xxa = p_148837_1_.readFloat();
      this.zza = p_148837_1_.readFloat();
      byte b0 = p_148837_1_.readByte();
      this.isJumping = (b0 & 1) > 0;
      this.isShiftKeyDown = (b0 & 2) > 0;
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat(this.xxa);
      p_148840_1_.writeFloat(this.zza);
      byte b0 = 0;
      if (this.isJumping) {
         b0 = (byte)(b0 | 1);
      }

      if (this.isShiftKeyDown) {
         b0 = (byte)(b0 | 2);
      }

      p_148840_1_.writeByte(b0);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerInput(this);
   }

   public float getXxa() {
      return this.xxa;
   }

   public float getZza() {
      return this.zza;
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   public boolean isShiftKeyDown() {
      return this.isShiftKeyDown;
   }
}
