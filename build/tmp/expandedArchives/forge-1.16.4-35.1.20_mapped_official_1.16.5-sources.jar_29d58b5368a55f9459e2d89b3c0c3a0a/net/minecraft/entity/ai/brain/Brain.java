package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Brain<E extends LivingEntity> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Supplier<Codec<Brain<E>>> codec;
   private final Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories = Maps.newHashMap();
   private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
   private final Map<Integer, Map<Activity, Set<Task<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
   private Schedule schedule = Schedule.EMPTY;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>>> activityRequirements = Maps.newHashMap();
   private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped = Maps.newHashMap();
   private Set<Activity> coreActivities = Sets.newHashSet();
   private final Set<Activity> activeActivities = Sets.newHashSet();
   private Activity defaultActivity = Activity.IDLE;
   private long lastScheduleUpdate = -9999L;

   public static <E extends LivingEntity> Brain.BrainCodec<E> provider(Collection<? extends MemoryModuleType<?>> p_233705_0_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_233705_1_) {
      return new Brain.BrainCodec<>(p_233705_0_, p_233705_1_);
   }

   public static <E extends LivingEntity> Codec<Brain<E>> codec(final Collection<? extends MemoryModuleType<?>> p_233710_0_, final Collection<? extends SensorType<? extends Sensor<? super E>>> p_233710_1_) {
      final MutableObject<Codec<Brain<E>>> mutableobject = new MutableObject<>();
      mutableobject.setValue((new MapCodec<Brain<E>>() {
         public <T> Stream<T> keys(DynamicOps<T> p_keys_1_) {
            return p_233710_0_.stream().flatMap((p_233734_0_) -> {
               return Util.toStream(p_233734_0_.getCodec().map((p_233727_1_) -> {
                  return Registry.MEMORY_MODULE_TYPE.getKey(p_233734_0_);
               }));
            }).map((p_233733_1_) -> {
               return p_keys_1_.createString(p_233733_1_.toString());
            });
         }

         public <T> DataResult<Brain<E>> decode(DynamicOps<T> p_decode_1_, MapLike<T> p_decode_2_) {
            MutableObject<DataResult<Builder<Brain.MemoryCodec<?>>>> mutableobject1 = new MutableObject<>(DataResult.success(ImmutableList.builder()));
            p_decode_2_.entries().forEach((p_233732_3_) -> {
               DataResult<MemoryModuleType<?>> dataresult = Registry.MEMORY_MODULE_TYPE.parse(p_decode_1_, p_233732_3_.getFirst());
               DataResult<? extends Brain.MemoryCodec<?>> dataresult1 = dataresult.flatMap((p_233729_3_) -> {
                  return this.captureRead(p_233729_3_, p_decode_1_, (T)p_233732_3_.getSecond());
               });
               mutableobject1.setValue(mutableobject1.getValue().apply2(Builder::add, dataresult1));
            });
            ImmutableList<Brain.MemoryCodec<?>> immutablelist = mutableobject1.getValue().resultOrPartial(Brain.LOGGER::error).map(Builder::build).orElseGet(ImmutableList::of);
            return DataResult.success(new Brain<>(p_233710_0_, p_233710_1_, immutablelist, mutableobject::getValue));
         }

         private <T, U> DataResult<Brain.MemoryCodec<U>> captureRead(MemoryModuleType<U> p_233728_1_, DynamicOps<T> p_233728_2_, T p_233728_3_) {
            return p_233728_1_.getCodec().map(DataResult::success).orElseGet(() -> {
               return DataResult.error("No codec for memory: " + p_233728_1_);
            }).flatMap((p_233731_2_) -> {
               return p_233731_2_.parse(p_233728_2_, p_233728_3_);
            }).map((p_233726_1_) -> {
               return new Brain.MemoryCodec<>(p_233728_1_, Optional.of(p_233726_1_));
            });
         }

         public <T> RecordBuilder<T> encode(Brain<E> p_encode_1_, DynamicOps<T> p_encode_2_, RecordBuilder<T> p_encode_3_) {
            p_encode_1_.memories().forEach((p_233730_2_) -> {
               p_233730_2_.serialize(p_encode_2_, p_encode_3_);
            });
            return p_encode_3_;
         }
      }).fieldOf("memories").codec());
      return mutableobject.getValue();
   }

   public Brain(Collection<? extends MemoryModuleType<?>> p_i231494_1_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_i231494_2_, ImmutableList<Brain.MemoryCodec<?>> p_i231494_3_, Supplier<Codec<Brain<E>>> p_i231494_4_) {
      this.codec = p_i231494_4_;

      for(MemoryModuleType<?> memorymoduletype : p_i231494_1_) {
         this.memories.put(memorymoduletype, Optional.empty());
      }

      for(SensorType<? extends Sensor<? super E>> sensortype : p_i231494_2_) {
         this.sensors.put(sensortype, sensortype.create());
      }

      for(Sensor<? super E> sensor : this.sensors.values()) {
         for(MemoryModuleType<?> memorymoduletype1 : sensor.requires()) {
            this.memories.put(memorymoduletype1, Optional.empty());
         }
      }

      for(Brain.MemoryCodec<?> memorycodec : p_i231494_3_) {
         memorycodec.setMemoryInternal(this);
      }

   }

   public <T> DataResult<T> serializeStart(DynamicOps<T> p_233702_1_) {
      return this.codec.get().encodeStart(p_233702_1_, this);
   }

   private Stream<Brain.MemoryCodec<?>> memories() {
      return this.memories.entrySet().stream().map((p_233707_0_) -> {
         return Brain.MemoryCodec.createUnchecked(p_233707_0_.getKey(), p_233707_0_.getValue());
      });
   }

   public boolean hasMemoryValue(MemoryModuleType<?> p_218191_1_) {
      return this.checkMemory(p_218191_1_, MemoryModuleStatus.VALUE_PRESENT);
   }

   public <U> void eraseMemory(MemoryModuleType<U> p_218189_1_) {
      this.setMemory(p_218189_1_, Optional.empty());
   }

   public <U> void setMemory(MemoryModuleType<U> p_218205_1_, @Nullable U p_218205_2_) {
      this.setMemory(p_218205_1_, Optional.ofNullable(p_218205_2_));
   }

   public <U> void setMemoryWithExpiry(MemoryModuleType<U> p_233696_1_, U p_233696_2_, long p_233696_3_) {
      this.setMemoryInternal(p_233696_1_, Optional.of(Memory.of(p_233696_2_, p_233696_3_)));
   }

   public <U> void setMemory(MemoryModuleType<U> p_218226_1_, Optional<? extends U> p_218226_2_) {
      this.setMemoryInternal(p_218226_1_, p_218226_2_.map(Memory::of));
   }

   private <U> void setMemoryInternal(MemoryModuleType<U> p_233709_1_, Optional<? extends Memory<?>> p_233709_2_) {
      if (this.memories.containsKey(p_233709_1_)) {
         if (p_233709_2_.isPresent() && this.isEmptyCollection(p_233709_2_.get().getValue())) {
            this.eraseMemory(p_233709_1_);
         } else {
            this.memories.put(p_233709_1_, p_233709_2_);
         }
      }

   }

   public <U> Optional<U> getMemory(MemoryModuleType<U> p_218207_1_) {
      return (Optional<U>) this.memories.get(p_218207_1_).map(Memory::getValue);
   }

   public <U> boolean isMemoryValue(MemoryModuleType<U> p_233708_1_, U p_233708_2_) {
      return !this.hasMemoryValue(p_233708_1_) ? false : this.getMemory(p_233708_1_).filter((p_233704_1_) -> {
         return p_233704_1_.equals(p_233708_2_);
      }).isPresent();
   }

   public boolean checkMemory(MemoryModuleType<?> p_218196_1_, MemoryModuleStatus p_218196_2_) {
      Optional<? extends Memory<?>> optional = this.memories.get(p_218196_1_);
      if (optional == null) {
         return false;
      } else {
         return p_218196_2_ == MemoryModuleStatus.REGISTERED || p_218196_2_ == MemoryModuleStatus.VALUE_PRESENT && optional.isPresent() || p_218196_2_ == MemoryModuleStatus.VALUE_ABSENT && !optional.isPresent();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule p_218203_1_) {
      this.schedule = p_218203_1_;
   }

   public void setCoreActivities(Set<Activity> p_218199_1_) {
      this.coreActivities = p_218199_1_;
   }

   @Deprecated
   public List<Task<? super E>> getRunningBehaviors() {
      List<Task<? super E>> list = new ObjectArrayList<>();

      for(Map<Activity, Set<Task<? super E>>> map : this.availableBehaviorsByPriority.values()) {
         for(Set<Task<? super E>> set : map.values()) {
            for(Task<? super E> task : set) {
               if (task.getStatus() == Task.Status.RUNNING) {
                  list.add(task);
               }
            }
         }
      }

      return list;
   }

   public void useDefaultActivity() {
      this.setActiveActivity(this.defaultActivity);
   }

   public Optional<Activity> getActiveNonCoreActivity() {
      for(Activity activity : this.activeActivities) {
         if (!this.coreActivities.contains(activity)) {
            return Optional.of(activity);
         }
      }

      return Optional.empty();
   }

   public void setActiveActivityIfPossible(Activity p_218202_1_) {
      if (this.activityRequirementsAreMet(p_218202_1_)) {
         this.setActiveActivity(p_218202_1_);
      } else {
         this.useDefaultActivity();
      }

   }

   private void setActiveActivity(Activity p_233713_1_) {
      if (!this.isActive(p_233713_1_)) {
         this.eraseMemoriesForOtherActivitesThan(p_233713_1_);
         this.activeActivities.clear();
         this.activeActivities.addAll(this.coreActivities);
         this.activeActivities.add(p_233713_1_);
      }
   }

   private void eraseMemoriesForOtherActivitesThan(Activity p_233715_1_) {
      for(Activity activity : this.activeActivities) {
         if (activity != p_233715_1_) {
            Set<MemoryModuleType<?>> set = this.activityMemoriesToEraseWhenStopped.get(activity);
            if (set != null) {
               for(MemoryModuleType<?> memorymoduletype : set) {
                  this.eraseMemory(memorymoduletype);
               }
            }
         }
      }

   }

   public void updateActivityFromSchedule(long p_218211_1_, long p_218211_3_) {
      if (p_218211_3_ - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = p_218211_3_;
         Activity activity = this.getSchedule().getActivityAt((int)(p_218211_1_ % 24000L));
         if (!this.activeActivities.contains(activity)) {
            this.setActiveActivityIfPossible(activity);
         }
      }

   }

   public void setActiveActivityToFirstValid(List<Activity> p_233706_1_) {
      for(Activity activity : p_233706_1_) {
         if (this.activityRequirementsAreMet(activity)) {
            this.setActiveActivity(activity);
            break;
         }
      }

   }

   public void setDefaultActivity(Activity p_218200_1_) {
      this.defaultActivity = p_218200_1_;
   }

   public void addActivity(Activity p_233698_1_, int p_233698_2_, ImmutableList<? extends Task<? super E>> p_233698_3_) {
      this.addActivity(p_233698_1_, this.createPriorityPairs(p_233698_2_, p_233698_3_));
   }

   public void addActivityAndRemoveMemoryWhenStopped(Activity p_233699_1_, int p_233699_2_, ImmutableList<? extends Task<? super E>> p_233699_3_, MemoryModuleType<?> p_233699_4_) {
      Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> set = ImmutableSet.of(Pair.of(p_233699_4_, MemoryModuleStatus.VALUE_PRESENT));
      Set<MemoryModuleType<?>> set1 = ImmutableSet.of(p_233699_4_);
      this.addActivityAndRemoveMemoriesWhenStopped(p_233699_1_, this.createPriorityPairs(p_233699_2_, p_233699_3_), set, set1);
   }

   public void addActivity(Activity p_218208_1_, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> p_218208_2_) {
      this.addActivityAndRemoveMemoriesWhenStopped(p_218208_1_, p_218208_2_, ImmutableSet.of(), Sets.newHashSet());
   }

   public void addActivityWithConditions(Activity p_233700_1_, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> p_233700_2_, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> p_233700_3_) {
      this.addActivityAndRemoveMemoriesWhenStopped(p_233700_1_, p_233700_2_, p_233700_3_, Sets.newHashSet());
   }

   private void addActivityAndRemoveMemoriesWhenStopped(Activity p_233701_1_, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> p_233701_2_, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> p_233701_3_, Set<MemoryModuleType<?>> p_233701_4_) {
      this.activityRequirements.put(p_233701_1_, p_233701_3_);
      if (!p_233701_4_.isEmpty()) {
         this.activityMemoriesToEraseWhenStopped.put(p_233701_1_, p_233701_4_);
      }

      for(Pair<Integer, ? extends Task<? super E>> pair : p_233701_2_) {
         this.availableBehaviorsByPriority.computeIfAbsent(pair.getFirst(), (p_233703_0_) -> {
            return Maps.newHashMap();
         }).computeIfAbsent(p_233701_1_, (p_233717_0_) -> {
            return Sets.newLinkedHashSet();
         }).add(pair.getSecond());
      }

   }

   public boolean isActive(Activity p_218214_1_) {
      return this.activeActivities.contains(p_218214_1_);
   }

   public Brain<E> copyWithoutBehaviors() {
      Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codec);

      for(Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
         MemoryModuleType<?> memorymoduletype = entry.getKey();
         if (entry.getValue().isPresent()) {
            brain.memories.put(memorymoduletype, entry.getValue());
         }
      }

      return brain;
   }

   public void tick(ServerWorld p_218210_1_, E p_218210_2_) {
      this.forgetOutdatedMemories();
      this.tickSensors(p_218210_1_, p_218210_2_);
      this.startEachNonRunningBehavior(p_218210_1_, p_218210_2_);
      this.tickEachRunningBehavior(p_218210_1_, p_218210_2_);
   }

   private void tickSensors(ServerWorld p_233711_1_, E p_233711_2_) {
      for(Sensor<? super E> sensor : this.sensors.values()) {
         sensor.tick(p_233711_1_, p_233711_2_);
      }

   }

   private void forgetOutdatedMemories() {
      for(Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
         if (entry.getValue().isPresent()) {
            Memory<?> memory = entry.getValue().get();
            memory.tick();
            if (memory.hasExpired()) {
               this.eraseMemory(entry.getKey());
            }
         }
      }

   }

   public void stopAll(ServerWorld p_218227_1_, E p_218227_2_) {
      long i = p_218227_2_.level.getGameTime();

      for(Task<? super E> task : this.getRunningBehaviors()) {
         task.doStop(p_218227_1_, p_218227_2_, i);
      }

   }

   private void startEachNonRunningBehavior(ServerWorld p_218218_1_, E p_218218_2_) {
      long i = p_218218_1_.getGameTime();

      for(Map<Activity, Set<Task<? super E>>> map : this.availableBehaviorsByPriority.values()) {
         for(Entry<Activity, Set<Task<? super E>>> entry : map.entrySet()) {
            Activity activity = entry.getKey();
            if (this.activeActivities.contains(activity)) {
               for(Task<? super E> task : entry.getValue()) {
                  if (task.getStatus() == Task.Status.STOPPED) {
                     task.tryStart(p_218218_1_, p_218218_2_, i);
                  }
               }
            }
         }
      }

   }

   private void tickEachRunningBehavior(ServerWorld p_218222_1_, E p_218222_2_) {
      long i = p_218222_1_.getGameTime();

      for(Task<? super E> task : this.getRunningBehaviors()) {
         task.tickOrStop(p_218222_1_, p_218222_2_, i);
      }

   }

   private boolean activityRequirementsAreMet(Activity p_218217_1_) {
      if (!this.activityRequirements.containsKey(p_218217_1_)) {
         return false;
      } else {
         for(Pair<MemoryModuleType<?>, MemoryModuleStatus> pair : this.activityRequirements.get(p_218217_1_)) {
            MemoryModuleType<?> memorymoduletype = pair.getFirst();
            MemoryModuleStatus memorymodulestatus = pair.getSecond();
            if (!this.checkMemory(memorymoduletype, memorymodulestatus)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isEmptyCollection(Object p_218213_1_) {
      return p_218213_1_ instanceof Collection && ((Collection)p_218213_1_).isEmpty();
   }

   ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> createPriorityPairs(int p_233692_1_, ImmutableList<? extends Task<? super E>> p_233692_2_) {
      int i = p_233692_1_;
      Builder<Pair<Integer, ? extends Task<? super E>>> builder = ImmutableList.builder();

      for(Task<? super E> task : p_233692_2_) {
         builder.add(Pair.of(i++, task));
      }

      return builder.build();
   }

   public static final class BrainCodec<E extends LivingEntity> {
      private final Collection<? extends MemoryModuleType<?>> memoryTypes;
      private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes;
      private final Codec<Brain<E>> codec;

      private BrainCodec(Collection<? extends MemoryModuleType<?>> p_i231498_1_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_i231498_2_) {
         this.memoryTypes = p_i231498_1_;
         this.sensorTypes = p_i231498_2_;
         this.codec = Brain.codec(p_i231498_1_, p_i231498_2_);
      }

      public Brain<E> makeBrain(Dynamic<?> p_233748_1_) {
         return this.codec.parse(p_233748_1_).resultOrPartial(Brain.LOGGER::error).orElseGet(() -> {
            return new Brain<>(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> {
               return this.codec;
            });
         });
      }
   }

   static final class MemoryCodec<U> {
      private final MemoryModuleType<U> type;
      private final Optional<? extends Memory<U>> value;

      private static <U> Brain.MemoryCodec<U> createUnchecked(MemoryModuleType<U> p_233743_0_, Optional<? extends Memory<?>> p_233743_1_) {
         return new Brain.MemoryCodec<>(p_233743_0_, (Optional<? extends Memory<U>>) p_233743_1_);
      }

      private MemoryCodec(MemoryModuleType<U> p_i231496_1_, Optional<? extends Memory<U>> p_i231496_2_) {
         this.type = p_i231496_1_;
         this.value = p_i231496_2_;
      }

      private void setMemoryInternal(Brain<?> p_233738_1_) {
         p_233738_1_.setMemoryInternal(this.type, this.value);
      }

      public <T> void serialize(DynamicOps<T> p_233740_1_, RecordBuilder<T> p_233740_2_) {
         this.type.getCodec().ifPresent((p_233741_3_) -> {
            this.value.ifPresent((p_233742_4_) -> {
               p_233740_2_.add(Registry.MEMORY_MODULE_TYPE.encodeStart(p_233740_1_, this.type), p_233741_3_.encodeStart(p_233740_1_, p_233742_4_));
            });
         });
      }
   }
}
