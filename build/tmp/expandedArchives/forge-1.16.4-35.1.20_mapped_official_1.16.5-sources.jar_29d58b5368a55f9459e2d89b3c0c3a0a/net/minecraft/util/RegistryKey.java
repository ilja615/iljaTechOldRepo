package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class RegistryKey<T> implements Comparable<RegistryKey<?>> {
   private static final Map<String, RegistryKey<?>> VALUES = Collections.synchronizedMap(Maps.newIdentityHashMap());
   private final ResourceLocation registryName;
   private final ResourceLocation location;

   public static <T> RegistryKey<T> create(RegistryKey<? extends Registry<T>> p_240903_0_, ResourceLocation p_240903_1_) {
      return create(p_240903_0_.location, p_240903_1_);
   }

   public static <T> RegistryKey<Registry<T>> createRegistryKey(ResourceLocation p_240904_0_) {
      return create(Registry.ROOT_REGISTRY_NAME, p_240904_0_);
   }

   private static <T> RegistryKey<T> create(ResourceLocation p_240905_0_, ResourceLocation p_240905_1_) {
      String s = (p_240905_0_ + ":" + p_240905_1_).intern();
      return (RegistryKey<T>)VALUES.computeIfAbsent(s, (p_240906_2_) -> {
         return new RegistryKey(p_240905_0_, p_240905_1_);
      });
   }

   private RegistryKey(ResourceLocation p_i232592_1_, ResourceLocation p_i232592_2_) {
      this.registryName = p_i232592_1_;
      this.location = p_i232592_2_;
   }

   public String toString() {
      return "ResourceKey[" + this.registryName + " / " + this.location + ']';
   }

   public boolean isFor(RegistryKey<? extends Registry<?>> p_244356_1_) {
      return this.registryName.equals(p_244356_1_.location());
   }

   public ResourceLocation location() {
      return this.location;
   }

   public static <T> Function<ResourceLocation, RegistryKey<T>> elementKey(RegistryKey<? extends Registry<T>> p_240902_0_) {
      return (p_240907_1_) -> {
         return create(p_240902_0_, p_240907_1_);
      };
   }

   public ResourceLocation getRegistryName() {
      return this.registryName;
   }

   @Override
   public int compareTo(RegistryKey<?> o) {
      int ret = this.getRegistryName().compareTo(o.getRegistryName());
      if (ret == 0) ret = this.location().compareTo(o.location());
      return ret;
   }
}
