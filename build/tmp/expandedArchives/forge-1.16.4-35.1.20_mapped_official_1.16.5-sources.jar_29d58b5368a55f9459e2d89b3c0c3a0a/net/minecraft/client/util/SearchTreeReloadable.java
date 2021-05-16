package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
public class SearchTreeReloadable<T> implements IMutableSearchTree<T> {
   protected SuffixArray<T> namespaceTree = new SuffixArray<>();
   protected SuffixArray<T> pathTree = new SuffixArray<>();
   private final Function<T, Stream<ResourceLocation>> idGetter;
   private final List<T> contents = Lists.newArrayList();
   private final Object2IntMap<T> orderT = new Object2IntOpenHashMap<>();

   public SearchTreeReloadable(Function<T, Stream<ResourceLocation>> p_i50896_1_) {
      this.idGetter = p_i50896_1_;
   }

   public void refresh() {
      this.namespaceTree = new SuffixArray<>();
      this.pathTree = new SuffixArray<>();

      for(T t : this.contents) {
         this.index(t);
      }

      this.namespaceTree.generate();
      this.pathTree.generate();
   }

   public void add(T p_217872_1_) {
      this.orderT.put(p_217872_1_, this.contents.size());
      this.contents.add(p_217872_1_);
      this.index(p_217872_1_);
   }

   public void clear() {
      this.contents.clear();
      this.orderT.clear();
   }

   protected void index(T p_194042_1_) {
      this.idGetter.apply(p_194042_1_).forEach((p_217873_2_) -> {
         this.namespaceTree.add(p_194042_1_, p_217873_2_.getNamespace().toLowerCase(Locale.ROOT));
         this.pathTree.add(p_194042_1_, p_217873_2_.getPath().toLowerCase(Locale.ROOT));
      });
   }

   protected int comparePosition(T p_217874_1_, T p_217874_2_) {
      return Integer.compare(this.orderT.getInt(p_217874_1_), this.orderT.getInt(p_217874_2_));
   }

   public List<T> search(String p_194038_1_) {
      int i = p_194038_1_.indexOf(58);
      if (i == -1) {
         return this.pathTree.search(p_194038_1_);
      } else {
         List<T> list = this.namespaceTree.search(p_194038_1_.substring(0, i).trim());
         String s = p_194038_1_.substring(i + 1).trim();
         List<T> list1 = this.pathTree.search(s);
         return Lists.newArrayList(new SearchTreeReloadable.JoinedIterator<>(list.iterator(), list1.iterator(), this::comparePosition));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class JoinedIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> firstIterator;
      private final PeekingIterator<T> secondIterator;
      private final Comparator<T> orderT;

      public JoinedIterator(Iterator<T> p_i50270_1_, Iterator<T> p_i50270_2_, Comparator<T> p_i50270_3_) {
         this.firstIterator = Iterators.peekingIterator(p_i50270_1_);
         this.secondIterator = Iterators.peekingIterator(p_i50270_2_);
         this.orderT = p_i50270_3_;
      }

      protected T computeNext() {
         while(this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
            int i = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if (i == 0) {
               this.secondIterator.next();
               return this.firstIterator.next();
            }

            if (i < 0) {
               this.firstIterator.next();
            } else {
               this.secondIterator.next();
            }
         }

         return this.endOfData();
      }
   }
}
