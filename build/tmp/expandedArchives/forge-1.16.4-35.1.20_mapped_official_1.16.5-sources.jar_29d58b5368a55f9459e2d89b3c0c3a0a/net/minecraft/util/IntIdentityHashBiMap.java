package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class IntIdentityHashBiMap<K> implements IObjectIntIterable<K> {
   private static final Object EMPTY_SLOT = null;
   private K[] keys;
   private int[] values;
   private K[] byId;
   private int nextId;
   private int size;

   public IntIdentityHashBiMap(int p_i46830_1_) {
      p_i46830_1_ = (int)((float)p_i46830_1_ / 0.8F);
      this.keys = (K[])(new Object[p_i46830_1_]);
      this.values = new int[p_i46830_1_];
      this.byId = (K[])(new Object[p_i46830_1_]);
   }

   public int getId(@Nullable K p_148757_1_) {
      return this.getValue(this.indexOf(p_148757_1_, this.hash(p_148757_1_)));
   }

   @Nullable
   public K byId(int p_148745_1_) {
      return (K)(p_148745_1_ >= 0 && p_148745_1_ < this.byId.length ? this.byId[p_148745_1_] : null);
   }

   private int getValue(int p_186805_1_) {
      return p_186805_1_ == -1 ? -1 : this.values[p_186805_1_];
   }

   public int add(K p_186808_1_) {
      int i = this.nextId();
      this.addMapping(p_186808_1_, i);
      return i;
   }

   private int nextId() {
      while(this.nextId < this.byId.length && this.byId[this.nextId] != null) {
         ++this.nextId;
      }

      return this.nextId;
   }

   private void grow(int p_186807_1_) {
      K[] ak = this.keys;
      int[] aint = this.values;
      this.keys = (K[])(new Object[p_186807_1_]);
      this.values = new int[p_186807_1_];
      this.byId = (K[])(new Object[p_186807_1_]);
      this.nextId = 0;
      this.size = 0;

      for(int i = 0; i < ak.length; ++i) {
         if (ak[i] != null) {
            this.addMapping(ak[i], aint[i]);
         }
      }

   }

   public void addMapping(K p_186814_1_, int p_186814_2_) {
      int i = Math.max(p_186814_2_, this.size + 1);
      if ((float)i >= (float)this.keys.length * 0.8F) {
         int j;
         for(j = this.keys.length << 1; j < p_186814_2_; j <<= 1) {
         }

         this.grow(j);
      }

      int k = this.findEmpty(this.hash(p_186814_1_));
      this.keys[k] = p_186814_1_;
      this.values[k] = p_186814_2_;
      this.byId[p_186814_2_] = p_186814_1_;
      ++this.size;
      if (p_186814_2_ == this.nextId) {
         ++this.nextId;
      }

   }

   private int hash(@Nullable K p_186811_1_) {
      return (MathHelper.murmurHash3Mixer(System.identityHashCode(p_186811_1_)) & Integer.MAX_VALUE) % this.keys.length;
   }

   private int indexOf(@Nullable K p_186816_1_, int p_186816_2_) {
      for(int i = p_186816_2_; i < this.keys.length; ++i) {
         if (this.keys[i] == p_186816_1_) {
            return i;
         }

         if (this.keys[i] == EMPTY_SLOT) {
            return -1;
         }
      }

      for(int j = 0; j < p_186816_2_; ++j) {
         if (this.keys[j] == p_186816_1_) {
            return j;
         }

         if (this.keys[j] == EMPTY_SLOT) {
            return -1;
         }
      }

      return -1;
   }

   private int findEmpty(int p_186806_1_) {
      for(int i = p_186806_1_; i < this.keys.length; ++i) {
         if (this.keys[i] == EMPTY_SLOT) {
            return i;
         }
      }

      for(int j = 0; j < p_186806_1_; ++j) {
         if (this.keys[j] == EMPTY_SLOT) {
            return j;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   public Iterator<K> iterator() {
      return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
   }

   public void clear() {
      Arrays.fill(this.keys, (Object)null);
      Arrays.fill(this.byId, (Object)null);
      this.nextId = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }
}
