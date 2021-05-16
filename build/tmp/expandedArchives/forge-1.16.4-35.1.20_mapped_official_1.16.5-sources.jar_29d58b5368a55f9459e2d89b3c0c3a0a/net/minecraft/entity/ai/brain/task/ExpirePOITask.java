package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class ExpirePOITask extends Task<LivingEntity> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final Predicate<PointOfInterestType> poiPredicate;

   public ExpirePOITask(PointOfInterestType p_i50338_1_, MemoryModuleType<GlobalPos> p_i50338_2_) {
      super(ImmutableMap.of(p_i50338_2_, MemoryModuleStatus.VALUE_PRESENT));
      this.poiPredicate = p_i50338_1_.getPredicate();
      this.memoryType = p_i50338_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      GlobalPos globalpos = p_212832_2_.getBrain().getMemory(this.memoryType).get();
      return p_212832_1_.dimension() == globalpos.dimension() && globalpos.pos().closerThan(p_212832_2_.position(), 16.0D);
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      GlobalPos globalpos = brain.getMemory(this.memoryType).get();
      BlockPos blockpos = globalpos.pos();
      ServerWorld serverworld = p_212831_1_.getServer().getLevel(globalpos.dimension());
      if (serverworld != null && !this.poiDoesntExist(serverworld, blockpos)) {
         if (this.bedIsOccupied(serverworld, blockpos, p_212831_2_)) {
            brain.eraseMemory(this.memoryType);
            p_212831_1_.getPoiManager().release(blockpos);
            DebugPacketSender.sendPoiTicketCountPacket(p_212831_1_, blockpos);
         }
      } else {
         brain.eraseMemory(this.memoryType);
      }

   }

   private boolean bedIsOccupied(ServerWorld p_223019_1_, BlockPos p_223019_2_, LivingEntity p_223019_3_) {
      BlockState blockstate = p_223019_1_.getBlockState(p_223019_2_);
      return blockstate.getBlock().is(BlockTags.BEDS) && blockstate.getValue(BedBlock.OCCUPIED) && !p_223019_3_.isSleeping();
   }

   private boolean poiDoesntExist(ServerWorld p_223020_1_, BlockPos p_223020_2_) {
      return !p_223020_1_.getPoiManager().exists(p_223020_2_, this.poiPredicate);
   }
}
