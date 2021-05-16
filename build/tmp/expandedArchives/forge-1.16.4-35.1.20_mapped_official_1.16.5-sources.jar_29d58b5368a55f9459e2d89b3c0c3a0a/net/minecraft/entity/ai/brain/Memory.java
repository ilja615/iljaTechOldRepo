package net.minecraft.entity.ai.brain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class Memory<T> {
   private final T value;
   private long timeToLive;

   public Memory(T p_i231551_1_, long p_i231551_2_) {
      this.value = p_i231551_1_;
      this.timeToLive = p_i231551_2_;
   }

   public void tick() {
      if (this.canExpire()) {
         --this.timeToLive;
      }

   }

   public static <T> Memory<T> of(T p_234068_0_) {
      return new Memory<>(p_234068_0_, Long.MAX_VALUE);
   }

   public static <T> Memory<T> of(T p_234069_0_, long p_234069_1_) {
      return new Memory<>(p_234069_0_, p_234069_1_);
   }

   public T getValue() {
      return this.value;
   }

   public boolean hasExpired() {
      return this.timeToLive <= 0L;
   }

   public String toString() {
      return this.value.toString() + (this.canExpire() ? " (ttl: " + this.timeToLive + ")" : "");
   }

   public boolean canExpire() {
      return this.timeToLive != Long.MAX_VALUE;
   }

   public static <T> Codec<Memory<T>> codec(Codec<T> p_234066_0_) {
      return RecordCodecBuilder.create((p_234067_1_) -> {
         return p_234067_1_.group(p_234066_0_.fieldOf("value").forGetter((p_234071_0_) -> {
            return p_234071_0_.value;
         }), Codec.LONG.optionalFieldOf("ttl").forGetter((p_234065_0_) -> {
            return p_234065_0_.canExpire() ? Optional.of(p_234065_0_.timeToLive) : Optional.empty();
         })).apply(p_234067_1_, (p_234070_0_, p_234070_1_) -> {
            return new Memory<>(p_234070_0_, p_234070_1_.orElse(Long.MAX_VALUE));
         });
      });
   }
}
