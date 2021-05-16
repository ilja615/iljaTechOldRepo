package net.minecraft.network.datasync;

import net.minecraft.network.PacketBuffer;

public interface IDataSerializer<T> {
   void write(PacketBuffer p_187160_1_, T p_187160_2_);

   T read(PacketBuffer p_187159_1_);

   default DataParameter<T> createAccessor(int p_187161_1_) {
      return new DataParameter<>(p_187161_1_, this);
   }

   T copy(T p_192717_1_);
}
