package net.minecraft.util.palette;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HashMapPalette<T> implements IPalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final IntIdentityHashBiMap<T> values;
   private final IResizeCallback<T> resizeHandler;
   private final Function<CompoundNBT, T> reader;
   private final Function<T, CompoundNBT> writer;
   private final int bits;

   public HashMapPalette(ObjectIntIdentityMap<T> p_i48964_1_, int p_i48964_2_, IResizeCallback<T> p_i48964_3_, Function<CompoundNBT, T> p_i48964_4_, Function<T, CompoundNBT> p_i48964_5_) {
      this.registry = p_i48964_1_;
      this.bits = p_i48964_2_;
      this.resizeHandler = p_i48964_3_;
      this.reader = p_i48964_4_;
      this.writer = p_i48964_5_;
      this.values = new IntIdentityHashBiMap<>(1 << p_i48964_2_);
   }

   public int idFor(T p_186041_1_) {
      int i = this.values.getId(p_186041_1_);
      if (i == -1) {
         i = this.values.add(p_186041_1_);
         if (i >= 1 << this.bits) {
            i = this.resizeHandler.onResize(this.bits + 1, p_186041_1_);
         }
      }

      return i;
   }

   public boolean maybeHas(Predicate<T> p_230341_1_) {
      for(int i = 0; i < this.getSize(); ++i) {
         if (p_230341_1_.test(this.values.byId(i))) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public T valueFor(int p_186039_1_) {
      return this.values.byId(p_186039_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
      this.values.clear();
      int i = p_186038_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         this.values.add(this.registry.byId(p_186038_1_.readVarInt()));
      }

   }

   public void write(PacketBuffer p_186037_1_) {
      int i = this.getSize();
      p_186037_1_.writeVarInt(i);

      for(int j = 0; j < i; ++j) {
         p_186037_1_.writeVarInt(this.registry.getId(this.values.byId(j)));
      }

   }

   public int getSerializedSize() {
      int i = PacketBuffer.getVarIntSize(this.getSize());

      for(int j = 0; j < this.getSize(); ++j) {
         i += PacketBuffer.getVarIntSize(this.registry.getId(this.values.byId(j)));
      }

      return i;
   }

   public int getSize() {
      return this.values.size();
   }

   public void read(ListNBT p_196968_1_) {
      this.values.clear();

      for(int i = 0; i < p_196968_1_.size(); ++i) {
         this.values.add(this.reader.apply(p_196968_1_.getCompound(i)));
      }

   }

   public void write(ListNBT p_196969_1_) {
      for(int i = 0; i < this.getSize(); ++i) {
         p_196969_1_.add(this.writer.apply(this.values.byId(i)));
      }

   }
}
