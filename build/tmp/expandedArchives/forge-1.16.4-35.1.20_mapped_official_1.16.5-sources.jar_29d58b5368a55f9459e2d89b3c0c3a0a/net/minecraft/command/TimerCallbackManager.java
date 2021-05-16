package net.minecraft.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackManager<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TimerCallbackSerializers<T> callbacksRegistry;
   private final Queue<TimerCallbackManager.Entry<T>> queue = new PriorityQueue<>(createComparator());
   private UnsignedLong sequentialId = UnsignedLong.ZERO;
   private final Table<String, Long, TimerCallbackManager.Entry<T>> events = HashBasedTable.create();

   private static <T> Comparator<TimerCallbackManager.Entry<T>> createComparator() {
      return Comparator.<TimerCallbackManager.Entry<T>>comparingLong((p_227578_0_) -> {
         return p_227578_0_.triggerTime;
      }).thenComparing((p_227577_0_) -> {
         return p_227577_0_.sequentialId;
      });
   }

   public TimerCallbackManager(TimerCallbackSerializers<T> p_i232176_1_, Stream<Dynamic<INBT>> p_i232176_2_) {
      this(p_i232176_1_);
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      p_i232176_2_.forEach((p_237478_1_) -> {
         if (!(p_237478_1_.getValue() instanceof CompoundNBT)) {
            LOGGER.warn("Invalid format of events: {}", (Object)p_237478_1_);
         } else {
            this.loadEvent((CompoundNBT)p_237478_1_.getValue());
         }
      });
   }

   public TimerCallbackManager(TimerCallbackSerializers<T> p_i51188_1_) {
      this.callbacksRegistry = p_i51188_1_;
   }

   public void tick(T p_216331_1_, long p_216331_2_) {
      while(true) {
         TimerCallbackManager.Entry<T> entry = this.queue.peek();
         if (entry == null || entry.triggerTime > p_216331_2_) {
            return;
         }

         this.queue.remove();
         this.events.remove(entry.id, p_216331_2_);
         entry.callback.handle(p_216331_1_, this, p_216331_2_);
      }
   }

   public void schedule(String p_227576_1_, long p_227576_2_, ITimerCallback<T> p_227576_4_) {
      if (!this.events.contains(p_227576_1_, p_227576_2_)) {
         this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
         TimerCallbackManager.Entry<T> entry = new TimerCallbackManager.Entry<>(p_227576_2_, this.sequentialId, p_227576_1_, p_227576_4_);
         this.events.put(p_227576_1_, p_227576_2_, entry);
         this.queue.add(entry);
      }
   }

   public int remove(String p_227575_1_) {
      Collection<TimerCallbackManager.Entry<T>> collection = this.events.row(p_227575_1_).values();
      collection.forEach(this.queue::remove);
      int i = collection.size();
      collection.clear();
      return i;
   }

   public Set<String> getEventsIds() {
      return Collections.unmodifiableSet(this.events.rowKeySet());
   }

   private void loadEvent(CompoundNBT p_216329_1_) {
      CompoundNBT compoundnbt = p_216329_1_.getCompound("Callback");
      ITimerCallback<T> itimercallback = this.callbacksRegistry.deserialize(compoundnbt);
      if (itimercallback != null) {
         String s = p_216329_1_.getString("Name");
         long i = p_216329_1_.getLong("TriggerTime");
         this.schedule(s, i, itimercallback);
      }

   }

   private CompoundNBT storeEvent(TimerCallbackManager.Entry<T> p_216332_1_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", p_216332_1_.id);
      compoundnbt.putLong("TriggerTime", p_216332_1_.triggerTime);
      compoundnbt.put("Callback", this.callbacksRegistry.serialize(p_216332_1_.callback));
      return compoundnbt;
   }

   public ListNBT store() {
      ListNBT listnbt = new ListNBT();
      this.queue.stream().sorted(createComparator()).map(this::storeEvent).forEach(listnbt::add);
      return listnbt;
   }

   public static class Entry<T> {
      public final long triggerTime;
      public final UnsignedLong sequentialId;
      public final String id;
      public final ITimerCallback<T> callback;

      private Entry(long p_i50837_1_, UnsignedLong p_i50837_3_, String p_i50837_4_, ITimerCallback<T> p_i50837_5_) {
         this.triggerTime = p_i50837_1_;
         this.sequentialId = p_i50837_3_;
         this.id = p_i50837_4_;
         this.callback = p_i50837_5_;
      }
   }
}
