package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

public class PatrolVillageGoal extends RandomWalkingGoal {
   public PatrolVillageGoal(CreatureEntity p_i231547_1_, double p_i231547_2_) {
      super(p_i231547_1_, p_i231547_2_, 240, false);
   }

   @Nullable
   protected Vector3d getPosition() {
      float f = this.mob.level.random.nextFloat();
      if (this.mob.level.random.nextFloat() < 0.3F) {
         return this.getPositionTowardsAnywhere();
      } else {
         Vector3d vector3d;
         if (f < 0.7F) {
            vector3d = this.getPositionTowardsVillagerWhoWantsGolem();
            if (vector3d == null) {
               vector3d = this.getPositionTowardsPoi();
            }
         } else {
            vector3d = this.getPositionTowardsPoi();
            if (vector3d == null) {
               vector3d = this.getPositionTowardsVillagerWhoWantsGolem();
            }
         }

         return vector3d == null ? this.getPositionTowardsAnywhere() : vector3d;
      }
   }

   @Nullable
   private Vector3d getPositionTowardsAnywhere() {
      return RandomPositionGenerator.getLandPos(this.mob, 10, 7);
   }

   @Nullable
   private Vector3d getPositionTowardsVillagerWhoWantsGolem() {
      ServerWorld serverworld = (ServerWorld)this.mob.level;
      List<VillagerEntity> list = serverworld.getEntities(EntityType.VILLAGER, this.mob.getBoundingBox().inflate(32.0D), this::doesVillagerWantGolem);
      if (list.isEmpty()) {
         return null;
      } else {
         VillagerEntity villagerentity = list.get(this.mob.level.random.nextInt(list.size()));
         Vector3d vector3d = villagerentity.position();
         return RandomPositionGenerator.getLandPosTowards(this.mob, 10, 7, vector3d);
      }
   }

   @Nullable
   private Vector3d getPositionTowardsPoi() {
      SectionPos sectionpos = this.getRandomVillageSection();
      if (sectionpos == null) {
         return null;
      } else {
         BlockPos blockpos = this.getRandomPoiWithinSection(sectionpos);
         return blockpos == null ? null : RandomPositionGenerator.getLandPosTowards(this.mob, 10, 7, Vector3d.atBottomCenterOf(blockpos));
      }
   }

   @Nullable
   private SectionPos getRandomVillageSection() {
      ServerWorld serverworld = (ServerWorld)this.mob.level;
      List<SectionPos> list = SectionPos.cube(SectionPos.of(this.mob), 2).filter((p_234030_1_) -> {
         return serverworld.sectionsToVillage(p_234030_1_) == 0;
      }).collect(Collectors.toList());
      return list.isEmpty() ? null : list.get(serverworld.random.nextInt(list.size()));
   }

   @Nullable
   private BlockPos getRandomPoiWithinSection(SectionPos p_234029_1_) {
      ServerWorld serverworld = (ServerWorld)this.mob.level;
      PointOfInterestManager pointofinterestmanager = serverworld.getPoiManager();
      List<BlockPos> list = pointofinterestmanager.getInRange((p_234027_0_) -> {
         return true;
      }, p_234029_1_.center(), 8, PointOfInterestManager.Status.IS_OCCUPIED).map(PointOfInterest::getPos).collect(Collectors.toList());
      return list.isEmpty() ? null : list.get(serverworld.random.nextInt(list.size()));
   }

   private boolean doesVillagerWantGolem(VillagerEntity p_234028_1_) {
      return p_234028_1_.wantsToSpawnGolem(this.mob.level.getGameTime());
   }
}
