package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SortedArraySet<T> extends AbstractSet<T> {
   private final Comparator<T> comparator;
   private T[] contents;
   private int size;

   private SortedArraySet(int p_i225697_1_, Comparator<T> p_i225697_2_) {
      this.comparator = p_i225697_2_;
      if (p_i225697_1_ < 0) {
         throw new IllegalArgumentException("Initial capacity (" + p_i225697_1_ + ") is negative");
      } else {
         this.contents = (T[])castRawArray(new Object[p_i225697_1_]);
      }
   }

   public static <T extends Comparable<T>> SortedArraySet<T> create(int p_226172_0_) {
      return new SortedArraySet<>(p_226172_0_, Comparator.<T>naturalOrder());
   }

   private static <T> T[] castRawArray(Object[] p_226177_0_) {
      return (T[])(p_226177_0_);
   }

   private int findIndex(T p_226182_1_) {
      return Arrays.binarySearch(this.contents, 0, this.size, p_226182_1_, this.comparator);
   }

   private static int getInsertionPosition(int p_226179_0_) {
      return -p_226179_0_ - 1;
   }

   public boolean add(T p_add_1_) {
      int i = this.findIndex(p_add_1_);
      if (i >= 0) {
         return false;
      } else {
         int j = getInsertionPosition(i);
         this.addInternal(p_add_1_, j);
         return true;
      }
   }

   private void grow(int p_226181_1_) {
      if (p_226181_1_ > this.contents.length) {
         if (this.contents != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            p_226181_1_ = (int)Math.max(Math.min((long)this.contents.length + (long)(this.contents.length >> 1), 2147483639L), (long)p_226181_1_);
         } else if (p_226181_1_ < 10) {
            p_226181_1_ = 10;
         }

         Object[] aobject = new Object[p_226181_1_];
         System.arraycopy(this.contents, 0, aobject, 0, this.size);
         this.contents = (T[])castRawArray(aobject);
      }
   }

   private void addInternal(T p_226176_1_, int p_226176_2_) {
      this.grow(this.size + 1);
      if (p_226176_2_ != this.size) {
         System.arraycopy(this.contents, p_226176_2_, this.contents, p_226176_2_ + 1, this.size - p_226176_2_);
      }

      this.contents[p_226176_2_] = p_226176_1_;
      ++this.size;
   }

   private void removeInternal(int p_226183_1_) {
      --this.size;
      if (p_226183_1_ != this.size) {
         System.arraycopy(this.contents, p_226183_1_ + 1, this.contents, p_226183_1_, this.size - p_226183_1_);
      }

      this.contents[this.size] = null;
   }

   private T getInternal(int p_226184_1_) {
      return this.contents[p_226184_1_];
   }

   public T addOrGet(T p_226175_1_) {
      int i = this.findIndex(p_226175_1_);
      if (i >= 0) {
         return this.getInternal(i);
      } else {
         this.addInternal(p_226175_1_, getInsertionPosition(i));
         return p_226175_1_;
      }
   }

   public boolean remove(Object p_remove_1_) {
      int i = this.findIndex((T)p_remove_1_);
      if (i >= 0) {
         this.removeInternal(i);
         return true;
      } else {
         return false;
      }
   }

   public T first() {
      return this.getInternal(0);
   }

   public boolean contains(Object p_contains_1_) {
      int i = this.findIndex((T)p_contains_1_);
      return i >= 0;
   }

   public Iterator<T> iterator() {
      return new SortedArraySet.Itr();
   }

   public int size() {
      return this.size;
   }

   public Object[] toArray() {
      return this.contents.clone();
   }

   public <U> U[] toArray(U[] p_toArray_1_) {
      if (p_toArray_1_.length < this.size) {
         return (U[])(Arrays.copyOf(this.contents, this.size, p_toArray_1_.getClass()));
      } else {
         System.arraycopy(this.contents, 0, p_toArray_1_, 0, this.size);
         if (p_toArray_1_.length > this.size) {
            p_toArray_1_[this.size] = null;
         }

         return p_toArray_1_;
      }
   }

   public void clear() {
      Arrays.fill(this.contents, 0, this.size, (Object)null);
      this.size = 0;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         if (p_equals_1_ instanceof SortedArraySet) {
            SortedArraySet<?> sortedarrayset = (SortedArraySet)p_equals_1_;
            if (this.comparator.equals(sortedarrayset.comparator)) {
               return this.size == sortedarrayset.size && Arrays.equals(this.contents, sortedarrayset.contents);
            }
         }

         return super.equals(p_equals_1_);
      }
   }

   class Itr implements Iterator<T> {
      private int index;
      private int last = -1;

      private Itr() {
      }

      public boolean hasNext() {
         return this.index < SortedArraySet.this.size;
      }

      public T next() {
         if (this.index >= SortedArraySet.this.size) {
            throw new NoSuchElementException();
         } else {
            this.last = this.index++;
            return SortedArraySet.this.contents[this.last];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            SortedArraySet.this.removeInternal(this.last);
            --this.index;
            this.last = -1;
         }
      }
   }
}
