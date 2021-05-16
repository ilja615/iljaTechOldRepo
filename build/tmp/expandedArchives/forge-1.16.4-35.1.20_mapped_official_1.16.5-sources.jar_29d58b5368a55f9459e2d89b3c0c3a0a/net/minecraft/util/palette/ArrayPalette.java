package net.minecraft.util.palette;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArrayPalette<T> implements IPalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T[] values;
   private final IResizeCallback<T> resizeHandler;
   private final Function<CompoundNBT, T> reader;
   private final int bits;
   private int size;

   public ArrayPalette(ObjectIntIdentityMap<T> p_i48962_1_, int p_i48962_2_, IResizeCallback<T> p_i48962_3_, Function<CompoundNBT, T> p_i48962_4_) {
      this.registry = p_i48962_1_;
      this.values = (T[])(new Object[1 << p_i48962_2_]);
      this.bits = p_i48962_2_;
      this.resizeHandler = p_i48962_3_;
      this.reader = p_i48962_4_;
   }

   public int idFor(T p_186041_1_) {
      for(int i = 0; i < this.size; ++i) {
         if (this.values[i] == p_186041_1_) {
            return i;
         }
      }

      int j = this.size;
      if (j < this.values.length) {
         this.values[j] = p_186041_1_;
         ++this.size;
         return j;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, p_186041_1_);
      }
   }

   public boolean maybeHas(Predicate<T> p_230341_1_) {
      for(int i = 0; i < this.size; ++i) {
         if (p_230341_1_.test(this.values[i])) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public T valueFor(int p_186039_1_) {
      return (T)(p_186039_1_ >= 0 && p_186039_1_ < this.size ? this.values[p_186039_1_] : null);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
      this.size = p_186038_1_.readVarInt();

      for(int i = 0; i < this.size; ++i) {
         this.values[i] = this.registry.byId(p_186038_1_.readVarInt());
      }

   }

   public void write(PacketBuffer p_186037_1_) {
      p_186037_1_.writeVarInt(this.size);

      for(int i = 0; i < this.size; ++i) {
         p_186037_1_.writeVarInt(this.registry.getId(this.values[i]));
      }

   }

   public int getSerializedSize() {
      int i = PacketBuffer.getVarIntSize(this.getSize());

      for(int j = 0; j < this.getSize(); ++j) {
         i += PacketBuffer.getVarIntSize(this.registry.getId(this.values[j]));
      }

      return i;
   }

   public int getSize() {
      return this.size;
   }

   public void read(ListNBT p_196968_1_) {
      for(int i = 0; i < p_196968_1_.size(); ++i) {
         this.values[i] = this.reader.apply(p_196968_1_.getCompound(i));
      }

      this.size = p_196968_1_.size();
   }
}
