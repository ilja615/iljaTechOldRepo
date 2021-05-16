package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnParticlePacket implements IPacket<IClientPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private float xDist;
   private float yDist;
   private float zDist;
   private float maxSpeed;
   private int count;
   private boolean overrideLimiter;
   private IParticleData particle;

   public SSpawnParticlePacket() {
   }

   public <T extends IParticleData> SSpawnParticlePacket(T p_i229960_1_, boolean p_i229960_2_, double p_i229960_3_, double p_i229960_5_, double p_i229960_7_, float p_i229960_9_, float p_i229960_10_, float p_i229960_11_, float p_i229960_12_, int p_i229960_13_) {
      this.particle = p_i229960_1_;
      this.overrideLimiter = p_i229960_2_;
      this.x = p_i229960_3_;
      this.y = p_i229960_5_;
      this.z = p_i229960_7_;
      this.xDist = p_i229960_9_;
      this.yDist = p_i229960_10_;
      this.zDist = p_i229960_11_;
      this.maxSpeed = p_i229960_12_;
      this.count = p_i229960_13_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      ParticleType<?> particletype = Registry.PARTICLE_TYPE.byId(p_148837_1_.readInt());
      if (particletype == null) {
         particletype = ParticleTypes.BARRIER;
      }

      this.overrideLimiter = p_148837_1_.readBoolean();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.xDist = p_148837_1_.readFloat();
      this.yDist = p_148837_1_.readFloat();
      this.zDist = p_148837_1_.readFloat();
      this.maxSpeed = p_148837_1_.readFloat();
      this.count = p_148837_1_.readInt();
      this.particle = this.readParticle(p_148837_1_, particletype);
   }

   private <T extends IParticleData> T readParticle(PacketBuffer p_199855_1_, ParticleType<T> p_199855_2_) {
      return p_199855_2_.getDeserializer().fromNetwork(p_199855_2_, p_199855_1_);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
      p_148840_1_.writeBoolean(this.overrideLimiter);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeFloat(this.xDist);
      p_148840_1_.writeFloat(this.yDist);
      p_148840_1_.writeFloat(this.zDist);
      p_148840_1_.writeFloat(this.maxSpeed);
      p_148840_1_.writeInt(this.count);
      this.particle.writeToNetwork(p_148840_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOverrideLimiter() {
      return this.overrideLimiter;
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
   public float getXDist() {
      return this.xDist;
   }

   @OnlyIn(Dist.CLIENT)
   public float getYDist() {
      return this.yDist;
   }

   @OnlyIn(Dist.CLIENT)
   public float getZDist() {
      return this.zDist;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMaxSpeed() {
      return this.maxSpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public int getCount() {
      return this.count;
   }

   @OnlyIn(Dist.CLIENT)
   public IParticleData getParticle() {
      return this.particle;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleParticleEvent(this);
   }
}
