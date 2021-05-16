package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class InteractWithDoorTask extends Task<LivingEntity> {
   @Nullable
   private PathPoint lastCheckedNode;
   private int remainingCooldown;

   public InteractWithDoorTask() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Path path = p_212832_2_.getBrain().getMemory(MemoryModuleType.PATH).get();
      if (!path.notStarted() && !path.isDone()) {
         if (!Objects.equals(this.lastCheckedNode, path.getNextNode())) {
            this.remainingCooldown = 20;
            return true;
         } else {
            if (this.remainingCooldown > 0) {
               --this.remainingCooldown;
            }

            return this.remainingCooldown == 0;
         }
      } else {
         return false;
      }
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Path path = p_212831_2_.getBrain().getMemory(MemoryModuleType.PATH).get();
      this.lastCheckedNode = path.getNextNode();
      PathPoint pathpoint = path.getPreviousNode();
      PathPoint pathpoint1 = path.getNextNode();
      BlockPos blockpos = pathpoint.asBlockPos();
      BlockState blockstate = p_212831_1_.getBlockState(blockpos);
      if (blockstate.is(BlockTags.WOODEN_DOORS)) {
         DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
         if (!doorblock.isOpen(blockstate)) {
            doorblock.setOpen(p_212831_1_, blockstate, blockpos, true);
         }

         this.rememberDoorToClose(p_212831_1_, p_212831_2_, blockpos);
      }

      BlockPos blockpos1 = pathpoint1.asBlockPos();
      BlockState blockstate1 = p_212831_1_.getBlockState(blockpos1);
      if (blockstate1.is(BlockTags.WOODEN_DOORS)) {
         DoorBlock doorblock1 = (DoorBlock)blockstate1.getBlock();
         if (!doorblock1.isOpen(blockstate1)) {
            doorblock1.setOpen(p_212831_1_, blockstate1, blockpos1, true);
            this.rememberDoorToClose(p_212831_1_, p_212831_2_, blockpos1);
         }
      }

      closeDoorsThatIHaveOpenedOrPassedThrough(p_212831_1_, p_212831_2_, pathpoint, pathpoint1);
   }

   public static void closeDoorsThatIHaveOpenedOrPassedThrough(ServerWorld p_242294_0_, LivingEntity p_242294_1_, @Nullable PathPoint p_242294_2_, @Nullable PathPoint p_242294_3_) {
      Brain<?> brain = p_242294_1_.getBrain();
      if (brain.hasMemoryValue(MemoryModuleType.DOORS_TO_CLOSE)) {
         Iterator<GlobalPos> iterator = brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get().iterator();

         while(iterator.hasNext()) {
            GlobalPos globalpos = iterator.next();
            BlockPos blockpos = globalpos.pos();
            if ((p_242294_2_ == null || !p_242294_2_.asBlockPos().equals(blockpos)) && (p_242294_3_ == null || !p_242294_3_.asBlockPos().equals(blockpos))) {
               if (isDoorTooFarAway(p_242294_0_, p_242294_1_, globalpos)) {
                  iterator.remove();
               } else {
                  BlockState blockstate = p_242294_0_.getBlockState(blockpos);
                  if (!blockstate.is(BlockTags.WOODEN_DOORS)) {
                     iterator.remove();
                  } else {
                     DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
                     if (!doorblock.isOpen(blockstate)) {
                        iterator.remove();
                     } else if (areOtherMobsComingThroughDoor(p_242294_0_, p_242294_1_, blockpos)) {
                        iterator.remove();
                     } else {
                        doorblock.setOpen(p_242294_0_, blockstate, blockpos, false);
                        iterator.remove();
                     }
                  }
               }
            }
         }
      }

   }

   private static boolean areOtherMobsComingThroughDoor(ServerWorld p_242295_0_, LivingEntity p_242295_1_, BlockPos p_242295_2_) {
      Brain<?> brain = p_242295_1_.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.LIVING_ENTITIES) ? false : brain.getMemory(MemoryModuleType.LIVING_ENTITIES).get().stream().filter((p_242298_1_) -> {
         return p_242298_1_.getType() == p_242295_1_.getType();
      }).filter((p_242299_1_) -> {
         return p_242295_2_.closerThan(p_242299_1_.position(), 2.0D);
      }).anyMatch((p_242297_2_) -> {
         return isMobComingThroughDoor(p_242295_0_, p_242297_2_, p_242295_2_);
      });
   }

   private static boolean isMobComingThroughDoor(ServerWorld p_242300_0_, LivingEntity p_242300_1_, BlockPos p_242300_2_) {
      if (!p_242300_1_.getBrain().hasMemoryValue(MemoryModuleType.PATH)) {
         return false;
      } else {
         Path path = p_242300_1_.getBrain().getMemory(MemoryModuleType.PATH).get();
         if (path.isDone()) {
            return false;
         } else {
            PathPoint pathpoint = path.getPreviousNode();
            if (pathpoint == null) {
               return false;
            } else {
               PathPoint pathpoint1 = path.getNextNode();
               return p_242300_2_.equals(pathpoint.asBlockPos()) || p_242300_2_.equals(pathpoint1.asBlockPos());
            }
         }
      }
   }

   private static boolean isDoorTooFarAway(ServerWorld p_242296_0_, LivingEntity p_242296_1_, GlobalPos p_242296_2_) {
      return p_242296_2_.dimension() != p_242296_0_.dimension() || !p_242296_2_.pos().closerThan(p_242296_1_.position(), 2.0D);
   }

   private void rememberDoorToClose(ServerWorld p_242301_1_, LivingEntity p_242301_2_, BlockPos p_242301_3_) {
      Brain<?> brain = p_242301_2_.getBrain();
      GlobalPos globalpos = GlobalPos.of(p_242301_1_.dimension(), p_242301_3_);
      if (brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).isPresent()) {
         brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get().add(globalpos);
      } else {
         brain.setMemory(MemoryModuleType.DOORS_TO_CLOSE, Sets.newHashSet(globalpos));
      }

   }
}
