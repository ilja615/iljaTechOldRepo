package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.WeightedList;
import net.minecraft.world.server.ServerWorld;

public class MultiTask<E extends LivingEntity> extends Task<E> {
   private final Set<MemoryModuleType<?>> exitErasedMemories;
   private final MultiTask.Ordering orderPolicy;
   private final MultiTask.RunType runningPolicy;
   private final WeightedList<Task<? super E>> behaviors = new WeightedList<>();

   public MultiTask(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51503_1_, Set<MemoryModuleType<?>> p_i51503_2_, MultiTask.Ordering p_i51503_3_, MultiTask.RunType p_i51503_4_, List<Pair<Task<? super E>, Integer>> p_i51503_5_) {
      super(p_i51503_1_);
      this.exitErasedMemories = p_i51503_2_;
      this.orderPolicy = p_i51503_3_;
      this.runningPolicy = p_i51503_4_;
      p_i51503_5_.forEach((p_220411_1_) -> {
         this.behaviors.add(p_220411_1_.getFirst(), p_220411_1_.getSecond());
      });
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, E p_212834_2_, long p_212834_3_) {
      return this.behaviors.stream().filter((p_220414_0_) -> {
         return p_220414_0_.getStatus() == Task.Status.RUNNING;
      }).anyMatch((p_220413_4_) -> {
         return p_220413_4_.canStillUse(p_212834_1_, p_212834_2_, p_212834_3_);
      });
   }

   protected boolean timedOut(long p_220383_1_) {
      return false;
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      this.orderPolicy.apply(this.behaviors);
      this.runningPolicy.apply(this.behaviors, p_212831_1_, p_212831_2_, p_212831_3_);
   }

   protected void tick(ServerWorld p_212833_1_, E p_212833_2_, long p_212833_3_) {
      this.behaviors.stream().filter((p_220408_0_) -> {
         return p_220408_0_.getStatus() == Task.Status.RUNNING;
      }).forEach((p_220409_4_) -> {
         p_220409_4_.tickOrStop(p_212833_1_, p_212833_2_, p_212833_3_);
      });
   }

   protected void stop(ServerWorld p_212835_1_, E p_212835_2_, long p_212835_3_) {
      this.behaviors.stream().filter((p_220407_0_) -> {
         return p_220407_0_.getStatus() == Task.Status.RUNNING;
      }).forEach((p_220412_4_) -> {
         p_220412_4_.doStop(p_212835_1_, p_212835_2_, p_212835_3_);
      });
      this.exitErasedMemories.forEach(p_212835_2_.getBrain()::eraseMemory);
   }

   public String toString() {
      Set<? extends Task<? super E>> set = this.behaviors.stream().filter((p_220410_0_) -> {
         return p_220410_0_.getStatus() == Task.Status.RUNNING;
      }).collect(Collectors.toSet());
      return "(" + this.getClass().getSimpleName() + "): " + set;
   }

   static enum Ordering {
      ORDERED((p_220627_0_) -> {
      }),
      SHUFFLED(WeightedList::shuffle);

      private final Consumer<WeightedList<?>> consumer;

      private Ordering(Consumer<WeightedList<?>> p_i50849_3_) {
         this.consumer = p_i50849_3_;
      }

      public void apply(WeightedList<?> p_220628_1_) {
         this.consumer.accept(p_220628_1_);
      }
   }

   static enum RunType {
      RUN_ONE {
         public <E extends LivingEntity> void apply(WeightedList<Task<? super E>> p_220630_1_, ServerWorld p_220630_2_, E p_220630_3_, long p_220630_4_) {
            p_220630_1_.stream().filter((p_220634_0_) -> {
               return p_220634_0_.getStatus() == Task.Status.STOPPED;
            }).filter((p_220633_4_) -> {
               return p_220633_4_.tryStart(p_220630_2_, p_220630_3_, p_220630_4_);
            }).findFirst();
         }
      },
      TRY_ALL {
         public <E extends LivingEntity> void apply(WeightedList<Task<? super E>> p_220630_1_, ServerWorld p_220630_2_, E p_220630_3_, long p_220630_4_) {
            p_220630_1_.stream().filter((p_220632_0_) -> {
               return p_220632_0_.getStatus() == Task.Status.STOPPED;
            }).forEach((p_220631_4_) -> {
               p_220631_4_.tryStart(p_220630_2_, p_220630_3_, p_220630_4_);
            });
         }
      };

      private RunType() {
      }

      public abstract <E extends LivingEntity> void apply(WeightedList<Task<? super E>> p_220630_1_, ServerWorld p_220630_2_, E p_220630_3_, long p_220630_4_);
   }
}
