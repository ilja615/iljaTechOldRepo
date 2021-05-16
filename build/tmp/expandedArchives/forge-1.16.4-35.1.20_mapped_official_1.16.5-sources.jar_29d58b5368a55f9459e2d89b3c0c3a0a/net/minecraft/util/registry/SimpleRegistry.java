package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T> {
   protected static final Logger LOGGER = LogManager.getLogger();
   private final ObjectList<T> byId = new ObjectArrayList<>(256);
   private final Object2IntMap<T> toId = new Object2IntOpenCustomHashMap<>(Util.identityStrategy());
   private final BiMap<ResourceLocation, T> storage;
   private final BiMap<RegistryKey<T>, T> keyStorage;
   private final Map<T, Lifecycle> lifecycles;
   private Lifecycle elementsLifecycle;
   protected Object[] randomCache;
   private int nextId;

   public SimpleRegistry(RegistryKey<? extends Registry<T>> p_i232509_1_, Lifecycle p_i232509_2_) {
      super(p_i232509_1_, p_i232509_2_);
      this.toId.defaultReturnValue(-1);
      this.storage = HashBiMap.create();
      this.keyStorage = HashBiMap.create();
      this.lifecycles = Maps.newIdentityHashMap();
      this.elementsLifecycle = p_i232509_2_;
   }

   public static <T> MapCodec<SimpleRegistry.Entry<T>> withNameAndId(RegistryKey<? extends Registry<T>> p_243541_0_, MapCodec<T> p_243541_1_) {
      return RecordCodecBuilder.mapCodec((p_243542_2_) -> {
         return p_243542_2_.group(ResourceLocation.CODEC.xmap(RegistryKey.elementKey(p_243541_0_), RegistryKey::location).fieldOf("name").forGetter((p_243545_0_) -> {
            return p_243545_0_.key;
         }), Codec.INT.fieldOf("id").forGetter((p_243543_0_) -> {
            return p_243543_0_.id;
         }), p_243541_1_.forGetter((p_243538_0_) -> {
            return p_243538_0_.value;
         })).apply(p_243542_2_, SimpleRegistry.Entry::new);
      });
   }

   public <V extends T> V registerMapping(int p_218382_1_, RegistryKey<T> p_218382_2_, V p_218382_3_, Lifecycle p_218382_4_) {
      return this.registerMapping(p_218382_1_, p_218382_2_, p_218382_3_, p_218382_4_, true);
   }

   private <V extends T> V registerMapping(int p_243537_1_, RegistryKey<T> p_243537_2_, V p_243537_3_, Lifecycle p_243537_4_, boolean p_243537_5_) {
      Validate.notNull(p_243537_2_);
      Validate.notNull((T)p_243537_3_);
      this.byId.size(Math.max(this.byId.size(), p_243537_1_ + 1));
      this.byId.set(p_243537_1_, p_243537_3_);
      this.toId.put((T)p_243537_3_, p_243537_1_);
      this.randomCache = null;
      if (p_243537_5_ && this.keyStorage.containsKey(p_243537_2_)) {
         LOGGER.debug("Adding duplicate key '{}' to registry", (Object)p_243537_2_);
      }

      if (this.storage.containsValue(p_243537_3_)) {
         LOGGER.error("Adding duplicate value '{}' to registry", p_243537_3_);
      }

      this.storage.put(p_243537_2_.location(), (T)p_243537_3_);
      this.keyStorage.put(p_243537_2_, (T)p_243537_3_);
      this.lifecycles.put((T)p_243537_3_, p_243537_4_);
      this.elementsLifecycle = this.elementsLifecycle.add(p_243537_4_);
      if (this.nextId <= p_243537_1_) {
         this.nextId = p_243537_1_ + 1;
      }

      return p_243537_3_;
   }

   public <V extends T> V register(RegistryKey<T> p_218381_1_, V p_218381_2_, Lifecycle p_218381_3_) {
      return this.registerMapping(this.nextId, p_218381_1_, p_218381_2_, p_218381_3_);
   }

   public <V extends T> V registerOrOverride(OptionalInt p_241874_1_, RegistryKey<T> p_241874_2_, V p_241874_3_, Lifecycle p_241874_4_) {
      Validate.notNull(p_241874_2_);
      Validate.notNull((T)p_241874_3_);
      T t = this.keyStorage.get(p_241874_2_);
      int i;
      if (t == null) {
         i = p_241874_1_.isPresent() ? p_241874_1_.getAsInt() : this.nextId;
      } else {
         i = this.toId.getInt(t);
         if (p_241874_1_.isPresent() && p_241874_1_.getAsInt() != i) {
            throw new IllegalStateException("ID mismatch");
         }

         this.toId.removeInt(t);
         this.lifecycles.remove(t);
      }

      return this.registerMapping(i, p_241874_2_, p_241874_3_, p_241874_4_, false);
   }

   @Nullable
   public ResourceLocation getKey(T p_177774_1_) {
      return this.storage.inverse().get(p_177774_1_);
   }

   public Optional<RegistryKey<T>> getResourceKey(T p_230519_1_) {
      return Optional.ofNullable(this.keyStorage.inverse().get(p_230519_1_));
   }

   public int getId(@Nullable T p_148757_1_) {
      return this.toId.getInt(p_148757_1_);
   }

   @Nullable
   public T get(@Nullable RegistryKey<T> p_230516_1_) {
      return this.keyStorage.get(p_230516_1_);
   }

   @Nullable
   public T byId(int p_148745_1_) {
      return (T)(p_148745_1_ >= 0 && p_148745_1_ < this.byId.size() ? this.byId.get(p_148745_1_) : null);
   }

   public Lifecycle lifecycle(T p_241876_1_) {
      return this.lifecycles.get(p_241876_1_);
   }

   public Lifecycle elementsLifecycle() {
      return this.elementsLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.byId.iterator(), Objects::nonNull);
   }

   @Nullable
   public T get(@Nullable ResourceLocation p_82594_1_) {
      return this.storage.get(p_82594_1_);
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.storage.keySet());
   }

   public Set<Map.Entry<RegistryKey<T>, T>> entrySet() {
      return Collections.unmodifiableMap(this.keyStorage).entrySet();
   }

   @Nullable
   public T getRandom(Random p_186801_1_) {
      if (this.randomCache == null) {
         Collection<?> collection = this.storage.values();
         if (collection.isEmpty()) {
            return (T)null;
         }

         this.randomCache = collection.toArray(new Object[collection.size()]);
      }

      return Util.getRandom((T[])this.randomCache, p_186801_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean containsKey(ResourceLocation p_212607_1_) {
      return this.storage.containsKey(p_212607_1_);
   }

   public static <T> Codec<SimpleRegistry<T>> networkCodec(RegistryKey<? extends Registry<T>> p_243539_0_, Lifecycle p_243539_1_, Codec<T> p_243539_2_) {
      return withNameAndId(p_243539_0_, p_243539_2_.fieldOf("element")).codec().listOf().xmap((p_243540_2_) -> {
         SimpleRegistry<T> simpleregistry = new SimpleRegistry<>(p_243539_0_, p_243539_1_);

         for(SimpleRegistry.Entry<T> entry : p_243540_2_) {
            simpleregistry.registerMapping(entry.id, entry.key, entry.value, p_243539_1_);
         }

         return simpleregistry;
      }, (p_243544_0_) -> {
         Builder<SimpleRegistry.Entry<T>> builder = ImmutableList.builder();

         for(T t : p_243544_0_) {
            builder.add(new SimpleRegistry.Entry<>(p_243544_0_.getResourceKey(t).get(), p_243544_0_.getId(t), t));
         }

         return builder.build();
      });
   }

   public static <T> Codec<SimpleRegistry<T>> dataPackCodec(RegistryKey<? extends Registry<T>> p_241744_0_, Lifecycle p_241744_1_, Codec<T> p_241744_2_) {
      return SimpleRegistryCodec.create(p_241744_0_, p_241744_1_, p_241744_2_);
   }

   public static <T> Codec<SimpleRegistry<T>> directCodec(RegistryKey<? extends Registry<T>> p_241745_0_, Lifecycle p_241745_1_, Codec<T> p_241745_2_) {
      return Codec.unboundedMap(ResourceLocation.CODEC.xmap(RegistryKey.elementKey(p_241745_0_), RegistryKey::location), p_241745_2_).xmap((p_239656_2_) -> {
         SimpleRegistry<T> simpleregistry = new SimpleRegistry<>(p_241745_0_, p_241745_1_);
         p_239656_2_.forEach((p_239653_2_, p_239653_3_) -> {
            simpleregistry.register(p_239653_2_, p_239653_3_, p_241745_1_);
         });
         return simpleregistry;
      }, (p_239651_0_) -> {
         return ImmutableMap.copyOf(p_239651_0_.keyStorage);
      });
   }

   public static class Entry<T> {
      public final RegistryKey<T> key;
      public final int id;
      public final T value;

      public Entry(RegistryKey<T> p_i242072_1_, int p_i242072_2_, T p_i242072_3_) {
         this.key = p_i242072_1_;
         this.id = p_i242072_2_;
         this.value = p_i242072_3_;
      }
   }
}
