package net.minecraft.util.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.RegistryKey;

public final class SimpleRegistryCodec<E> implements Codec<SimpleRegistry<E>> {
   private final Codec<SimpleRegistry<E>> directCodec;
   private final RegistryKey<? extends Registry<E>> registryKey;
   private final Codec<E> elementCodec;

   public static <E> SimpleRegistryCodec<E> create(RegistryKey<? extends Registry<E>> p_241793_0_, Lifecycle p_241793_1_, Codec<E> p_241793_2_) {
      return new SimpleRegistryCodec<>(p_241793_0_, p_241793_1_, p_241793_2_);
   }

   private SimpleRegistryCodec(RegistryKey<? extends Registry<E>> p_i232587_1_, Lifecycle p_i232587_2_, Codec<E> p_i232587_3_) {
      this.directCodec = SimpleRegistry.directCodec(p_i232587_1_, p_i232587_2_, p_i232587_3_);
      this.registryKey = p_i232587_1_;
      this.elementCodec = p_i232587_3_;
   }

   public <T> DataResult<T> encode(SimpleRegistry<E> p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_) {
      return this.directCodec.encode(p_encode_1_, p_encode_2_, p_encode_3_);
   }

   public <T> DataResult<Pair<SimpleRegistry<E>, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_) {
      DataResult<Pair<SimpleRegistry<E>, T>> dataresult = this.directCodec.decode(p_decode_1_, p_decode_2_);
      return p_decode_1_ instanceof WorldSettingsImport ? dataresult.flatMap((p_240862_2_) -> {
         return ((WorldSettingsImport)p_decode_1_).decodeElements(p_240862_2_.getFirst(), this.registryKey, this.elementCodec).map((p_240861_1_) -> {
            return Pair.of(p_240861_1_, (T)p_240862_2_.getSecond());
         });
      }) : dataresult;
   }

   public String toString() {
      return "RegistryDataPackCodec[" + this.directCodec + " " + this.registryKey + " " + this.elementCodec + "]";
   }
}
