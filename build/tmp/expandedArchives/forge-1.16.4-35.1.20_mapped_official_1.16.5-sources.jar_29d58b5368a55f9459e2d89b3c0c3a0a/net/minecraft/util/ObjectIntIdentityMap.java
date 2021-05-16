package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable<T> {
   protected int nextId;
   protected final IdentityHashMap<T, Integer> tToId;
   protected final List<T> idToT;

   public ObjectIntIdentityMap() {
      this(512);
   }

   public ObjectIntIdentityMap(int p_i46984_1_) {
      this.idToT = Lists.newArrayListWithExpectedSize(p_i46984_1_);
      this.tToId = new IdentityHashMap<>(p_i46984_1_);
   }

   public void addMapping(T p_148746_1_, int p_148746_2_) {
      this.tToId.put(p_148746_1_, p_148746_2_);

      while(this.idToT.size() <= p_148746_2_) {
         this.idToT.add((T)null);
      }

      this.idToT.set(p_148746_2_, p_148746_1_);
      if (this.nextId <= p_148746_2_) {
         this.nextId = p_148746_2_ + 1;
      }

   }

   public void add(T p_195867_1_) {
      this.addMapping(p_195867_1_, this.nextId);
   }

   public int getId(T p_148757_1_) {
      Integer integer = this.tToId.get(p_148757_1_);
      return integer == null ? -1 : integer;
   }

   @Nullable
   public final T byId(int p_148745_1_) {
      return (T)(p_148745_1_ >= 0 && p_148745_1_ < this.idToT.size() ? this.idToT.get(p_148745_1_) : null);
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.idToT.iterator(), Predicates.notNull());
   }

   public int size() {
      return this.tToId.size();
   }
}
