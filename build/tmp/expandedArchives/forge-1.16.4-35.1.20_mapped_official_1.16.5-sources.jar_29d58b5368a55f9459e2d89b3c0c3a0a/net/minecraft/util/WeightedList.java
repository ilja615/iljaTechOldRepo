package net.minecraft.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
   protected final List<WeightedList.Entry<U>> entries;
   private final Random random = new Random();

   public WeightedList() {
      this(Lists.newArrayList());
   }

   private WeightedList(List<WeightedList.Entry<U>> p_i231541_1_) {
      this.entries = Lists.newArrayList(p_i231541_1_);
   }

   public static <U> Codec<WeightedList<U>> codec(Codec<U> p_234002_0_) {
      return WeightedList.Entry.<U>codec(p_234002_0_).listOf().xmap(WeightedList::new, (p_234001_0_) -> {
         return p_234001_0_.entries;
      });
   }

   public WeightedList<U> add(U p_226313_1_, int p_226313_2_) {
      this.entries.add(new WeightedList.Entry(p_226313_1_, p_226313_2_));
      return this;
   }

   public WeightedList<U> shuffle() {
      return this.shuffle(this.random);
   }

   public WeightedList<U> shuffle(Random p_226314_1_) {
      this.entries.forEach((p_234004_1_) -> {
         p_234004_1_.setRandom(p_226314_1_.nextFloat());
      });
      this.entries.sort(Comparator.comparingDouble((p_234003_0_) -> {
         return p_234003_0_.getRandWeight();
      }));
      return this;
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public Stream<U> stream() {
      return this.entries.stream().map(WeightedList.Entry::getData);
   }

   public U getOne(Random p_226318_1_) {
      return this.shuffle(p_226318_1_).stream().findFirst().orElseThrow(RuntimeException::new);
   }

   public String toString() {
      return "WeightedList[" + this.entries + "]";
   }

   public static class Entry<T> {
      private final T data;
      private final int weight;
      private double randWeight;

      private Entry(T p_i231542_1_, int p_i231542_2_) {
         this.weight = p_i231542_2_;
         this.data = p_i231542_1_;
      }

      private double getRandWeight() {
         return this.randWeight;
      }

      private void setRandom(float p_220648_1_) {
         this.randWeight = -Math.pow((double)p_220648_1_, (double)(1.0F / (float)this.weight));
      }

      public T getData() {
         return this.data;
      }

      public String toString() {
         return "" + this.weight + ":" + this.data;
      }

      public static <E> Codec<WeightedList.Entry<E>> codec(final Codec<E> p_234008_0_) {
         return new Codec<WeightedList.Entry<E>>() {
            public <T> DataResult<Pair<WeightedList.Entry<E>, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_) {
               Dynamic<T> dynamic = new Dynamic<>(p_decode_1_, p_decode_2_);
               return dynamic.get("data").flatMap(p_234008_0_::parse).map((p_234012_1_) -> {
                  return new WeightedList.Entry(p_234012_1_, dynamic.get("weight").asInt(1));
               }).map((p_234013_1_) -> {
                  return Pair.of(p_234013_1_, p_decode_1_.empty());
               });
            }

            public <T> DataResult<T> encode(WeightedList.Entry<E> p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_) {
               return p_encode_2_.mapBuilder().add("weight", p_encode_2_.createInt(p_encode_1_.weight)).add("data", p_234008_0_.encodeStart(p_encode_2_, p_encode_1_.data)).build(p_encode_3_);
            }
         };
      }
   }
}
