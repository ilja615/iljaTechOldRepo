package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class FarmTask extends Task<VillagerEntity> {
   @Nullable
   private BlockPos aboveFarmlandPos;
   private long nextOkStartTime;
   private int timeWorkedSoFar;
   private final List<BlockPos> validFarmlandAroundVillager = Lists.newArrayList();

   public FarmTask() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(p_212832_1_, p_212832_2_)) {
         return false;
      } else if (p_212832_2_.getVillagerData().getProfession() != VillagerProfession.FARMER) {
         return false;
      } else {
         BlockPos.Mutable blockpos$mutable = p_212832_2_.blockPosition().mutable();
         this.validFarmlandAroundVillager.clear();

         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               for(int k = -1; k <= 1; ++k) {
                  blockpos$mutable.set(p_212832_2_.getX() + (double)i, p_212832_2_.getY() + (double)j, p_212832_2_.getZ() + (double)k);
                  if (this.validPos(blockpos$mutable, p_212832_1_)) {
                     this.validFarmlandAroundVillager.add(new BlockPos(blockpos$mutable));
                  }
               }
            }
         }

         this.aboveFarmlandPos = this.getValidFarmland(p_212832_1_);
         return this.aboveFarmlandPos != null;
      }
   }

   @Nullable
   private BlockPos getValidFarmland(ServerWorld p_223517_1_) {
      return this.validFarmlandAroundVillager.isEmpty() ? null : this.validFarmlandAroundVillager.get(p_223517_1_.getRandom().nextInt(this.validFarmlandAroundVillager.size()));
   }

   private boolean validPos(BlockPos p_223516_1_, ServerWorld p_223516_2_) {
      BlockState blockstate = p_223516_2_.getBlockState(p_223516_1_);
      Block block = blockstate.getBlock();
      Block block1 = p_223516_2_.getBlockState(p_223516_1_.below()).getBlock();
      return block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(blockstate) || blockstate.isAir() && block1 instanceof FarmlandBlock;
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.nextOkStartTime && this.aboveFarmlandPos != null) {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(this.aboveFarmlandPos));
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosWrapper(this.aboveFarmlandPos), 0.5F, 1));
      }

   }

   protected void stop(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      this.timeWorkedSoFar = 0;
      this.nextOkStartTime = p_212835_3_ + 40L;
   }

   protected void tick(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      if (this.aboveFarmlandPos == null || this.aboveFarmlandPos.closerThan(p_212833_2_.position(), 1.0D)) {
         if (this.aboveFarmlandPos != null && p_212833_3_ > this.nextOkStartTime) {
            BlockState blockstate = p_212833_1_.getBlockState(this.aboveFarmlandPos);
            Block block = blockstate.getBlock();
            Block block1 = p_212833_1_.getBlockState(this.aboveFarmlandPos.below()).getBlock();
            if (block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(blockstate)) {
               p_212833_1_.destroyBlock(this.aboveFarmlandPos, true, p_212833_2_);
            }

            if (blockstate.isAir() && block1 instanceof FarmlandBlock && p_212833_2_.hasFarmSeeds()) {
               Inventory inventory = p_212833_2_.getInventory();

               for(int i = 0; i < inventory.getContainerSize(); ++i) {
                  ItemStack itemstack = inventory.getItem(i);
                  boolean flag = false;
                  if (!itemstack.isEmpty()) {
                     if (itemstack.getItem() == Items.WHEAT_SEEDS) {
                        p_212833_1_.setBlock(this.aboveFarmlandPos, Blocks.WHEAT.defaultBlockState(), 3);
                        flag = true;
                     } else if (itemstack.getItem() == Items.POTATO) {
                        p_212833_1_.setBlock(this.aboveFarmlandPos, Blocks.POTATOES.defaultBlockState(), 3);
                        flag = true;
                     } else if (itemstack.getItem() == Items.CARROT) {
                        p_212833_1_.setBlock(this.aboveFarmlandPos, Blocks.CARROTS.defaultBlockState(), 3);
                        flag = true;
                     } else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
                        p_212833_1_.setBlock(this.aboveFarmlandPos, Blocks.BEETROOTS.defaultBlockState(), 3);
                        flag = true;
                     } else if (itemstack.getItem() instanceof net.minecraftforge.common.IPlantable) {
                        if (((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlantType(p_212833_1_, aboveFarmlandPos) == net.minecraftforge.common.PlantType.CROP) {
                           p_212833_1_.setBlock(aboveFarmlandPos, ((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlant(p_212833_1_, aboveFarmlandPos), 3);
                           flag = true;
                        }
                     }
                  }

                  if (flag) {
                     p_212833_1_.playSound((PlayerEntity)null, (double)this.aboveFarmlandPos.getX(), (double)this.aboveFarmlandPos.getY(), (double)this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                     itemstack.shrink(1);
                     if (itemstack.isEmpty()) {
                        inventory.setItem(i, ItemStack.EMPTY);
                     }
                     break;
                  }
               }
            }

            if (block instanceof CropsBlock && !((CropsBlock)block).isMaxAge(blockstate)) {
               this.validFarmlandAroundVillager.remove(this.aboveFarmlandPos);
               this.aboveFarmlandPos = this.getValidFarmland(p_212833_1_);
               if (this.aboveFarmlandPos != null) {
                  this.nextOkStartTime = p_212833_3_ + 20L;
                  p_212833_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosWrapper(this.aboveFarmlandPos), 0.5F, 1));
                  p_212833_2_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(this.aboveFarmlandPos));
               }
            }
         }

         ++this.timeWorkedSoFar;
      }
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.timeWorkedSoFar < 200;
   }
}
