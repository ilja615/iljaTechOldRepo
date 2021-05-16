package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class BeehiveTileEntity extends TileEntity implements ITickableTileEntity {
   private final List<BeehiveTileEntity.Bee> stored = Lists.newArrayList();
   @Nullable
   private BlockPos savedFlowerPos = null;

   public BeehiveTileEntity() {
      super(TileEntityType.BEEHIVE);
   }

   public void setChanged() {
      if (this.isFireNearby()) {
         this.emptyAllLivingFromHive((PlayerEntity)null, this.level.getBlockState(this.getBlockPos()), BeehiveTileEntity.State.EMERGENCY);
      }

      super.setChanged();
   }

   public boolean isFireNearby() {
      if (this.level == null) {
         return false;
      } else {
         for(BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
            if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isEmpty() {
      return this.stored.isEmpty();
   }

   public boolean isFull() {
      return this.stored.size() == 3;
   }

   public void emptyAllLivingFromHive(@Nullable PlayerEntity p_226963_1_, BlockState p_226963_2_, BeehiveTileEntity.State p_226963_3_) {
      List<Entity> list = this.releaseAllOccupants(p_226963_2_, p_226963_3_);
      if (p_226963_1_ != null) {
         for(Entity entity : list) {
            if (entity instanceof BeeEntity) {
               BeeEntity beeentity = (BeeEntity)entity;
               if (p_226963_1_.position().distanceToSqr(entity.position()) <= 16.0D) {
                  if (!this.isSedated()) {
                     beeentity.setTarget(p_226963_1_);
                  } else {
                     beeentity.setStayOutOfHiveCountdown(400);
                  }
               }
            }
         }
      }

   }

   private List<Entity> releaseAllOccupants(BlockState p_226965_1_, BeehiveTileEntity.State p_226965_2_) {
      List<Entity> list = Lists.newArrayList();
      this.stored.removeIf((p_226966_4_) -> {
         return this.releaseOccupant(p_226965_1_, p_226966_4_, list, p_226965_2_);
      });
      return list;
   }

   public void addOccupant(Entity p_226961_1_, boolean p_226961_2_) {
      this.addOccupantWithPresetTicks(p_226961_1_, p_226961_2_, 0);
   }

   public int getOccupantCount() {
      return this.stored.size();
   }

   public static int getHoneyLevel(BlockState p_226964_0_) {
      return p_226964_0_.getValue(BeehiveBlock.HONEY_LEVEL);
   }

   public boolean isSedated() {
      return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
   }

   protected void sendDebugPackets() {
      DebugPacketSender.sendHiveInfo(this);
   }

   public void addOccupantWithPresetTicks(Entity p_226962_1_, boolean p_226962_2_, int p_226962_3_) {
      if (this.stored.size() < 3) {
         p_226962_1_.stopRiding();
         p_226962_1_.ejectPassengers();
         CompoundNBT compoundnbt = new CompoundNBT();
         p_226962_1_.save(compoundnbt);
         this.stored.add(new BeehiveTileEntity.Bee(compoundnbt, p_226962_3_, p_226962_2_ ? 2400 : 600));
         if (this.level != null) {
            if (p_226962_1_ instanceof BeeEntity) {
               BeeEntity beeentity = (BeeEntity)p_226962_1_;
               if (beeentity.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                  this.savedFlowerPos = beeentity.getSavedFlowerPos();
               }
            }

            BlockPos blockpos = this.getBlockPos();
            this.level.playSound((PlayerEntity)null, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         p_226962_1_.remove();
      }
   }

   private boolean releaseOccupant(BlockState p_235651_1_, BeehiveTileEntity.Bee p_235651_2_, @Nullable List<Entity> p_235651_3_, BeehiveTileEntity.State p_235651_4_) {
      if ((this.level.isNight() || this.level.isRaining()) && p_235651_4_ != BeehiveTileEntity.State.EMERGENCY) {
         return false;
      } else {
         BlockPos blockpos = this.getBlockPos();
         CompoundNBT compoundnbt = p_235651_2_.entityData;
         compoundnbt.remove("Passengers");
         compoundnbt.remove("Leash");
         compoundnbt.remove("UUID");
         Direction direction = p_235651_1_.getValue(BeehiveBlock.FACING);
         BlockPos blockpos1 = blockpos.relative(direction);
         boolean flag = !this.level.getBlockState(blockpos1).getCollisionShape(this.level, blockpos1).isEmpty();
         if (flag && p_235651_4_ != BeehiveTileEntity.State.EMERGENCY) {
            return false;
         } else {
            Entity entity = EntityType.loadEntityRecursive(compoundnbt, this.level, (p_226960_0_) -> {
               return p_226960_0_;
            });
            if (entity != null) {
               if (!entity.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                  return false;
               } else {
                  if (entity instanceof BeeEntity) {
                     BeeEntity beeentity = (BeeEntity)entity;
                     if (this.hasSavedFlowerPos() && !beeentity.hasSavedFlowerPos() && this.level.random.nextFloat() < 0.9F) {
                        beeentity.setSavedFlowerPos(this.savedFlowerPos);
                     }

                     if (p_235651_4_ == BeehiveTileEntity.State.HONEY_DELIVERED) {
                        beeentity.dropOffNectar();
                        if (p_235651_1_.getBlock().is(BlockTags.BEEHIVES)) {
                           int i = getHoneyLevel(p_235651_1_);
                           if (i < 5) {
                              int j = this.level.random.nextInt(100) == 0 ? 2 : 1;
                              if (i + j > 5) {
                                 --j;
                              }

                              this.level.setBlockAndUpdate(this.getBlockPos(), p_235651_1_.setValue(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
                           }
                        }
                     }

                     this.setBeeReleaseData(p_235651_2_.ticksInHive, beeentity);
                     if (p_235651_3_ != null) {
                        p_235651_3_.add(beeentity);
                     }

                     float f = entity.getBbWidth();
                     double d3 = flag ? 0.0D : 0.55D + (double)(f / 2.0F);
                     double d0 = (double)blockpos.getX() + 0.5D + d3 * (double)direction.getStepX();
                     double d1 = (double)blockpos.getY() + 0.5D - (double)(entity.getBbHeight() / 2.0F);
                     double d2 = (double)blockpos.getZ() + 0.5D + d3 * (double)direction.getStepZ();
                     entity.moveTo(d0, d1, d2, entity.yRot, entity.xRot);
                  }

                  this.level.playSound((PlayerEntity)null, blockpos, SoundEvents.BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  return this.level.addFreshEntity(entity);
               }
            } else {
               return false;
            }
         }
      }
   }

   private void setBeeReleaseData(int p_235650_1_, BeeEntity p_235650_2_) {
      int i = p_235650_2_.getAge();
      if (i < 0) {
         p_235650_2_.setAge(Math.min(0, i + p_235650_1_));
      } else if (i > 0) {
         p_235650_2_.setAge(Math.max(0, i - p_235650_1_));
      }

      p_235650_2_.setInLoveTime(Math.max(0, p_235650_2_.getInLoveTime() - p_235650_1_));
      p_235650_2_.resetTicksWithoutNectarSinceExitingHive();
   }

   private boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   private void tickOccupants() {
      Iterator<BeehiveTileEntity.Bee> iterator = this.stored.iterator();

      BeehiveTileEntity.Bee beehivetileentity$bee;
      for(BlockState blockstate = this.getBlockState(); iterator.hasNext(); beehivetileentity$bee.ticksInHive++) {
         beehivetileentity$bee = iterator.next();
         if (beehivetileentity$bee.ticksInHive > beehivetileentity$bee.minOccupationTicks) {
            BeehiveTileEntity.State beehivetileentity$state = beehivetileentity$bee.entityData.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
            if (this.releaseOccupant(blockstate, beehivetileentity$bee, (List<Entity>)null, beehivetileentity$state)) {
               iterator.remove();
            }
         }
      }

   }

   public void tick() {
      if (!this.level.isClientSide) {
         this.tickOccupants();
         BlockPos blockpos = this.getBlockPos();
         if (this.stored.size() > 0 && this.level.getRandom().nextDouble() < 0.005D) {
            double d0 = (double)blockpos.getX() + 0.5D;
            double d1 = (double)blockpos.getY();
            double d2 = (double)blockpos.getZ() + 0.5D;
            this.level.playSound((PlayerEntity)null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         this.sendDebugPackets();
      }
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.stored.clear();
      ListNBT listnbt = p_230337_2_.getList("Bees", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         BeehiveTileEntity.Bee beehivetileentity$bee = new BeehiveTileEntity.Bee(compoundnbt.getCompound("EntityData"), compoundnbt.getInt("TicksInHive"), compoundnbt.getInt("MinOccupationTicks"));
         this.stored.add(beehivetileentity$bee);
      }

      this.savedFlowerPos = null;
      if (p_230337_2_.contains("FlowerPos")) {
         this.savedFlowerPos = NBTUtil.readBlockPos(p_230337_2_.getCompound("FlowerPos"));
      }

   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.put("Bees", this.writeBees());
      if (this.hasSavedFlowerPos()) {
         p_189515_1_.put("FlowerPos", NBTUtil.writeBlockPos(this.savedFlowerPos));
      }

      return p_189515_1_;
   }

   public ListNBT writeBees() {
      ListNBT listnbt = new ListNBT();

      for(BeehiveTileEntity.Bee beehivetileentity$bee : this.stored) {
         beehivetileentity$bee.entityData.remove("UUID");
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.put("EntityData", beehivetileentity$bee.entityData);
         compoundnbt.putInt("TicksInHive", beehivetileentity$bee.ticksInHive);
         compoundnbt.putInt("MinOccupationTicks", beehivetileentity$bee.minOccupationTicks);
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   static class Bee {
      private final CompoundNBT entityData;
      private int ticksInHive;
      private final int minOccupationTicks;

      private Bee(CompoundNBT p_i225767_1_, int p_i225767_2_, int p_i225767_3_) {
         p_i225767_1_.remove("UUID");
         this.entityData = p_i225767_1_;
         this.ticksInHive = p_i225767_2_;
         this.minOccupationTicks = p_i225767_3_;
      }
   }

   public static enum State {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;
   }
}
