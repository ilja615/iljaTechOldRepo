package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityVelocityPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private int xa;
   private int ya;
   private int za;

   public SEntityVelocityPacket() {
   }

   public SEntityVelocityPacket(Entity p_i46914_1_) {
      this(p_i46914_1_.getId(), p_i46914_1_.getDeltaMovement());
   }

   public SEntityVelocityPacket(int p_i50764_1_, Vector3d p_i50764_2_) {
      this.id = p_i50764_1_;
      double d0 = 3.9D;
      double d1 = MathHelper.clamp(p_i50764_2_.x, -3.9D, 3.9D);
      double d2 = MathHelper.clamp(p_i50764_2_.y, -3.9D, 3.9D);
      double d3 = MathHelper.clamp(p_i50764_2_.z, -3.9D, 3.9D);
      this.xa = (int)(d1 * 8000.0D);
      this.ya = (int)(d2 * 8000.0D);
      this.za = (int)(d3 * 8000.0D);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.xa = p_148837_1_.readShort();
      this.ya = p_148837_1_.readShort();
      this.za = p_148837_1_.readShort();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeShort(this.xa);
      p_148840_1_.writeShort(this.ya);
      p_148840_1_.writeShort(this.za);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetEntityMotion(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public int getXa() {
      return this.xa;
   }

   @OnlyIn(Dist.CLIENT)
   public int getYa() {
      return this.ya;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZa() {
      return this.za;
   }
}
