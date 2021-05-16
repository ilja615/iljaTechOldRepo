package net.minecraft.util.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DelegatingDynamicOps;

public class WorldGenSettingsExport<T> extends DelegatingDynamicOps<T> {
   private final DynamicRegistries registryHolder;

   public static <T> WorldGenSettingsExport<T> create(DynamicOps<T> p_240896_0_, DynamicRegistries p_240896_1_) {
      return new WorldGenSettingsExport<>(p_240896_0_, p_240896_1_);
   }

   private WorldGenSettingsExport(DynamicOps<T> p_i232591_1_, DynamicRegistries p_i232591_2_) {
      super(p_i232591_1_);
      this.registryHolder = p_i232591_2_;
   }

   protected <E> DataResult<T> encode(E p_241811_1_, T p_241811_2_, RegistryKey<? extends Registry<E>> p_241811_3_, Codec<E> p_241811_4_) {
      Optional<MutableRegistry<E>> optional = this.registryHolder.registry(p_241811_3_);
      if (optional.isPresent()) {
         MutableRegistry<E> mutableregistry = optional.get();
         Optional<RegistryKey<E>> optional1 = mutableregistry.getResourceKey(p_241811_1_);
         if (optional1.isPresent()) {
            RegistryKey<E> registrykey = optional1.get();
            return ResourceLocation.CODEC.encode(registrykey.location(), this.delegate, p_241811_2_);
         }
      }

      return p_241811_4_.encode(p_241811_1_, this, p_241811_2_);
   }
}
