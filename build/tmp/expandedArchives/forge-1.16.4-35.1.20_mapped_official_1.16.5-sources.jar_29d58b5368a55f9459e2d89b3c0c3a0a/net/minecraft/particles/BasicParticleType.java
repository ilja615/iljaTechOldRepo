package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;

public class BasicParticleType extends ParticleType<BasicParticleType> implements IParticleData {
   private static final IParticleData.IDeserializer<BasicParticleType> DESERIALIZER = new IParticleData.IDeserializer<BasicParticleType>() {
      public BasicParticleType fromCommand(ParticleType<BasicParticleType> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         return (BasicParticleType)p_197544_1_;
      }

      public BasicParticleType fromNetwork(ParticleType<BasicParticleType> p_197543_1_, PacketBuffer p_197543_2_) {
         return (BasicParticleType)p_197543_1_;
      }
   };
   private final Codec<BasicParticleType> codec = Codec.unit(this::getType);

   public BasicParticleType(boolean p_i50791_1_) {
      super(p_i50791_1_, DESERIALIZER);
   }

   public BasicParticleType getType() {
      return this;
   }

   public Codec<BasicParticleType> codec() {
      return this.codec;
   }

   public void writeToNetwork(PacketBuffer p_197553_1_) {
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this).toString();
   }
}
