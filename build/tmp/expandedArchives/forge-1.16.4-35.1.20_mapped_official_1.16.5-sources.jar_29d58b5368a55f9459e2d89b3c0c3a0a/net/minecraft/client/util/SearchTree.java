package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTree<T> extends SearchTreeReloadable<T> {
   protected SuffixArray<T> tree = new SuffixArray<>();
   private final Function<T, Stream<String>> filler;

   public SearchTree(Function<T, Stream<String>> p_i47612_1_, Function<T, Stream<ResourceLocation>> p_i47612_2_) {
      super(p_i47612_2_);
      this.filler = p_i47612_1_;
   }

   public void refresh() {
      this.tree = new SuffixArray<>();
      super.refresh();
      this.tree.generate();
   }

   protected void index(T p_194042_1_) {
      super.index(p_194042_1_);
      this.filler.apply(p_194042_1_).forEach((p_217880_2_) -> {
         this.tree.add(p_194042_1_, p_217880_2_.toLowerCase(Locale.ROOT));
      });
   }

   public List<T> search(String p_194038_1_) {
      int i = p_194038_1_.indexOf(58);
      if (i < 0) {
         return this.tree.search(p_194038_1_);
      } else {
         List<T> list = this.namespaceTree.search(p_194038_1_.substring(0, i).trim());
         String s = p_194038_1_.substring(i + 1).trim();
         List<T> list1 = this.pathTree.search(s);
         List<T> list2 = this.tree.search(s);
         return Lists.newArrayList(new SearchTreeReloadable.JoinedIterator<>(list.iterator(), new SearchTree.MergingIterator<>(list1.iterator(), list2.iterator(), this::comparePosition), this::comparePosition));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class MergingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> firstIterator;
      private final PeekingIterator<T> secondIterator;
      private final Comparator<T> orderT;

      public MergingIterator(Iterator<T> p_i49977_1_, Iterator<T> p_i49977_2_, Comparator<T> p_i49977_3_) {
         this.firstIterator = Iterators.peekingIterator(p_i49977_1_);
         this.secondIterator = Iterators.peekingIterator(p_i49977_2_);
         this.orderT = p_i49977_3_;
      }

      protected T computeNext() {
         boolean flag = !this.firstIterator.hasNext();
         boolean flag1 = !this.secondIterator.hasNext();
         if (flag && flag1) {
            return this.endOfData();
         } else if (flag) {
            return this.secondIterator.next();
         } else if (flag1) {
            return this.firstIterator.next();
         } else {
            int i = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if (i == 0) {
               this.secondIterator.next();
            }

            return (T)(i <= 0 ? this.firstIterator.next() : this.secondIterator.next());
         }
      }
   }
}
