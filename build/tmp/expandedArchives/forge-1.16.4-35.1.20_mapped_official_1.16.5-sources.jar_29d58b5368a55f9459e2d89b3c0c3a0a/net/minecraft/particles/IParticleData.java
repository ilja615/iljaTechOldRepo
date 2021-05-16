package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;

public interface IParticleData {
   ParticleType<?> getType();

   void writeToNetwork(PacketBuffer p_197553_1_);

   String writeToString();

   @Deprecated
   public interface IDeserializer<T extends IParticleData> {
      T fromCommand(ParticleType<T> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException;

      T fromNetwork(ParticleType<T> p_197543_1_, PacketBuffer p_197543_2_);
   }
}
