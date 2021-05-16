package net.minecraft.util.palette;

import java.util.function.Predicate;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IdentityPalette<T> implements IPalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T defaultValue;

   public IdentityPalette(ObjectIntIdentityMap<T> p_i48965_1_, T p_i48965_2_) {
      this.registry = p_i48965_1_;
      this.defaultValue = p_i48965_2_;
   }

   public int idFor(T p_186041_1_) {
      int i = this.registry.getId(p_186041_1_);
      return i == -1 ? 0 : i;
   }

   public boolean maybeHas(Predicate<T> p_230341_1_) {
      return true;
   }

   public T valueFor(int p_186039_1_) {
      T t = this.registry.byId(p_186039_1_);
      return (T)(t == null ? this.defaultValue : t);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
   }

   public void write(PacketBuffer p_186037_1_) {
   }

   public int getSerializedSize() {
      return PacketBuffer.getVarIntSize(0);
   }

   public void read(ListNBT p_196968_1_) {
   }
}
