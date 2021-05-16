package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReuseableStream<T> {
   private final List<T> cache = Lists.newArrayList();
   private final Spliterator<T> source;

   public ReuseableStream(Stream<T> p_i49816_1_) {
      this.source = p_i49816_1_.spliterator();
   }

   public Stream<T> getStream() {
      return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
         private int index;

         public boolean tryAdvance(Consumer<? super T> p_tryAdvance_1_) {
            while(true) {
               if (this.index >= ReuseableStream.this.cache.size()) {
                  if (ReuseableStream.this.source.tryAdvance(ReuseableStream.this.cache::add)) {
                     continue;
                  }

                  return false;
               }

               p_tryAdvance_1_.accept(ReuseableStream.this.cache.get(this.index++));
               return true;
            }
         }
      }, false);
   }
}
