package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SExplosionPacket implements IPacket<IClientPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private float power;
   private List<BlockPos> toBlow;
   private float knockbackX;
   private float knockbackY;
   private float knockbackZ;

   public SExplosionPacket() {
   }

   public SExplosionPacket(double p_i47099_1_, double p_i47099_3_, double p_i47099_5_, float p_i47099_7_, List<BlockPos> p_i47099_8_, Vector3d p_i47099_9_) {
      this.x = p_i47099_1_;
      this.y = p_i47099_3_;
      this.z = p_i47099_5_;
      this.power = p_i47099_7_;
      this.toBlow = Lists.newArrayList(p_i47099_8_);
      if (p_i47099_9_ != null) {
         this.knockbackX = (float)p_i47099_9_.x;
         this.knockbackY = (float)p_i47099_9_.y;
         this.knockbackZ = (float)p_i47099_9_.z;
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.x = (double)p_148837_1_.readFloat();
      this.y = (double)p_148837_1_.readFloat();
      this.z = (double)p_148837_1_.readFloat();
      this.power = p_148837_1_.readFloat();
      int i = p_148837_1_.readInt();
      this.toBlow = Lists.newArrayListWithCapacity(i);
      int j = MathHelper.floor(this.x);
      int k = MathHelper.floor(this.y);
      int l = MathHelper.floor(this.z);

      for(int i1 = 0; i1 < i; ++i1) {
         int j1 = p_148837_1_.readByte() + j;
         int k1 = p_148837_1_.readByte() + k;
         int l1 = p_148837_1_.readByte() + l;
         this.toBlow.add(new BlockPos(j1, k1, l1));
      }

      this.knockbackX = p_148837_1_.readFloat();
      this.knockbackY = p_148837_1_.readFloat();
      this.knockbackZ = p_148837_1_.readFloat();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat((float)this.x);
      p_148840_1_.writeFloat((float)this.y);
      p_148840_1_.writeFloat((float)this.z);
      p_148840_1_.writeFloat(this.power);
      p_148840_1_.writeInt(this.toBlow.size());
      int i = MathHelper.floor(this.x);
      int j = MathHelper.floor(this.y);
      int k = MathHelper.floor(this.z);

      for(BlockPos blockpos : this.toBlow) {
         int l = blockpos.getX() - i;
         int i1 = blockpos.getY() - j;
         int j1 = blockpos.getZ() - k;
         p_148840_1_.writeByte(l);
         p_148840_1_.writeByte(i1);
         p_148840_1_.writeByte(j1);
      }

      p_148840_1_.writeFloat(this.knockbackX);
      p_148840_1_.writeFloat(this.knockbackY);
      p_148840_1_.writeFloat(this.knockbackZ);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleExplosion(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getKnockbackX() {
      return this.knockbackX;
   }

   @OnlyIn(Dist.CLIENT)
   public float getKnockbackY() {
      return this.knockbackY;
   }

   @OnlyIn(Dist.CLIENT)
   public float getKnockbackZ() {
      return this.knockbackZ;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPower() {
      return this.power;
   }

   @OnlyIn(Dist.CLIENT)
   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }
}
